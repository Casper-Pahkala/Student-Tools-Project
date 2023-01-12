package com.example.kalenteri;

public class TokenStatusModel {
    String onlineStatus,token;

    public TokenStatusModel(String token, String onlineStatus) {
        this.onlineStatus = onlineStatus;
        this.token = token;
    }

    public String getOnlineStatus() {
        return onlineStatus;
    }

    public String getToken() {
        return token;
    }

    public TokenStatusModel() {
    }
}
