package com.finapp.card.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCardStatusRequestDto {
    @NotBlank(message = "accountNumber is required")
    @Pattern(regexp = "\\d{10}", message = "accountNumber must be 10 digits")
    private  String accountNumber;

    @NotBlank(message = "currentPin is required")
    @Pattern(regexp = "\\d{4}", message = "currentPin must be 4 digits")
    private String currentPin;
}
