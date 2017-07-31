package fr.free.nrw.commons.mwapi.api.response;

public class ApiResponse {
    public QueryResponse query;
    public LoginResponse clientlogin;

    @Override
    public String toString() {
        return "ApiResponse{" +
                "query=" + query +
                ", clientlogin=" + clientlogin +
                '}';
    }
}
