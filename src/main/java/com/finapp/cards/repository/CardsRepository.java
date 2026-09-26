package com.finapp.cards.repository;

import com.finapp.account.entity.Account;
import com.finapp.cards.dto.CardsDto;
import com.finapp.cards.entity.Cards;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardsRepository extends JpaRepository<Cards, Long>
{
    CardsDto findByCardId(Long cardId);
    Cards findByCardNumber (String cardNumber);
    void createCards(String cardDto);
    boolean activateCard(CardsDto cardsDto);
    boolean deactivateCard(String CardNumber);
    //boolean updateCard(CardsDto cardsDto);
    boolean deleteCard(Long cardId);
}

