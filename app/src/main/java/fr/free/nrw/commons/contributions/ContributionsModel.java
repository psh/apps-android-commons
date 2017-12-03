package fr.free.nrw.commons.contributions;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import fr.free.nrw.commons.auth.SessionManager;
import fr.free.nrw.commons.mwapi.MediaWikiApi;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.subjects.BehaviorSubject;

@Singleton
public class ContributionsModel {

    public static final int UPLOAD_COUNT_UNAVAILABLE = 0;

    private final SessionManager sessionManager;
    private final MediaWikiApi mediaWikiApi;
    private final Scheduler ioScheduler;
    private BehaviorSubject<Integer> uploadCount;

    @Inject
    public ContributionsModel(SessionManager sessionManager, MediaWikiApi mediaWikiApi, @Named("io") Scheduler ioScheduler) {
        this.sessionManager = sessionManager;
        this.mediaWikiApi = mediaWikiApi;
        this.ioScheduler = ioScheduler;
        this.uploadCount = BehaviorSubject.create();
    }

    public Observable<Integer> observeUploadCount() {
        return uploadCount;
    }

    public void refreshUploadCount() {
        mediaWikiApi.getUploadCount(sessionManager.getCurrentAccountName())
                .subscribeOn(ioScheduler)
                .observeOn(ioScheduler)
                .onErrorReturnItem(UPLOAD_COUNT_UNAVAILABLE)
                .subscribe(count -> uploadCount.onNext(count));
    }
}
