package fr.free.nrw.commons.explore.categories.search

import fr.free.nrw.commons.di.MainThreadScheduler
import fr.free.nrw.commons.explore.paging.BasePagingPresenter
import fr.free.nrw.commons.explore.paging.PagingContract
import io.reactivex.Scheduler
import javax.inject.Inject

interface SearchCategoriesFragmentPresenter : PagingContract.Presenter<String>

class SearchCategoriesFragmentPresenterImpl
    @Inject
    constructor(
        @MainThreadScheduler mainThreadScheduler: Scheduler,
        dataSourceFactory: PageableSearchCategoriesDataSource,
    ) : BasePagingPresenter<String>(mainThreadScheduler, dataSourceFactory),
        SearchCategoriesFragmentPresenter
