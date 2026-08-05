package com.eneik.generated.dtos;

public class SsoLoginResponseDto {
    private String accessToken;
    private String tokenType;
    private Integer expiresIn;
    private SsoUserDto user;

    public SsoLoginResponseDto() {}

    public SsoLoginResponseDto(String accessToken, String tokenType, Integer expiresIn, SsoUserDto user) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
        this.user = user;
    }

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }

    public Integer getExpiresIn() { return expiresIn; }
    public void setExpiresIn(Integer expiresIn) { this.expiresIn = expiresIn; }

    public SsoUserDto getUser() { return user; }
    public void setUser(SsoUserDto user) { this.user = user; }
}
