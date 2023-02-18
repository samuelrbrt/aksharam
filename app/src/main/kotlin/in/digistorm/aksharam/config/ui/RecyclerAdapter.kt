package `in`.digistorm.aksharam.config.ui

import `in`.digistorm.aksharam.config.ext.bind
import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import `in`.digistorm.aksharam.BR


open class RecyclerAdapter<T, B : ViewDataBinding>(
    @LayoutRes private val itemLayoutResId: Int,
    private val viewHolder: (B) -> BaseViewHolder<T, B>,
    var items: List<T> = mutableListOf(),
) : RecyclerView.Adapter<BaseViewHolder<T, B>>() {
    override fun onBindViewHolder(holder: BaseViewHolder<T, B>, position: Int) {
        holder.bindTo(items[position], position)
    }

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<T, B> {
        return parent.bind(itemLayoutResId, viewHolder).apply { adapter = this@RecyclerAdapter }
    }

    @SuppressLint("NotifyDataSetChanged")
    open fun updateItems(
        newItems: List<T>?,
        itemSame: (T, T) -> Boolean = { i1, i2 -> i1 == i2 },
        contentSame: (T, T) -> Boolean = { i1, i2 -> i1 == i2 }
    ) {
        if (newItems == null) {
            items = mutableListOf()
            notifyDataSetChanged()
            return
        }

        if (items.isEmpty()) {
            items = newItems
            notifyDataSetChanged()
            return
        }

        val diffResult = DiffUtil.calculateDiff(DiffCalc(items, newItems, itemSame, contentSame))
        items = newItems
        diffResult.dispatchUpdatesTo(this)
    }

    fun getItemPosition(item: T) = items.indexOf(item)

    class DiffCalc<T>(
        private val oldItem: List<T>,
        private val newItems: List<T>,
        private val itemSame: (T, T) -> Boolean,
        private val contentSame: (T, T) -> Boolean
    ) : DiffUtil.Callback() {
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            itemSame(oldItem[oldItemPosition], newItems[newItemPosition])

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            contentSame(oldItem[oldItemPosition], newItems[newItemPosition])

        override fun getOldListSize() = oldItem.size

        override fun getNewListSize() = newItems.size
    }
}

open class BaseViewHolder<T, B : ViewDataBinding>(protected val itemBinding: B) : RecyclerView.ViewHolder(itemBinding.root) {
    open lateinit var adapter: RecyclerAdapter<T, B>

    open fun bindTo(item: T?, position: Int) {
        itemBinding.setVariable(BR.item, item)
        itemBinding.executePendingBindings()
    }
}