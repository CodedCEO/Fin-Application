package com.finapp.account.repository;

import com.finapp.account.entity.Cards;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CardsRepository extends JpaRepository<Cards, Long> {
    // CardsDto findByCardId(Long cardId);
    Optional<Cards>  findByAccountNumber(String accountNumber);
   // void createCards(String cardDto);
   // boolean activateCard(CardsDto cardsDto);
   // boolean deactivateCard(String CardNumber);
   // boolean updateCard(CardsDto cardsDto);
  //  boolean deleteCard(Long cardId);
}

