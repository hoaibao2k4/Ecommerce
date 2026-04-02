package com.sparkminds.ecommerce.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataResponse<T> {
    private String message;
    private Integer status;
    private T data;

    // use for put patch post delete method
    public DataResponse(String message, Integer status) {
        this.message = message;
        this.status = status;
    }
}
