package fr.free.nrw.commons.bookmarks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import fr.free.nrw.commons.Media
import fr.free.nrw.commons.R
import fr.free.nrw.commons.bookmarks.category.BookmarkCategoriesFragment
import fr.free.nrw.commons.bookmarks.items.BookmarkItemsFragment
import fr.free.nrw.commons.bookmarks.locations.BookmarkLocationsFragment
import fr.free.nrw.commons.bookmarks.pictures.BookmarkPicturesFragment
import fr.free.nrw.commons.category.CategoryImagesCallback
import fr.free.nrw.commons.category.GridViewAdapter
import fr.free.nrw.commons.contributions.MainActivity
import fr.free.nrw.commons.databinding.FragmentFeaturedRootBinding
import fr.free.nrw.commons.di.CommonsDaggerSupportFragment
import fr.free.nrw.commons.media.MediaDetailPagerFragment
import fr.free.nrw.commons.media.MediaDetailPagerFragment.MediaDetailProvider
import fr.free.nrw.commons.navtab.NavTab
import timber.log.Timber

class BookmarkListRootFragment : CommonsDaggerSupportFragment,
    FragmentManager.OnBackStackChangedListener,
    MediaDetailProvider, OnItemClickListener, CategoryImagesCallback {
    private var mediaDetails: MediaDetailPagerFragment? = null
    private var bookmarksPagerAdapter: BookmarksPagerAdapter? = null
    var listFragment: Fragment? = null

    var binding: FragmentFeaturedRootBinding? = null

    constructor()

    constructor(bundle: Bundle, bookmarksPagerAdapter: BookmarksPagerAdapter?) {
        val title = bundle.getString("categoryName")
        val order = bundle.getInt("order")
        val orderItem = bundle.getInt("orderItem")

        when (order) {
            0 -> listFragment = BookmarkPicturesFragment()
            1 -> listFragment = BookmarkLocationsFragment()
            3 -> listFragment = BookmarkCategoriesFragment()
        }
        if (orderItem == 2) {
            listFragment = BookmarkItemsFragment()
        }

        listFragment!!.arguments = bundleOf("categoryName" to title)
        this.bookmarksPagerAdapter = bookmarksPagerAdapter
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)
        binding = FragmentFeaturedRootBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState == null) {
            setFragment(listFragment!!, mediaDetails)
        }
    }

    fun setFragment(fragment: Fragment, otherFragment: Fragment?) {
        if (fragment.isAdded && otherFragment != null) {
            childFragmentManager
                .beginTransaction()
                .hide(otherFragment)
                .show(fragment)
                .addToBackStack("CONTRIBUTION_LIST_FRAGMENT_TAG")
                .commit()
            childFragmentManager.executePendingTransactions()
        } else if (fragment.isAdded && otherFragment == null) {
            childFragmentManager
                .beginTransaction()
                .show(fragment)
                .addToBackStack("CONTRIBUTION_LIST_FRAGMENT_TAG")
                .commit()
            childFragmentManager.executePendingTransactions()
        } else if (!fragment.isAdded && otherFragment != null) {
            childFragmentManager
                .beginTransaction()
                .hide(otherFragment)
                .add(R.id.explore_container, fragment)
                .addToBackStack("CONTRIBUTION_LIST_FRAGMENT_TAG")
                .commit()
            childFragmentManager.executePendingTransactions()
        } else if (!fragment.isAdded) {
            childFragmentManager
                .beginTransaction()
                .replace(R.id.explore_container, fragment)
                .addToBackStack("CONTRIBUTION_LIST_FRAGMENT_TAG")
                .commit()
            childFragmentManager.executePendingTransactions()
        }
    }

    fun removeFragment(fragment: Fragment) {
        childFragmentManager
            .beginTransaction()
            .remove(fragment)
            .commit()
        childFragmentManager.executePendingTransactions()
    }

    override fun onMediaClicked(position: Int) {
        Timber.d("on media clicked")
    }

    /**
     * This method is called mediaDetailPagerFragment. It returns the Media Object at that Index
     *
     * @param i It is the index of which media object is to be returned which is same as current
     * index of viewPager.
     * @return Media Object
     */
    override fun getMediaAtPosition(i: Int): Media? {
        return if (bookmarksPagerAdapter!!.mediaAdapter == null) {
            // not yet ready to return data
            null
        } else {
            bookmarksPagerAdapter!!.mediaAdapter!!.getItem(i) as Media
        }
    }

    /**
     * This method is called on from getCount of MediaDetailPagerFragment The viewpager will contain
     * same number of media items as that of media elements in adapter.
     *
     * @return Total Media count in the adapter
     */
    override fun getTotalMediaCount(): Int {
        if (bookmarksPagerAdapter!!.mediaAdapter == null) {
            return 0
        }
        return bookmarksPagerAdapter!!.mediaAdapter!!.count
    }

    override fun getContributionStateAt(position: Int): Int? = null

    /**
     * Reload media detail fragment once media is nominated
     *
     * @param index item position that has been nominated
     */
    override fun refreshNominatedMedia(index: Int) {
        if (mediaDetails != null && !listFragment!!.isVisible) {
            removeFragment(mediaDetails!!)
            mediaDetails = MediaDetailPagerFragment.newInstance(false, true)
            (parentFragment as BookmarkFragment).setScroll(false)
            setFragment(mediaDetails!!, listFragment)
            mediaDetails!!.showImage(index)
        }
    }

    /**
     * This method is called on success of API call for featured images or mobile uploads. The
     * viewpager will notified that number of items have changed.
     */
    override fun viewPagerNotifyDataSetChanged() {
        if (mediaDetails != null) {
            mediaDetails!!.notifyDataSetChanged()
        }
    }

    fun backPressed(): Boolean {
        //check mediaDetailPage fragment is not null then we check mediaDetail.is Visible or not to avoid NullPointerException
        if (mediaDetails != null) {
            if (mediaDetails!!.isVisible) {
                // todo add get list fragment
                (parentFragment as BookmarkFragment).setupTabLayout()
                val removed = mediaDetails!!.removedItems
                removeFragment(mediaDetails!!)
                (parentFragment as BookmarkFragment).setScroll(true)
                setFragment(listFragment!!, mediaDetails)
                (activity as MainActivity).showTabs()
                if (listFragment is BookmarkPicturesFragment) {
                    val adapter = ((listFragment as BookmarkPicturesFragment)
                        .getAdapter() as GridViewAdapter?)
                    val i: Iterator<*> = removed.iterator()
                    while (i.hasNext()) {
                        adapter!!.remove(adapter.getItem(i.next() as Int))
                    }
                    mediaDetails!!.clearRemoved()
                }
            } else {
                moveToContributionsFragment()
            }
        } else {
            moveToContributionsFragment()
        }
        // notify mediaDetails did not handled the backPressed further actions required.
        return false
    }

    private fun moveToContributionsFragment() {
        (activity as MainActivity).setSelectedItemId(NavTab.CONTRIBUTIONS.code())
        (activity as MainActivity).showTabs()
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
        Timber.d("on media clicked")
        binding!!.exploreContainer.visibility = View.VISIBLE
        (parentFragment as BookmarkFragment).binding!!.tabLayout.visibility = View.GONE
        mediaDetails = MediaDetailPagerFragment.newInstance(false, true)
        (parentFragment as BookmarkFragment).setScroll(false)
        setFragment(mediaDetails!!, listFragment)
        mediaDetails!!.showImage(position)
    }

    override fun onBackStackChanged() = Unit

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}
