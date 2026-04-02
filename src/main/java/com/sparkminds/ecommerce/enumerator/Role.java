package com.sparkminds.ecommerce.enumerator;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Role {
    @JsonProperty("admin")
    ADMIN,
    @JsonProperty("user")
    USER;
}