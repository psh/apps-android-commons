package fr.free.nrw.commons.explore.media

import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Displays the image search screen.
 */
@AndroidEntryPoint
class SearchMediaFragment : PageableMediaFragment() {
    @Inject
    lateinit var presenter: SearchMediaFragmentPresenter

    override val injectedPresenter
        get() = presenter
}
