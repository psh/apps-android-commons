package fr.free.nrw.commons.mwapi.api.request;

import com.google.gson.annotations.SerializedName;

public final class ClientLoginRequest extends ApiRequest {
    @SerializedName("loginreturnurl")
    private final String loginReturnUrl = "https://commons.wikimedia.org";
    @SerializedName("rememberMe")
    private final String rememberMe = "1";
    @SerializedName("logintoken")
    private final String loginToken;
    private final String username;
    private final String password;
    public ClientLoginRequest(String loginToken, String username, String password) {
        super("clientlogin");
        this.loginToken = loginToken;
        this.username = username;
        this.password = password;
    }
}
