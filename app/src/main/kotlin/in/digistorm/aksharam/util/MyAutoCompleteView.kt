package `in`.digistorm.aksharam.util

import android.content.Context
import android.util.AttributeSet
import android.widget.ArrayAdapter
import androidx.databinding.BindingAdapter
import androidx.navigation.findNavController
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import `in`.digistorm.aksharam.R

class MyAutoCompleteView: MaterialAutoCompleteTextView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrSet: AttributeSet) : super(context, attrSet)
    constructor(context: Context, attrSet: AttributeSet, defStyleAttr: Int) : super(context, attrSet, defStyleAttr)

    private val logTag = javaClass.simpleName

    val stringListAdapter: ArrayAdapter<String> = ArrayAdapter(
        context,
        R.layout.drop_down_item,
        arrayListOf()
    )

    init {
        setAdapter(stringListAdapter)
    }

    var myItems: List<String> = listOf()
        set(value) {
            logDebug(logTag, "items received $value")
            field = value
        }
}