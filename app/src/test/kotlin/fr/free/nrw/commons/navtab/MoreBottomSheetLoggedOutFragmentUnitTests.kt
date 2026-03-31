package fr.free.nrw.commons.navtab

import android.os.Looper
import androidx.lifecycle.Lifecycle
import androidx.test.ext.junit.runners.AndroidJUnit4
import fr.free.nrw.commons.R
import fr.free.nrw.commons.TestCommonsApplication
import fr.free.nrw.commons.launchFragmentInHiltContainer
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode

@RunWith(AndroidJUnit4::class)
@Config(sdk = [23], application = TestCommonsApplication::class)
@LooperMode(LooperMode.Mode.PAUSED)
class MoreBottomSheetLoggedOutFragmentUnitTests {
    private lateinit var fragment: MoreBottomSheetLoggedOutFragment

    @Before
    fun setUp() {
        launchFragmentInHiltContainer<MoreBottomSheetLoggedOutFragment> {
            fragment = this as MoreBottomSheetLoggedOutFragment
        }
    }

    @Test
    @Throws(Exception::class)
    fun testOnSettingsClicked() {
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        fragment.onSettingsClicked()
    }

    @Test
    @Throws(Exception::class)
    fun testOnAboutClicked() {
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        fragment.onAboutClicked()
    }

    @Test
    @Throws(Exception::class)
    fun testOnFeedbackClicked() {
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        fragment.onFeedbackClicked()
    }

    @Test
    @Throws(Exception::class)
    fun testOnLogoutClicked() {
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        fragment.onLogoutClicked()
    }
}
