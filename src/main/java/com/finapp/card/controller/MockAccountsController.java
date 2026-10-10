package com.finapp.card.controller;

import com.finapp.card.dto.AccountsDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/accounts")
public class MockAccountsController {

    @GetMapping("/fetch")
    public ResponseEntity<AccountsDto> fetchAccountDetails(@RequestParam("accountNumber") Long accountNumber) {
        AccountsDto account = new AccountsDto();
        account.setAccountNumber(accountNumber);
        account.setCustomerId(1001L);
        account.setCustomerName("GODWIN UMOH");
        account.setAccountBalance(BigDecimal.valueOf(250000.00));
        account.setAccountActive(true);
        return ResponseEntity.ok(account);
    }

    @GetMapping("/fee-account")
    public ResponseEntity<AccountsDto> fetchAccountByEmail(@RequestParam("email") String email) {
        AccountsDto feeAccount = new AccountsDto();
        feeAccount.setAccountNumber(9988776655L);
        feeAccount.setCustomerName("FINAPP REVENUE");
        return ResponseEntity.ok(feeAccount);
    }

    @PostMapping("/transfer")
    public ResponseEntity<Void> transfer(@RequestParam("sourceAccount") Long sourceAccount,
                                         @RequestParam("destinationAccount") Long destinationAccount,
                                         @RequestParam("amount") BigDecimal amount) {
        return ResponseEntity.ok().build();
    }
}