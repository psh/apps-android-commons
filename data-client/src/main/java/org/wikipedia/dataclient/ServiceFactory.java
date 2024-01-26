package org.wikipedia.dataclient;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.LruCache;

import okhttp3.OkHttpClient;
import org.wikipedia.AppAdapter;
import org.wikipedia.json.GsonUtil;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public final class ServiceFactory {
    private static final int SERVICE_CACHE_SIZE = 8;
    private static LruCache<Long, Service> SERVICE_CACHE = new LruCache<>(SERVICE_CACHE_SIZE);
    private static LruCache<Long, RestService> REST_SERVICE_CACHE = new LruCache<>(SERVICE_CACHE_SIZE);

    public static Service get(@NonNull WikiSite wiki) {
        long hashCode = wiki.hashCode();
        if (SERVICE_CACHE.get(hashCode) != null) {
            return SERVICE_CACHE.get(hashCode);
        }

        Retrofit r = createRetrofit(wiki.url() + "/", AppAdapter.get().getOkHttpClient(wiki));

        Service s = r.create(Service.class);
        SERVICE_CACHE.put(hashCode, s);
        return s;
    }

    public static <T> T get(@NonNull WikiSite wiki, @Nullable String baseUrl, Class<T> service) {
        return createRetrofit(
            TextUtils.isEmpty(baseUrl) ? wiki.url() + "/" : baseUrl,
            AppAdapter.get().getOkHttpClient(wiki)
        ).create(service);
    }

    public static RestService getRest(@NonNull WikiSite wiki) {
        long hashCode = wiki.hashCode();
        if (REST_SERVICE_CACHE.get(hashCode) != null) {
            return REST_SERVICE_CACHE.get(hashCode);
        }

        Retrofit r = createRetrofit(TextUtils.isEmpty(AppAdapter.get().getRestbaseUriFormat())
                        ? wiki.url() + "/" + RestService.REST_API_PREFIX
                        : String.format(AppAdapter.get().getRestbaseUriFormat(), "https", wiki.authority()),
            AppAdapter.get().getOkHttpClient(wiki));

        RestService s = r.create(RestService.class);
        REST_SERVICE_CACHE.put(hashCode, s);
        return s;
    }

    public static Retrofit createRetrofit(@NonNull String baseUrl, OkHttpClient httpClient) {
        return new Retrofit.Builder()
                .client(httpClient)
                .baseUrl(baseUrl)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(GsonUtil.getDefaultGson()))
                .build();
    }

    private ServiceFactory() { }
}
