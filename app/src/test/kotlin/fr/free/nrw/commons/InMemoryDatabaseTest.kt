package fr.free.nrw.commons

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import fr.free.nrw.commons.data.DBOpenHelper
import fr.free.nrw.commons.db.AppDatabase
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [21], application = TestCommonsApplication::class)
abstract class InMemoryDatabaseTest {
    val database: AppDatabase by lazy {
        Room.inMemoryDatabaseBuilder(
            context = ApplicationProvider.getApplicationContext(),
            klass = AppDatabase::class.java
        ).build()
    }

    val openHelper = DBOpenHelper(database.openHelper)
}