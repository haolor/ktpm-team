package com.e_commerce.dto.auth.accountDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthenticationDTO {
    private String accountName;
    private String accessToken;
    private String refreshToken;
    private String role;
}
