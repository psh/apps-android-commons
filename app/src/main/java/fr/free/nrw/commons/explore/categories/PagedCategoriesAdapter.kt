package fr.free.nrw.commons.explore.categories

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import fr.free.nrw.commons.category.CATEGORY_PREFIX
import fr.free.nrw.commons.databinding.ItemRecentSearchesBinding

class PagedSearchCategoriesAdapter(private val onCategoryClicked: (String) -> Unit) :
    PagedListAdapter<String, CategoryItemViewHolder>(PagedSearchDiffItemCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryItemViewHolder {
        return CategoryItemViewHolder(
            ItemRecentSearchesBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            onCategoryClicked
        )
    }

    override fun onBindViewHolder(holder: CategoryItemViewHolder, position: Int) =
        holder.bind(getItem(position)!!)
}

class CategoryItemViewHolder(
    val binding: ItemRecentSearchesBinding,
    val onCategoryClicked: (String) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: String) {
        binding.root.setOnClickListener { onCategoryClicked(item) }
        binding.textView1.text = item.substringAfter(CATEGORY_PREFIX)
    }
}

private object PagedSearchDiffItemCallback : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String) =
        oldItem == newItem

    override fun areContentsTheSame(oldItem: String, newItem: String) =
        oldItem == newItem
}
