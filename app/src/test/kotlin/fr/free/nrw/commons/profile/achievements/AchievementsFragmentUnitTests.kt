package fr.free.nrw.commons.profile.achievements

import android.accounts.Account
import android.content.Context
import android.os.Looper
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.test.core.app.ApplicationProvider
import fr.free.nrw.commons.OkHttpConnectionFactory
import fr.free.nrw.commons.R
import fr.free.nrw.commons.TestCommonsApplication
import fr.free.nrw.commons.auth.SessionManager
import fr.free.nrw.commons.createTestClient
import fr.free.nrw.commons.kvstore.JsonKvStore
import fr.free.nrw.commons.profile.ProfileActivity
import fr.free.nrw.commons.utils.ConfigUtils
import fr.free.nrw.commons.utils.SystemThemeUtils
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.powermock.reflect.Whitebox
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode
import org.robolectric.fakes.RoboMenuItem
import org.robolectric.shadows.ShadowToast
import java.lang.reflect.Method

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [23], application = TestCommonsApplication::class)
@LooperMode(LooperMode.Mode.PAUSED)
class AchievementsFragmentUnitTests {
    private lateinit var fragment: AchievementsFragment

    private lateinit var context: Context

    private lateinit var menuItem: MenuItem

    private lateinit var achievements: Achievements

    private lateinit var view: View

    private lateinit var layoutInflater: LayoutInflater

    @Mock
    private lateinit var sessionManager: SessionManager

    @Mock
    private lateinit var parentView: ViewGroup

    @Mock
    private lateinit var account: Account

    @Mock
    private lateinit var defaultKvStore: JsonKvStore

    @Mock
    private lateinit var systemThemeUtils: SystemThemeUtils

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        fragment = AchievementsFragment()
        Whitebox.setInternalState(fragment, "sessionManager", sessionManager)
        Whitebox.setInternalState(fragment, "okHttpJsonApiClient", org.mockito.Mockito.mock(fr.free.nrw.commons.mwapi.OkHttpJsonApiClient::class.java))
        Mockito.`when`(sessionManager.userName).thenReturn("Test")
        Mockito.`when`(sessionManager.currentAccount).thenReturn(account)

        context = ApplicationProvider.getApplicationContext()
        menuItem = RoboMenuItem(context)
        OkHttpConnectionFactory.CLIENT = createTestClient()

        val controller = Robolectric.buildActivity(ProfileActivity::class.java)
        val activity = controller.get()
        Whitebox.setInternalState(activity, "defaultKvStore", defaultKvStore)
        Whitebox.setInternalState(activity, "systemThemeUtils", systemThemeUtils)
        Whitebox.setInternalState(activity, "sessionManager", sessionManager)
        controller.create()

        val fragmentManager: FragmentManager = activity.supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(fragment, null)
        fragmentTransaction.commitNowAllowingStateLoss()

        layoutInflater = LayoutInflater.from(activity)
        view = fragment.onCreateView(layoutInflater, activity.findViewById(R.id.container), null)

        achievements = Achievements(0, 0, 0, 0, 0, 0, 0)
    }

    @Test
    @Throws(Exception::class)
    fun testOnCreateView() {
        fragment.onCreateView(layoutInflater, null, null)
    }

    @Test
    @Throws(Exception::class)
    fun checkFragmentNotNull() {
        Assert.assertNotNull(fragment)
    }

    @Test
    @Throws(Exception::class)
    fun testShowInfoDialog() {
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        fragment.showInfoDialog()
    }

    @Test
    @Throws(Exception::class)
    fun testShowUploadInfo() {
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        fragment.showUploadInfo()
    }

    @Test
    @Throws(Exception::class)
    fun testShowRevertedInfo() {
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        fragment.showRevertedInfo()
    }

    @Test
    @Throws(Exception::class)
    fun testShowUsedByWikiInfo() {
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        fragment.showUsedByWikiInfo()
    }

    @Test
    @Throws(Exception::class)
    fun testShowImagesViaNearbyInfo() {
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        fragment.showImagesViaNearbyInfo()
    }

    @Test
    @Throws(Exception::class)
    fun testShowFeaturedImagesInfo() {
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        fragment.showFeaturedImagesInfo()
    }

    @Test
    @Throws(Exception::class)
    fun testShowThanksReceivedInfo() {
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        fragment.showThanksReceivedInfo()
    }

    @Test
    @Throws(Exception::class)
    fun testShowQualityImagesInfo() {
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        fragment.showQualityImagesInfo()
    }

    @Test
    @Throws(Exception::class)
    fun testOnOptionsItemSelected() {
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        fragment.onOptionsItemSelected(menuItem)
    }

    @Test
    @Throws(Exception::class)
    fun testLaunchAlert() {
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        val method: Method =
            AchievementsFragment::class.java.getDeclaredMethod(
                "launchAlert",
                String::class.java,
                String::class.java,
            )
        method.isAccessible = true
        method.invoke(fragment, "", "")
    }

    @Test
    @Throws(Exception::class)
    fun testHideProgressBar() {
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        val method: Method =
            AchievementsFragment::class.java.getDeclaredMethod(
                "hideProgressBar",
                Achievements::class.java,
            )
        method.isAccessible = true
        method.invoke(fragment, achievements)
    }

    @Test
    @Throws(Exception::class)
    fun testSetAchievementsUploadCount() {
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        val method: Method =
            AchievementsFragment::class.java.getDeclaredMethod(
                "setAchievementsUploadCount",
                Achievements::class.java,
                Int::class.java,
            )
        method.isAccessible = true
        method.invoke(fragment, achievements, 0)
    }

    @Test
    @Throws(Exception::class)
    fun testCheckAccount() {
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        val method: Method =
            AchievementsFragment::class.java.getDeclaredMethod(
                "checkAccount",
            )
        method.isAccessible = true
        method.invoke(fragment)
    }

    @Test
    @Throws(Exception::class)
    fun testSetUploadCount() {
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        Whitebox.setInternalState(fragment, "userName", "Test")
        val apiClient = Whitebox.getInternalState<fr.free.nrw.commons.mwapi.OkHttpJsonApiClient>(fragment, "okHttpJsonApiClient")
        Mockito.`when`(apiClient.getUploadCount(Mockito.anyString())).thenReturn(io.reactivex.Single.just(0))

        val method: Method =
            AchievementsFragment::class.java.getDeclaredMethod(
                "setUploadCount",
                Achievements::class.java,
            )
        method.isAccessible = true
        method.invoke(fragment, achievements)
    }

    @Test
    @Throws(Exception::class)
    fun testOnError() {
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        val method: Method =
            AchievementsFragment::class.java.getDeclaredMethod(
                "onError",
            )
        method.isAccessible = true
        method.invoke(fragment)
    }

    @Test
    @Throws(Exception::class)
    fun testShowSnackBarWithRetryTrue() {
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        val method: Method =
            AchievementsFragment::class.java.getDeclaredMethod(
                "showSnackBarWithRetry",
                Boolean::class.java,
            )
        method.isAccessible = true
        method.invoke(fragment, true)
    }

    @Test
    @Throws(Exception::class)
    fun testShowSnackBarWithRetryFalse() {
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        val method: Method =
            AchievementsFragment::class.java.getDeclaredMethod(
                "showSnackBarWithRetry",
                Boolean::class.java,
            )
        method.isAccessible = true
        method.invoke(fragment, false)
    }

    @Test
    @Throws(Exception::class)
    fun testSetWikidataEditCount() {
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        val method: Method =
            AchievementsFragment::class.java.getDeclaredMethod(
                "setWikidataEditCount",
            )
        method.isAccessible = true
        method.invoke(fragment)
    }

    @Test
    @Throws(Exception::class)
    fun testSetAchievements() {
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        val method: Method =
            AchievementsFragment::class.java.getDeclaredMethod(
                "setAchievements",
            )
        method.isAccessible = true
        method.invoke(fragment)
    }

    @Test
    @Throws(Exception::class)
    fun testMenuVisibilityOverrideNotVisible() {
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        val method: Method =
            AchievementsFragment::class.java.getDeclaredMethod(
                "setMenuVisibility",
                Boolean::class.java,
            )
        method.isAccessible = true
        method.invoke(fragment, false)
        assertToast()
    }

    @Test
    @Throws(Exception::class)
    fun testMenuVisibilityOverrideVisibleWithContext() {
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        Mockito.`when`(parentView.context).thenReturn(context)
        val method: Method =
            AchievementsFragment::class.java.getDeclaredMethod(
                "setMenuVisibility",
                Boolean::class.java,
            )
        method.isAccessible = true
        method.invoke(fragment, true)
        assertToast()
    }

    private fun assertToast() {
        val latestToast = ShadowToast.getTextOfLatestToast()
        if (ConfigUtils.isBetaFlavour) {
            latestToast?.let {
                Assert.assertEquals(
                    it,
                    context.getString(R.string.achievements_unavailable_beta),
                )
            }
        } else {
            latestToast?.let {
                Assert.assertEquals(
                    context.getString(R.string.user_not_logged_in),
                    it,
                )
            }
        }
    }
}
