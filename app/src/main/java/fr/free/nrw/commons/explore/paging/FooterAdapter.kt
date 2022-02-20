package fr.free.nrw.commons.explore.paging

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import fr.free.nrw.commons.R
import fr.free.nrw.commons.databinding.ListItemLoadMoreBinding
import fr.free.nrw.commons.databinding.ListItemProgressBinding
import kotlinx.android.extensions.LayoutContainer

class FooterAdapter(private val onRefreshClicked: () -> Unit) :
    ListAdapter<FooterItem, FooterViewHolder>(object :
        DiffUtil.ItemCallback<FooterItem>() {
        override fun areItemsTheSame(oldItem: FooterItem, newItem: FooterItem) = oldItem == newItem

        override fun areContentsTheSame(oldItem: FooterItem, newItem: FooterItem) =
            oldItem == newItem
    }) {

    override fun getItemViewType(position: Int): Int {
        return getItem(position).ordinal
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FooterViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (FooterItem.values()[viewType]) {
            FooterItem.LoadingItem -> LoadingViewHolder(
                ListItemProgressBinding.inflate(inflater).root
            )
            FooterItem.RefreshItem -> RefreshViewHolder(
                ListItemLoadMoreBinding.inflate(inflater).root,
                onRefreshClicked
            )
        }
    }

    override fun onBindViewHolder(holder: FooterViewHolder, position: Int) {}
}

open class FooterViewHolder(override val containerView: View) :
    RecyclerView.ViewHolder(containerView),
    LayoutContainer

class LoadingViewHolder(containerView: View) : FooterViewHolder(containerView)

class RefreshViewHolder(containerView: View, onRefreshClicked: () -> Unit) :
    FooterViewHolder(containerView) {
    init {
        containerView.findViewById<View>(R.id.listItemLoadMoreButton)
            ?.setOnClickListener { onRefreshClicked() }
    }
}

enum class FooterItem { LoadingItem, RefreshItem }
