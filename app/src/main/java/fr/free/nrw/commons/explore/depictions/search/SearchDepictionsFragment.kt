package fr.free.nrw.commons.explore.depictions.search

import dagger.hilt.android.AndroidEntryPoint
import fr.free.nrw.commons.R
import fr.free.nrw.commons.explore.depictions.PageableDepictionsFragment
import javax.inject.Inject

/**
 * Display depictions in search fragment
 */
@AndroidEntryPoint
class SearchDepictionsFragment : PageableDepictionsFragment() {
    @Inject
    lateinit var presenter: SearchDepictionsFragmentPresenter

    override val injectedPresenter
        get() = presenter

    override fun getEmptyText(query: String) = getString(R.string.depictions_not_found, query)
}
