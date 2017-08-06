package fr.free.nrw.commons.mwapi.request;

import java.util.HashMap;
import java.util.Map;

abstract class AbstractRequestBuilder implements RequestBuilder.ActionRequest, RequestBuilder.ParameterBuilder {
    protected Map<String, String> params = new HashMap<>();

    @Override
    public RequestBuilder.ParameterBuilder param(String name, String value) {
        params.put(name, value);
        return this;
    }

    @Override
    public RequestBuilder.ParameterBuilder param(String name, int value) {
        params.put(name, "" + value);
        return this;
    }

    @Override
    public RequestBuilder.ParameterBuilder action(String action) {
        params.put("format", "json");
        params.put("action", action);
        return this;
    }
}
