package fr.free.nrw.commons.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import fr.free.nrw.commons.bookmarks.items.BookmarkItemsContentProvider

/**
 * This Class Represents the Module for dependency injection (using dagger)
 * so, if a developer needs to add a new ContentProvider to the commons app
 * then that must be mentioned here to inject the dependencies
 */
@Module
@Suppress("unused")
abstract class ContentProviderBuilderModule {
    @ContributesAndroidInjector
    abstract fun bindBookmarkItemContentProvider(): BookmarkItemsContentProvider
}
