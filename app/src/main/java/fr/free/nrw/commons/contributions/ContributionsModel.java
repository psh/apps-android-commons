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
    private final MediaWikiApi mediaWikiApi;
    private final SessionManager sessionManager;
    private final Scheduler ioScheduler;
    private final BehaviorSubject<Integer> uploadCount;

    @Inject
    public ContributionsModel(MediaWikiApi mediaWikiApi,
                              SessionManager sessionManager,
                              @Named("io") Scheduler ioScheduler) {
        this.mediaWikiApi = mediaWikiApi;
        this.sessionManager = sessionManager;
        this.ioScheduler = ioScheduler;
        this.uploadCount = BehaviorSubject.create();
    }

    public Observable<Integer> observeUploadCount() {
        return uploadCount;
    }

    public void refreshUploadCount() {
        String account = sessionManager.getCurrentAccount().name;
        mediaWikiApi.getUploadCount(account)
                .subscribeOn(ioScheduler)
                .onErrorReturnItem(0)
                .subscribe(uploadCount::onNext);
    }
}
