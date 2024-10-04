package org.example.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenInfo {
    private String access_token;
    private String token_type;
    private long expires_in;
}
