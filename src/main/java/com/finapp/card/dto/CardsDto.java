package com.finapp.card.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(name = "Cards", description = "Schema to hold Card details")
public class CardsDto {

    @NotEmpty(message = "Card reference cannot be null or empty")
    @Schema(description = "Unique card reference identifier", example = "REFNIT7U9ZO065S")
    private String reference;

    @NotEmpty(message = "Card name cannot be null or empty")
    @Schema(description = "Name embossed on the card", example = "GODWIN UMOH")
    private String cardName;

    @NotEmpty(message = "Account number cannot be null or empty")
    @Pattern(regexp = "(^$|[0-9]{10})", message = "Account number must be 10 digits")
    @Schema(description = "Account number linked to the card", example = "1000000001")
    private String accountNumber;

    @NotEmpty(message = "Card type cannot be null or empty")
    @Schema(description = "Card type/scheme", example = "MASTERCARD")
    private String cardType;

    @NotEmpty(message = "Masked PAN cannot be null or empty")
    @Schema(description = "Masked Primary Account Number", example = "0006-****-****-5939")
    private String maskedPan;

    @Schema(description = "Masked Card Verification Value", example = "***")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String maskedCvv;

    @Schema(description = "Masked PIN / Password", example = "****")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String maskedPin;

    @NotNull(message = "Total limit cannot be null")
    @PositiveOrZero(message = "Total limit must be zero or a positive amount")
    @Schema(description = "Total spending limit assigned to the card", example = "100000.00")
    private BigDecimal totalLimit;

    @NotEmpty(message = "Card expiration date cannot be null or empty")
    @Pattern(regexp = "(0[1-9]|1[0-2])/[0-9]{2}", message = "Expiration must follow MM/YY format")
    @Schema(description = "Card expiration date", example = "10/28")
    private String expiration;

    @Schema(description = "Indicates whether the card is activated", example = "true")
    private boolean cardActivated;

    @NotEmpty(message = "Card status cannot be null or empty")
    @Schema(description = "Current lifecycle status of the card", example = "ACTIVE")
    private String cardStatus;

    @NotEmpty(message = "Request mode cannot be null or empty")
    @Schema(description = "Request mode: PHYSICAL or VIRTUAL", example = "PHYSICAL")
    private String requestMode;

    @Schema(description = "Branch where the physical card was issued", example = "Victoria Island")
    private String issuingBranch;

    @Schema(description = "Flag indicating if the card can perform transactions", example = "true")
    private boolean cardCanTransact;
}