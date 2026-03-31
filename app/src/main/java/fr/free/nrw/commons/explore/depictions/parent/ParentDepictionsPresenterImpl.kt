package fr.free.nrw.commons.explore.depictions.parent

import fr.free.nrw.commons.di.MainThreadScheduler
import fr.free.nrw.commons.explore.paging.BasePagingPresenter
import fr.free.nrw.commons.explore.paging.PagingContract
import fr.free.nrw.commons.upload.structure.depictions.DepictedItem
import io.reactivex.Scheduler
import javax.inject.Inject

interface ParentDepictionsPresenter : PagingContract.Presenter<DepictedItem>

class ParentDepictionsPresenterImpl
    @Inject
    constructor(
        @MainThreadScheduler mainThreadScheduler: Scheduler,
        dataSourceFactory: PageableParentDepictionsDataSource,
    ) : BasePagingPresenter<DepictedItem>(mainThreadScheduler, dataSourceFactory),
        ParentDepictionsPresenter
