package com.finapp.account.mapper;

import com.finapp.account.dto.AccountsDto;
import com.finapp.account.dto.request.CardDetailResponseDto;
import com.finapp.account.entity.Account;
import com.finapp.account.dto.request.RequestCardDto;
import com.finapp.account.entity.Cards;

public class CardsMapper {

    public static Cards mapToCardCreation(RequestCardDto requestCardDto, Cards card) {

        card.setRequestMode(requestCardDto.getRequestMode().toUpperCase());
        card.setCardType(requestCardDto.getCardType().toUpperCase());
        card.setIssuingBranch(requestCardDto.getBankBranch());
        card.setAccountNumber(requestCardDto.getAccountNumber().trim());

        return card;
    }

    public static CardDetailResponseDto mapToCardDetailDto(Cards card) {

        return CardDetailResponseDto.builder()
                .cardType(card.getCardType())
                .cardStatus(card.getCardStatus())
                .accountNumber(card.getAccountNumber())
                .branch(card.getIssuingBranch())
                .cardName(card.getCardName())
                .maskedPan(card.getMaskedPan())
                .expireAt(card.getExpiration())
                .active(card.isCardActivated())
                .createdAt(card.getCreatedAt())
                .requestMode(card.getRequestMode())
                .build();
    }

}
