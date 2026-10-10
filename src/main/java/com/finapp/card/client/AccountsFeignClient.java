package com.finapp.card.client;

import com.finapp.card.dto.AccountsDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@FeignClient(
        name = "account",
        url = "${account.service.url:http://localhost:8080}",
        fallback = AccountsFeignClientFallback.class
)
public interface AccountsFeignClient {

    @GetMapping("/api/accounts/fetch")
    ResponseEntity<AccountsDto> fetchAccountDetails(@RequestParam("accountNumber") Long accountNumber);

    @GetMapping("/api/accounts/fee-account")
    ResponseEntity<AccountsDto> fetchAccountByEmail(@RequestParam("email") String email);

    @PostMapping("/api/accounts/transfer")
    ResponseEntity<Void> transfer(@RequestParam("sourceAccount") Long sourceAccount,
                                  @RequestParam("destinationAccount") Long destinationAccount,
                                  @RequestParam("amount") BigDecimal amount);
}