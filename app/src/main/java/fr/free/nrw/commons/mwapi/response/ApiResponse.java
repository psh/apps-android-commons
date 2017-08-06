package fr.free.nrw.commons.mwapi.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Map;

public class ApiResponse {
    @SerializedName("query")
    public QueryResponse query;
    @SerializedName("clientlogin")
    public LoginResponse clientlogin;
    @SerializedName("continue")
    public Continue cont;
    @SerializedName("edit")
    public EditResponse edit;
    @SerializedName("parse")
    public ParseResponse parse;

    public String parsedContent() {
        return parse != null ? parse.parsedContent() : "";
    }

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

    @SuppressWarnings("WeakerAccess")
    public class ParseResponse {
        @SerializedName("title")
        public String title;
        @SerializedName("pageid")
        public String pageId;
        @SerializedName("parsetree")
        public Map<String, String> parseTree;

        public String parsedContent() {
            return parseTree != null && parseTree.size() > 0 ? new ArrayList<>(parseTree.values()).get(0) : "";
        }
    }
}
