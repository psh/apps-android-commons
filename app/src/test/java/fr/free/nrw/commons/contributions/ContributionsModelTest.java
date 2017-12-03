package fr.free.nrw.commons.contributions;

import android.accounts.Account;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import fr.free.nrw.commons.auth.AccountUtil;
import fr.free.nrw.commons.auth.SessionManager;
import fr.free.nrw.commons.mwapi.MediaWikiApi;
import io.reactivex.Single;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.Schedulers;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ContributionsModelTest {

    private static final String ACCOUNT_NAME = "my_account";

    @Mock
    MediaWikiApi mediaWikiApi;
    @Mock
    SessionManager sessionManager;

    private ContributionsModel testObject;
    private TestObserver<Integer> uploadCountObserver;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        when(sessionManager.getCurrentAccountName()).thenReturn(ACCOUNT_NAME);

        testObject = new ContributionsModel(sessionManager, mediaWikiApi, Schedulers.trampoline());
        uploadCountObserver = new TestObserver<>();
        testObject.observeUploadCount().observeOn(Schedulers.trampoline()).subscribe(uploadCountObserver);
    }

    @Test
    public void refreshingContributionCountNotifiesObservers() {
        when(mediaWikiApi.getUploadCount(ACCOUNT_NAME)).thenReturn(Single.just(1234));

        testObject.refreshUploadCount();

        verify(sessionManager).getCurrentAccountName();
        uploadCountObserver.assertValue(1234);
    }

    @Test
    public void errorsNotified() {
        when(mediaWikiApi.getUploadCount(ACCOUNT_NAME)).thenReturn(Single.error(new Exception()));

        testObject.refreshUploadCount();

        verify(sessionManager).getCurrentAccountName();
        uploadCountObserver.assertValue(ContributionsModel.UPLOAD_COUNT_UNAVAILABLE);
    }
}