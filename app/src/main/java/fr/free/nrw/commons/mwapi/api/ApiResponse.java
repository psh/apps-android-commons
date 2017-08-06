package fr.free.nrw.commons.mwapi.api;

import com.google.gson.annotations.SerializedName;

public class ApiResponse {
    @SerializedName("query")
    public QueryResponse query;
    @SerializedName("clientlogin")
    public LoginResponse clientlogin;
    @SerializedName("continue")
    public Continue cont;
    @SerializedName("edit")
    public EditResponse edit;

    @Override
    public String toString() {
        return "ApiResponse{" +
                "query=" + query +
                ", clientlogin=" + clientlogin +
                ", continue=" + cont +
                ", edit=" + edit +
                '}';
    }

    @SuppressWarnings("WeakerAccess")
    public class Continue {
        @SerializedName("iistart")
        public String iistart;
        @SerializedName("continue")
        public String continueIndicator;
    }

    @SuppressWarnings("WeakerAccess")
    public class EditResponse {
        @SerializedName("result")
        public String result;
    }
}
