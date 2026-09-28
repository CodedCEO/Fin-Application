package com.finapp.account.service;

import com.finapp.account.dto.request.ActivateCardRequestDto;
import com.finapp.account.dto.request.CardDetailResponseDto;
import com.finapp.account.dto.request.UpdateCardStatusRequestDto;
import com.finapp.account.dto.request.RequestCardDto;
import com.finapp.account.dto.response.CardCreationResponseDto;
import com.finapp.account.dto.response.PaginatedResponseDto;

public interface ICardsService {
    CardCreationResponseDto createCard(RequestCardDto requestCardDto);
    void activateCard(ActivateCardRequestDto activateCardRequestDto);
    CardDetailResponseDto fetchSingleCard(String accountNumber);
    PaginatedResponseDto<CardDetailResponseDto> fetchAllCards(int page, int size);
    void deactivateCard(UpdateCardStatusRequestDto updateCardStatusRequestDto);
    void reactivateCard(UpdateCardStatusRequestDto updateCardStatusRequestDto);

//    //boolean updateCard(CardsDto cardsDto);
//    boolean deleteCard(Long cardId);
}
