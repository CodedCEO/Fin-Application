package com.finapp.account.repository;


import com.finapp.account.entity.Account;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;


@Repository
public interface AccountsRepository extends JpaRepository<Account, Long> {
    //select * from accounts where customer_id = 12345
    Account findByCustomerId(Long customerId);
    //select * from accounts where account_number = 12345
    Account findByAccountNumber(Long accountNumber);
    //delete from accounts where customer_id = 12345
    @Transactional
    void deleteByCustomerId(Long customerId);
}

