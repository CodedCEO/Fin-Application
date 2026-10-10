package com.finapp.card.service;

import com.finapp.card.dto.request.ActivateCardRequestDto;
import com.finapp.card.dto.request.CardDetailResponseDto;
import com.finapp.card.dto.request.UpdateCardStatusRequestDto;
import com.finapp.card.dto.request.RequestCardDto;
import com.finapp.card.dto.response.CardCreationResponseDto;
import com.finapp.card.dto.response.PaginatedResponseDto;

public interface ICardsService {
    CardCreationResponseDto createCard(RequestCardDto requestCardDto);
    void activateCard(ActivateCardRequestDto activateCardRequestDto);
    CardDetailResponseDto fetchSingleCard(String accountNumber);
    PaginatedResponseDto<CardDetailResponseDto> fetchAllCards(int page, int size);
    void deactivateCard(UpdateCardStatusRequestDto updateCardStatusRequestDto);
    void reactivateCard(UpdateCardStatusRequestDto updateCardStatusRequestDto);
    void deleteCard(String accountNumber);

}
