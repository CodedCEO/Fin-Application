package com.finapp.account.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "Accounts")
@Getter @Setter
public class Account extends  BaseEntity{

    @Column(name = "customer_id")
    private Long customerId;

    @Id
    @Column(name = "account_number")
    private Long accountNumber;

    @Column(name = "account_type")
    private String accountType;

    @Column(name = "branch_address")
    private String branchAddress;

    @Column(name = "account_active")
    private boolean accountActive;

    @Column(name = "account_balance", nullable = false )
    private BigDecimal accountBalance;


}
