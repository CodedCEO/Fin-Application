package com.finapp.account.service.impl;


import com.finapp.account.dto.AccountsDto;
import com.finapp.account.dto.CustomerDto;
import com.finapp.account.entity.Account;
import com.finapp.account.entity.Customer;
import com.finapp.account.exception.FinAppValidationException;
import com.finapp.account.mapper.AccountsMapper;
import com.finapp.account.mapper.CustomerMapper;
import com.finapp.account.repository.AccountsRepository;
import com.finapp.account.repository.CustomerRepository;
import com.finapp.account.service.IAccountsService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Random;

@Service
public class AccountsServiceImpl  implements IAccountsService {

    @Autowired
    private AccountsRepository accountsRepository;

    @Autowired
    private CustomerRepository customerRepository;



    /**
     * @param customerDto - CustomerDto Object
     */
    @Override
    public Long createAccount(CustomerDto customerDto) {
        Customer customer = CustomerMapper.mapToCustomer(customerDto, new Customer());
        Account account = AccountsMapper.mapToAccount(customerDto.getAccountsDto(), new Account());
        Customer savedCustomer = customerRepository.save(customer);
        account.setCustomerId(savedCustomer.getCustomerId());
        Account newAccount = generateAccountId(account);
        accountsRepository.save(newAccount);
        return newAccount.getAccountNumber();

    }

    /**
     * @param account - Customer Object
     * @return the new account details
     */
    private Account generateAccountId(Account account) {
        long randomAccNumber = 1000000000 + new Random().nextInt(900000000);
        account.setAccountNumber(randomAccNumber);
        return account;
    }

    /**
     * @param mobileNumber - Input Mobile Number
     * @return Accounts Details based on a given mobileNumber
     */
    @Override
    public CustomerDto fetchAccount(String mobileNumber) {
        Customer customer = customerRepository.findByMobileNumber(mobileNumber);
        Account account = accountsRepository.findByCustomerId(customer.getCustomerId());

        CustomerDto customerDto = new CustomerDto();
        customerDto.setFullName(customer.getFullName());
        customerDto.setEmail(customer.getEmail());
        customerDto.setMobileNumber(customer.getMobileNumber());

        AccountsDto accountsDto = new AccountsDto();
        accountsDto.setAccountType(account.getAccountType());
        accountsDto.setAccountNumber(account.getAccountNumber());
        accountsDto.setBranchAddress(account.getBranchAddress());

        customerDto.setAccountsDto(accountsDto);
        return customerDto;
    }

    /**
     * @param customerDto - CustomerDto Object
     * @return boolean indicating if the update of Account details is successful or not
     */
    @Override
    public boolean updateAccount(CustomerDto customerDto) {
        boolean isUpdated = false;
        AccountsDto accountDto = customerDto.getAccountsDto();

        if(accountDto == null ) {
            throw new FinAppValidationException(HttpStatus.BAD_REQUEST, "Request body cannot be null or empty");
        }

        Account account = accountsRepository.findByAccountNumber(accountDto.getAccountNumber())
                    .orElseThrow(() -> new FinAppValidationException(HttpStatus.NOT_FOUND, "Account not found"));

        if(account !=null) {

            account.setAccountNumber(accountDto.getAccountNumber());
            account.setAccountType(accountDto.getAccountType());
            account.setBranchAddress(accountDto.getBranchAddress());
            account = accountsRepository.save(account);

            Long customerId = account.getCustomerId();
            Customer customer = customerRepository.findByCustomerId(customerId);
            if (customer != null) {
                customer.setFullName(customerDto.getFullName());
                customer.setEmail(customerDto.getEmail());
                customer.setMobileNumber(customerDto.getMobileNumber());
                customerRepository.save(customer);
            }
            isUpdated = true;
        }

        return  isUpdated;
    }

    /**
     * @param mobileNumber - Input Mobile Number
     * @return boolean indicating if the delete of Account details is successful or not
     */
    @Override
    public boolean deleteAccount(String mobileNumber) {
        Customer customer = customerRepository.findByMobileNumber(mobileNumber);
        if(customer !=null){
            Account account = accountsRepository.findByCustomerId(customer.getCustomerId());
            //Optional
        //  accountsRepository.deleteByCustomerId(customer.getCustomerId());
        //  customerRepository.deleteById(customer.getCustomerId());
            customerRepository.delete(customer);
            accountsRepository.delete(account);
            return true;

        }
        return false;
    }

    @Transactional
    private void debitAndCredit(Long sourceAccountNumber, Long destinationAccountNumber, BigDecimal amount) {

        Account sourceAccount = accountsRepository
                .findByAccountNumber(sourceAccountNumber)
                .orElseThrow(() -> new RuntimeException("Source account not found"));

        Account destinationAccount = accountsRepository
                .findByAccountNumber(destinationAccountNumber)
                .orElseThrow(() ->
                        new RuntimeException("Destination account not found"));

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        if (sourceAccount.getAccountBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient funds");
        }

        sourceAccount.setAccountBalance(sourceAccount.getAccountBalance().subtract(amount));

        destinationAccount.setAccountBalance(destinationAccount.getAccountBalance().add(amount)
        );

        accountsRepository.save(sourceAccount);
        accountsRepository.save(destinationAccount);
    }

    @Transactional
    public void credit(Long accountNumber, BigDecimal amount) {

        Account account = accountsRepository
                .findByAccountNumber(accountNumber)
                .orElseThrow(() ->
                        new RuntimeException("Account not found"));

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        account.setAccountBalance(account.getAccountBalance().add(amount));

        accountsRepository.save(account);
    }

    @Override
    @Transactional
    public void transfer(Long sourceAccountNumber, Long destinationAccountNumber, BigDecimal amount) {

        debitAndCredit(sourceAccountNumber, destinationAccountNumber, amount);
    }


}
