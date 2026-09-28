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
    public ResponseEntity<FinappCustomResponseDto<CardCreationResponseDto>> createCard(
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
                .status(HttpStatus.OK)
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
                .status(HttpStatus.OK)
                .body(responseBody);

    }

    @DeleteMapping("/delete")
    public ResponseEntity<FinappCustomResponseDto<Void>> deleteCard(
            @Valid @PathVariable(name = "accountNumber") String accountNumber) {

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

//package com.finapp.account.controller;
//
//import com.finapp.account.constants.CardsConstants;
//import com.finapp.account.dto.request.ActivateCardRequestDto;
//import com.finapp.account.dto.request.CardDetailResponseDto;
//import com.finapp.account.dto.request.UpdateCardStatusRequestDto;
//import com.finapp.account.dto.request.RequestCardDto;
//import com.finapp.account.dto.response.CardCreationResponseDto;
//import com.finapp.account.dto.response.FinappCustomResponseDto;
//import com.finapp.account.dto.response.PaginatedResponseDto;
//import com.finapp.account.enums.Status;
//import com.finapp.account.service.ICardsService;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.Parameter;
//import io.swagger.v3.oas.annotations.media.Content;
//import io.swagger.v3.oas.annotations.media.Schema;
//import io.swagger.v3.oas.annotations.responses.ApiResponse;
//import io.swagger.v3.oas.annotations.responses.ApiResponses;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import jakarta.validation.Valid;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/cards")
//@Tag(
//        name = "Card Management",
//        description = "APIs for creating, activating, retrieving, deactivating and reactivating cards"
//)
//public class CardsController {
//
//    @Autowired
//    private ICardsService iCardsService;
//
//    @Operation(
//            summary = "Create a new card",
//            description = "Creates a new card using the account and card details provided in the request."
//    )
//    @ApiResponses({
//            @ApiResponse(
//                    responseCode = "201",
//                    description = "Card created successfully"
//            ),
//            @ApiResponse(
//                    responseCode = "400",
//                    description = "Invalid card request",
//                    content = @Content
//            ),
//            @ApiResponse(
//                    responseCode = "404",
//                    description = "Account not found",
//                    content = @Content
//            ),
//            @ApiResponse(
//                    responseCode = "500",
//                    description = "Internal server error",
//                    content = @Content
//            )
//    })
//    @PostMapping("/create")
//    public ResponseEntity<FinappCustomResponseDto<CardCreationResponseDto>> createAccount(
//            @Valid @RequestBody RequestCardDto requestCardDto) {
//
//        CardCreationResponseDto cardCreationResponseDto =
//                iCardsService.createCard(requestCardDto);
//
//        FinappCustomResponseDto<CardCreationResponseDto> responseBody =
//                new FinappCustomResponseDto<>(
//                        CardsConstants.MESSAGE_201,
//                        cardCreationResponseDto,
//                        Status.SUCCESS
//                );
//
//        return ResponseEntity
//                .status(HttpStatus.CREATED)
//                .body(responseBody);
//    }
//
//
//    @Operation(
//            summary = "Activate a card",
//            description = "Activates a card using the account and card information provided."
//    )
//    @ApiResponses({
//            @ApiResponse(
//                    responseCode = "201",
//                    description = "Card activated successfully"
//            ),
//            @ApiResponse(
//                    responseCode = "400",
//                    description = "Invalid activation request",
//                    content = @Content
//            ),
//            @ApiResponse(
//                    responseCode = "404",
//                    description = "Card or account not found",
//                    content = @Content
//            ),
//            @ApiResponse(
//                    responseCode = "409",
//                    description = "Card cannot be activated in its current state",
//                    content = @Content
//            )
//    })
//    @PostMapping("/activate")
//    public ResponseEntity<FinappCustomResponseDto<Void>> activateCard(
//            @Valid @RequestBody ActivateCardRequestDto activateCardRequestDto) {
//
//        iCardsService.activateCard(activateCardRequestDto);
//
//        FinappCustomResponseDto<Void> responseBody =
//                new FinappCustomResponseDto<>(
//                        CardsConstants.MESSAGE_301,
//                        null,
//                        Status.SUCCESS
//                );
//
//        return ResponseEntity
//                .status(HttpStatus.CREATED)
//                .body(responseBody);
//    }
//
//
//    @Operation(
//            summary = "Get card details",
//            description = "Retrieves the details of a card using the associated account number."
//    )
//    @ApiResponses({
//            @ApiResponse(
//                    responseCode = "302",
//                    description = "Card details retrieved successfully"
//            ),
//            @ApiResponse(
//                    responseCode = "404",
//                    description = "Card not found",
//                    content = @Content
//            ),
//            @ApiResponse(
//                    responseCode = "400",
//                    description = "Invalid account number",
//                    content = @Content
//            )
//    })
//    @GetMapping("/{accountNumber}")
//    public ResponseEntity<FinappCustomResponseDto<CardDetailResponseDto>> getSingleCard(
//            @Parameter(
//                    description = "Customer account number associated with the card",
//                    example = "12345678901",
//                    required = true
//            )
//            @PathVariable(name = "accountNumber") String accountNumber) {
//
//        CardDetailResponseDto cardDetailResponseDto =
//                iCardsService.fetchSingleCard(accountNumber);
//
//        FinappCustomResponseDto<CardDetailResponseDto> responseBody =
//                new FinappCustomResponseDto<>(
//                        CardsConstants.MESSAGE_202,
//                        cardDetailResponseDto,
//                        Status.SUCCESS
//                );
//
//        return ResponseEntity
//                .status(HttpStatus.FOUND)
//                .body(responseBody);
//    }
//
//
//    @Operation(
//            summary = "Get all cards",
//            description = "Retrieves a paginated list of all cards."
//    )
//    @ApiResponses({
//            @ApiResponse(
//                    responseCode = "200",
//                    description = "Cards retrieved successfully"
//            ),
//            @ApiResponse(
//                    responseCode = "400",
//                    description = "Invalid pagination parameters",
//                    content = @Content
//            )
//    })
//    @GetMapping
//    public ResponseEntity<
//            FinappCustomResponseDto<
//                    PaginatedResponseDto<CardDetailResponseDto>>> getAllCards(
//
//            @Parameter(
//                    description = "Page number (zero-based)",
//                    example = "0"
//            )
//            @RequestParam(
//                    name = "page",
//                    defaultValue = CardsConstants.DEFAULT_PAGE_NUMBER
//            ) int page,
//
//            @Parameter(
//                    description = "Number of cards to return per page",
//                    example = "10"
//            )
//            @RequestParam(
//                    name = "size",
//                    defaultValue = CardsConstants.DEFAULT_PAGE_SIZE
//            ) int size) {
//
//        PaginatedResponseDto<CardDetailResponseDto> cards =
//                iCardsService.fetchAllCards(page, size);
//
//        FinappCustomResponseDto<PaginatedResponseDto<CardDetailResponseDto>> responseBody =
//                new FinappCustomResponseDto<>(
//                        CardsConstants.MESSAGE_203,
//                        cards,
//                        Status.SUCCESS
//                );
//
//        return ResponseEntity
//                .status(HttpStatus.OK)
//                .body(responseBody);
//    }
//
//
//    @Operation(
//            summary = "Deactivate a card",
//            description = "Deactivates an active card and prevents it from being used."
//    )
//    @ApiResponses({
//            @ApiResponse(
//                    responseCode = "202",
//                    description = "Card deactivation request accepted"
//            ),
//            @ApiResponse(
//                    responseCode = "400",
//                    description = "Invalid request",
//                    content = @Content
//            ),
//            @ApiResponse(
//                    responseCode = "404",
//                    description = "Card not found",
//                    content = @Content
//            ),
//            @ApiResponse(
//                    responseCode = "409",
//                    description = "Card cannot be deactivated in its current state",
//                    content = @Content
//            )
//    })
//    @PatchMapping("/deactivate")
//    public ResponseEntity<FinappCustomResponseDto<Void>> deactivateCard(
//            @Valid @RequestBody UpdateCardStatusRequestDto updateCardStatusRequestDto) {
//
//        iCardsService.deactivateCard(updateCardStatusRequestDto);
//
//        FinappCustomResponseDto<Void> responseBody =
//                new FinappCustomResponseDto<>(
//                        CardsConstants.MESSAGE_302,
//                        null,
//                        Status.SUCCESS
//                );
//
//        return ResponseEntity
//                .status(HttpStatus.ACCEPTED)
//                .body(responseBody);
//    }
//
//
//    @Operation(
//            summary = "Reactivate a card",
//            description = "Reactivates a previously deactivated card."
//    )
//    @ApiResponses({
//            @ApiResponse(
//                    responseCode = "202",
//                    description = "Card reactivation request accepted"
//            ),
//            @ApiResponse(
//                    responseCode = "400",
//                    description = "Invalid request",
//                    content = @Content
//            ),
//            @ApiResponse(
//                    responseCode = "404",
//                    description = "Card not found",
//                    content = @Content
//            ),
//            @ApiResponse(
//                    responseCode = "409",
//                    description = "Card cannot be reactivated in its current state",
//                    content = @Content
//            )
//    })
//    @PatchMapping("/reactivate")
//    public ResponseEntity<FinappCustomResponseDto<Void>> reactivateCard(
//            @Valid @RequestBody UpdateCardStatusRequestDto updateCardStatusRequestDto) {
//
//        iCardsService.reactivateCard(updateCardStatusRequestDto);
//
//        FinappCustomResponseDto<Void> responseBody =
//                new FinappCustomResponseDto<>(
//                        CardsConstants.MESSAGE_303,
//                        null,
//                        Status.SUCCESS
//                );
//
//        return ResponseEntity
//                .status(HttpStatus.ACCEPTED)
//                .body(responseBody);
//    }

