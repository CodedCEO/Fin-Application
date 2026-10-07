package com.finapp.account.controller;

import com.finapp.account.constants.CardsConstants;
import com.finapp.account.dto.request.ActivateCardRequestDto;
import com.finapp.account.dto.request.CardDetailResponseDto;
import com.finapp.account.dto.request.UpdateCardStatusRequestDto;
import com.finapp.account.dto.request.RequestCardDto;
import com.finapp.account.dto.response.CardCreationResponseDto;
import com.finapp.account.dto.response.FinappCustomResponseDto;
import com.finapp.account.dto.response.PaginatedResponseDto;
import com.finapp.account.enums.Status;
import com.finapp.account.service.ICardsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "Card Management REST APIs in FinApp",
        description = "REST APIs in FinApp to CREATE, ACTIVATE, FETCH, UPDATE AND DELETE card details"
)
@RestController
@RequestMapping("/api/cards")
@Tag(
        name = "CRUD REST APIs for Cards in FinApp",
        description = "REST APIs in FinApp to CREATE, ACTIVATE, FETCH, DEACTIVATE, REACTIVATE AND DELETE card details"
)
public class CardsController {

    @Autowired
    private ICardsService iCardsService;

    @Operation(
            summary = "Create Card REST API",
<<<<<<< HEAD
            description = "REST API to create a new card for an existing account inside FinApp"
=======
            description = "REST API to create a new card inside FinApp"
>>>>>>> 231d2bc (Add OpenAPI Operation annotations to CardsController and upgrade Lombok to 1.18.36)
    )
    @PostMapping("/create")
    public ResponseEntity<FinappCustomResponseDto<CardCreationResponseDto>> createCard(
            @Valid @RequestBody RequestCardDto requestCardDto) {

        CardCreationResponseDto cardCreationResponseDto = iCardsService.createCard(requestCardDto);

        FinappCustomResponseDto<CardCreationResponseDto> responseBody = new FinappCustomResponseDto<>(
                CardsConstants.MESSAGE_201,
                cardCreationResponseDto,
                Status.SUCCESS
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(responseBody);
    }

    @Operation(
            summary = "Activate Card REST API",
<<<<<<< HEAD
            description = "REST API to activate a card using the default PIN and CVV, and set a new PIN"
=======
            description = "REST API to activate a card inside FinApp"
>>>>>>> 231d2bc (Add OpenAPI Operation annotations to CardsController and upgrade Lombok to 1.18.36)
    )
    @PostMapping("/activate")
    public ResponseEntity<FinappCustomResponseDto<Void>> activateCard(
            @Valid @RequestBody ActivateCardRequestDto activateCardRequestDto) {

        iCardsService.activateCard(activateCardRequestDto);

        FinappCustomResponseDto<Void> responseBody = new FinappCustomResponseDto<>(
                CardsConstants.MESSAGE_301,
                null,
                Status.SUCCESS
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(responseBody);
    }

    @Operation(
            summary = "Fetch Card Details REST API",
            description = "REST API to fetch card details based on an account number"
    )
    @GetMapping("/{accountNumber}")
    public ResponseEntity<FinappCustomResponseDto<CardDetailResponseDto>> getSingleCard(
            @PathVariable(name = "accountNumber") String accountNumber) {

        CardDetailResponseDto cardDetailResponseDto = iCardsService.fetchSingleCard(accountNumber);

        FinappCustomResponseDto<CardDetailResponseDto> responseBody = new FinappCustomResponseDto<>(
                CardsConstants.MESSAGE_202,
                cardDetailResponseDto,
                Status.SUCCESS
        );

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .body(responseBody);
    }

    @Operation(
            summary = "Fetch All Cards REST API",
<<<<<<< HEAD
            description = "REST API to fetch a paginated list of all cards"
=======
            description = "REST API to fetch paginated card details inside FinApp"
>>>>>>> 231d2bc (Add OpenAPI Operation annotations to CardsController and upgrade Lombok to 1.18.36)
    )
    @GetMapping
    public ResponseEntity<FinappCustomResponseDto<PaginatedResponseDto<CardDetailResponseDto>>> getAllCards(
            @RequestParam(name = "page", defaultValue = CardsConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(name = "size", defaultValue = CardsConstants.DEFAULT_PAGE_SIZE) int size) {

        PaginatedResponseDto<CardDetailResponseDto> cards = iCardsService.fetchAllCards(page, size);

        FinappCustomResponseDto<PaginatedResponseDto<CardDetailResponseDto>> responseBody = new FinappCustomResponseDto<>(
                CardsConstants.MESSAGE_203,
                cards,
                Status.SUCCESS
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(responseBody);
    }

    @Operation(
            summary = "Deactivate Card REST API",
<<<<<<< HEAD
            description = "REST API to deactivate an active card so it can no longer transact"
=======
            description = "REST API to deactivate an active card inside FinApp"
>>>>>>> 231d2bc (Add OpenAPI Operation annotations to CardsController and upgrade Lombok to 1.18.36)
    )
    @PatchMapping("/deactivate")
    public ResponseEntity<FinappCustomResponseDto<Void>> deactivateCard(
            @Valid @RequestBody UpdateCardStatusRequestDto updateCardStatusRequestDto) {

        iCardsService.deactivateCard(updateCardStatusRequestDto);

        FinappCustomResponseDto<Void> responseBody = new FinappCustomResponseDto<>(
                CardsConstants.MESSAGE_302,
                null,
                Status.SUCCESS
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(responseBody);
    }

    @Operation(
            summary = "Reactivate Card REST API",
<<<<<<< HEAD
            description = "REST API to reactivate a previously deactivated card"
=======
            description = "REST API to reactivate a card inside FinApp"
>>>>>>> 231d2bc (Add OpenAPI Operation annotations to CardsController and upgrade Lombok to 1.18.36)
    )
    @PatchMapping("/reactivate")
    public ResponseEntity<FinappCustomResponseDto<Void>> reactivateCard(
            @Valid @RequestBody UpdateCardStatusRequestDto updateCardStatusRequestDto) {

        iCardsService.reactivateCard(updateCardStatusRequestDto);

        FinappCustomResponseDto<Void> responseBody = new FinappCustomResponseDto<>(
                CardsConstants.MESSAGE_303,
                null,
                Status.SUCCESS
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(responseBody);
    }

    @Operation(
            summary = "Delete Card REST API",
<<<<<<< HEAD
            description = "REST API to delete a card based on an account number"
    )
    @DeleteMapping("/delete")
=======
            description = "REST API to delete card details based on an account number"
    )
    @DeleteMapping("/delete/{accountNumber}")
>>>>>>> 231d2bc (Add OpenAPI Operation annotations to CardsController and upgrade Lombok to 1.18.36)
    public ResponseEntity<FinappCustomResponseDto<Void>> deleteCard(
            @PathVariable(name = "accountNumber") String accountNumber) {

        iCardsService.deleteCard(accountNumber);

        FinappCustomResponseDto<Void> responseBody = new FinappCustomResponseDto<>(
                CardsConstants.MESSAGE_304,
                null,
                Status.SUCCESS
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(responseBody);
    }
}