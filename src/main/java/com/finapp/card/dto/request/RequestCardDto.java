package com.finapp.card.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RequestCardDto {
    @NotBlank(message = "accountNumber is required")
    @Pattern(regexp = "\\d{10}", message = "accountNumber must be 10 digits")
    private String accountNumber;

    @NotBlank(message = "cardType is required")
    private String cardType;

    @NotNull(message = "requestMode is required")
    private String requestMode;

    private String bankBranch;
}
