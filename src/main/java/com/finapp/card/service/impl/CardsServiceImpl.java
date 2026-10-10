package com.finapp.card.service.impl;

import com.finapp.card.client.AccountsFeignClient;
import com.finapp.card.constants.CardsConstants;
import com.finapp.card.dto.AccountsDto;
import com.finapp.card.dto.request.ActivateCardRequestDto;
import com.finapp.card.dto.request.CardDetailResponseDto;
import com.finapp.card.dto.request.RequestCardDto;
import com.finapp.card.dto.request.UpdateCardStatusRequestDto;
import com.finapp.card.dto.response.CardCreationResponseDto;
import com.finapp.card.dto.response.PaginatedResponseDto;
import com.finapp.card.entity.Cards;
import com.finapp.card.enums.CardType;
import com.finapp.card.enums.RequestMode;
import com.finapp.card.enums.Status;
import com.finapp.card.exception.FinAppValidationException;
import com.finapp.card.mapper.CardsMapper;
import com.finapp.card.repository.CardsRepository;
import com.finapp.card.service.ICardsService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HexFormat;

@Service
@Slf4j
@RequiredArgsConstructor
public class CardsServiceImpl implements ICardsService {

    private final CardsRepository cardsRepository;
    private final AccountsFeignClient accountsFeignClient;

    @Value("${card.bin}")
    private String BIN;

    @Value("${card.pan.secret}")
    private String panSecret;

    @Value("${card.fee.account}")
    private String cardFeeAccount;

    @Value("${card.expiry}")
    private int cardExpiry;

    @Value("${card.default.limit}")
    private BigDecimal cardLimit;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public CardCreationResponseDto createCard(RequestCardDto requestCardDto) {
        if (requestCardDto == null) {
            throw new IllegalArgumentException("Card request DTO cannot be null or empty");
        }

        String trackingReference = generateReference();
        log.info("trackingReference={}", trackingReference);

        requestCardDto.setBankBranch(validateCardCreationRequestBody(requestCardDto));

        Long accountNumber = Long.parseLong(requestCardDto.getAccountNumber().trim());

        // Call remote Accounts microservice
        AccountsDto account = validateAccount(accountNumber);

        BigDecimal accountBalance = account.getAccountBalance() != null ? account.getAccountBalance() : BigDecimal.ZERO;
        CardType cardType = CardType.from(requestCardDto.getCardType());

        BigDecimal cardFeeBigDecimal = extractFeeAsBigDecimal(cardType);

        if (accountBalance.compareTo(cardFeeBigDecimal) < 0) {
            throw new FinAppValidationException(HttpStatus.FORBIDDEN, "Insufficient balance.");
        }

        Cards cards = CardsMapper.mapToCardCreation(requestCardDto, new Cards());

        String customerName = account.getCustomerName() != null ? account.getCustomerName().toUpperCase() : "FINAPP CUSTOMER";

        Long cardFeeDestinationAccount = Long.parseLong(getCardFeeAccount(cardFeeAccount));

        log.info("Performing card transaction on senderAccountNumber={}, destinationAccountNumber={}, amount={}",
                accountNumber, cardFeeDestinationAccount, cardFeeBigDecimal);

        accountsFeignClient.transfer(accountNumber, cardFeeDestinationAccount, cardFeeBigDecimal);

        String cardPan = generateCardPan(cardType.getPanLength());
        String defaultPin = generateDefaultCardPin();
        String cvv = generateCvv();
        String expiry = generateExpiryDate(cardExpiry);

        cards.setReference(trackingReference);
        cards.setCardName(customerName);
        cards.setCardActivated(false);
        cards.setCardStatus(Status.PENDING_ACTIVATION.name());
        cards.setCardFee(cardFeeBigDecimal.longValue()); // Converted to Long to match Cards.java
        cards.setEncryptedCvv(hashCvv(cvv));
        cards.setExpiration(expiry);
        cards.setPanHash(hashPan(cardPan, panSecret));
        cards.setMaskedPan(maskPan(cardPan));
        cards.setEncryptedPin(hashPin(defaultPin));
        cards.setAccountNumber(String.valueOf(accountNumber));
        cards.setAccountId(account.getCustomerId() != null ? account.getCustomerId() : accountNumber); // Matches nullable = false
        cards.setCardCanTransact(false);
        cards.setTotalLimit(cardLimit != null ? cardLimit.longValue() : 0L); // Converted to Long to match Cards.java
        cards.setPanLength(cardType.getPanLength());

        cardsRepository.save(cards);

        AccountsDto updatedAccount = validateAccount(accountNumber);
        BigDecimal newBalance = updatedAccount.getAccountBalance();

        return CardCreationResponseDto.builder()
                .cardReference(trackingReference)
                .cardActivated(false)
                .cardName(customerName)
                .accontBalance(newBalance)
                .pan(formatPanWithHyphens(cardPan))
                .cvv(cvv)
                .defaultPin(defaultPin)
                .expiration(expiry)
                .createdAt(LocalDate.now())
                .build();
    }

    @Override
    public void activateCard(ActivateCardRequestDto activateCardRequestDto) {
        validateActivateCardRequest(activateCardRequestDto);

        Cards card = cardsRepository.findByAccountNumber(activateCardRequestDto.getAccountNumber())
                .orElseThrow(() -> new FinAppValidationException(HttpStatus.NOT_FOUND, "No card found"));

        // Boolean wrapper uses getCardActivated() generated by Lombok
        if (Boolean.TRUE.equals(card.getCardActivated())) {
            throw new FinAppValidationException(HttpStatus.FORBIDDEN, "Card is already activated");
        }

        if (validateCardStatus(card)) {
            throw new FinAppValidationException(HttpStatus.FORBIDDEN, "Card is inactive. Kindly contact your business concierge");
        }

        boolean defaultPinMatch = verifyPin(activateCardRequestDto.getDefaultPin().trim(), card.getEncryptedPin());
        boolean cvvMatch = verifyCvv(activateCardRequestDto.getCvv().trim(), card.getEncryptedCvv());

        if (!defaultPinMatch || !cvvMatch) {
            throw new FinAppValidationException(HttpStatus.BAD_REQUEST, "Invalid default PIN or CVV provided");
        }

        String hashNewPin = hashPin(activateCardRequestDto.getNewPin().trim());

        card.setEncryptedPin(hashNewPin);
        card.setCardActivated(true);
        card.setCardCanTransact(true);
        card.setCardStatus(Status.ACTIVE.name());
        card.setCardActivatedAt(LocalDateTime.now());

        cardsRepository.save(card);
        log.info("Card activation successful");
    }

    @Override
    public CardDetailResponseDto fetchSingleCard(String accountNumber) {
        if (!StringUtils.hasText(accountNumber)) {
            throw new FinAppValidationException(HttpStatus.BAD_REQUEST, "accountNumber cannot be empty or null.");
        }

        log.info("accountNumber={}", accountNumber);

        Cards card = cardsRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new FinAppValidationException(HttpStatus.NOT_FOUND, "Card does not exist"));

        return CardsMapper.mapToCardDetailDto(card);
    }

    @Override
    public PaginatedResponseDto<CardDetailResponseDto> fetchAllCards(int page, int size) {
        validatePaginationRequest(page, size);

        log.info("page={}, size={}", page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by("id"));
        Page<CardDetailResponseDto> cards = cardsRepository.findAll(pageable)
                .map(CardsMapper::mapToCardDetailDto);

        return PaginatedResponseDto.from(cards);
    }

    @Override
    public void deactivateCard(UpdateCardStatusRequestDto updateCardStatusRequestDto) {
        if (updateCardStatusRequestDto == null) {
            throw new FinAppValidationException(HttpStatus.BAD_REQUEST, "Request body cannot be null or empty.");
        }

        String accNumber = updateCardStatusRequestDto.getAccountNumber();

        if (!StringUtils.hasText(accNumber)) {
            throw new FinAppValidationException(HttpStatus.BAD_REQUEST, "accountNumber is required.");
        } else if (accNumber.trim().length() != 10) {
            throw new FinAppValidationException(HttpStatus.BAD_REQUEST, "accountNumber must be 10 digits");
        }

        if (!StringUtils.hasText(updateCardStatusRequestDto.getCurrentPin())) {
            throw new FinAppValidationException(HttpStatus.BAD_REQUEST, "currentPin is required.");
        } else if (updateCardStatusRequestDto.getCurrentPin().trim().length() != 4) {
            throw new FinAppValidationException(HttpStatus.BAD_REQUEST, "currentPin must be 4 digits");
        }

        AccountsDto account = validateAccount(Long.parseLong(accNumber.trim()));

        Cards card = cardsRepository.findByAccountNumber(updateCardStatusRequestDto.getAccountNumber().trim())
                .orElseThrow(() -> new FinAppValidationException(HttpStatus.NOT_FOUND, "Card not found."));

        if (Status.INACTIVE.name().equals(card.getCardStatus())) {
            throw new FinAppValidationException(HttpStatus.FORBIDDEN, "Card is already deactivated");
        }

        if (verifyPin(updateCardStatusRequestDto.getCurrentPin(), card.getEncryptedPin()) && account.isAccountActive()) {
            card.setCardStatus(Status.INACTIVE.name());
            card.setCardCanTransact(false);
            cardsRepository.save(card);
        }

        log.info("Card with accountNumber={} is successfully deactivated", accNumber);
    }

    @Override
    public void reactivateCard(UpdateCardStatusRequestDto updateCardStatusRequestDto) {
        if (updateCardStatusRequestDto == null) {
            throw new FinAppValidationException(HttpStatus.BAD_REQUEST, "Request body cannot be null or empty.");
        }

        String accNumber = updateCardStatusRequestDto.getAccountNumber();

        if (!StringUtils.hasText(accNumber)) {
            throw new FinAppValidationException(HttpStatus.BAD_REQUEST, "accountNumber is required.");
        } else if (accNumber.trim().length() != 10) {
            throw new FinAppValidationException(HttpStatus.BAD_REQUEST, "accountNumber must be 10 digits");
        }

        if (!StringUtils.hasText(updateCardStatusRequestDto.getCurrentPin())) {
            throw new FinAppValidationException(HttpStatus.BAD_REQUEST, "currentPin is required.");
        } else if (updateCardStatusRequestDto.getCurrentPin().trim().length() != 4) {
            throw new FinAppValidationException(HttpStatus.BAD_REQUEST, "currentPin must be 4 digits");
        }

        AccountsDto account = validateAccount(Long.parseLong(accNumber.trim()));

        Cards card = cardsRepository.findByAccountNumber(updateCardStatusRequestDto.getAccountNumber().trim())
                .orElseThrow(() -> new FinAppValidationException(HttpStatus.NOT_FOUND, "Card not found."));

        if (Status.ACTIVE.name().equals(card.getCardStatus())) {
            throw new FinAppValidationException(HttpStatus.FORBIDDEN, "Card is already active");
        }

        if (verifyPin(updateCardStatusRequestDto.getCurrentPin(), card.getEncryptedPin()) && account.isAccountActive()) {
            card.setCardStatus(Status.ACTIVE.name());
            card.setCardCanTransact(true);
            cardsRepository.save(card);
        }

        log.info("Card with accountNumber={} is successfully reactivated", accNumber);
    }

    @Override
    @Transactional
    public void deleteCard(String accountNumber) {
        if (!StringUtils.hasText(accountNumber)) {
            throw new FinAppValidationException(HttpStatus.BAD_REQUEST, "Account number is required");
        }

        if (accountNumber.trim().length() != 10) {
            throw new FinAppValidationException(HttpStatus.NOT_ACCEPTABLE, "Account number must be 10 digits");
        }

        String cleanedAccNumber = accountNumber.trim();
        validateAccount(Long.parseLong(cleanedAccNumber));

        Cards card = cardsRepository.findByAccountNumber(cleanedAccNumber)
                .orElseThrow(() -> new FinAppValidationException(HttpStatus.NOT_FOUND, "Card not found."));

        cardsRepository.delete(card);
        log.info("Card with accountNumber={} is successfully deleted", cleanedAccNumber);
    }

    private boolean validateCardStatus(Cards card) {
        return Status.ACTIVE.name().equals(card.getCardStatus());
    }

    private String generateCardPan(int panLength) {
        if (BIN == null || !BIN.matches("\\d{6}")) {
            throw new IllegalArgumentException("BIN must be exactly 6 digits");
        }

        if (panLength < BIN.length() + 2) {
            throw new IllegalArgumentException("PAN length must be at least " + (BIN.length() + 2));
        }

        int accountIdentifierLength = panLength - BIN.length() - 1;
        StringBuilder pan = new StringBuilder(BIN);

        for (int i = 0; i < accountIdentifierLength; i++) {
            pan.append(SECURE_RANDOM.nextInt(10));
        }

        int checkDigit = calculateLuhnCheckDigit(pan.toString());
        pan.append(checkDigit);

        return pan.toString();
    }

    private int calculateLuhnCheckDigit(String number) {
        int sum = 0;
        boolean doubleDigit = true;

        for (int i = number.length() - 1; i >= 0; i--) {
            int digit = number.charAt(i) - '0';
            if (doubleDigit) {
                digit *= 2;
                if (digit > 9) {
                    digit -= 9;
                }
            }
            sum += digit;
            doubleDigit = !doubleDigit;
        }

        return (10 - (sum % 10)) % 10;
    }

    private String generateDefaultCardPin() {
        return String.format("%04d", SECURE_RANDOM.nextInt(10000));
    }

    private String hashPin(String pin) {
        return passwordEncoder.encode(pin);
    }

    private boolean verifyPin(String plainPin, String hashedPin) {
        return passwordEncoder.matches(plainPin, hashedPin);
    }

    private String maskPan(String pan) {
        if (pan == null || pan.length() < 4) {
            throw new IllegalArgumentException("Invalid PAN");
        }
        int maskedLength = pan.length() - 4;
        return "*".repeat(maskedLength) + pan.substring(maskedLength);
    }

    private String hashPan(String pan, String secretKey) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(keySpec);
            byte[] hash = mac.doFinal(pan.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to hash PAN", e);
        }
    }

    private String generateReference() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder reference = new StringBuilder("REF");
        for (int i = 0; i < 12; i++) {
            reference.append(characters.charAt(SECURE_RANDOM.nextInt(characters.length())));
        }
        return reference.toString();
    }

    private String validateCardCreationRequestBody(RequestCardDto requestCardDto) {
        if (!StringUtils.hasText(requestCardDto.getAccountNumber())) {
            throw new IllegalArgumentException("accountNumber cannot be null or empty");
        }

        if (requestCardDto.getRequestMode() == null) {
            throw new IllegalArgumentException("requestMode cannot be null or empty");
        }

        if (RequestMode.PHYSICAL.name().equalsIgnoreCase(requestCardDto.getRequestMode())) {
            if (!StringUtils.hasText(requestCardDto.getBankBranch())) {
                throw new IllegalArgumentException("bankBranch cannot be null or empty when requestMode is PHYSICAL");
            }
        }

        if (!StringUtils.hasText(requestCardDto.getCardType())) {
            throw new IllegalArgumentException("cardType cannot be null or empty");
        }

        return RequestMode.PHYSICAL.name().equalsIgnoreCase(requestCardDto.getRequestMode())
                ? requestCardDto.getBankBranch() : RequestMode.ONLINE.name();
    }

    private String generateCvv() {
        int cvvValue = 100 + SECURE_RANDOM.nextInt(900);
        return String.valueOf(cvvValue);
    }

    public String generateExpiryDate(int validityYears) {
        LocalDate expiryDate = LocalDate.now().plusYears(validityYears);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");
        return expiryDate.format(formatter);
    }

    public String hashCvv(String cvv) {
        if (!StringUtils.hasText(cvv)) {
            throw new IllegalArgumentException("CVV cannot be null or empty");
        }
        return passwordEncoder.encode(cvv);
    }

    public boolean verifyCvv(String rawCvv, String hashedCvv) {
        if (!StringUtils.hasText(rawCvv) || !StringUtils.hasText(hashedCvv)) {
            return false;
        }
        return passwordEncoder.matches(rawCvv, hashedCvv);
    }

    private String formatPanWithHyphens(String pan) {
        if (!StringUtils.hasText(pan)) {
            return "";
        }
        String cleanPan = pan.replaceAll("[\\s-]", "").trim();
        return cleanPan.replaceAll("(?<=\\G.{4})(?!$)", "-");
    }

    public void validateActivateCardRequest(ActivateCardRequestDto request) {
        if (request == null) {
            throw new IllegalArgumentException("Request body cannot be null or empty");
        }

        if (request.getAccountNumber() == null || !request.getAccountNumber().matches("\\d{10}")) {
            throw new IllegalArgumentException("accountNumber must be exactly 10 digits");
        }

        if (!StringUtils.hasText(request.getDefaultPin()) || !request.getDefaultPin().matches("\\d{4}")) {
            throw new IllegalArgumentException("defaultPin must be 4 digits");
        }

        if (!StringUtils.hasText(request.getNewPin())) {
            throw new IllegalArgumentException("newPin must be 4 digits");
        }

        if (!StringUtils.hasText(request.getCvv())) {
            throw new IllegalArgumentException("cvv is required");
        }

        if (!request.getCvv().matches("\\d{3}")) {
            throw new IllegalArgumentException("cvv must be 3 digits");
        }
    }

    private void validatePaginationRequest(int page, int size) {
        if (page < 0) {
            throw new FinAppValidationException(HttpStatus.BAD_REQUEST, "page cannot be negative.");
        }

        if (size < 1 || size > CardsConstants.MAX_PAGE_SIZE) {
            throw new FinAppValidationException(HttpStatus.BAD_REQUEST,
                    "size must be between 1 and " + CardsConstants.MAX_PAGE_SIZE + ".");
        }
    }

    private AccountsDto validateAccount(Long accountNumber) {
        ResponseEntity<AccountsDto> response = accountsFeignClient.fetchAccountDetails(accountNumber);
        if (response == null || response.getBody() == null) {
            throw new FinAppValidationException(HttpStatus.NOT_FOUND,
                    "Account does not exist. Kindly contact your business concierge");
        }

        AccountsDto account = response.getBody();
        if (!account.isAccountActive()) {
            throw new FinAppValidationException(HttpStatus.NOT_FOUND,
                    "Account does not exist or is inactive. Kindly contact your business concierge");
        }

        return account;
    }

    private String getCardFeeAccount(String email) {
        ResponseEntity<AccountsDto> response = accountsFeignClient.fetchAccountByEmail(email);
        if (response == null || response.getBody() == null || response.getBody().getAccountNumber() == null) {
            throw new FinAppValidationException(HttpStatus.NOT_FOUND, "Fee collection account not found.");
        }
        return String.valueOf(response.getBody().getAccountNumber());
    }

    private BigDecimal extractFeeAsBigDecimal(CardType cardType) {
        Object fee = cardType.getFee();
        if (fee instanceof BigDecimal bigDecimalFee) {
            return bigDecimalFee;
        } else if (fee instanceof Number numberFee) {
            return BigDecimal.valueOf(numberFee.doubleValue());
        }
        return BigDecimal.ZERO;
    }
}