package com.finapp.account.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CardCreationResponseDto {
    private String cardReference;
    private String cardName;
    private String pan;
    private String cvv;
    private String expiration;
    private String defaultPin;
    private boolean cardActivated;
    private BigDecimal accontBalance;
    private LocalDate createdAt;

}
