package com.finapp.card.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class AccountsDto {
    private Long accountNumber;
    private Long customerId;
    private String customerName;
    private String accountType;
    private String branchAddress;
    private BigDecimal accountBalance;
    private boolean accountActive;
}