package com.sparkminds.ecommerce.enumerator;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum OrderStatus {
    @JsonProperty("pending")
    PENDING,

    @JsonProperty("confirmed")
    CONFIRMED,

    @JsonProperty("shipped")
    SHIPPED,

    @JsonProperty("delivered")
    DELIVERED,

    @JsonProperty("cancelled")
    CANCELLED;

    public boolean canTransitionTo(OrderStatus nextStatus){
        switch (this) {
            case PENDING:
                return nextStatus == CONFIRMED || nextStatus == CANCELLED;
            case CONFIRMED:
                return nextStatus == SHIPPED || nextStatus == CANCELLED;
            case SHIPPED:
                return nextStatus == DELIVERED;
            case DELIVERED, CANCELLED:
                return false;
            default:
                return false;
        }
    }

}
