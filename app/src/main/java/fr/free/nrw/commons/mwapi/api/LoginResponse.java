package fr.free.nrw.commons.mwapi.api;

public class LoginResponse {
    public String status;
    public String message;
    public String messagecode;
    public String username;

    public String getStatusCodeToReturn() {
        if (status.equals("PASS")) {
            return status;
        } else if (status.equals("FAIL")) {
            return messagecode;
        }
        /* else if (
                status.equals("UI")
                        && loginApiResult.getString("/api/clientlogin/requests/_v/@id").equals("TOTPAuthenticationRequest")
                        && loginApiResult.getString("/api/clientlogin/requests/_v/@provider").equals("Two-factor authentication (OATH).")
                ) {
            return "2FA";
        }*/

        // UI, REDIRECT, RESTART
        return "genericerror-" + status;
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", messagecode='" + messagecode + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
