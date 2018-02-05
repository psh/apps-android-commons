package fr.free.nrw.commons.upload.queue;

import android.database.Cursor;

import java.util.List;

import javax.inject.Inject;

import fr.free.nrw.commons.contributions.Contribution;
import fr.free.nrw.commons.contributions.ContributionDao;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.schedulers.Schedulers;

import static fr.free.nrw.commons.contributions.Contribution.STATE_COMPLETED;

public class UploadQueueModel {

    private final ContributionDao dao;
    private ObservableEmitter<List<Contribution>> emitter;

    @Inject
    public UploadQueueModel(ContributionDao dao) {
        this.dao = dao;
    }

    public Observable<List<Contribution>> observeContributions() {
        return Observable.create(e -> UploadQueueModel.this.emitter = e);
    }

    public void reloadContributions() {
        Observable.fromCallable(dao::loadAllContributions)
                .subscribeOn(Schedulers.io())
                .flatMap(this::toObservable)
                .filter(c -> c.getState() != STATE_COMPLETED)
                .toList()
                .subscribe(list -> emitter.onNext(list));
    }

    private Observable<Contribution> toObservable(Cursor cursor) {
        return Observable.create(e -> {
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                do {
                    e.onNext(dao.fromCursor(cursor));
                } while (cursor.moveToNext());
            }
            e.onComplete();
        });
    }
}
