package fr.free.nrw.commons.contributions

import dagger.Binds
import dagger.Module

/**
 * The Dagger Module for contributions related presenters and (some other objects maybe in future)
 */
@Module
abstract class ContributionsModule {
    @Binds
    abstract fun bindsContibutionsPresenter(
        presenter: ContributionsPresenter?
    ): ContributionsContract.UserActionListener?
}
