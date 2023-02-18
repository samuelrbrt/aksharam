package `in`.digistorm.aksharam.config.ui

import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import android.view.View.*
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import com.google.android.material.appbar.CollapsingToolbarLayout
import java.text.SimpleDateFormat
import java.util.*

@BindingAdapter(
    "bind:span_text",
    "bind:bold_span_start",
    "bind:bold_span_end",
    "bind:color",
    "bind:color_span_start",
    "bind:color_span_end",
    requireAll = false
)
fun TextView.setBoldSpan(
    text: String,
    start: Int,
    end: Int,
    color: Int,
    colorStart: Int,
    colorEnd: Int
) {
    val builder = SpannableStringBuilder(text)
    builder.setSpan(StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
    builder.setSpan(AbsoluteSizeSpan(18, true), start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE)

    if (color != 0) {
        builder.setSpan(
            ForegroundColorSpan(color),
            colorStart,
            colorEnd,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
    }

    this.text = builder
}

@BindingAdapter("date", "date_format", requireAll = true)
fun TextView.dateText(date: Date?, format: String) {
    if (date == null) {
        text = null
        return
    }

    text = SimpleDateFormat(format, Locale.US).format(date).toString()
}

@BindingAdapter("bind:linearSnapper")
fun RecyclerView.linearSnapper(linear: Boolean) {
    val helper: SnapHelper = LinearSnapHelper().apply {}
    helper.attachToRecyclerView(this)
}

@BindingAdapter("isVisible")
fun View.isVisible(visible: Boolean) {
    visibility = if (visible) VISIBLE else INVISIBLE
}

@BindingAdapter("isShowing")
fun View.isShowing(showing: Boolean) {
    visibility = if (showing) VISIBLE else GONE
}

@BindingAdapter("bind:drawableEnd")
fun TextView.drawableEnd(drawable: Drawable?) {
    setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
}

@BindingAdapter("bind:collapsedTitle", "bind:collapsingToolbar", requireAll = true)
fun AppBarLayout.setCollapsedTitle(
    title: String?,
    collapsingToolbarLayout: CollapsingToolbarLayout
) {
    addOnOffsetChangedListener(object : OnOffsetChangedListener {
        var isShow = false
        var scrollRange = -1
        override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
            if (scrollRange == -1) {
                scrollRange = appBarLayout.totalScrollRange
            }
            if (scrollRange + verticalOffset <= 100) {
                collapsingToolbarLayout.title = title
                isShow = false
            } else if (!isShow) {
                collapsingToolbarLayout.title = " "
                isShow = true
            }
        }
    })
}
