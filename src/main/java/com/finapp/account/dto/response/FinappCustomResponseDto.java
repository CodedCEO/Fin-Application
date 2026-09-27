package com.finapp.account.dto.response;

import com.finapp.account.enums.Status;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FinappCustomResponseDto<T> {
    private String message;
    private T data;
    private Status status;

    public FinappCustomResponseDto(String message, T data, Status status) {
        this.message = message;
        this.data = data;
        this.status = status;
    }
}
