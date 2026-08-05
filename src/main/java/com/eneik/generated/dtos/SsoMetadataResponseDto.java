package com.eneik.generated.dtos;

import java.util.List;

public class SsoMetadataResponseDto {
    private String issuer;
    private String authorizationEndpoint;
    private String tokenEndpoint;
    private List<String> activeProviders;

    public SsoMetadataResponseDto() {}

    public SsoMetadataResponseDto(String issuer, String authorizationEndpoint, String tokenEndpoint, List<String> activeProviders) {
        this.issuer = issuer;
        this.authorizationEndpoint = authorizationEndpoint;
        this.tokenEndpoint = tokenEndpoint;
        this.activeProviders = activeProviders;
    }

    public String getIssuer() { return issuer; }
    public void setIssuer(String issuer) { this.issuer = issuer; }

    public String getAuthorizationEndpoint() { return authorizationEndpoint; }
    public void setAuthorizationEndpoint(String authorizationEndpoint) { this.authorizationEndpoint = authorizationEndpoint; }

    public String getTokenEndpoint() { return tokenEndpoint; }
    public void setTokenEndpoint(String tokenEndpoint) { this.tokenEndpoint = tokenEndpoint; }

    public List<String> getActiveProviders() { return activeProviders; }
    public void setActiveProviders(List<String> activeProviders) { this.activeProviders = activeProviders; }
}
