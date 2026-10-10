package com.finapp.card.controller;

import com.finapp.card.constants.CardsConstants;
import com.finapp.card.dto.request.ActivateCardRequestDto;
import com.finapp.card.dto.request.CardDetailResponseDto;
import com.finapp.card.dto.request.RequestCardDto;
import com.finapp.card.dto.request.UpdateCardStatusRequestDto;
import com.finapp.card.dto.response.CardCreationResponseDto;
import com.finapp.card.dto.response.FinappCustomResponseDto;
import com.finapp.card.dto.response.PaginatedResponseDto;
import com.finapp.card.enums.Status;
import com.finapp.card.service.ICardsService;
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
public class CardsController {

    @Autowired
    private ICardsService iCardsService;

    @Operation(
            summary = "Create Card REST API",
            description = "REST API to create a new card inside FinApp"
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
            description = "REST API to activate a card inside FinApp"
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
            description = "REST API to fetch paginated card details inside FinApp"
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
            description = "REST API to deactivate an active card inside FinApp"
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
            description = "REST API to reactivate a card inside FinApp"
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
            description = "REST API to delete card details based on an account number"
    )
    @DeleteMapping("/delete/{accountNumber}")
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