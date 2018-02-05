package fr.free.nrw.commons.upload.queue;

import java.util.List;

import fr.free.nrw.commons.contributions.Contribution;
import fr.free.nrw.commons.upload.UploadService;

public interface UploadQueueContract {
    interface View {
        void showConnectionLost();

        void showNonWifi();

        void hideConnectionBanner();

        void displayContributions(List<Contribution> contributions);
    }

    interface Presenter {
        void start(View view);

        void stop();

        void setUploadService(UploadService uploadService);
    }
}
