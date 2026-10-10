package com.finapp.card.client;

import com.finapp.card.dto.AccountsDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class AccountsFeignClientFallback implements AccountsFeignClient {

    @Override
    public ResponseEntity<AccountsDto> fetchAccountDetails(Long accountNumber) {
        AccountsDto mockAccount = new AccountsDto();
        mockAccount.setAccountNumber(accountNumber);
        mockAccount.setCustomerId(101L);
        mockAccount.setCustomerName("FINAPP CUSTOMER");
        mockAccount.setAccountBalance(BigDecimal.valueOf(500000.00));
        mockAccount.setAccountActive(true);
        return ResponseEntity.ok(mockAccount);
    }

    @Override
    public ResponseEntity<AccountsDto> fetchAccountByEmail(String email) {
        AccountsDto feeAccount = new AccountsDto();
        feeAccount.setAccountNumber(9988776655L);
        feeAccount.setCustomerName("FEE REVENUE ACCOUNT");
        feeAccount.setAccountBalance(BigDecimal.valueOf(1000000.00));
        feeAccount.setAccountActive(true);
        return ResponseEntity.ok(feeAccount);
    }

    @Override
    public ResponseEntity<Void> transfer(Long sourceAccount, Long destinationAccount, BigDecimal amount) {
        // Return 200 OK for fee deduction when Accounts service is unreachable
        return ResponseEntity.ok().build();
    }
}