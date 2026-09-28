package com.finapp.account.dto.request;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardDetailResponseDto {
    private String cardName;
    private String accountNumber;
    private String maskedPan;
    private boolean active;
    private String expireAt;
    private String cardType;
    private String requestMode;
    private String branch;
    private LocalDateTime createdAt;
    private String cardStatus;
}
