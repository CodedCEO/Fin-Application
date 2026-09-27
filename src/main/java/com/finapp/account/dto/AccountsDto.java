package com.finapp.account.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountsDto {

    private Long accountNumber;
    private String accountType;
    private String branchAddress;
    private BigDecimal amount;
}
