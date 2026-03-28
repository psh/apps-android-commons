package fr.free.nrw.commons.di

import android.content.ContentProvider
import androidx.sqlite.db.SupportSQLiteDatabase
import fr.free.nrw.commons.data.DBOpenHelper
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.EntryPointAccessors
import javax.inject.Inject

abstract class CommonsDaggerContentProvider : ContentProvider() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface ContentProviderEntryPoint {
        fun getDBOpenHelper(): DBOpenHelper
    }

    var dbOpenHelper: DBOpenHelper? = null

    override fun onCreate(): Boolean {
        inject()
        return true
    }

    fun requireDbOpenHelper(): DBOpenHelper = dbOpenHelper!!

    fun requireDb(): SupportSQLiteDatabase = requireDbOpenHelper().writableDatabase!!

    private fun inject() {
        val entryPoint = EntryPointAccessors.fromApplication(context!!.applicationContext, ContentProviderEntryPoint::class.java)
        dbOpenHelper = entryPoint.getDBOpenHelper()
    }
}
