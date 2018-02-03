package fr.free.nrw.commons.upload.queue;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

import static java.lang.annotation.RetentionPolicy.SOURCE;

@Singleton
public class ConnectivityMonitor extends BroadcastReceiver {
    public static final int NO_CONNECTION = 0;
    public static final int CONNECTION_WIFI = 1;
    public static final int CONNECTION_NON_WIFI = 2;

    @Retention(SOURCE)
    @IntDef({NO_CONNECTION, CONNECTION_WIFI, CONNECTION_NON_WIFI})
    public @interface ConnectionType {
    }

    private BehaviorSubject<Integer> eventBus = BehaviorSubject.create();

    @Inject
    public ConnectivityMonitor() {
    }

    public Observable<Integer> observeConnection() {
        return eventBus;
    }

    @ConnectionType
    public int getConnectionType() {
        return eventBus.getValue();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        @ConnectionType int type = NO_CONNECTION;
        if (cm != null) {
            NetworkInfo network = cm.getActiveNetworkInfo();
            if (network != null) {
                type = network.getType() == ConnectivityManager.TYPE_WIFI
                        ? CONNECTION_WIFI : CONNECTION_NON_WIFI;
            }
        }
        eventBus.onNext(type);
    }
}
