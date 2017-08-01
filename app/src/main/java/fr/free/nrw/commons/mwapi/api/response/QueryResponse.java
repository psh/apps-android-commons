package fr.free.nrw.commons.mwapi.api.response;

import com.google.gson.annotations.SerializedName;

public class QueryResponse {
    public TokenResponse tokens;
    @SerializedName("userinfo")
    public UserInfoResponse userInfo;

    @Override
    public String toString() {
        return "QueryResponse{" +
                "tokens=" + tokens +
                ", userInfo=" + userInfo +
                '}';
    }

    @SuppressWarnings("WeakerAccess")
    public static class TokenResponse {
        @SerializedName("logintoken")
        public String loginToken;

        @Override
        public String toString() {
            return "TokenResponse{" +
                    "loginToken='" + loginToken + '\'' +
                    '}';
        }
    }

    @SuppressWarnings("WeakerAccess")
    public class UserInfoResponse {
        public String id;
        public String name;
    }
}
