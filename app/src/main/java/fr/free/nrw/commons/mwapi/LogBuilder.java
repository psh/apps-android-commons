package fr.free.nrw.commons.mwapi;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import fr.free.nrw.commons.BuildConfig;
import fr.free.nrw.commons.CommonsApplication;
import fr.free.nrw.commons.Utils;
import fr.free.nrw.commons.settings.Prefs;
import okhttp3.HttpUrl;

@SuppressWarnings("WeakerAccess")
public class LogBuilder {
    private Map<String, Object> data;
    private long rev;
    private String schema;
    private Gson gsonParser;

    LogBuilder(String schema, long revision, Gson gsonParser) {
        this.gsonParser = gsonParser;
        this.data = new HashMap<>();
        this.schema = schema;
        this.rev = revision;
    }

    public LogBuilder param(String key, Object value) {
        data.put(key, value);
        return this;
    }

    URL toUrl() {
        try {
            return new URL(toUrlString());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    HttpUrl toHttpUrl() {
        return HttpUrl.parse(toUrlString());
    }

    // force param disregards user preference
    // Use *only* for tracking the user preference change for EventLogging
    // Attempting to use anywhere else will cause kitten explosions
    public void log(boolean force) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(CommonsApplication.getInstance());
        if (!settings.getBoolean(Prefs.TRACKING_ENABLED, true) && !force) {
            return; // User has disabled tracking
        }
        LogTask logTask = new LogTask();
        logTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, this);
    }

    public void log() {
        log(false);
    }

    String toUrlString() {
        Map<String, Object> fullData = new HashMap<>();
        fullData.put("schema", schema);
        fullData.put("revision", rev);
        fullData.put("wiki", CommonsApplication.EVENTLOG_WIKI);
        data.put("device", EventLog.DEVICE);
        data.put("platform", "Android/" + Build.VERSION.RELEASE);
        data.put("appversion", "Android/" + BuildConfig.VERSION_NAME);
        fullData.put("event", data);
        return CommonsApplication.EVENTLOG_URL + "?" + Utils.urlEncode(gsonParser.toJson(fullData)) + ";";
    }
}
