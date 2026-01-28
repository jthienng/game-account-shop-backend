package com.sas.demo.domain.product.dto.response;

import aQute.bnd.annotation.metatype.Meta;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class UserResponse {
    private UUID id;

    private String username;

    private String mail;

    private String role;

    private Boolean enabled;

    @JsonProperty("last_login")
    private LocalDateTime lastLogin;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;


}
