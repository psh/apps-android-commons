package fr.free.nrw.commons.explore.paging

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import fr.free.nrw.commons.databinding.ListItemLoadMoreBinding
import fr.free.nrw.commons.databinding.ListItemProgressBinding

class FooterAdapter(private val onRefreshClicked: () -> Unit) :
    ListAdapter<FooterItem, FooterViewHolder>(FooterDiffItemCallback) {

    override fun getItemViewType(position: Int): Int = getItem(position).ordinal

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FooterViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (FooterItem.values()[viewType]) {
            FooterItem.LoadingItem ->
                LoadingViewHolder(ListItemProgressBinding.inflate(inflater, parent, false))

            FooterItem.RefreshItem ->
                RefreshViewHolder(
                    ListItemLoadMoreBinding.inflate(inflater, parent, false),
                    onRefreshClicked
                )
        }
    }

    override fun onBindViewHolder(holder: FooterViewHolder, position: Int) = Unit
}

enum class FooterItem { LoadingItem, RefreshItem }

abstract class FooterViewHolder(containerView: View) : RecyclerView.ViewHolder(containerView)

class LoadingViewHolder(binding: ListItemProgressBinding) : FooterViewHolder(binding.root)

class RefreshViewHolder(binding: ListItemLoadMoreBinding, onRefreshClicked: () -> Unit) :
    FooterViewHolder(binding.root) {
    init {
        binding.root.setOnClickListener { onRefreshClicked() }
    }
}

private object FooterDiffItemCallback : DiffUtil.ItemCallback<FooterItem>() {
    override fun areItemsTheSame(oldItem: FooterItem, newItem: FooterItem) =
        oldItem == newItem

    override fun areContentsTheSame(oldItem: FooterItem, newItem: FooterItem) =
        oldItem == newItem
}
