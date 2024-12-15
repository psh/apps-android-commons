package fr.free.nrw.commons.upload

import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.facebook.drawee.view.SimpleDraweeView
import fr.free.nrw.commons.R
import fr.free.nrw.commons.databinding.ItemUploadThumbnailBinding
import fr.free.nrw.commons.filepicker.UploadableFile
import java.io.File

/**
 * The adapter class for image thumbnails to be shown while uploading.
 */
internal class ThumbnailsAdapter(
    private val callback: Callback,
    private val onThumbnailDeletedListener: OnThumbnailDeletedListener
) : RecyclerView.Adapter<ThumbnailsAdapter.ViewHolder>() {
    var uploadableFiles: List<UploadableFile> = emptyList()
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        return ViewHolder(
            ItemUploadThumbnailBinding.inflate(
                LayoutInflater.from(viewGroup.context), viewGroup, false
            )
        )
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) = viewHolder.bind(position)

    override fun getItemCount(): Int = uploadableFiles.size

    inner class ViewHolder(b: ItemUploadThumbnailBinding) : RecyclerView.ViewHolder(b.root) {
        var rlContainer: RelativeLayout = b.rlContainer
        var background: SimpleDraweeView = b.ivThumbnail
        var ivCross: ImageView = b.icCross

        /**
         * Binds a row item to the ViewHolder
         */
        fun bind(position: Int) {
            background.setImageURI(uploadableFiles[position].imageUri)

            if (position == callback.currentSelectedFilePosition()) {
                val border = GradientDrawable().apply {
                    shape = GradientDrawable.RECTANGLE
                    setStroke(8, ContextCompat.getColor(rlContainer.context, R.color.primaryColor))
                }
                rlContainer.isEnabled = true
                rlContainer.isClickable = true
                rlContainer.alpha = 1.0f
                rlContainer.background = border
                rlContainer.elevation = 10f
            } else {
                rlContainer.isEnabled = false
                rlContainer.isClickable = false
                rlContainer.alpha = 0.7f
                rlContainer.background = null
                rlContainer.elevation = 0f
            }

            ivCross.setOnClickListener {
                onThumbnailDeletedListener.onThumbnailDeleted(position)
            }
        }
    }

    private val UploadableFile.imageUri: Uri? get() =
        Uri.fromFile(File(Uri.parse(file.path).toString()))

    /**
     * Callback used to get the current selected file position
     */
    internal fun interface Callback {
        fun currentSelectedFilePosition(): Int
    }

    /**
     * Interface to listen to thumbnail delete events
     */
    fun interface OnThumbnailDeletedListener {
        fun onThumbnailDeleted(position: Int)
    }
}
