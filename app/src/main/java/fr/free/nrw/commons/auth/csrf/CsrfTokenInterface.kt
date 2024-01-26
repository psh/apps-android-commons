package fr.free.nrw.commons.auth.csrf

import fr.free.nrw.commons.wikidata.WikidataConstants
import org.wikipedia.dataclient.mwapi.MwQueryResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers

interface CsrfTokenInterface {
    @Headers("Cache-Control: no-cache")
    @GET(WikidataConstants.MW_API_PREFIX + "action=query&meta=tokens&type=csrf")
    fun getCsrfTokenCall(): Call<MwQueryResponse?>
}
