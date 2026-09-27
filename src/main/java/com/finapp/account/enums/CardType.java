package com.finapp.account.enums;


import java.math.BigDecimal;

public enum CardType {
    VISA (BigDecimal.valueOf(1500), 16),
    VERVE (BigDecimal.valueOf(1000), 19),
    MASTERCARD(BigDecimal.valueOf(1200), 16);

    private final BigDecimal fee;
    private final int panLength;

    CardType(BigDecimal fee, int panLength) {
        this.fee =  fee;
        this.panLength = panLength;
    }

    public BigDecimal getFee(){
        return fee;
    }

    public int getPanLength(){
        return panLength;
    }

    public static CardType from(String value) {

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Card type is required");
        }

        try {
            return valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Unsupported card type: " + value);
        }
    }
}
