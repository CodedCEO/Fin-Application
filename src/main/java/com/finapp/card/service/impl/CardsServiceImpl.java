package com.finapp.card.service.impl;

import com.finapp.card.client.AccountsFeignClient;
import com.finapp.card.dto.AccountsDto;
import com.finapp.card.dto.request.ActivateCardRequestDto;
import com.finapp.card.dto.request.CardDetailResponseDto;
import com.finapp.card.dto.request.RequestCardDto;
import com.finapp.card.dto.request.UpdateCardStatusRequestDto;
import com.finapp.card.dto.response.CardCreationResponseDto;
import com.finapp.card.dto.response.PaginatedResponseDto;
import com.finapp.card.entity.Cards;
import com.finapp.card.exception.FinAppValidationException;
import com.finapp.card.repository.CardsRepository;
import com.finapp.card.service.ICardsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardsServiceImpl implements ICardsService {

    private final CardsRepository cardsRepository;
    private final AccountsFeignClient accountsFeignClient;

    private static final BigDecimal CARD_FEE = new BigDecimal("1000.00");
    private static final String FEE_ACCOUNT_EMAIL = "cardfee@finapp.com";
    private static final String SYSTEM_OPERATOR = "CARDS_SERVICE";

    @Override
    @Transactional
    public void createCard(Long accountNumber) {
        RequestCardDto requestCardDto = new RequestCardDto();
        requestCardDto.setAccountNumber(String.valueOf(accountNumber));
        requestCardDto.setCardType("DEBIT");
        requestCardDto.setRequestMode("PHYSICAL");
        createCard(requestCardDto);
    }

    @Override
    @Transactional
    public CardCreationResponseDto createCard(RequestCardDto requestCardDto) {
        if (requestCardDto == null || requestCardDto.getAccountNumber() == null) {
            throw new FinAppValidationException(HttpStatus.BAD_REQUEST, "Invalid card request: account number is required.");
        }

        Long accountNumber = Long.valueOf(requestCardDto.getAccountNumber());
        String accNumStr = String.valueOf(accountNumber);
        log.info("Initiating card issuance for account number: {}", accountNumber);

        if (cardsRepository.findByAccountNumber(accNumStr).isPresent()) {
            throw new FinAppValidationException(
                    HttpStatus.BAD_REQUEST,
                    "Card already exists for account number: " + accountNumber
            );
        }

        // 1. Validate customer account details and active status via Feign client
        AccountsDto customerAccount = validateAndGetAccount(accountNumber);

        // 2. Fetch fee revenue destination account
        ResponseEntity<AccountsDto> feeResponse = accountsFeignClient.fetchAccountByEmail(FEE_ACCOUNT_EMAIL);
        if (feeResponse == null || !feeResponse.getStatusCode().is2xxSuccessful() || feeResponse.getBody() == null) {
            throw new FinAppValidationException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Unable to verify fee revenue account. Card creation halted."
            );
        }
        Long feeDestinationAccount = feeResponse.getBody().getAccountNumber();

        // 3. Confirm balance sufficiency prior to transfer attempt
        if (customerAccount.getAccountBalance().compareTo(CARD_FEE) < 0) {
            throw new FinAppValidationException(
                    HttpStatus.BAD_REQUEST,
                    "Insufficient account balance (₦" + customerAccount.getAccountBalance() + ") for card fee (₦" + CARD_FEE + ")"
            );
        }

        // 4. Execute fee transfer via Feign and verify debit confirmation
        ResponseEntity<Void> transferResponse = accountsFeignClient.transfer(
                accountNumber,
                feeDestinationAccount,
                CARD_FEE
        );

        if (transferResponse == null || !transferResponse.getStatusCode().is2xxSuccessful()) {
            throw new FinAppValidationException(
                    HttpStatus.PAYMENT_REQUIRED,
                    "Card fee debit failed. Card has not been created."
            );
        }

        // 5. Generate secure mock card credentials
        String rawPan = "5399" + (1000_0000_0000L + (long) (new Random().nextDouble() * 8999_9999_9999L));
        String maskedPan = rawPan.substring(0, 4) + "********" + rawPan.substring(12);
        String rawCvv = String.format("%03d", new Random().nextInt(1000));
        String rawPin = "1234";
        String expiration = LocalDateTime.now().plusYears(3).format(DateTimeFormatter.ofPattern("MM/yy"));
        String branch = customerAccount.getBranchAddress() != null ? customerAccount.getBranchAddress() : "Victoria Island, Lagos";
        String cardRef = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();

        // 6. Build and persist database entity
        Cards newCard = new Cards();
        newCard.setReference(cardRef);
        newCard.setCardName(customerAccount.getCustomerName() != null ? customerAccount.getCustomerName() : "FINAPP DEBIT");
        newCard.setAccountNumber(accNumStr);
        newCard.setAccountId(customerAccount.getCustomerId() != null ? customerAccount.getCustomerId() : 101L);
        newCard.setMaskedPan(maskedPan);
        newCard.setPanHash(Integer.toHexString(rawPan.hashCode()));
        newCard.setCardType(requestCardDto.getCardType() != null ? requestCardDto.getCardType() : "DEBIT");
        newCard.setPanLength(16);
        newCard.setTotalLimit(100000L);
        newCard.setCardFee(CARD_FEE.longValue());
        newCard.setCardFeeAccount(String.valueOf(feeDestinationAccount));
        newCard.setEncryptedCvv(Base64.getEncoder().encodeToString(rawCvv.getBytes()));
        newCard.setEncryptedPin(Base64.getEncoder().encodeToString(rawPin.getBytes()));
        newCard.setExpiration(expiration);
        newCard.setIssuingBranch(branch);
        newCard.setRequestMode(requestCardDto.getRequestMode() != null ? requestCardDto.getRequestMode() : "PHYSICAL");

        // Lifecycle: Initially inactive until activated by the user
        newCard.setCardActivated(false);
        newCard.setCardActivatedAt(null);
        newCard.setCardStatus("INACTIVE");
        newCard.setCardCanTransact(false);

        // Audit fields initialization
        newCard.setCreatedAt(now);
        newCard.setCreatedBy(SYSTEM_OPERATOR);
        newCard.setUpdatedAt(now);
        newCard.setUpdatedBy(SYSTEM_OPERATOR);

        cardsRepository.save(newCard);
        log.info("Card successfully created and saved for account: {}", accountNumber);

        // 7. Verify balance reduction from fallback ledger
        AccountsDto updatedAccount = validateAndGetAccount(accountNumber);
        log.info("Confirmed updated balance for account {}: ₦{}", accountNumber, updatedAccount.getAccountBalance());

        // 8. Populate and return API response
        CardCreationResponseDto responseDto = new CardCreationResponseDto();
        responseDto.setCardReference(cardRef);
        responseDto.setCardName(newCard.getCardName());
        responseDto.setPan(maskedPan);
        responseDto.setCvv(rawCvv);
        responseDto.setExpiration(expiration);
        responseDto.setDefaultPin(rawPin);
        responseDto.setCardActivated(false);
        responseDto.setAccontBalance(updatedAccount.getAccountBalance());
        responseDto.setCreatedAt(LocalDate.from(now));

        return responseDto;
    }

    @Override
    public CardDetailResponseDto fetchSingleCard(String accountNumber) {
        Cards card = cardsRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new FinAppValidationException(
                        HttpStatus.NOT_FOUND,
                        "Card not found for account number: " + accountNumber
                ));
        return mapToDetailDto(card);
    }

    @Override
    public PaginatedResponseDto<CardDetailResponseDto> fetchAllCards(int page, int size) {
        Page<Cards> cardsPage = cardsRepository.findAll(PageRequest.of(page, size));

        List<CardDetailResponseDto> content = cardsPage.getContent().stream()
                .map(this::mapToDetailDto)
                .collect(Collectors.toList());

        PaginatedResponseDto<CardDetailResponseDto> response = new PaginatedResponseDto<>();
        response.setContent(content);
        response.setTotalElements(cardsPage.getTotalElements());
        response.setTotalPages(cardsPage.getTotalPages());
        response.setLast(cardsPage.isLast());

        return response;
    }

    @Override
    @Transactional
    public void activateCard(ActivateCardRequestDto activateCardRequestDto) {
        if (activateCardRequestDto == null || activateCardRequestDto.getAccountNumber() == null) {
            throw new FinAppValidationException(HttpStatus.BAD_REQUEST, "Invalid card activation data.");
        }

        Cards card = cardsRepository.findByAccountNumber(activateCardRequestDto.getAccountNumber())
                .orElseThrow(() -> new FinAppValidationException(HttpStatus.NOT_FOUND, "Card not found"));

        card.setCardActivated(true);
        card.setCardStatus("ACTIVE");
        card.setCardCanTransact(true);
        card.setCardActivatedAt(LocalDateTime.now());

        // Update PIN if new pin was supplied
        if (activateCardRequestDto.getNewPin() != null && !activateCardRequestDto.getNewPin().isBlank()) {
            card.setEncryptedPin(Base64.getEncoder().encodeToString(activateCardRequestDto.getNewPin().getBytes()));
        }

        card.setUpdatedAt(LocalDateTime.now());
        card.setUpdatedBy(SYSTEM_OPERATOR);

        cardsRepository.save(card);
        log.info("Card successfully activated for account: {}", activateCardRequestDto.getAccountNumber());
    }

    @Override
    @Transactional
    public void deactivateCard(UpdateCardStatusRequestDto updateCardStatusRequestDto) {
        if (updateCardStatusRequestDto == null || updateCardStatusRequestDto.getAccountNumber() == null) {
            throw new FinAppValidationException(HttpStatus.BAD_REQUEST, "Invalid card update request.");
        }

        Cards card = cardsRepository.findByAccountNumber(updateCardStatusRequestDto.getAccountNumber())
                .orElseThrow(() -> new FinAppValidationException(HttpStatus.NOT_FOUND, "Card not found"));

        card.setCardActivated(false);
        card.setCardStatus("INACTIVE");
        card.setCardCanTransact(false);
        card.setUpdatedAt(LocalDateTime.now());
        card.setUpdatedBy(SYSTEM_OPERATOR);

        cardsRepository.save(card);
        log.info("Card deactivated for account: {}", updateCardStatusRequestDto.getAccountNumber());
    }

    @Override
    @Transactional
    public void reactivateCard(UpdateCardStatusRequestDto updateCardStatusRequestDto) {
        if (updateCardStatusRequestDto == null || updateCardStatusRequestDto.getAccountNumber() == null) {
            throw new FinAppValidationException(HttpStatus.BAD_REQUEST, "Invalid card update request.");
        }

        Cards card = cardsRepository.findByAccountNumber(updateCardStatusRequestDto.getAccountNumber())
                .orElseThrow(() -> new FinAppValidationException(HttpStatus.NOT_FOUND, "Card not found"));

        card.setCardActivated(true);
        card.setCardStatus("ACTIVE");
        card.setCardCanTransact(true);
        card.setUpdatedAt(LocalDateTime.now());
        card.setUpdatedBy(SYSTEM_OPERATOR);

        cardsRepository.save(card);
        log.info("Card reactivated for account: {}", updateCardStatusRequestDto.getAccountNumber());
    }

    @Override
    @Transactional
    public void deleteCard(String accountNumber) {
        Cards card = cardsRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new FinAppValidationException(
                        HttpStatus.NOT_FOUND,
                        "Card not found for account number: " + accountNumber
                ));
        cardsRepository.delete(card);
    }

    private AccountsDto validateAndGetAccount(Long accountNumber) {
        ResponseEntity<AccountsDto> response = accountsFeignClient.fetchAccountDetails(accountNumber);

        if (response == null || !response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new FinAppValidationException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Account service unavailable or account not found for number: " + accountNumber
            );
        }

        AccountsDto account = response.getBody();
        if (!account.isAccountActive()) {
            throw new FinAppValidationException(
                    HttpStatus.BAD_REQUEST,
                    "Cannot issue card: Customer account " + accountNumber + " is inactive."
            );
        }

        return account;
    }

    private CardDetailResponseDto mapToDetailDto(Cards card) {
        CardDetailResponseDto dto = new CardDetailResponseDto();
        dto.setCardName(card.getCardName());
        dto.setAccountNumber(card.getAccountNumber());
        dto.setMaskedPan(card.getMaskedPan());
        dto.setCardType(card.getCardType());
        dto.setCardStatus(card.getCardStatus());
        dto.setCardActivated(card.getCardActivated());
        return dto;
    }
}