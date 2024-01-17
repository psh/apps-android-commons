package fr.free.nrw.commons.upload.depicts

import android.app.ProgressDialog
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nhaarman.mockitokotlin2.whenever
import depictedItem
import fr.free.nrw.commons.Media
import fr.free.nrw.commons.R
import fr.free.nrw.commons.TestAppAdapter
import fr.free.nrw.commons.TestCommonsApplication
import fr.free.nrw.commons.kvstore.JsonKvStore
import fr.free.nrw.commons.upload.UploadBaseFragment
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import org.powermock.reflect.Whitebox
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode
import org.wikipedia.AppAdapter
import java.lang.reflect.Method

@RunWith(AndroidJUnit4::class)
@Config(sdk = [21], application = TestCommonsApplication::class)
@LooperMode(LooperMode.Mode.PAUSED)
class DepictsFragmentUnitTests {

    private lateinit var scenario: FragmentScenario<DepictsFragment>

    @Mock
    private lateinit var mockCallback: UploadBaseFragment.Callback
    @Mock
    private lateinit var mockAdapter: UploadDepictsAdapter
    @Mock
    private lateinit var applicationKvStore: JsonKvStore
    @Mock
    private lateinit var media: Media

    @Before
    fun setUp() {
        AppAdapter.set(TestAppAdapter())
        MockitoAnnotations.openMocks(this)

        scenario = launchFragmentInContainer(
            initialState = Lifecycle.State.RESUMED,
            themeResId = R.style.LightAppTheme
        ) {
            DepictsFragment().apply {
                callback = mockCallback
            }
        }

        scenario.onFragment {
            it.adapter = mockAdapter
        }
    }

    @Test
    @Throws(Exception::class)
    fun testInit() {
        val method: Method = DepictsFragment::class.java.getDeclaredMethod(
            "init"
        )
        method.isAccessible = true
        scenario.onFragment { method.invoke(it) }
    }

    @Test
    @Throws(Exception::class)
    fun `Test init when media is not null`() {
        scenario.onFragment { Whitebox.setInternalState(it, "media", media) }

        val method: Method = DepictsFragment::class.java.getDeclaredMethod("init")
        method.isAccessible = true
        scenario.onFragment { method.invoke(it) }
    }

    @Test
    @Throws(Exception::class)
    fun testOnBecameVisible() {
        val method: Method = DepictsFragment::class.java.getDeclaredMethod(
            "onBecameVisible"
        )
        method.isAccessible = true
        scenario.onFragment { method.invoke(it) }
    }

    @Test
    @Throws(Exception::class)
    fun testGoToNextScreen() {
        scenario.onFragment { it.goToNextScreen() }
    }

    @Test
    @Throws(Exception::class)
    fun testGoToPreviousScreen() {
        scenario.onFragment { it.goToPreviousScreen() }
    }

    @Test
    @Throws(Exception::class)
    fun testNoDepictionSelected() {
        scenario.onFragment { it.noDepictionSelected() }
    }

    @Test
    @Throws(Exception::class)
    fun testShowProgress() {
        scenario.onFragment { it.showProgress(true) }
    }

    @Test
    @Throws(Exception::class)
    fun testShowErrorCaseTrue() {
        scenario.onFragment { it.showError(true) }
    }

    @Test
    @Throws(Exception::class)
    fun testShowErrorCaseFalse() {
        scenario.onFragment { it.showError(false) }
    }

    @Test
    @Throws(Exception::class)
    fun testSetDepictsList() {
        scenario.onFragment { it.setDepictsList(listOf()) }
    }

    @Test
    @Throws(Exception::class)
    fun `Test setDepictsList when list is not empty`() {
        scenario.onFragment { it.setDepictsList(listOf(depictedItem())) }
    }

    @Test
    @Throws(Exception::class)
    fun `Test setDepictsList when applicationKvStore returns true`() {
        scenario.onFragment{
            Whitebox.setInternalState(it, "applicationKvStore", applicationKvStore)
        }
        whenever(applicationKvStore.getBoolean("first_edit_depict")).thenReturn(true)
        scenario.onFragment { it.setDepictsList(listOf(depictedItem())) }
    }

    @Test
    @Throws(Exception::class)
    fun testOnNextButtonClicked() {
        scenario.onFragment { it.onNextButtonClicked() }
    }

    @Test
    @Throws(Exception::class)
    fun testOnPreviousButtonClicked() {
        scenario.onFragment { it.onPreviousButtonClicked() }
    }

    @Test
    @Throws(Exception::class)
    fun testSearchForDepictions() {
        val method: Method = DepictsFragment::class.java.getDeclaredMethod(
            "searchForDepictions",
            String::class.java
        )
        method.isAccessible = true
        scenario.onFragment { method.invoke(it, "") }
    }

    @Test
    @Throws(Exception::class)
    fun testInitRecyclerView() {
        val method: Method = DepictsFragment::class.java.getDeclaredMethod(
            "initRecyclerView"
        )
        method.isAccessible = true
        scenario.onFragment { method.invoke(it) }
    }

    @Test
    @Throws(Exception::class)
    fun `Test initRecyclerView when media is not null`() {
        scenario.onFragment { Whitebox.setInternalState(it, "media", media) }

        val method: Method = DepictsFragment::class.java.getDeclaredMethod(
            "initRecyclerView"
        )
        method.isAccessible = true

        scenario.onFragment { method.invoke(it) }
    }

    @Test
    @Throws(Exception::class)
    fun testGoBackToPreviousScreen() {
        scenario.onFragment { it.goBackToPreviousScreen() }
    }

    @Test
    @Throws(Exception::class)
    fun testShowProgressDialog() {
        scenario.onFragment { it.showProgressDialog() }
    }

    @Test
    @Throws(Exception::class)
    fun testDismissProgressDialog() {
        scenario.onFragment {
            Whitebox.setInternalState(it, "progressDialog", mock<ProgressDialog?>())
            it.dismissProgressDialog()
        }
    }
}