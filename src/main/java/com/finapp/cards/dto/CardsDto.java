package com.finapp.cards.dto;
import lombok.*;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class CardsDto {
    private Long cardId;
    private String CardNumber;
    private String CardType;
    private String CardStatus;
    private String expiryDate;
    private String customerId;

}
