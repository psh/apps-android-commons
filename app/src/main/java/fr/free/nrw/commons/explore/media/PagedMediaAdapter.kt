package fr.free.nrw.commons.explore.media

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.facebook.drawee.view.SimpleDraweeView
import fr.free.nrw.commons.Media
import fr.free.nrw.commons.R
import fr.free.nrw.commons.explore.paging.BaseViewHolder
import fr.free.nrw.commons.explore.paging.inflate

class PagedMediaAdapter(private val onImageClicked: (Int) -> Unit) :
    PagedListAdapter<Media, SearchImagesViewHolder>(object : DiffUtil.ItemCallback<Media>() {
        override fun areItemsTheSame(oldItem: Media, newItem: Media) =
            oldItem.pageId == newItem.pageId

        override fun areContentsTheSame(oldItem: Media, newItem: Media) =
            oldItem.pageId == newItem.pageId
    }) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        SearchImagesViewHolder(
            parent.inflate(R.layout.layout_category_images),
            onImageClicked
        )

    override fun onBindViewHolder(holder: SearchImagesViewHolder, position: Int) {
        holder.bind(getItem(position)!! to position)
    }
}

class SearchImagesViewHolder(containerView: View, val onImageClicked: (Int) -> Unit) :
    BaseViewHolder<Pair<Media, Int>>(containerView) {
    private val imageView = containerView.findViewById<SimpleDraweeView>(R.id.categoryImageView)
    private val title = containerView.findViewById<TextView>(R.id.categoryImageTitle)
    private val author = containerView.findViewById<TextView>(R.id.categoryImageAuthor)

    override fun bind(item: Pair<Media, Int>) {
        val media = item.first
        imageView.setOnClickListener { onImageClicked(item.second) }
        title.text = media.mostRelevantCaption
        imageView.setImageURI(media.thumbUrl)
        if (media.author?.isNotEmpty() == true) {
            author.visibility = View.VISIBLE
            author.text = title.context.getString(R.string.image_uploaded_by, media.user)
        } else {
            author.visibility = View.GONE
        }
    }

}
