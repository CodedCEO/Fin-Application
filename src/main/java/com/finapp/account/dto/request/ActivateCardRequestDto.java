package com.finapp.account.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ActivateCardRequestDto {
    @NotNull(message = "accountNumber is required")
    @Pattern(regexp = "\\d{10}", message = "accountNumber must be 10 digits")
    private String accountNumber;

    @NotBlank(message = "defaultPin is required")
    @Pattern(regexp = "\\d{4}", message = "defaultPin must be 4 digits")
    private String defaultPin;

    @NotBlank(message = "newPin is required")
    @Pattern(regexp = "\\d{4}", message = "defaultPin must be 4 digits")
    private String newPin;

    @NotBlank(message = "cvv is required")
    @Pattern(regexp = "\\d{3}", message = "defaultPin must be 3 digits")
    private String cvv;

}
