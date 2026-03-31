package fr.free.nrw.commons.explore.categories.parent

import fr.free.nrw.commons.di.MainThreadScheduler
import fr.free.nrw.commons.explore.paging.BasePagingPresenter
import fr.free.nrw.commons.explore.paging.PagingContract
import io.reactivex.Scheduler
import javax.inject.Inject

interface ParentCategoriesPresenter : PagingContract.Presenter<String>

class ParentCategoriesPresenterImpl
    @Inject
    constructor(
        @MainThreadScheduler mainThreadScheduler: Scheduler,
        dataSourceFactory: PageableParentCategoriesDataSource,
    ) : BasePagingPresenter<String>(mainThreadScheduler, dataSourceFactory),
        ParentCategoriesPresenter
