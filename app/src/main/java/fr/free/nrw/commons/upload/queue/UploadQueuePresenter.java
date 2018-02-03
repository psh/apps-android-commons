package fr.free.nrw.commons.upload.queue;

import android.content.SharedPreferences;

import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.disposables.Disposable;

public class UploadQueuePresenter implements UploadQueueContract.Presenter {
    private final SharedPreferences prefs;
    private final ConnectivityMonitor connectivityMonitor;
    private Disposable connectivityDisposable;
    private UploadQueueContract.View view;

    @Inject
    public UploadQueuePresenter(ConnectivityMonitor connectivityMonitor, @Named("default_preferences") SharedPreferences prefs) {
        this.connectivityMonitor = connectivityMonitor;
        this.prefs = prefs;
    }

    public void start(UploadQueueContract.View view) {
        this.view = view;
        connectivityDisposable = connectivityMonitor
                .observeConnection()
                .subscribe(this::onConnectivityChange);
    }

    public void stop() {
        if (connectivityDisposable != null && !connectivityDisposable.isDisposed()) {
            connectivityDisposable.dispose();
        }
    }

    private void onConnectivityChange(@ConnectivityMonitor.ConnectionType int connectionType) {
        switch (connectionType) {
            case ConnectivityMonitor.NO_CONNECTION:
                view.showConnectionLost();
                break;
            case ConnectivityMonitor.CONNECTION_NON_WIFI:
                if (prefs.getBoolean("uploadOverWifi", false)) {
                    view.showNonWifi();
                } else {
                    view.hideConnectionBanner();
                }
                break;
            case ConnectivityMonitor.CONNECTION_WIFI:
                view.hideConnectionBanner();
                break;
        }
    }
}
