package com.finapp.card.mapper;

import com.finapp.card.dto.CardsDto;
import com.finapp.card.dto.request.CardDetailResponseDto;
import com.finapp.card.dto.request.RequestCardDto;
import com.finapp.card.entity.Cards;
import com.finapp.card.enums.Status;

import java.math.BigDecimal;

public class CardsMapper {

    public static Cards mapToCardCreation(RequestCardDto requestCardDto, Cards card) {
        card.setRequestMode(requestCardDto.getRequestMode().toUpperCase());
        card.setCardType(requestCardDto.getCardType().toUpperCase());
        card.setIssuingBranch(requestCardDto.getBankBranch());
        card.setAccountNumber(requestCardDto.getAccountNumber().trim());
        return card;
    }

    public static CardDetailResponseDto mapToCardDetailDto(Cards card) {
        boolean cardIsActive = Status.ACTIVE.name().equalsIgnoreCase(card.getCardStatus());

        return CardDetailResponseDto.builder()
                .cardType(card.getCardType())
                .cardStatus(card.getCardStatus())
                .accountNumber(card.getAccountNumber())
                .branch(card.getIssuingBranch())
                .cardName(card.getCardName())
                .maskedPan(card.getMaskedPan())
                .expireAt(card.getExpiration())
                .active(cardIsActive)
                .createdAt(card.getCreatedAt())
                .requestMode(card.getRequestMode())
                .canTransact(card.getCardCanTransact())
                .cardActivated(card.getCardActivated())
                .build();
    }

    public static CardsDto mapToCardsDto(Cards card, CardsDto cardsDto) {
        cardsDto.setReference(card.getReference());
        cardsDto.setCardName(card.getCardName());
        cardsDto.setAccountNumber(card.getAccountNumber());
        cardsDto.setCardType(card.getCardType());
        cardsDto.setMaskedPan(card.getMaskedPan());

        // Static masking for sensitive secrets
        cardsDto.setMaskedCvv("***");
        cardsDto.setMaskedPin("****");

        cardsDto.setTotalLimit(card.getTotalLimit() != null ? BigDecimal.valueOf(card.getTotalLimit()) : BigDecimal.ZERO);
        cardsDto.setExpiration(card.getExpiration());
        cardsDto.setCardActivated(card.getCardActivated());
        cardsDto.setCardStatus(card.getCardStatus());
        cardsDto.setRequestMode(card.getRequestMode());
        cardsDto.setIssuingBranch(card.getIssuingBranch());
        cardsDto.setCardCanTransact(card.getCardCanTransact());

        return cardsDto;
    }

    public static Cards mapToCards(CardsDto cardsDto, Cards card) {
        card.setReference(cardsDto.getReference());
        card.setCardName(cardsDto.getCardName());
        card.setAccountNumber(cardsDto.getAccountNumber());
        card.setCardType(cardsDto.getCardType());

        // Safe conversion from BigDecimal to Long
        card.setTotalLimit(cardsDto.getTotalLimit() != null ? cardsDto.getTotalLimit().longValue() : 0L);

        card.setExpiration(cardsDto.getExpiration());
        card.setCardActivated(cardsDto.isCardActivated());
        card.setCardStatus(cardsDto.getCardStatus());
        card.setRequestMode(cardsDto.getRequestMode());
        card.setIssuingBranch(cardsDto.getIssuingBranch());
        card.setCardCanTransact(cardsDto.isCardCanTransact());
        return card;
    }
}