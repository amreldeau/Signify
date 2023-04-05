package com.example.signify

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.PagerAdapter

class MyPagerAdapter(private val context: Context, private val onTextViewClickListener: OnTextViewClickListener, private val notAvailableDays: Map<String, Boolean>) : PagerAdapter() {

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.grid_item, container, false)

        // Find the GridLayout inside the view
        val gridLayout = view.findViewById<GridLayout>(R.id.grid_layout)

        // Create 30 TextViews and add them to the GridLayout with different ids
        for (i in 0 until 30) {
            val textView = inflater.inflate(R.layout.textview_item, gridLayout, false) as TextView
            val textViewId = position * 30 + i + 1
            textView.id = textViewId
            textView.text = "$textViewId"
            if (!notAvailableDays.getOrDefault(textViewId.toString(), false)) {
                textView.setOnClickListener {
                    onTextViewClickListener.onTextViewClick(textViewId)
                    // Change the background drawable of the TextView when it's clicked
                    textView.setBackgroundResource(R.drawable.background_selected_month)
                }
            }else{
                textView.setBackgroundResource(R.drawable.background_not_available)
            }
            gridLayout.addView(textView)

        }

        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun getCount(): Int {
        return 12
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }
    interface OnTextViewClickListener {
        fun onTextViewClick(textViewId: Int)
    }
}