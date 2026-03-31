package fr.free.nrw.commons.explore.depictions.media

import fr.free.nrw.commons.Media
import fr.free.nrw.commons.di.MainThreadScheduler
import fr.free.nrw.commons.explore.paging.BasePagingPresenter
import fr.free.nrw.commons.explore.paging.PagingContract
import io.reactivex.Scheduler
import javax.inject.Inject

interface DepictedImagesPresenter : PagingContract.Presenter<Media>

/**
 * Presenter for DepictedImagesFragment
 */
class DepictedImagesPresenterImpl
    @Inject
    constructor(
        @MainThreadScheduler mainThreadScheduler: Scheduler,
        dataSourceFactory: PageableDepictedMediaDataSource,
    ) : BasePagingPresenter<Media>(mainThreadScheduler, dataSourceFactory),
        DepictedImagesPresenter
