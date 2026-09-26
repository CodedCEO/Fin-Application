package com.finapp.cards.service;

import com.finapp.account.dto.CustomerDto;
import com.finapp.cards.dto.CardsDto;

public interface ICardsService {
    void createCards(String cardDto);
    CardsDto fetchCard(Long cardId);
    boolean activateCard(CardsDto cardsDto);
    boolean deactivateCard(String CardNumber);
    //boolean updateCard(CardsDto cardsDto);
    boolean deleteCard(Long cardId);
}
