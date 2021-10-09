package fr.free.nrw.commons.review

import io.reactivex.Observable
import org.wikipedia.dataclient.mwapi.MwQueryResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Interface class for peer review calls
 */
interface ReviewInterface {
    @GET("w/api.php?action=query&format=json&formatversion=2&list=recentchanges&rcprop=title|ids&rctype=new|log&rctoponly=1&rcnamespace=6")
    fun getRecentChanges(@Query("rcstart") rcStart: String?): Observable<MwQueryResponse>

    @GET("w/api.php?action=query&format=json&formatversion=2&prop=revisions&rvprop=timestamp|ids|user&rvdir=newer&rvlimit=1")
    fun getFirstRevisionOfFile(@Query("titles") titles: String?): Observable<MwQueryResponse>
}