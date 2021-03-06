package mega.privacy.android.app.fragments.homepage

import androidx.recyclerview.widget.ListAdapter
import mega.privacy.android.app.components.scrollBar.SectionTitleProvider

abstract class BaseNodeItemAdapter(
    private val actionModeViewModel: ActionModeViewModel,
    private val itemOperationViewModel: ItemOperationViewModel,
    private val sortByHeaderViewModel: SortByHeaderViewModel
) : ListAdapter<NodeItem, NodeViewHolder>(NodeDiffCallback()),
    SectionTitleProvider {

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position).node) {
            null -> TYPE_HEADER
            else -> TYPE_ITEM
        }
    }

    override fun onBindViewHolder(holder: NodeViewHolder, position: Int) {
        holder.bind(
            actionModeViewModel,
            itemOperationViewModel,
            sortByHeaderViewModel,
            getItem(position)
        )
    }

    override fun getSectionTitle(position: Int): String? {
        if (position < 0 || position >= itemCount) {
            return null
        }

        val nodeName = getItem(position).node?.name ?: ""
        return if (nodeName == "") null else nodeName.substring(0, 1)
    }

    companion object {
        const val TYPE_ITEM = 0
        const val TYPE_HEADER = 1
    }
}