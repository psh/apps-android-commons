package fr.free.nrw.commons.recentlanguages

import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.test.core.app.ApplicationProvider
import fr.free.nrw.commons.R
import fr.free.nrw.commons.TestCommonsApplication
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [21], application = TestCommonsApplication::class)
@LooperMode(LooperMode.Mode.PAUSED)
class RecentLanguagesAdapterUnitTest {

    private val languages: List<Language> = listOf(
        Language("English", "en"),
        Language("Bengali", "bn")
    )
    private val adapter = RecentLanguagesAdapter(
        ApplicationProvider.getApplicationContext(), languages, hashMapOf(1 to "en")
    )

    @Mock
    private lateinit var viewGroup: ViewGroup

    @Mock
    private lateinit var convertView: View

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun testIsEnabled() {
        adapter.selectedLangCode = "es"
        assertEquals(adapter.isEnabled(0), false)
    }

    @Test
    fun testGetCount() {
        assertEquals(adapter.count, languages.size)
    }

    @Test
    fun testGetLanguageName() {
        assertEquals(adapter.getLanguageName(0), languages[0].languageName)
    }

    @Test
    fun testGetViewCorrectlyRecyclesWhenNeeded() {
        val view = adapter.getView(0, convertView, viewGroup)
        assertEquals(view, convertView)
    }

    @Test
    fun testGetViewConfigresNewViews() {
        val parent = LinearLayout(ApplicationProvider.getApplicationContext())
        val view = adapter.getView(0, null, parent)
        assertEquals("English [en]", view.findViewById<TextView>(R.id.tv_language).text)
    }

    @Test
    fun testGetLanguageCode() {
        val languageCode = languages[0].languageCode
        assertEquals(languageCode, adapter.getLanguageCode(0))
    }
}