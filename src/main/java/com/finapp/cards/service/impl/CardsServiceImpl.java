package com.finapp.cards.service.impl;

import com.finapp.cards.dto.CardsDto;
import com.finapp.cards.entity.Cards;
import com.finapp.cards.repository.CardsRepository;
import com.finapp.cards.service.ICardsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CardsServiceImpl implements ICardsService {
    @Autowired
    private CardsRepository cardsRepository;

    @Override
    public void createCards(String cardDto) {

    }

    @Override
    public CardsDto fetchCard(Long cardId) {
        CardsDto card = cardsRepository.findByCardId(cardId);
        if(card == null){
            return  null;
        }
        return card;
    }

    @Override
    public boolean activateCard(CardsDto cardsDto) {
        return false;
    }

    @Override
    public boolean deactivateCard(String CardNumber) {
        return false;
    }

    @Override
    public boolean deleteCard(Long cardId) {
        return false;
    }
}
