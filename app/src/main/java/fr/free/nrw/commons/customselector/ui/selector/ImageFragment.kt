package fr.free.nrw.commons.customselector.ui.selector

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import fr.free.nrw.commons.customselector.helper.ImageHelper
import fr.free.nrw.commons.customselector.listeners.ImageSelectListener
import fr.free.nrw.commons.customselector.model.CallbackStatus
import fr.free.nrw.commons.customselector.model.Image
import fr.free.nrw.commons.customselector.model.Result
import fr.free.nrw.commons.customselector.ui.adapter.ImageAdapter
import fr.free.nrw.commons.databinding.FragmentCustomSelectorBinding
import fr.free.nrw.commons.di.CommonsDaggerSupportFragment
import fr.free.nrw.commons.theme.BaseActivity
import javax.inject.Inject

/**
 * Custom Selector Image Fragment.
 */
class ImageFragment: CommonsDaggerSupportFragment() {

    /**
     * Current bucketId.
     */
    private var bucketId: Long? = null

    /**
     * Last ImageItem Id.
     */
    private var lastItemId: Long? = null

    /**
     * View model for images.
     */
    private var viewModel: CustomSelectorViewModel? = null

    /**
     * View Elements.
     */
    private var binding: FragmentCustomSelectorBinding? = null
    private lateinit var filteredImages: ArrayList<Image>

    /**
     * View model Factory.
     */
    lateinit var customSelectorViewModelFactory: CustomSelectorViewModelFactory
        @Inject set

    /**
     * Image loader for adapter.
     */
    var imageLoader: ImageLoader? = null
        @Inject set

    /**
     * Image Adapter for recycle view.
     */
    private lateinit var imageAdapter: ImageAdapter

    /**
     * GridLayoutManager for recycler view.
     */
    private lateinit var gridLayoutManager: GridLayoutManager


    companion object {

        /**
         * BucketId args name
         */
        const val BUCKET_ID = "BucketId"
        const val LAST_ITEM_ID = "LastItemId"

        /**
         * newInstance from bucketId.
         */
        fun newInstance(bucketId: Long, lastItemId: Long): ImageFragment {
            val fragment = ImageFragment()
            fragment.arguments = bundleOf(
                BUCKET_ID to bucketId,
                LAST_ITEM_ID to lastItemId
            )
            return fragment
        }
    }

    /**
     * OnCreate
     * Get BucketId, view Model.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bucketId = arguments?.getLong(BUCKET_ID)
        lastItemId = arguments?.getLong(LAST_ITEM_ID, 0)
        viewModel = ViewModelProvider(requireActivity(), customSelectorViewModelFactory)
            .get(CustomSelectorViewModel::class.java)
    }

    /**
     * OnCreateView
     * Init imageAdapter, gridLayoutManger.
     * SetUp recycler view.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentCustomSelectorBinding.inflate(inflater, container, false)
        imageAdapter =
            ImageAdapter(requireActivity(), activity as ImageSelectListener, imageLoader!!)
        gridLayoutManager = GridLayoutManager(context, getSpanCount())
        with(binding?.selectorRv!!) {
            this.layoutManager = gridLayoutManager
            setHasFixedSize(true)
            this.adapter = imageAdapter
        }

        viewModel?.result?.observe(viewLifecycleOwner) {
            handleResult(it)
        }

        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    /**
     * Handle view model result.
     */
    private fun handleResult(result: Result) {
        if (result.status is CallbackStatus.SUCCESS) {
            val images = result.images
            if (images.isNotEmpty()) {
                filteredImages = ImageHelper.filterImages(images, bucketId)
                imageAdapter.init(filteredImages)
                binding?.selectorRv?.let {
                    it.visibility = View.VISIBLE
                    lastItemId?.let { pos ->
                        (it.layoutManager as GridLayoutManager)
                            .scrollToPosition(ImageHelper.getIndexFromId(filteredImages, pos))
                    }
                }
            } else {
                binding?.emptyText?.let {
                    it.visibility = View.VISIBLE
                }
                binding?.selectorRv?.let {
                    it.visibility = View.GONE
                }
            }
        }
        binding?.loader?.let {
            it.visibility =
                if (result.status is CallbackStatus.FETCHING) View.VISIBLE else View.GONE
        }
    }

    /**
     * getSpanCount for GridViewManager.
     *
     * @return spanCount.
     */
    private fun getSpanCount(): Int {
        return 3
        // todo change span count depending on the device orientation and other factos.
    }

    /**
     * onResume
     * notifyDataSetChanged, rebuild the holder views to account for deleted images.
     */
    override fun onResume() {
        imageAdapter.notifyDataSetChanged()
        super.onResume()
    }

    /**
     * OnDestroy
     * Cleanup the imageLoader coroutine.
     * Save the Image Fragment state.
     */
    override fun onDestroy() {
        imageLoader?.cleanUP()

        val position = (binding?.selectorRv?.layoutManager as GridLayoutManager)
            .findFirstVisibleItemPosition()

        // Check for empty RecyclerView.
        if (position != -1) {
            requireContext().getSharedPreferences(
                "CustomSelector",
                BaseActivity.MODE_PRIVATE
            )?.let { prefs ->
                prefs.edit()?.let { editor ->
                    editor.putLong("ItemId", imageAdapter.getImageIdAt(position))?.apply()
                }
            }
        }
        super.onDestroy()
    }
}