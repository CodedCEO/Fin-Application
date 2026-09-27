package com.finapp.account.controller;

import com.finapp.account.constants.CardsConstants;
import com.finapp.account.dto.request.ActivateCardRequestDto;
import com.finapp.account.dto.request.RequestCardDto;
import com.finapp.account.dto.response.CardCreationResponseDto;
import com.finapp.account.dto.response.FinappCustomResponseDto;
import com.finapp.account.enums.Status;
import com.finapp.account.service.ICardsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
                CardsConstants.MESSAGE_201,  // The success message
                cardCreationResponseDto,        // The actual card data payload
                Status.SUCCESS              // Assuming Status is an enum
        );

        // 3. Return the exact matching types with HTTP 201 Created status
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(responseBody);

    }

    @PostMapping("/activate")
    public ResponseEntity<FinappCustomResponseDto<Void>> activateCard(
            @Valid @RequestBody ActivateCardRequestDto activateCardRequestDto) {

        FinappCustomResponseDto<Void> responseBody = new FinappCustomResponseDto<>(
                CardsConstants.MESSAGE_301,
               null,
                Status.SUCCESS
        );

        // 3. Return the exact matching types with HTTP 201 Created status
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(responseBody);

    }

}
