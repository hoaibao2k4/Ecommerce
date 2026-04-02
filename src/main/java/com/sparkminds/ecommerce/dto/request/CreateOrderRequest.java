package com.sparkminds.ecommerce.dto.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {
    @Valid
    @NotEmpty(message = "List items are required")
    private List<CreateItemsRequest> items;
    
    private String note;
}
