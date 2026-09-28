package com.finapp.account.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cards")
@Getter
@Setter
public class Cards extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String reference;

    @Column(name = "card_name", nullable = false)
    private String cardName;

    @Column(name = "account_number", nullable = false, unique = true)
    private String accountNumber;

    @Column(name = "pan_hash", nullable = false)
    private String panHash;

    @Column(name = "masked_pan", nullable = false)
    private String maskedPan;

    @Column(name = "card_type", nullable = false)
    private String cardType;

    @Column(name = "pan_length")
    private int panLength;

    @Column(name = "total_limit", nullable = false)
    private BigDecimal totalLimit;

    @Column(name = "card_fee", nullable = false)
    private BigDecimal cardFee;

    @Column(nullable = false)
    private String encryptedCvv;

    @Column(name = "card_expiration", nullable = false)
    private String expiration;

    @Column(name = "is_card_activated", nullable = false)
    private boolean cardActivated;

    @Column(name = "card_activated_at")
    private LocalDateTime cardActivatedAt;

    @Column(name = "card_status", nullable = false)
    private String cardStatus;

    @Column(name = "encrypted_pin")
    private String encryptedPin;

    @Column(name = "issuing_branch")
    private String issuingBranch;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(name = "request_mode", nullable = false)
    private String requestMode;

    @Column(name = "card_fee_account")
    private String cardFeeAccount;

}
