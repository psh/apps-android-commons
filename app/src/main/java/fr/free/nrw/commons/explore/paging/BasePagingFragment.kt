package fr.free.nrw.commons.explore.paging

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.MergeAdapter
import androidx.recyclerview.widget.RecyclerView
import fr.free.nrw.commons.R
import fr.free.nrw.commons.di.CommonsDaggerSupportFragment
import fr.free.nrw.commons.utils.ViewUtil


abstract class BasePagingFragment<T> : CommonsDaggerSupportFragment(),
    PagingContract.View<T> {

    abstract val pagedListAdapter: PagedListAdapter<T, *>
    abstract val injectedPresenter: PagingContract.Presenter<T>
    abstract val errorTextId: Int
    private val loadingAdapter by lazy { FooterAdapter { injectedPresenter.retryFailedRequest() } }
    private val mergeAdapter by lazy { MergeAdapter(pagedListAdapter, loadingAdapter) }
    private var searchResults: LiveData<PagedList<T>>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = inflater.inflate(R.layout.fragment_search_paginated, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<RecyclerView>(R.id.paginatedSearchResultsList).apply {
            layoutManager = GridLayoutManager(context, if (isPortrait) 1 else 2)
            adapter = mergeAdapter
        }
        injectedPresenter.listFooterData.observe(
            viewLifecycleOwner,
            Observer(loadingAdapter::submitList)
        )
    }

    /**
     * Called on configuration change, update the spanCount according to the orientation state.
     */
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        requireView().findViewById<RecyclerView>(R.id.paginatedSearchResultsList).apply {
            layoutManager = GridLayoutManager(context, if (isPortrait) 1 else 2)
        }
    }

    override fun observePagingResults(searchResults: LiveData<PagedList<T>>) {
        this.searchResults?.removeObservers(viewLifecycleOwner)
        this.searchResults = searchResults
        searchResults.observe(viewLifecycleOwner, Observer {
            pagedListAdapter.submitList(it)
        })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        injectedPresenter.onAttachView(this)
    }

    override fun onDetach() {
        super.onDetach()
        injectedPresenter.onDetachView()
    }

    override fun hideInitialLoadProgress() {
        requireView().findViewById<View>(R.id.paginatedSearchInitialLoadProgress).visibility = GONE
    }

    override fun showInitialLoadInProgress() {
        requireView().findViewById<View>(R.id.paginatedSearchInitialLoadProgress).visibility =
            VISIBLE
    }

    override fun showSnackbar() {
        ViewUtil.showShortSnackbar(
            requireView().findViewById<RecyclerView>(R.id.paginatedSearchResultsList),
            errorTextId
        )
    }

    fun onQueryUpdated(query: String) {
        injectedPresenter.onQueryUpdated(query)
    }

    override fun showEmptyText(query: String) {
        with(requireView().findViewById<TextView>(R.id.contentNotFound)) {
            text = getEmptyText(query)
            visibility = VISIBLE
        }
    }

    abstract fun getEmptyText(query: String): String

    override fun hideEmptyText() {
        requireView().findViewById<TextView>(R.id.contentNotFound).visibility = GONE
    }
}

private val Fragment.isPortrait get() = orientation == Configuration.ORIENTATION_PORTRAIT

private val Fragment.orientation get() = activity!!.resources.configuration.orientation
