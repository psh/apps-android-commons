package fr.free.nrw.commons.mwapi.api;

import java.util.HashMap;
import java.util.Map;

public class RequestBuilder {

    private Map<String,String> params = new HashMap<>();

    private RequestBuilder() {}

    public static RequestBuilder action(String action) {
        return new RequestBuilder().param("format", "json").param("action", action);
    }

    public RequestBuilder param(String name, String value) {
        params.put(name, value);
        return this;
    }

    public Map<String,String> build() {
        return params;
    }
}
