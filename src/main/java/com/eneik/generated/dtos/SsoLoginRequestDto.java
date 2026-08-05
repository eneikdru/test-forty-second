package com.eneik.generated.dtos;

public class SsoLoginRequestDto {
    private String provider;
    private String token;

    public SsoLoginRequestDto() {}

    public SsoLoginRequestDto(String provider, String token) {
        this.provider = provider;
        this.token = token;
    }

    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}
