package fr.free.nrw.commons.mwapi.api;

import fr.free.nrw.commons.mwapi.api.request.ClientLoginRequest;
import fr.free.nrw.commons.mwapi.api.request.LoginTokenQuery;
import fr.free.nrw.commons.mwapi.api.response.ApiResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiService {
    @FormUrlEncoded
    @POST("/w/api.php")
    Call<ApiResponse> loginToken(@Body LoginTokenQuery query);

    @FormUrlEncoded
    @POST("/w/api.php")
    Call<ApiResponse> login(@Body ClientLoginRequest loginRequest);
}
