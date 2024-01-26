package fr.free.nrw.commons.di

import okhttp3.OkHttpClient
import org.wikipedia.json.GsonUtil
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object CommonsServiceFactory {
    private val retrofitCache: MutableMap<String, Retrofit> = mutableMapOf()

    @JvmStatic
    fun <T> get(okHttpClient: OkHttpClient, baseUrl: String, service: Class<T>): T {
        var retrofit = retrofitCache[baseUrl]
        if (retrofit == null) {
            retrofit = createRetrofit(baseUrl, okHttpClient)
            retrofitCache[baseUrl] = retrofit
        }
        return retrofit.create(service)
    }

    private fun createRetrofit(baseUrl: String, httpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .client(httpClient)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(GsonUtil.getDefaultGson()))
            .baseUrl(baseUrl)
            .build()
    }
}