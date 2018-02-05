package fr.free.nrw.commons.upload.queue;

import android.content.SharedPreferences;

import javax.inject.Inject;
import javax.inject.Named;

import fr.free.nrw.commons.upload.UploadService;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

public class UploadQueuePresenter implements UploadQueueContract.Presenter {
    private final SharedPreferences prefs;
    private final UploadQueueModel model;
    private final ConnectivityMonitor connectivityMonitor;
    private CompositeDisposable subscriptions;
    private UploadQueueContract.View view;
    private UploadService uploadService;

    @Inject
    public UploadQueuePresenter(ConnectivityMonitor connectivityMonitor,
                                @Named("default_preferences") SharedPreferences prefs,
                                UploadQueueModel model) {
        this.connectivityMonitor = connectivityMonitor;
        this.prefs = prefs;
        this.model = model;
        this.subscriptions = new CompositeDisposable();
    }

    @Override
    public void start(UploadQueueContract.View view) {
        this.view = view;

        subscriptions.add(connectivityMonitor
                .observeConnection()
                .subscribe(this::onConnectivityChange));

        subscriptions.add(model.observeContributions()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(view::displayContributions));

        model.reloadContributions();
    }

    @Override
    public void stop() {
        subscriptions.dispose();
        view = null;
        uploadService = null;
    }

    @Override
    public void setUploadService(UploadService uploadService) {
        this.uploadService = uploadService;
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
