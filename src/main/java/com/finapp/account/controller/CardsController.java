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
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cards")
public class CardsController {

    @Autowired
    private ICardsService iCardsService;

    @PostMapping("/create")
    public ResponseEntity<FinappCustomResponseDto<CardCreationResponseDto>> createAccount(
            @Valid @RequestBody RequestCardDto requestCardDto) {

        CardCreationResponseDto  cardCreationResponseDto = iCardsService.createCard(requestCardDto);

        FinappCustomResponseDto<CardCreationResponseDto> responseBody = new FinappCustomResponseDto<>(
                CardsConstants.MESSAGE_201,
                cardCreationResponseDto,
                Status.SUCCESS
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(responseBody);

    }

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
                .status(HttpStatus.ACCEPTED)
                .body(responseBody);

    }

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
                .status(HttpStatus.ACCEPTED)
                .body(responseBody);

    }

}
