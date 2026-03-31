package fr.free.nrw.commons.bookmarks.items

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteQuery
import com.nhaarman.mockitokotlin2.any
import fr.free.nrw.commons.TestCommonsApplication
import fr.free.nrw.commons.data.DBOpenHelper
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import org.powermock.reflect.Whitebox
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [23], application = TestCommonsApplication::class)
class BookmarkItemsContentProviderUnitTest {
    private lateinit var contentProvider: BookmarkItemsContentProvider

    @Mock
    lateinit var dbOpenHelper: DBOpenHelper

    @Mock
    lateinit var database: SupportSQLiteDatabase

    @Mock
    lateinit var context: Context

    @Mock
    lateinit var contentResolver: ContentResolver

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        contentProvider = BookmarkItemsContentProvider()
        Whitebox.setInternalState(contentProvider, "dbOpenHelper", dbOpenHelper)
        Whitebox.setInternalState(contentProvider, "mContext", context)
        `when`(dbOpenHelper.writableDatabase).thenReturn(database)
        `when`(context.contentResolver).thenReturn(contentResolver)
    }

    @Test
    fun testGetType() {
        contentProvider.getType(mock(Uri::class.java))
    }

    @Test
    fun testQuery() {
        `when`(database.query(any<SupportSQLiteQuery>())).thenReturn(mock(android.database.Cursor::class.java))
        contentProvider.query(mock(Uri::class.java), null, null, null, null)
    }

    @Test
    fun testInsert() {
        `when`(database.insert(any(), any(), any())).thenReturn(1L)
        contentProvider.insert(BookmarkItemsContentProvider.BASE_URI, mock(android.content.ContentValues::class.java))
    }

    @Test
    fun testUpdate() {
        `when`(database.update(any(), any(), any(), any(), any())).thenReturn(1)
        val uri = BookmarkItemsContentProvider.uriForName("1")
        contentProvider.update(uri, mock(android.content.ContentValues::class.java), null, null)
    }

    @Test
    fun testDelete() {
        `when`(database.delete(any(), any(), any())).thenReturn(1)
        val uri = BookmarkItemsContentProvider.uriForName("1")
        contentProvider.delete(uri, null, null)
    }
}
