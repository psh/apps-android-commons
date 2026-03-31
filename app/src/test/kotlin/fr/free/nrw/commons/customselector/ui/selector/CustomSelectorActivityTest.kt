package fr.free.nrw.commons.customselector.ui.selector

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResult
import fr.free.nrw.commons.OkHttpConnectionFactory
import fr.free.nrw.commons.TestCommonsApplication
import fr.free.nrw.commons.contributions.ContributionDao
import fr.free.nrw.commons.createTestClient
import fr.free.nrw.commons.customselector.database.NotForUploadStatusDao
import fr.free.nrw.commons.customselector.model.Image
import fr.free.nrw.commons.customselector.ui.adapter.ImageAdapter
import fr.free.nrw.commons.databinding.ActivityCustomSelectorBinding
import fr.free.nrw.commons.databinding.CustomSelectorBottomLayoutBinding
import fr.free.nrw.commons.databinding.CustomSelectorToolbarBinding
import fr.free.nrw.commons.kvstore.JsonKvStore
import fr.free.nrw.commons.upload.FileUtilsWrapper
import fr.free.nrw.commons.utils.SystemThemeUtils
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.powermock.reflect.Whitebox
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.lang.reflect.Field
import java.lang.reflect.Method

/**
 * Custom Selector Activity Test
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [23], application = TestCommonsApplication::class)
class CustomSelectorActivityTest {
    private lateinit var activity: CustomSelectorActivity

    private lateinit var imageFragment: ImageFragment

    private lateinit var images: java.util.ArrayList<Image>

    private var uri: Uri = Mockito.mock(Uri::class.java)

    private lateinit var image: Image

    @Mock
    lateinit var contributionDao: ContributionDao

    @Mock
    lateinit var customSelectorViewModelFactory: CustomSelectorViewModelFactory

    @Mock
    lateinit var notForUploadStatusDao: NotForUploadStatusDao

    @Mock
    lateinit var fileUtilsWrapper: FileUtilsWrapper

    @Mock
    lateinit var defaultKvStore: JsonKvStore

    @Mock
    lateinit var systemThemeUtils: SystemThemeUtils

    /**
     * Set up the tests.
     */
    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        OkHttpConnectionFactory.CLIENT = createTestClient()

        val controller = Robolectric.buildActivity(CustomSelectorActivity::class.java)
        activity = controller.get()
        Whitebox.setInternalState(activity, "customSelectorViewModelFactory", customSelectorViewModelFactory)
        Whitebox.setInternalState(activity, "notForUploadStatusDao", notForUploadStatusDao)
        Whitebox.setInternalState(activity, "fileUtilsWrapper", fileUtilsWrapper)
        Whitebox.setInternalState(activity, "defaultKvStore", defaultKvStore)
        Whitebox.setInternalState(activity, "systemThemeUtils", systemThemeUtils)
        try {
            controller.create()
        } catch (e: Exception) {
            // Ignore for tests
        }

        val binding = ActivityCustomSelectorBinding.inflate(activity.layoutInflater)
        Whitebox.setInternalState(activity, "binding", binding)
        Whitebox.setInternalState(activity, "toolbarBinding", CustomSelectorToolbarBinding.bind(binding.root))
        Whitebox.setInternalState(activity, "bottomSheetBinding", CustomSelectorBottomLayoutBinding.bind(binding.root))
        Whitebox.setInternalState(activity, "viewModel", Mockito.mock(CustomSelectorViewModel::class.java))
        val mockViewModel = Whitebox.getInternalState(activity, "viewModel") as CustomSelectorViewModel
        val mockLiveData = Mockito.mock(androidx.lifecycle.MutableLiveData::class.java) as androidx.lifecycle.MutableLiveData<ArrayList<Image>>
        Mockito.`when`(mockViewModel.selectedImages).thenReturn(mockLiveData)
        Whitebox.setInternalState(activity, "prefs", activity.getSharedPreferences("CustomSelector", Activity.MODE_PRIVATE))

        imageFragment = ImageFragment.newInstance(1, 0)
        image = Image(1, "image", uri, "abc/abc", 1, "bucket1")
        images = ArrayList()

        Whitebox.setInternalState(activity, "imageFragment", imageFragment)
        Whitebox.setInternalState(imageFragment, "imageAdapter", Mockito.mock(ImageAdapter::class.java))
        Whitebox.setInternalState(imageFragment, "contributionDao", contributionDao)
    }

    /**
     * Test activity not null.
     */
    @Test
    @Throws(Exception::class)
    fun testActivityNotNull() {
        assertNotNull(activity)
    }

    /**
     * Test changeTitle function.
     */
    @Test
    @Throws(Exception::class)
    fun testChangeTitle() {
        activity.onFolderClick(1, "test", 0)
        val func = activity.javaClass.getDeclaredMethod("changeTitle", String::class.java, Int::class.java)
        func.isAccessible = true
        func.invoke(activity, "test", 0)
    }

    /**
     * Test onFolderClick function.
     */
    @Test
    @Throws(Exception::class)
    fun testOnFolderClick() {
        activity.onFolderClick(1, "test", 0)
    }

    /**
     * Test callback when result received.
     */
    @Test
    @Throws(Exception::class)
    fun testResultLauncher() {
        val intent = Mockito.mock(Intent::class.java)
        val activityResult = ActivityResult(Activity.RESULT_OK, intent)
        val func =
            activity.javaClass.getDeclaredMethod(
                "onFullScreenDataReceived",
                ActivityResult::class.java,
            )
        func.isAccessible = true
        func.invoke(activity, activityResult)
    }

    /**
     * Test showWelcomeDialog function.
     */
    @Test
    @Throws(Exception::class)
    fun testShowWelcomeDialog() {
        val func =
            activity.javaClass.getDeclaredMethod(
                "showWelcomeDialog",
            )
        func.isAccessible = true
        func.invoke(activity)
    }

    /**
     * Test onLongPress function.
     */
    @Test
    @Throws(Exception::class)
    fun testOnLongPress() {
        val func =
            activity.javaClass.getDeclaredMethod(
                "onLongPress",
                Int::class.java,
                ArrayList::class.java,
                ArrayList::class.java,
            )
        images.add(image)

        func.isAccessible = true
        func.invoke(activity, 0, images, images)
    }

    /**
     * Test selectedImagesChanged function.
     */
    @Test
    @Throws(Exception::class)
    fun testOnSelectedImagesChanged() {
        activity.onFolderClick(1, "test", 0)
        activity.onSelectedImagesChanged(ArrayList(), 0)
    }

    /**
     * Test onDone function.
     */
    @Test
    @Throws(Exception::class)
    fun testOnDone() {
        activity.onDone()
        activity.onFolderClick(1, "test", 0)
        activity.onSelectedImagesChanged(
            ArrayList(arrayListOf(Image(1, "test", Uri.parse("test"), "test", 1))),
            1,
        )
        activity.onDone()
    }

    /**
     * Test onClickNotForUpload function.
     */
    @Test
    @Throws(Exception::class)
    fun testOnClickNotForUpload() {
        activity.onFolderClick(1, "test", 0)
        val method: Method =
            CustomSelectorActivity::class.java.getDeclaredMethod(
                "onClickNotForUpload",
            )
        method.isAccessible = true
        method.invoke(activity)
        activity.onSelectedImagesChanged(
            ArrayList(arrayListOf(Image(1, "test", Uri.parse("test"), "test", 1))),
            0,
        )
        method.invoke(activity)
    }

    /**
     * Test setOnDataListener Function.
     */
    @Test
    @Throws(Exception::class)
    fun testSetOnDataListener() {
        activity.setOnDataListener(imageFragment)
    }

    /**
     * Test onBackPressed Function.
     */
    @Test
    @Throws(Exception::class)
    fun testOnBackPressed() {
        activity.onBackPressed()
    }

    /**
     * Test onDestroy Function.
     */
    @Test
    @Throws(Exception::class)
    fun testOnDestroy() {
        val method: Method =
            CustomSelectorActivity::class.java.getDeclaredMethod(
                "onDestroy",
            )
        method.isAccessible = true
        method.invoke(activity)
    }

    /**
     * Test displayUploadLimitWarning Function.
     */
    @Test
    @Throws(Exception::class)
    fun testDisplayUploadLimitWarning() {
        val method: Method =
            CustomSelectorActivity::class.java.getDeclaredMethod(
                "displayUploadLimitWarning",
            )
        method.isAccessible = true
        method.invoke(activity)
    }

    /**
     * Logic tests for the upload limit functionality.
     */
    @Test
    @Throws(Exception::class)
    fun testUploadLimit() {
        val overLimit: Field =
            CustomSelectorActivity::class.java.getDeclaredField("uploadLimitExceeded")
        overLimit.isAccessible = true
        val exceededBy: Field =
            CustomSelectorActivity::class.java.getDeclaredField("uploadLimitExceededBy")
        exceededBy.isAccessible = true
        val limit: Field =
            CustomSelectorActivity::class.java.getDeclaredField("uploadLimit")
        limit.isAccessible = true

        // all tests check integration with not for upload marking

        // test with list size limit
        for (i in 1..limit.getInt(activity)) {
            images.add(
                Image(
                    i.toLong(),
                    i.toString(),
                    uri,
                    "abc/abc",
                    1,
                    "bucket1",
                ),
            )
        }
        activity.onFolderClick(1, "test", 0)
        activity.onSelectedImagesChanged(images, 0)
        assertEquals(false, overLimit.getBoolean(activity))
        assertEquals(0, exceededBy.getInt(activity))
        activity.onSelectedImagesChanged(images, 1)
        assertEquals(false, overLimit.getBoolean(activity))
        assertEquals(0, exceededBy.getInt(activity))

        // test with list size limit+1
        images.add(image)
        activity.onSelectedImagesChanged(images, 0)
        assertEquals(true, overLimit.getBoolean(activity))
        assertEquals(1, exceededBy.getInt(activity))
        activity.onSelectedImagesChanged(images, 1)
        assertEquals(true, overLimit.getBoolean(activity))
        assertEquals(1, exceededBy.getInt(activity))

        // test with list size 1
        images.clear()
        images.add(image)
        activity.onSelectedImagesChanged(images, 0)
        assertEquals(false, overLimit.getBoolean(activity))
        assertEquals(0, exceededBy.getInt(activity))
        activity.onSelectedImagesChanged(images, 1)
        assertEquals(false, overLimit.getBoolean(activity))
        assertEquals(0, exceededBy.getInt(activity))
    }
}
