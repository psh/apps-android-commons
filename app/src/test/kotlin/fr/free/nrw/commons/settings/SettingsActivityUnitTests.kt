package fr.free.nrw.commons.settings

import android.content.Context
import android.os.Bundle
import android.os.Looper
import android.view.MenuItem
import androidx.test.core.app.ApplicationProvider
import fr.free.nrw.commons.TestCommonsApplication
import fr.free.nrw.commons.di.DefaultKvStore
import fr.free.nrw.commons.kvstore.JsonKvStore
import fr.free.nrw.commons.utils.SystemThemeUtils
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.powermock.reflect.Whitebox
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode
import org.robolectric.fakes.RoboMenuItem
import java.lang.reflect.Method

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [23], application = TestCommonsApplication::class)
@LooperMode(LooperMode.Mode.PAUSED)
class SettingsActivityUnitTests {
    private lateinit var activity: SettingsActivity
    private lateinit var context: Context
    private lateinit var menuItem: MenuItem

    @Mock
    private lateinit var savedInstanceState: Bundle

    @Mock
    private lateinit var systemThemeUtils: SystemThemeUtils

    @Mock
    @DefaultKvStore
    private lateinit var defaultKvStore: JsonKvStore

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        context = ApplicationProvider.getApplicationContext()

        activity = Robolectric.buildActivity(SettingsActivity::class.java).get()
        Whitebox.setInternalState(activity, "systemThemeUtils", systemThemeUtils)
        Whitebox.setInternalState(activity, "defaultKvStore", defaultKvStore)

        `when`(systemThemeUtils.isDeviceInNightMode()).thenReturn(false)

        try {
            val method = activity.javaClass.getDeclaredMethod("onCreate", Bundle::class.java)
            method.isAccessible = true
            method.invoke(activity, null)
        } catch (e: Exception) {
            try {
                val method = activity.javaClass.superclass.getDeclaredMethod("onCreate", Bundle::class.java)
                method.isAccessible = true
                method.invoke(activity, null)
            } catch (e2: Exception) {
            }
        }

        menuItem = RoboMenuItem(null)
    }

    @Test
    @Throws(Exception::class)
    fun checkActivityNotNull() {
        Assert.assertNotNull(activity)
    }

    @Test
    @Throws(Exception::class)
    fun testOnSupportNavigateUp() {
        activity.onSupportNavigateUp()
    }

    @Test
    @Throws(Exception::class)
    fun testOnOptionsItemSelectedCaseDefault() {
        activity.onOptionsItemSelected(menuItem)
    }

    @Test
    @Throws(Exception::class)
    fun testOnOptionsItemSelectedCaseHome() {
        menuItem = RoboMenuItem(android.R.id.home)
        activity.onOptionsItemSelected(menuItem)
    }

    @Test
    @Throws(Exception::class)
    fun testSetTotalUploadCount() {
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        val method: Method =
            SettingsActivity::class.java.getDeclaredMethod(
                "onPostCreate",
                Bundle::class.java,
            )
        method.isAccessible = true
        method.invoke(activity, savedInstanceState)
    }
}
