package com.example.signify

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.PagerAdapter

class MyPagerAdapter(
    private val context: Context,
    private val onTextViewClickListener: OnTextViewClickListener,
    private val notAvailableDays: Map<String, Boolean>

) : PagerAdapter() {

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.grid_item, container, false)
        val color = ContextCompat.getColor(context, R.color.gray)
        val whitecolor = ContextCompat.getColor(context, R.color.white)
        val wcolor = ContextCompat.getColor(context, R.color.grey)
        val gridLayout = view.findViewById<GridLayout>(R.id.grid_layout)
        val daysInMonth = arrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)


        val days = listOf("M", "T", "W", "T", "F", "S", "S")
        for (day in days) {
            val textView = inflater.inflate(R.layout.textview_item, gridLayout, false) as TextView
            textView.text = day
            textView.setTextColor(wcolor)
            textView.gravity = Gravity.CENTER
            gridLayout.addView(textView)
        }


        for (i in 1..daysInMonth[position]) {
            val textView = inflater.inflate(R.layout.textview_item, gridLayout, false) as TextView
            val textViewId = (position + 1) * 100 + i

            textView.id = textViewId
            textView.text = (textViewId-((position + 1) * 100)).toString()

            when (textViewId) {
                101, 1001 -> {
                    val params = textView.layoutParams as GridLayout.LayoutParams
                    params.rowSpec = GridLayout.spec(1)
                    params.columnSpec = GridLayout.spec(6)
                }
                401, 701-> {
                    val params = textView.layoutParams as GridLayout.LayoutParams
                    params.rowSpec = GridLayout.spec(1)
                    params.columnSpec = GridLayout.spec(5)
                }
                801-> {
                    val params = textView.layoutParams as GridLayout.LayoutParams
                    params.rowSpec = GridLayout.spec(1)
                    params.columnSpec = GridLayout.spec(1)
                }
                501-> {
                    val params = textView.layoutParams as GridLayout.LayoutParams
                    params.rowSpec = GridLayout.spec(1)
                    params.columnSpec = GridLayout.spec(0)
                }
                901, 1201-> {
                    val params = textView.layoutParams as GridLayout.LayoutParams
                    params.rowSpec = GridLayout.spec(1)
                    params.columnSpec = GridLayout.spec(4)
                }
                601-> {
                    val params = textView.layoutParams as GridLayout.LayoutParams
                    params.rowSpec = GridLayout.spec(1)
                    params.columnSpec = GridLayout.spec(3)
                }
                201, 301, 1101 -> {
                    val params = textView.layoutParams as GridLayout.LayoutParams
                    params.rowSpec = GridLayout.spec(1)
                    params.columnSpec = GridLayout.spec(2)
                }
            }

            if (!notAvailableDays.getOrDefault(textViewId.toString(), false)) {
                textView.setOnClickListener {
                    onTextViewClickListener.onTextViewClick(textViewId)
                    textView.setBackgroundResource(R.drawable.background_selected_month)
                    textView.setTextColor(whitecolor)
                }
            } else {

                textView.setTextColor(color)
                textView.paintFlags = textView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
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
