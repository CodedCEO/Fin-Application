package com.finapp.account.service.impl;

import com.finapp.account.dto.request.ActivateCardRequestDto;
import com.finapp.account.dto.request.CardDetailResponseDto;
import com.finapp.account.entity.Account;
import com.finapp.account.enums.Status;
import com.finapp.account.exception.FinAppValidationException;
import com.finapp.account.repository.AccountsRepository;
import com.finapp.account.repository.CustomerRepository;
import com.finapp.account.service.IAccountsService;
import com.finapp.account.dto.request.RequestCardDto;
import com.finapp.account.dto.response.CardCreationResponseDto;
import com.finapp.account.entity.Cards;
import com.finapp.account.enums.CardType;
import com.finapp.account.enums.RequestMode;
import com.finapp.account.mapper.CardsMapper;
import com.finapp.account.repository.CardsRepository;
import com.finapp.account.service.ICardsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.smartcardio.Card;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HexFormat;

@Service
@Slf4j
public class CardsServiceImpl implements ICardsService {

    @Autowired
    private CardsRepository cardsRepository;
    @Autowired
    private AccountsRepository accountsRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private IAccountsService accountService;

    @Value("${card.bin}")
    private String BIN;

    @Value("${card.pan.secret}")
    private String panSecret;

    @Value("${card.fee.account}")
    private Long cardFeeAccount;

    @Value("${card.expiry}")
    private int cardExpiry;

    @Value("${card.default.limit}")
    private BigDecimal cardLimit;


    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    public CardCreationResponseDto createCard(RequestCardDto requestCardDto) {
        if (requestCardDto == null) {
            throw new IllegalArgumentException("card request dto cannot be null or empty");
        }

        String trackingReference = generateReference();
        log.info("trackingReference={}", trackingReference);

        requestCardDto.setBankBranch(validateCardCreationRequestBody(requestCardDto));

        Long accountNumber = Long.parseLong(requestCardDto.getAccountNumber().trim());

        Account account = validateAccount(accountNumber);

        if (!Boolean.TRUE.equals(account.isAccountActive())) {
            throw new FinAppValidationException(HttpStatus.NOT_FOUND,
                    "Account does not exist or is inactive. Kindly contact your business concierge");
        }

        BigDecimal accountBalance = account.getAccountBalance();

        CardType cardType = CardType.from(requestCardDto.getCardType());

        if (accountBalance.compareTo(cardType.getFee()) < 0) {
            throw new FinAppValidationException(HttpStatus.FORBIDDEN, "Insufficient balance.");
        }

        Cards cards = CardsMapper.mapToCardCreation(requestCardDto,new Cards());

        String customerName = customerRepository.findByCustomerId(account.getCustomerId()).getFullName().toUpperCase();

        //accountService.transfer(accountNumber, cardFeeAccount, cardType.getFee());

        String cardPan = generateCardPan(cardType.getPanLength());
        String defaultPin = generateDefaultCardPin();
        String cvv = generateCvv();
        String expiry = generateExpiryDate(cardExpiry);

        cards.setReference(trackingReference);
        cards.setCardName(customerName);
        cards.setCardActivated(false);
        cards.setCardStatus(Status.PENDING_ACTIVATION.name());
        cards.setCardFee(cardType.getFee());
        cards.setEncryptedCvv(hashCvv(cvv));
        cards.setExpiration(expiry);
        cards.setPanHash(hashPan(cardPan, panSecret));
        cards.setMaskedPan(maskPan(cardPan));
        cards.setEncryptedPin(hashPin(defaultPin));
        cards.setAccount(account);
        cards.setTotalLimit(cardLimit);
        cards.setPanLength(cardType.getPanLength());

        cardsRepository.save(cards);

        return CardCreationResponseDto.builder()
                .cardReference(trackingReference)
                .cardActivated(false)
                .cardName(customerName)
                .accontBalance(accountBalance)
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

        if(card.isCardActivated()){
            throw new FinAppValidationException(HttpStatus.FORBIDDEN,"Card is already activated");
        }

        if(validateCardStatus(card)) {
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
        card.setCardStatus(Status.ACTIVE.name());
        card.setCardActivatedAt(LocalDateTime.now());
        card.setUpdatedAt(LocalDateTime.now());

        cardsRepository.save(card);

        log.info("Card activation successful");
    }

    @Override
    public CardDetailResponseDto fetchSingleCard(String accountNumber) {
        if(!StringUtils.hasText(accountNumber)) {
            throw new FinAppValidationException(HttpStatus.BAD_REQUEST, "accountNumber cannot be empty or null.");
        }

        log.info("accountNumber={}", accountNumber);

        Cards card = cardsRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new FinAppValidationException(HttpStatus.NOT_FOUND, "Card does not exist"));

        return CardsMapper.mapToCardDetailDto(card);
    }
//
//    @Override
//    public boolean deactivateCard(String CardNumber) {
//        return false;
//    }
//
//    @Override
//    public boolean deleteCard(Long cardId) {
//        return false;
//    }

    private boolean validateCardStatus(Cards card) {
        boolean status = false;

        if(Status.ACTIVE.name().equals(card.getCardStatus())) {
            return true;
        }

        return status;

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

        // Generate random account identifier
        for (int i = 0; i < accountIdentifierLength; i++) {
            pan.append(SECURE_RANDOM.nextInt(10));
        }

        // Generate Luhn check digit
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

        if(!StringUtils.hasText(requestCardDto.getCardType())) {
            throw new IllegalArgumentException("cardType cannot be null or empty");

        }

        return requestCardDto.getRequestMode() == RequestMode.PHYSICAL.name()
                ? requestCardDto.getBankBranch() : RequestMode.ONLINE.name();

    }

    /**
     * Generates a cryptographically secure 3-digit CVV string.
     */
    private String generateCvv() {
        int cvvValue = 100 + SECURE_RANDOM.nextInt(900);
        return String.valueOf(cvvValue);
    }

    /**
     * Generates an expiry date string (MM/yy) set to a specific number of years from now.
     * @param validityYears The lifespan of the card (e.g., 3 or 5 years)
     */
    public String generateExpiryDate(int validityYears) {
        LocalDate expiryDate = LocalDate.now().plusYears(validityYears);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");
        return expiryDate.format(formatter);
    }

    /**
     * Hashes the 3-digit CVV using BCrypt.
     * @param cvv The raw CVV string.
     * @return The securely hashed CVV string.
     */
    public String hashCvv(String cvv) {
        if (!StringUtils.hasText(cvv)) {
            throw new IllegalArgumentException("CVV cannot be null or empty");
        }
        return passwordEncoder.encode(cvv);
    }

    /**
     * Verifies if a raw CVV matches the stored secure hash.
     * @param rawCvv The CVV input from a user or request.
     * @param hashedCvv The hashed CVV fetched from the database.
     * @return true if they match, false otherwise.
     */
    public boolean verifyCvv(String rawCvv, String hashedCvv) {
        if (!StringUtils.hasText(rawCvv) || !StringUtils.hasText(hashedCvv)) {
            return false;
        }
        return passwordEncoder.matches(rawCvv, hashedCvv);
    }

    /**
     * Formats a raw PAN string by adding a hyphen after every 4 digits.
     * Works seamlessly for both 16-digit (Visa/Mastercard) and 19-digit (Verve) cards.
     *
     * @param pan The raw PAN string (e.g., "5123456789012345")
     * @return The formatted PAN string (e.g., "5123-4567-8901-2345"), or an empty string if input is invalid.
     */
    private String formatPanWithHyphens(String pan) {
        if (!StringUtils.hasText(pan)) {
            return "";
        }

        // Remove any accidental spaces or existing hyphens first, then trim
        String cleanPan = pan.replaceAll("[\\s-]", "").trim();

        // Uses regex lookbehind to insert a hyphen every 4 digits, except at the very end
        return cleanPan.replaceAll("(?<=\\G.{4})(?!$)", "-");
    }

    public void validateActivateCardRequest(ActivateCardRequestDto request) {
        if (request == null) {
            throw new IllegalArgumentException("Request body cannot be null or empty");
        }

        // 1. Validate accountNumber
        if (request.getAccountNumber() == null) {
            throw new IllegalArgumentException("accountNumber is required");
        }
        if (!request.getAccountNumber().matches("\\d{10}")) {
            throw new IllegalArgumentException("accountNumber must be exactly 10 digits");
        }

        // 2. Validate defaultPin
        if (!StringUtils.hasText(request.getDefaultPin())) {
            throw new IllegalArgumentException("defaultPin is required");
        }

        if (!request.getDefaultPin().matches("\\d{4}")) {
            throw new IllegalArgumentException("defaultPin must be 4 digits");
        }

        // 3. Validate newPin
        if (!StringUtils.hasText(request.getNewPin())) {
            throw new IllegalArgumentException("newPin is required");
        }
        if (!request.getNewPin().matches("\\d{4}")) {
            throw new IllegalArgumentException("newPin must be 4 digits");
        }

        // 4. Validate cvv
        if (!StringUtils.hasText(request.getCvv())) {
            throw new IllegalArgumentException("cvv is required");
        }

        if (!request.getCvv().matches("\\d{3}")) {
            throw new IllegalArgumentException("cvv must be 3 digits");
        }
    }

    private Account validateAccount(Long accountNumber) {
        return accountsRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new FinAppValidationException(HttpStatus.NOT_FOUND,
                        "Account does not exist or is inactive. Kindly contact your business concierge"));
    }



}
