package fr.free.nrw.commons.upload.queue;

public interface UploadQueueContract {
    interface View {
        void showConnectionLost();

        void showNonWifi();

        void hideConnectionBanner();
    }

    interface Presenter {
        void start(UploadQueueContract.View view);

        void stop();
    }
}
