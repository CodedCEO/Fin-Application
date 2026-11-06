package com.finapp.card.client;

import com.finapp.card.dto.AccountsDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AccountsFeignClientFallback implements AccountsFeignClient {

    private static final Long FEE_ACCOUNT_NUMBER = 9988776655L;
    private final Map<Long, AccountsDto> accountsLedger = new ConcurrentHashMap<>();

    @Override
    public synchronized ResponseEntity<AccountsDto> fetchAccountDetails(Long accountNumber) {
        if (accountNumber == null) {
            return ResponseEntity.badRequest().build();
        }

        AccountsDto account = accountsLedger.computeIfAbsent(accountNumber, number -> {
            AccountsDto demo = new AccountsDto();
            demo.setAccountNumber(number);
            demo.setCustomerId(101L);
            demo.setCustomerName("DEMO CUSTOMER");
            demo.setAccountType("SAVINGS");
            demo.setAccountBalance(new BigDecimal("500000.00"));
            demo.setAccountActive(true);
            return demo;
        });

        return ResponseEntity.ok(copyAccount(account));
    }

    @Override
    public synchronized ResponseEntity<AccountsDto> fetchAccountByEmail(String email) {
        AccountsDto feeAccount = accountsLedger.computeIfAbsent(FEE_ACCOUNT_NUMBER, number -> {
            AccountsDto demo = new AccountsDto();
            demo.setAccountNumber(number);
            demo.setCustomerId(999L);
            demo.setCustomerName("DEMO FEE REVENUE ACCOUNT");
            demo.setAccountType("BUSINESS");
            demo.setAccountBalance(new BigDecimal("1000000.00"));
            demo.setAccountActive(true);
            return demo;
        });

        return ResponseEntity.ok(copyAccount(feeAccount));
    }

    @Override
    public synchronized ResponseEntity<Void> transfer(Long sourceAccount, Long destinationAccount, BigDecimal amount) {
        if (sourceAccount == null || destinationAccount == null || amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseEntity.badRequest().build();
        }

        fetchAccountDetails(sourceAccount);
        fetchAccountByEmail("cardfee@finapp.com");

        AccountsDto source = accountsLedger.get(sourceAccount);
        AccountsDto destination = accountsLedger.get(destinationAccount);

        if (source == null || destination == null || !source.isAccountActive() || !destination.isAccountActive()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        if (source.getAccountBalance().compareTo(amount) < 0) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        // Apply ledger balance update
        source.setAccountBalance(source.getAccountBalance().subtract(amount));
        destination.setAccountBalance(destination.getAccountBalance().add(amount));

        return ResponseEntity.ok().build();
    }

    private AccountsDto copyAccount(AccountsDto original) {
        AccountsDto copy = new AccountsDto();
        copy.setAccountNumber(original.getAccountNumber());
        copy.setCustomerId(original.getCustomerId());
        copy.setCustomerName(original.getCustomerName());
        copy.setAccountType(original.getAccountType());
        copy.setAccountBalance(original.getAccountBalance());
        copy.setAccountActive(original.isAccountActive());
        return copy;
    }
}