
package com.finapp.card.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cards extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reference")
    private String reference;

    @Column(name = "card_name")
    private String cardName;

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @Column(name = "pan_hash")
    private String panHash;

    @Column(name = "masked_pan")
    private String maskedPan;

    @Column(name = "card_type")
    private String cardType;

    @Column(name = "pan_length")
    private Integer panLength;

    @Column(name = "total_limit")
    private Long totalLimit;

    @Column(name = "card_fee")
    private Long cardFee;

    @Column(name = "encrypted_cvv")
    private String encryptedCvv;

    @Column(name = "expiration")
    private String expiration;

    @Column(name = "card_activated")
    private Boolean cardActivated;

    @Column(name = "card_activated_at")
    private LocalDateTime cardActivatedAt;

    @Column(name = "card_status")
    private String cardStatus;

    @Column(name = "encrypted_pin")
    private String encryptedPin;

    @Column(name = "issuing_branch")
    private String issuingBranch;

    @Column(name = "request_mode")
    private String requestMode;

    @Column(name = "card_fee_account")
    private String cardFeeAccount;

    @Column(name = "card_can_transact")
    private Boolean cardCanTransact;

    public boolean isCardCanTransact() {

        return false;
    }
}
