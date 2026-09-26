package com.finapp.account.mapper;


import com.finapp.account.dto.AccountsDto;
import com.finapp.account.entity.Account;

public class AccountsMapper {

    public static AccountsDto mapToAccountDto(Account account, AccountsDto accountDto) {
        accountDto.setAccountNumber(account.getAccountNumber());
        accountDto.setAccountType(account.getAccountType());
        accountDto.setBranchAddress(account.getBranchAddress());
        return accountDto;
    }

    public static Account mapToAccount(AccountsDto accountDto, Account account) {
        //account.setAccountNumber(accountDto.getAccountNumber());
        account.setAccountType(accountDto.getAccountType());
        account.setBranchAddress(accountDto.getBranchAddress());
        return account;
    }

}
