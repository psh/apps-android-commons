package fr.free.nrw.commons.explore.categories

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import fr.free.nrw.commons.explore.categories.media.CategoryMediaPresenter
import fr.free.nrw.commons.explore.categories.media.CategoryMediaPresenterImpl
import fr.free.nrw.commons.explore.categories.parent.ParentCategoriesPresenter
import fr.free.nrw.commons.explore.categories.parent.ParentCategoriesPresenterImpl
import fr.free.nrw.commons.explore.categories.sub.SubCategoriesPresenter
import fr.free.nrw.commons.explore.categories.sub.SubCategoriesPresenterImpl

@Module
@InstallIn(SingletonComponent::class)
abstract class CategoriesModule {
    @Binds
    abstract fun bindsCategoryMediaPresenter(presenter: CategoryMediaPresenterImpl): CategoryMediaPresenter

    @Binds
    abstract fun bindsSubCategoriesPresenter(presenter: SubCategoriesPresenterImpl): SubCategoriesPresenter

    @Binds
    abstract fun bindsParentCategoriesPresenter(presenter: ParentCategoriesPresenterImpl): ParentCategoriesPresenter
}
