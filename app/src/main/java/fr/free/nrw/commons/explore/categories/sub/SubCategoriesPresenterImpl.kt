package fr.free.nrw.commons.explore.categories.sub

import fr.free.nrw.commons.di.MainThreadScheduler
import fr.free.nrw.commons.explore.paging.BasePagingPresenter
import fr.free.nrw.commons.explore.paging.PagingContract
import io.reactivex.Scheduler
import javax.inject.Inject

interface SubCategoriesPresenter : PagingContract.Presenter<String>

class SubCategoriesPresenterImpl
    @Inject
    constructor(
        @MainThreadScheduler mainThreadScheduler: Scheduler,
        dataSourceFactory: PageableSubCategoriesDataSource,
    ) : BasePagingPresenter<String>(mainThreadScheduler, dataSourceFactory),
        SubCategoriesPresenter
