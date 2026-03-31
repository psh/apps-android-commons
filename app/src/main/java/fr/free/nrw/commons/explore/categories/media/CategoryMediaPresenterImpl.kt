package fr.free.nrw.commons.explore.categories.media

import fr.free.nrw.commons.Media
import fr.free.nrw.commons.di.MainThreadScheduler
import fr.free.nrw.commons.explore.paging.BasePagingPresenter
import fr.free.nrw.commons.explore.paging.PagingContract
import io.reactivex.Scheduler
import javax.inject.Inject

interface CategoryMediaPresenter : PagingContract.Presenter<Media>

/**
 * Presenter for DepictedImagesFragment
 */
class CategoryMediaPresenterImpl
    @Inject
    constructor(
        @MainThreadScheduler mainThreadScheduler: Scheduler,
        dataSourceFactory: PageableCategoriesMediaDataSource,
    ) : BasePagingPresenter<Media>(mainThreadScheduler, dataSourceFactory),
        CategoryMediaPresenter
