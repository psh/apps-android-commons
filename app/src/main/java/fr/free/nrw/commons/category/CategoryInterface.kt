package fr.free.nrw.commons.category

import io.reactivex.Single
import org.wikipedia.dataclient.mwapi.MwQueryResponse
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap

/**
 * Interface for interacting with Commons category related APIs
 */
interface CategoryInterface {
    /**
     * Searches for categories with the specified name.
     *
     * @param filter    The string to be searched
     * @param itemLimit How many results are returned
     * @return
     */
    @GET(
        "w/api.php?action=query&format=json&formatversion=2"
                + "&generator=search&gsrnamespace=14"
    )
    fun searchCategories(
        @Query("gsrsearch") filter: String?,
        @Query("gsrlimit") itemLimit: Int,
        @Query("gsroffset") offset: Int
    ): Single<MwQueryResponse>

    /**
     * Searches for categories starting with the specified prefix.
     *
     * @param prefix    The string to be searched
     * @param itemLimit How many results are returned
     * @return
     */
    @GET(
        "w/api.php?action=query&format=json&formatversion=2"
                + "&generator=allcategories&prop=categoryinfo"
    )
    fun searchCategoriesForPrefix(
        @Query("gacprefix") prefix: String?,
        @Query("gaclimit") itemLimit: Int,
        @Query("gacoffset") offset: Int
    ): Single<MwQueryResponse>

    @GET(
        "w/api.php?action=query&format=json&formatversion=2"
                + "&generator=categorymembers&gcmtype=subcat"
                + "&prop=info&gcmlimit=50"
    )
    fun getSubCategoryList(
        @Query("gcmtitle") categoryName: String?,
        @QueryMap(encoded = true) continuation: Map<String, String>
    ): Single<MwQueryResponse>

    @GET(
        "w/api.php?action=query&format=json&formatversion=2"
                + "&generator=categories&prop=info&gcllimit=50"
    )
    fun getParentCategoryList(
        @Query("titles") categoryName: String?,
        @QueryMap(encoded = true) continuation: Map<String, String>
    ): Single<MwQueryResponse>
}