package fr.free.nrw.commons.di

import okhttp3.OkHttpClient
import org.wikipedia.dataclient.ServiceFactory

object CommonsServiceFactory {
    @JvmStatic
    fun <T> get(okHttpClient: OkHttpClient, baseUrl: String, service: Class<T>): T {
        return ServiceFactory.createRetrofit(baseUrl, okHttpClient).create(service)
    }
}