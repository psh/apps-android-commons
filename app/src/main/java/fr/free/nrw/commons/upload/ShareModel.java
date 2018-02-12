package fr.free.nrw.commons.upload;

import android.content.Intent;
import android.net.Uri;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import fr.free.nrw.commons.contributions.Contribution;

@Singleton
public class ShareModel {
    private List<Contribution> photosList;

    @Inject
    public ShareModel() {
        photosList = Collections.emptyList();
    }

    public boolean isEmpty() {
        return photosList.isEmpty();
    }

    public Contribution getAt(int index) {
        return photosList.get(index);
    }

    public void initFromIntent(Intent intent) {
        clear();

        if (intent == null || intent.getAction() == null) {
            return;
        }

        if (intent.getAction().equals(Intent.ACTION_SEND)) {
            initializeSingle(intent);
        } else if (intent.getAction().equals(Intent.ACTION_SEND_MULTIPLE)) {
            initializeMultiple(intent);
        }
    }

    private void clear() {
        photosList = new ArrayList<>();
    }

    private void initializeSingle(Intent intent) {
        String source = (intent.hasExtra(UploadService.EXTRA_SOURCE))
                ? intent.getStringExtra(UploadService.EXTRA_SOURCE)
                : Contribution.SOURCE_EXTERNAL;

        Contribution up = new Contribution();
        up.setLocalUri(intent.getParcelableExtra(Intent.EXTRA_STREAM));
        up.setTag("mimeType", intent.getType());
        up.setTag("sequence", 1);
        up.setSource(source);
        up.setMultiple(false);
        photosList.add(up);
    }

    private void initializeMultiple(Intent intent) {
        ArrayList<Uri> urisList = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        for (int i = 0; i < urisList.size(); i++) {
            Contribution up = new Contribution();
            Uri uri = urisList.get(i);
            up.setLocalUri(uri);
            up.setTag("mimeType", intent.getType());
            up.setTag("sequence", i);
            up.setSource(Contribution.SOURCE_EXTERNAL);
            up.setMultiple(true);
            photosList.add(up);
        }
    }
}
