package com.finapp.card.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerDto {

    private String fullName;
    private String email;
    private String mobileNumber;
    private CardsDto cardsDto;

}
