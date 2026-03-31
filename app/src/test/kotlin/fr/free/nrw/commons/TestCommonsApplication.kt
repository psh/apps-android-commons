package fr.free.nrw.commons

import android.content.Context

class TestCommonsApplication : CommonsApplication() {

    override fun onCreate() {
        super.onCreate()
        setTheme(R.style.Theme_AppCompat)
        context = applicationContext
    }

    companion object {
        private var context: Context? = null

        fun getContext(): Context? = context
    }
}
