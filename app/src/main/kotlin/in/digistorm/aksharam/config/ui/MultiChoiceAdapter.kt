package `in`.digistorm.aksharam.config.ui

import androidx.databinding.ViewDataBinding
import `in`.digistorm.aksharam.BR


class MultiChoiceAdapter<T, B : ViewDataBinding>(
    itemLayoutResId: Int,
    items: List<T> = mutableListOf(),
    private var selectedItems: List<Boolean> = items.map { false },
    viewHolder: (B) -> MultiChoiceViewHolder<T, B>,
) : RecyclerAdapter<T, B>(itemLayoutResId, viewHolder, items) {

    fun setChoice(position: Int, isSelected: Boolean = !selectedItems[position]): Boolean {
        val newSelectedItems = selectedItems.toMutableList()
        newSelectedItems[position] = isSelected
        selectedItems = newSelectedItems
        notifyItemChanged(position)
        return isSelected
    }

    override fun updateItems(newItems: List<T>?, itemSame: (T, T) -> Boolean, contentSame: (T, T) -> Boolean) {
        selectedItems = newItems?.map { false } ?: emptyList()

        super.updateItems(newItems, itemSame, contentSame)
    }

    fun getChosenItems() = items.filterIndexed { i, _ -> selectedItems[i] }

    override fun onBindViewHolder(holder: BaseViewHolder<T, B>, position: Int) {
        (holder as MultiChoiceViewHolder).bindTo(items[position], position, selectedItems[position])
    }
}

open class MultiChoiceViewHolder<T, B : ViewDataBinding>(itemBinding: B) :
    BaseViewHolder<T, B>(itemBinding) {

    init {
        itemBinding.root.setOnClickListener {
            val selectedPosition = absoluteAdapterPosition
            val isSelected = (adapter as MultiChoiceAdapter).setChoice(selectedPosition)
            onItemClick(adapter.items[selectedPosition], selectedPosition, isSelected)
        }
    }

    open fun bindTo(item: T, position: Int, isSelected: Boolean) {
        itemBinding.setVariable(BR.isSelected, isSelected)
        super.bindTo(item, position)
    }

    open fun onItemClick(item: T, position: Int, isSelected: Boolean) {}
}