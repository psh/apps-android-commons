package fr.free.nrw.commons.explore

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import fr.free.nrw.commons.explore.categories.search.SearchCategoriesFragmentPresenter
import fr.free.nrw.commons.explore.categories.search.SearchCategoriesFragmentPresenterImpl
import fr.free.nrw.commons.explore.depictions.search.SearchDepictionsFragmentPresenter
import fr.free.nrw.commons.explore.depictions.search.SearchDepictionsFragmentPresenterImpl
import fr.free.nrw.commons.explore.media.SearchMediaFragmentPresenter
import fr.free.nrw.commons.explore.media.SearchMediaFragmentPresenterImpl

/**
 * The Dagger Module for explore:depictions related presenters and (some other objects maybe in future)
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class SearchModule {
    @Binds
    abstract fun bindsSearchDepictionsFragmentPresenter(presenter: SearchDepictionsFragmentPresenterImpl): SearchDepictionsFragmentPresenter

    @Binds
    abstract fun bindsSearchCategoriesFragmentPresenter(presenter: SearchCategoriesFragmentPresenterImpl): SearchCategoriesFragmentPresenter

    @Binds
    abstract fun bindsSearchMediaFragmentPresenter(presenter: SearchMediaFragmentPresenterImpl): SearchMediaFragmentPresenter
}
