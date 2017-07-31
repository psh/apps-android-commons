package fr.free.nrw.commons.mwapi.api.response;

public class LoginResponse {
    public String status;
    public String username;

    @Override
    public String toString() {
        return "LoginResponse{" +
                "status='" + status + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
