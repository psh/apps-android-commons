package fr.free.nrw.commons.review

import android.os.Bundle
import android.os.Looper
import android.widget.Button
import androidx.annotation.IdRes
import androidx.core.os.bundleOf
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.soloader.SoLoader
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import fr.free.nrw.commons.Media
import fr.free.nrw.commons.R
import fr.free.nrw.commons.TestAppAdapter
import fr.free.nrw.commons.TestCommonsApplication
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode
import org.wikipedia.AppAdapter
import java.lang.reflect.Method

@RunWith(AndroidJUnit4::class)
@Config(sdk = [21], application = TestCommonsApplication::class)
@LooperMode(LooperMode.Mode.PAUSED)
class ReviewImageFragmentTest {
    private lateinit var scenario: FragmentScenario<ReviewImageFragment>

    @Mock
    private lateinit var savedInstanceState: Bundle

    @Mock
    private lateinit var reviewImageFragmentCallback: ReviewImageFragment.ReviewImageFragmentCallback

    @Before
    fun setUp() {

        MockitoAnnotations.openMocks(this)
        AppAdapter.set(TestAppAdapter())
        SoLoader.setInTestMode()

        Fresco.initialize(ApplicationProvider.getApplicationContext())

        scenario = launchFragmentInContainer(
            initialState = Lifecycle.State.RESUMED,
            themeResId = R.style.LightAppTheme,
            fragmentArgs = bundleOf("position" to 1)
        ) {
            ReviewImageFragment().apply {
                callback = reviewImageFragmentCallback
            }
        }
    }

    @Test
    @Throws(Exception::class)
    fun testOnDisableButton() {
        scenario.onFragment {
            it.disableButtons()

            it.onButton(R.id.button_yes) {
                assertEquals(isEnabled, false)
                assertEquals(alpha, 0.5f)
            }

            it.onButton(R.id.button_no) {
                assertEquals(isEnabled, false)
                assertEquals(alpha, 0.5f)
            }
        }
    }

    @Test
    @Throws(Exception::class)
    fun testOnEnableButton() {
        scenario.onFragment {
            it.enableButtons()

            it.onButton(R.id.button_yes) {
                assertEquals(isEnabled, true)
                assertEquals(alpha, 1f)
            }

            it.onButton(R.id.button_no) {
                assertEquals(isEnabled, true)
                assertEquals(alpha, 1f)
            }
        }
    }


    @Test
    @Throws(Exception::class)
    fun testOnUpdateCategoriesQuestion() {
        shadowOf(Looper.getMainLooper()).idle()
        val media = mock(Media::class.java)
        whenever(reviewImageFragmentCallback.media).thenReturn(media)
        Assert.assertNotNull(media)
        val categories = mapOf("Category:" to false)
        doReturn(categories).`when`(media).categoriesHiddenStatus
        Assert.assertNotNull(media.categoriesHiddenStatus)
        val method: Method =
            ReviewImageFragment::class.java.getDeclaredMethod("updateCategoriesQuestion")
        method.isAccessible = true
        scenario.onFragment { method.invoke(it) }
    }

    @Test
    @Throws(Exception::class)
    fun testOnSaveInstanceState() {
        scenario.onFragment { it.onSaveInstanceState(savedInstanceState) }
    }

    @Test
    @Throws(Exception::class)
    fun testOnYesButtonClicked() {
        shadowOf(Looper.getMainLooper()).idle()
        scenario.onFragment { it.onYesButtonClicked() }
    }

    @Test
    @Throws(Exception::class)
    fun testOnGetReviewCallback() {
        val method: Method =
            ReviewImageFragment::class.java.getDeclaredMethod("getReviewCallback")
        method.isAccessible = true
        scenario.onFragment { method.invoke(it) }
    }

    private fun ReviewImageFragment.onButton(@IdRes id: Int, block: Button.() -> Unit) =
        block(requireView().findViewById(id))
}