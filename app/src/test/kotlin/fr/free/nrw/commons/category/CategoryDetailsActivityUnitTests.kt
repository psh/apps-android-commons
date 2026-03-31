package fr.free.nrw.commons.category

import android.content.Context
import android.view.Menu
import android.view.MenuItem
import android.os.Bundle
import androidx.test.core.app.ApplicationProvider
import fr.free.nrw.commons.OkHttpConnectionFactory
import fr.free.nrw.commons.TestCommonsApplication
import fr.free.nrw.commons.createTestClient
import fr.free.nrw.commons.explore.categories.media.CategoriesMediaFragment
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
import org.robolectric.annotation.Config
import org.robolectric.fakes.RoboMenu
import org.robolectric.fakes.RoboMenuItem
import java.lang.reflect.Field

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [23], application = TestCommonsApplication::class)
class CategoryDetailsActivityUnitTests {
    private lateinit var activity: CategoryDetailsActivity

    private lateinit var context: Context

    private lateinit var menuItem: MenuItem

    private lateinit var menu: Menu

    @Mock
    private lateinit var categoriesMediaFragment: CategoriesMediaFragment

    @Mock
    private lateinit var systemThemeUtils: SystemThemeUtils

    @Mock
    private lateinit var defaultKvStore: JsonKvStore

    @Mock
    private lateinit var categoryViewModelFactory: CategoryDetailsViewModel.ViewModelFactory

    @Mock
    private lateinit var viewModel: CategoryDetailsViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        OkHttpConnectionFactory.CLIENT = createTestClient()

        context = ApplicationProvider.getApplicationContext()

        activity = Robolectric.buildActivity(CategoryDetailsActivity::class.java).get()

        Whitebox.setInternalState(activity, "systemThemeUtils", systemThemeUtils)
        Whitebox.setInternalState(activity, "defaultKvStore", defaultKvStore)
        Whitebox.setInternalState(activity, "categoryViewModelFactory", categoryViewModelFactory)
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

        val fieldCategoriesMediaFragment: Field =
            CategoryDetailsActivity::class.java.getDeclaredField("categoriesMediaFragment")
        fieldCategoriesMediaFragment.isAccessible = true
        fieldCategoriesMediaFragment.set(activity, categoriesMediaFragment)

        menuItem = RoboMenuItem(null)

        menu = RoboMenu(context)
    }

    @Test
    @Throws(Exception::class)
    fun checkActivityNotNull() {
        Assert.assertNotNull(activity)
    }

    @Test
    @Throws(Exception::class)
    fun testOnMediaClicked() {
        activity.onMediaClicked(0)
    }

    @Test
    @Throws(Exception::class)
    fun testGetMediaAtPosition() {
        activity.getMediaAtPosition(0)
    }

    @Test
    @Throws(Exception::class)
    fun testGetTotalMediaCount() {
        activity.getTotalMediaCount()
    }

    @Test
    @Throws(Exception::class)
    fun testGetContributionStateAt() {
        activity.getContributionStateAt(0)
    }

    @Test
    @Throws(Exception::class)
    fun testOnCreateOptionsMenu() {
        activity.onCreateOptionsMenu(menu)
    }

    @Test
    @Throws(Exception::class)
    fun testOnOptionsItemSelected() {
        activity.onOptionsItemSelected(menuItem)
    }

    @Test
    @Throws(Exception::class)
    fun testOnBackPressed() {
        activity.onBackPressed()
    }

    @Test
    @Throws(Exception::class)
    fun testViewPagerNotifyDataSetChanged() {
        activity.viewPagerNotifyDataSetChanged()
    }
}
