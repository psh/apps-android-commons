package fr.free.nrw.commons.mwapi.api.response;

import com.google.gson.annotations.SerializedName;

public class QueryResponse {
    public TokenResponse tokens;

    @Override
    public String toString() {
        return "QueryResponse{" +
                "tokens=" + tokens +
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
}
