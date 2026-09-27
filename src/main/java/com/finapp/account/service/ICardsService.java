package com.finapp.account.service;

import com.finapp.account.dto.request.ActivateCardRequestDto;
import com.finapp.account.dto.request.RequestCardDto;
import com.finapp.account.dto.response.CardCreationResponseDto;

public interface ICardsService {
    CardCreationResponseDto createCard(RequestCardDto requestCardDto);
    void activateCard(ActivateCardRequestDto activateCardRequestDto);
//    RequestCardDto fetchCard(Long cardId);
//    boolean deactivateCard(String CardNumber);
//    //boolean updateCard(CardsDto cardsDto);
//    boolean deleteCard(Long cardId);
}
