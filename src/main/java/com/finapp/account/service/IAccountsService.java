package com.finapp.account.service;

import com.finapp.account.dto.CustomerDto;

import java.math.BigDecimal;

public interface IAccountsService {
    Long createAccount(CustomerDto customerDto);

    CustomerDto fetchAccount(String mobileNumber);

    boolean updateAccount(CustomerDto customerDto);

    boolean deleteAccount(String mobileNumber);

    void transfer(Long sourceAccountNumber, Long destinationAccountNumber, BigDecimal amount);

}
