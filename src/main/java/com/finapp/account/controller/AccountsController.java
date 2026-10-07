package com.finapp.account.controller;


import com.finapp.account.constants.AccountsConstants;
import com.finapp.account.dto.CustomerDto;
import com.finapp.account.dto.ResponseDto;
import com.finapp.account.dto.response.FinappCustomResponseDto;
import com.finapp.account.enums.Status;
import com.finapp.account.service.impl.AccountsServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.finapp.account.service.IAccountsService;


@RestController
@RequestMapping("/api/accounts")
@Tag(
        name = "CRUD REST APIs for Accounts in FinApp",
        description = "CRUD REST APIs in FinApp to CREATE, UPDATE, FETCH AND DELETE account details"
)
public class AccountsController {

    @Autowired
    private AccountsServiceImpl accountsService;

    @Operation(
            summary = "Create Account REST API",
            description = "REST API to create new Customer &  Account inside FinApp"
    )
    @PostMapping("/create")
    public ResponseEntity<FinappCustomResponseDto<Long>> createAccount(@RequestBody CustomerDto customerDto) {
        Long accountNumber = accountsService.createAccount(customerDto);
        FinappCustomResponseDto<Long> accountResponse = new FinappCustomResponseDto<>(
                AccountsConstants.MESSAGE_201,
                accountNumber,
                Status.SUCCESS

        );
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(accountResponse);
    }

    @Operation(
            summary = "Fetch Account Details REST API",
            description = "REST API to fetch Customer &  Account details based on a mobile number"
    )
    @GetMapping("/api/fetch")
    public ResponseEntity<CustomerDto> fetchAccountDetails(@RequestParam String mobileNumber) {
        CustomerDto customerDto = accountsService.fetchAccount(mobileNumber);
        return ResponseEntity.status(HttpStatus.FOUND).body(customerDto);
    }

    @Operation(
            summary = "Update Account Details REST API",
            description = "REST API to update Customer &  Account details based on a account number"
    )
    @PutMapping("/api/update")
    public ResponseEntity<ResponseDto> updateAccountDetails(@RequestBody CustomerDto customerDto) {
        boolean isUpdated = accountsService.updateAccount(customerDto);
        if(isUpdated) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseDto( AccountsConstants.MESSAGE_200));
        }else{
            return ResponseEntity
                    .status(HttpStatus.EXPECTATION_FAILED)
                    .body(new ResponseDto(AccountsConstants.MESSAGE_417_UPDATE));
        }
    }

    @Operation(
            summary = "Delete Account & Customer Details REST API",
            description = "REST API to delete Customer &  Account details based on a mobile number"
    )
    @DeleteMapping("/api/delete")
    public ResponseEntity<ResponseDto> deleteAccountDetails(@RequestParam String mobileNumber) {
        boolean isDeleted = accountsService.deleteAccount(mobileNumber);
        if(isDeleted) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseDto(AccountsConstants.MESSAGE_200));
        }else{
            return ResponseEntity
                    .status(HttpStatus.EXPECTATION_FAILED)
                    .body(new ResponseDto(AccountsConstants.MESSAGE_417_DELETE));
        }
    }




}
