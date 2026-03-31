package fr.free.nrw.commons.explore.depictions.child

import fr.free.nrw.commons.di.MainThreadScheduler
import fr.free.nrw.commons.explore.paging.BasePagingPresenter
import fr.free.nrw.commons.explore.paging.PagingContract
import fr.free.nrw.commons.upload.structure.depictions.DepictedItem
import io.reactivex.Scheduler
import javax.inject.Inject

interface ChildDepictionsPresenter : PagingContract.Presenter<DepictedItem>

class ChildDepictionsPresenterImpl
    @Inject
    constructor(
        @MainThreadScheduler mainThreadScheduler: Scheduler,
        dataSourceFactory: PageableChildDepictionsDataSource,
    ) : BasePagingPresenter<DepictedItem>(mainThreadScheduler, dataSourceFactory),
        ChildDepictionsPresenter
