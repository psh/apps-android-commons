package fr.free.nrw.commons.category

import android.content.ContentProviderClient
import android.content.ContentValues
import android.database.Cursor
import android.database.MatrixCursor
import android.database.sqlite.SQLiteDatabase
import android.os.RemoteException
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.inOrder
import com.nhaarman.mockitokotlin2.isA
import com.nhaarman.mockitokotlin2.isNull
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import fr.free.nrw.commons.TestCommonsApplication
import fr.free.nrw.commons.category.CategoryDao.Table.ALL_FIELDS
import fr.free.nrw.commons.category.CategoryDao.Table.COLUMN_DESCRIPTION
import fr.free.nrw.commons.category.CategoryDao.Table.COLUMN_ID
import fr.free.nrw.commons.category.CategoryDao.Table.COLUMN_LAST_USED
import fr.free.nrw.commons.category.CategoryDao.Table.COLUMN_NAME
import fr.free.nrw.commons.category.CategoryDao.Table.COLUMN_THUMBNAIL
import fr.free.nrw.commons.category.CategoryDao.Table.COLUMN_TIMES_USED
import fr.free.nrw.commons.category.CategoryDao.Table.CREATE_TABLE_STATEMENT
import fr.free.nrw.commons.category.CategoryDao.Table.DROP_TABLE_STATEMENT
import fr.free.nrw.commons.category.CategoryDao.Table.onCreate
import fr.free.nrw.commons.category.CategoryDao.Table.onDelete
import fr.free.nrw.commons.category.CategoryDao.Table.onUpdate
import fr.free.nrw.commons.explore.recentsearches.RecentSearchesContentProvider.Companion.uriForId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.verifyNoInteractions
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.Date

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [21], application = TestCommonsApplication::class)
class CategoryDaoTest {
    private val columns =
        arrayOf(
            COLUMN_ID,
            COLUMN_NAME,
            COLUMN_DESCRIPTION,
            COLUMN_THUMBNAIL,
            COLUMN_LAST_USED,
            COLUMN_TIMES_USED,
        )
    private val client: ContentProviderClient = mock()
    private val database: SQLiteDatabase = mock()
    private val captor = argumentCaptor<ContentValues>()
    private val queryCaptor = argumentCaptor<Array<String>>()

    private lateinit var testObject: CategoryDao

    @Before
    fun setUp() {
        testObject = CategoryDao { client }
    }

    @Test
    fun createTable() {
        onCreate(database)
        verify(database).execSQL(CREATE_TABLE_STATEMENT)
    }

    @Test
    fun deleteTable() {
        onDelete(database)
        inOrder(database) {
            verify(database).execSQL(DROP_TABLE_STATEMENT)
            verify(database).execSQL(CREATE_TABLE_STATEMENT)
        }
    }

    @Test
    fun migrateTableVersionFrom_v1_to_v2() {
        onUpdate(database, 1, 2)
        // Table didn't exist before v5
        verifyNoInteractions(database)
    }

    @Test
    fun migrateTableVersionFrom_v2_to_v3() {
        onUpdate(database, 2, 3)
        // Table didn't exist before v5
        verifyNoInteractions(database)
    }

    @Test
    fun migrateTableVersionFrom_v3_to_v4() {
        onUpdate(database, 3, 4)
        // Table didn't exist before v5
        verifyNoInteractions(database)
    }

    @Test
    fun migrateTableVersionFrom_v4_to_v5() {
        onUpdate(database, 4, 5)
        verify(database).execSQL(CREATE_TABLE_STATEMENT)
    }

    @Test
    fun migrateTableVersionFrom_v5_to_v6() {
        onUpdate(database, 5, 6)
        // Table didn't change in version 6
        verifyNoInteractions(database)
    }

    @Test
    fun migrateTableVersionFrom_v6_to_v7() {
        onUpdate(database, 6, 7)
        // Table didn't change in version 7
        verifyNoInteractions(database)
    }

    @Test
    fun migrateTableVersionFrom_v7_to_v8() {
        onUpdate(database, 7, 8)
        // Table didn't change in version 8
        verifyNoInteractions(database)
    }

    @Test
    fun createFromCursor() {
        createCursor(1).let { cursor ->
            cursor.moveToFirst()
            testObject.fromCursor(cursor).let {
                assertEquals(CategoryContentProvider.uriForId(1), it.contentUri)
                assertEquals("showImageWithItem", it.name)
                assertEquals(123L, it.lastUsed?.time)
                assertEquals(2, it.timesUsed)
            }
        }
    }

    @Test
    fun saveExistingCategory() {
        createCursor(1).let {
            val category = testObject.fromCursor(it.apply { moveToFirst() })

            testObject.save(category)

            verify(client).update(
                eq(category.contentUri)!!,
                captor.capture(),
                isNull(),
                isNull()
            )
            captor.firstValue.let { cv ->
                assertEquals(5, cv.size())
                assertEquals(category.name, cv.getAsString(COLUMN_NAME))
                assertEquals(category.description, cv.getAsString(COLUMN_DESCRIPTION))
                assertEquals(category.thumbnail, cv.getAsString(COLUMN_THUMBNAIL))
                assertEquals(category.lastUsed?.time, cv.getAsLong(COLUMN_LAST_USED))
                assertEquals(category.timesUsed, cv.getAsInteger(COLUMN_TIMES_USED))
            }
        }
    }

    @Test
    fun saveNewCategory() {
        val contentUri = uriForId(111)
        whenever(client.insert(isA(), isA())).thenReturn(contentUri)
        val category =
            Category(
                null,
                "showImageWithItem",
                "description",
                "image",
                Date(234L),
                1,
            )

        testObject.save(category)

        verify(client).insert(eq(CategoryContentProvider.BASE_URI), captor.capture())
        captor.firstValue.let { cv ->
            assertEquals(5, cv.size())
            assertEquals(category.name, cv.getAsString(COLUMN_NAME))
            assertEquals(category.description, cv.getAsString(COLUMN_DESCRIPTION))
            assertEquals(category.thumbnail, cv.getAsString(COLUMN_THUMBNAIL))
            assertEquals(category.lastUsed?.time, cv.getAsLong(COLUMN_LAST_USED))
            assertEquals(category.timesUsed, cv.getAsInteger(COLUMN_TIMES_USED))
            assertEquals(contentUri, category.contentUri)
        }
    }

    @Test(expected = RuntimeException::class)
    fun testSaveTranslatesRemoteExceptions() {
        whenever(client.insert(isA(), isA())).thenThrow(RemoteException(""))
        testObject.save(Category())
    }

    @Test
    fun whenTheresNoDataFindReturnsNull_nullCursor() {
        whenever(client.query(any(), any(), any(), any(), any())).thenReturn(null)
        assertNull(testObject.find("showImageWithItem"))
    }

    @Test
    fun whenTheresNoDataFindReturnsNull_emptyCursor() {
        whenever(client.query(any(), any(), any(), any(), any())).thenReturn(createCursor(0))
        assertNull(testObject.find("showImageWithItem"))
    }

    @Test
    fun cursorsAreClosedAfterUse() {
        val mockCursor: Cursor = mock()
        whenever(client.query(any(), any(), any(), any(), anyOrNull())).thenReturn(mockCursor)
        whenever(mockCursor.moveToFirst()).thenReturn(false)

        testObject.find("showImageWithItem")

        verify(mockCursor).close()
    }

    @Test
    fun findCategory() {
        whenever(client.query(any(), any(), any(), any(), anyOrNull())).thenReturn(createCursor(1))

        val category = testObject.find("showImageWithItem")
        assertNotNull(category)

        assertEquals(CategoryContentProvider.uriForId(1), category?.contentUri)
        assertEquals("showImageWithItem", category?.name)
        assertEquals("description", category?.description)
        assertEquals("image", category?.thumbnail)
        assertEquals(123L, category?.lastUsed?.time)
        assertEquals(2, category?.timesUsed)

        verify(client).query(
            eq(CategoryContentProvider.BASE_URI),
            eq(ALL_FIELDS),
            eq("$COLUMN_NAME=?"),
            queryCaptor.capture(),
            isNull(),
        )
        assertEquals("showImageWithItem", queryCaptor.firstValue[0])
    }

    @Test(expected = RuntimeException::class)
    fun findCategoryTranslatesExceptions() {
        whenever(client.query(any(), any(), any(), any(), anyOrNull())).thenThrow(RemoteException(""))
        testObject.find("showImageWithItem")
    }

    @Test(expected = RuntimeException::class)
    fun recentCategoriesTranslatesExceptions() {
        whenever(client.query(any(), any(), anyOrNull(), any(), any())).thenThrow(RemoteException(""))
        testObject.recentCategories(1)
    }

    @Test
    fun recentCategoriesReturnsEmptyList_nullCursor() {
        whenever(client.query(any(), any(), anyOrNull(), any(), any())).thenReturn(null)
        assertTrue(testObject.recentCategories(1).isEmpty())
    }

    @Test
    fun recentCategoriesReturnsEmptyList_emptyCursor() {
        whenever(client.query(any(), any(), any(), any(), any())).thenReturn(createCursor(0))
        assertTrue(testObject.recentCategories(1).isEmpty())
    }

    @Test
    fun cursorsAreClosedAfterRecentCategoriesQuery() {
        val mockCursor: Cursor = mock()
        whenever(client.query(any(), any(), anyOrNull(), any(), any())).thenReturn(mockCursor)
        whenever(mockCursor.moveToFirst()).thenReturn(false)

        testObject.recentCategories(1)

        verify(mockCursor).close()
    }

    @Test
    fun recentCategoriesReturnsLessThanLimit() {
        whenever(client.query(any(), any(), anyOrNull(), any(), any())).thenReturn(createCursor(1))

        val result = testObject.recentCategories(10)

        assertEquals(1, result.size)
        assertEquals("showImageWithItem", result[0].name)

        verify(client).query(
            eq(CategoryContentProvider.BASE_URI),
            eq(ALL_FIELDS),
            isNull(),
            queryCaptor.capture(),
            eq("$COLUMN_LAST_USED DESC"),
        )
        assertEquals(0, queryCaptor.firstValue.size)
    }

    @Test
    fun recentCategoriesHonorsLimit() {
        whenever(client.query(any(), any(), anyOrNull(), any(), any())).thenReturn(createCursor(10))

        val result = testObject.recentCategories(5)

        assertEquals(5, result.size)
    }

    private fun createCursor(rowCount: Int) =
        MatrixCursor(columns, rowCount).apply {
            for (i in 0 until rowCount) {
                addRow(listOf("1", "showImageWithItem", "description", "image", "123", "2"))
            }
        }
}
