package fr.free.nrw.commons.di

import android.content.ContentProvider
import fr.free.nrw.commons.data.DBOpenHelper
import fr.free.nrw.commons.di.ApplicationlessInjection.Companion.getInstance
import javax.inject.Inject

abstract class CommonsDaggerContentProvider : ContentProvider() {
    @Inject
    @JvmField
    var dbOpenHelper: DBOpenHelper? = null

    override fun onCreate(): Boolean {
        if (dbOpenHelper == null) {
            inject()
        }
        return true
    }

    fun requireDbOpenHelper(): DBOpenHelper = dbOpenHelper!!

    fun requireDb() = requireDbOpenHelper().writableDatabase

    private fun inject() {
        val injection = getInstance(context!!)

        val serviceInjector = injection.contentProviderInjector()
            ?: throw NullPointerException("ApplicationlessInjection.contentProviderInjector() returned null")

        serviceInjector.inject(this)
    }
}
