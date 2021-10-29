package fr.free.nrw.commons.explore.media

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import fr.free.nrw.commons.Media
import fr.free.nrw.commons.R
import fr.free.nrw.commons.databinding.LayoutCategoryImagesBinding

class PagedMediaAdapter(private val onImageClicked: (Int) -> Unit) :
    PagedListAdapter<Media, SearchImagesViewHolder>(PagedMediaDiffItemCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = SearchImagesViewHolder(
        LayoutCategoryImagesBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        onImageClicked
    )

    override fun onBindViewHolder(holder: SearchImagesViewHolder, position: Int) {
        holder.bind(getItem(position)!! to position)
    }
}

class SearchImagesViewHolder(
    val binding: LayoutCategoryImagesBinding,
    val onImageClicked: (Int) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Pair<Media, Int>) = with(binding) {
        val media = item.first
        root.setOnClickListener { onImageClicked(item.second) }
        categoryImageTitle.text = media.mostRelevantCaption
        categoryImageView.setImageURI(media.thumbUrl)
        if (media.author?.isNotEmpty() == true) {
            categoryImageAuthor.visibility = View.VISIBLE
            categoryImageAuthor.text = root.context.getString(
                R.string.image_uploaded_by, media.user
            )
        } else {
            categoryImageAuthor.visibility = View.GONE
        }
    }
}

private object PagedMediaDiffItemCallback : DiffUtil.ItemCallback<Media>() {
    override fun areItemsTheSame(oldItem: Media, newItem: Media) =
        oldItem.pageId == newItem.pageId

    override fun areContentsTheSame(oldItem: Media, newItem: Media) =
        oldItem.pageId == newItem.pageId
}