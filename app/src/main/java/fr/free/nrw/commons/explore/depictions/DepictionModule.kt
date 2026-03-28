package fr.free.nrw.commons.explore.depictions

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import fr.free.nrw.commons.explore.depictions.child.ChildDepictionsPresenter
import fr.free.nrw.commons.explore.depictions.child.ChildDepictionsPresenterImpl
import fr.free.nrw.commons.explore.depictions.media.DepictedImagesPresenter
import fr.free.nrw.commons.explore.depictions.media.DepictedImagesPresenterImpl
import fr.free.nrw.commons.explore.depictions.parent.ParentDepictionsPresenter
import fr.free.nrw.commons.explore.depictions.parent.ParentDepictionsPresenterImpl

/**
 * The Dagger Module for explore:depictions related presenters and (some other objects maybe in future)
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class DepictionModule {
    @Binds
    abstract fun bindsParentDepictionPresenter(presenter: ParentDepictionsPresenterImpl): ParentDepictionsPresenter

    @Binds
    abstract fun bindsChildDepictionPresenter(presenter: ChildDepictionsPresenterImpl): ChildDepictionsPresenter

    @Binds
    abstract fun bindsDepictedImagesContractPresenter(presenter: DepictedImagesPresenterImpl): DepictedImagesPresenter
}
