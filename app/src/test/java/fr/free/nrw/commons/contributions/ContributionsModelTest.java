package fr.free.nrw.commons.contributions;

import android.accounts.Account;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Random;

import fr.free.nrw.commons.BuildConfig;
import fr.free.nrw.commons.TestCommonsApplication;
import fr.free.nrw.commons.auth.AccountUtil;
import fr.free.nrw.commons.auth.SessionManager;
import fr.free.nrw.commons.mwapi.MediaWikiApi;
import io.reactivex.Single;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.Schedulers;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, application = TestCommonsApplication.class)
public class ContributionsModelTest {
    @Mock
    MediaWikiApi mockMediaWikiApi;
    @Mock
    SessionManager mockSessionManager;

    private ContributionsModel contributionsModel;
    private TestObserver<Integer> contributionCountObserver;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(mockSessionManager.getCurrentAccount()).thenReturn(new Account("my_account", AccountUtil.ACCOUNT_TYPE));

        contributionsModel = new ContributionsModel(mockMediaWikiApi, mockSessionManager, Schedulers.trampoline());

        contributionCountObserver = new TestObserver<>();
        contributionsModel.observeUploadCount()
                .observeOn(Schedulers.trampoline())
                .subscribe(contributionCountObserver);
    }

    @Test
    public void subscribersReceiveUpdatesAboutContributionCount() {
        int expectedCount = new Random().nextInt();
        when(mockMediaWikiApi.getUploadCount("my_account")).thenReturn(Single.just(expectedCount));

        contributionsModel.refreshUploadCount();

        verify(mockSessionManager).getCurrentAccount();
        verify(mockMediaWikiApi).getUploadCount("my_account");
        contributionCountObserver.assertValue(expectedCount);
    }
}