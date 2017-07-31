package fr.free.nrw.commons.mwapi.api.request;

abstract class ApiRequest {
    protected final String format = "json";
    protected final String action;

    ApiRequest(String action) {
        this.action = action;
    }
}
