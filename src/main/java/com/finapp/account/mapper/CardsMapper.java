package com.finapp.account.mapper;

import com.finapp.account.dto.AccountsDto;
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

}
