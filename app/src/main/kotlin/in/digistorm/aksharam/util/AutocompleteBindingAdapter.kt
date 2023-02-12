package `in`.digistorm.aksharam.util

import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import `in`.digistorm.aksharam.R
import `in`.digistorm.aksharam.activities.main.TabbedViewsDirections
import `in`.digistorm.aksharam.activities.main.letters.LetterCategoryAdapter
import `in`.digistorm.aksharam.activities.main.letters.LettersTabViewModel

private val logTag: String = "AutoCompleteBindingAdapter"

@BindingAdapter("myList")
fun MyAutoCompleteView.writeSimpleItems(newItems: ArrayList<String>) {
    logDebug(logTag, "Write simple items $newItems")
    if (myItems != newItems) {
        myItems = newItems
        stringListAdapter.clear()
        stringListAdapter.addAll(newItems)
        stringListAdapter.notifyDataSetChanged()
    }
}

@BindingAdapter("itemClicked")
fun MyAutoCompleteView.setItemClicked(listener: OnItemClickListener) {
    logDebug(logTag, "Item clicked!")
    onItemClickListener = listener
}

@BindingAdapter("letters_category_wise")
fun RecyclerView.setLettersCategoryWise(newLettersCategoryWise: List<Map<String, ArrayList<Pair<String, String>>>>) {
    logDebug(logTag, "Write letters category wise: $newLettersCategoryWise")
    if(adapter == null)
        logDebug(logTag, "Could not acquire adapter.")
    (adapter as? LetterCategoryAdapter)?.submitList(newLettersCategoryWise)
}