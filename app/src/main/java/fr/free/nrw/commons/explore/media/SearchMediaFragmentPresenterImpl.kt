package fr.free.nrw.commons.explore.media

import fr.free.nrw.commons.Media
import fr.free.nrw.commons.di.MainThreadScheduler
import fr.free.nrw.commons.explore.paging.BasePagingPresenter
import fr.free.nrw.commons.explore.paging.PagingContract
import io.reactivex.Scheduler
import javax.inject.Inject

interface SearchMediaFragmentPresenter : PagingContract.Presenter<Media>

class SearchMediaFragmentPresenterImpl
    @Inject
    constructor(
        @MainThreadScheduler mainThreadScheduler: Scheduler,
        dataSourceFactory: PageableMediaDataSource,
    ) : BasePagingPresenter<Media>(mainThreadScheduler, dataSourceFactory),
        SearchMediaFragmentPresenter
