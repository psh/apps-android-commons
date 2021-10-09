package fr.free.nrw.commons.wikidata

import fr.free.nrw.commons.wikidata.model.WbCreateClaimResponse
import io.reactivex.Observable
import org.wikipedia.dataclient.Service
import org.wikipedia.dataclient.mwapi.MwQueryResponse
import retrofit2.http.*

interface WikidataInterface {
    /**
     * Get edit token for wikidata wiki site
     */
    @GET(Service.MW_API_PREFIX + "action=query&meta=tokens&type=csrf")
    @Headers("Cache-Control: no-cache")
    fun getCsrfToken(): Observable<MwQueryResponse>

    /**
     * Wikidata create claim API. Posts a new claim for the given entity ID
     */
    @Headers("Cache-Control: no-cache")
    @POST("w/api.php?format=json&action=wbsetclaim")
    @FormUrlEncoded
    fun postSetClaim(
        @Field("claim") request: String,
        @Field("tags") tags: String,
        @Field("token") token: String
    ): Observable<WbCreateClaimResponse>
}