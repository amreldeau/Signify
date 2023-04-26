package com.example.signify

import android.content.Context
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
    private val notAvailableDays: Map<String, Boolean>,
    private val resultTextView: TextView,

) : PagerAdapter() {
    private var startDate: Int? = null
    private var endDate: Int? = null
    private val selectedRange = mutableListOf<Int>()

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.grid_item, container, false)
        val color = ContextCompat.getColor(context, R.color.gray)
        val whitecolor = ContextCompat.getColor(context, R.color.white)
        val wcolor = ContextCompat.getColor(context, R.color.grey)
        val gridLayout = view.findViewById<GridLayout>(R.id.grid_layout)
        val daysInMonth = arrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
        var unselectable: Boolean = false

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
            if(!selectedRange.isEmpty()) {
                if (textViewId == selectedRange.min()) {
                    textView.setBackgroundResource(R.drawable.background_startdate)
                    textView.setTextColor(whitecolor)
                }
                if (textViewId == selectedRange.max()) {
                    textView.setBackgroundResource(R.drawable.background_enddate)
                    textView.setTextColor(whitecolor)
                }

                if (textViewId in selectedRange && textViewId != selectedRange.max() && textViewId != selectedRange.min()) {
                    textView.setBackgroundResource(R.drawable.background_range)

                }
            }
            // check if date is not available
            if (!notAvailableDays.getOrDefault(textViewId.toString(), false)) {
                textView.setOnClickListener {
                    if (startDate == null) {
                        // set start date
                        startDate = textViewId
                        textView.setBackgroundResource(R.drawable.background_selected_month)
                        textView.setTextColor(whitecolor)
                        selectedRange.add(startDate!!)
                    } else if (endDate == null && textViewId > startDate!!) {
                        endDate = textViewId
                        for(dday in startDate!!..endDate!!){
                            if(notAvailableDays.getOrDefault(dday.toString(), false)){
                                unselectable = true
                            }
                        }
                        if(!unselectable){
                            endDate = textViewId
                            textView.setBackgroundResource(R.drawable.background_selected_month)
                            textView.setTextColor(whitecolor)
                            selectedRange.clear()
                            val rangeToExclude = listOf(132..200, 229..300, 332..400,431..500,532..600,631..700,732..800,832..900,931..1000,1032..1100,1131..1200)
                            val filteredRange = (startDate!!..endDate!!)
                                .filterNot { day -> rangeToExclude.any { day in it } }
                            selectedRange.addAll(filteredRange)

                            // get the view at the target page
                            var id = 0


                            for(day in selectedRange) {
                                if(day in endDate!! - (endDate!! % 100)-100..(endDate!!/ 100) * 100 && day == startDate){
                                    id = 0
                                    val targetPageView = container.getChildAt(id)
                                    val targetTextView = targetPageView.findViewById<TextView>(day)
                                    targetTextView.setBackgroundResource(R.drawable.background_startdate)
                                    targetTextView.setTextColor(whitecolor)
                                }
                                if(day in (endDate!! / 100) * 100..endDate!! && day == startDate && day !in 100..200){
                                    id = 1
                                    val targetPageView = container.getChildAt(id)
                                    val targetTextView = targetPageView.findViewById<TextView>(day)
                                    targetTextView.setBackgroundResource(R.drawable.background_startdate)
                                    targetTextView.setTextColor(whitecolor)
                                }
                                if(day in (endDate!! / 100) * 100..endDate!! && day == startDate && day in 100..200){
                                    id = 0
                                    val targetPageView = container.getChildAt(id)
                                    val targetTextView = targetPageView.findViewById<TextView>(day)
                                    targetTextView.setBackgroundResource(R.drawable.background_startdate)
                                    targetTextView.setTextColor(whitecolor)
                                }
                                if(day in endDate!! - (endDate!! % 100)-100..(endDate!!/ 100) * 100 && day != startDate){
                                    id = 0
                                    val targetPageView = container.getChildAt(id)
                                    val targetTextView = targetPageView.findViewById<TextView>(day)
                                    targetTextView.setBackgroundResource(R.drawable.background_range)

                                }
                                if(day in (endDate!! / 100) * 100..endDate!! && day == endDate && day !in 100..200){
                                    id = 1
                                    val targetPageView = container.getChildAt(id)
                                    val targetTextView = targetPageView.findViewById<TextView>(day)
                                    targetTextView.setBackgroundResource(R.drawable.background_enddate)
                                    targetTextView.setTextColor(whitecolor)
                                }
                                if(day in (endDate!! / 100) * 100..endDate!! && day == endDate && day in 100..200){
                                    id = 0
                                    val targetPageView = container.getChildAt(id)
                                    val targetTextView = targetPageView.findViewById<TextView>(day)
                                    targetTextView.setBackgroundResource(R.drawable.background_enddate)
                                    targetTextView.setTextColor(whitecolor)
                                }
                                if(day in (endDate!!/ 100) * 100..endDate!! && day != endDate && day != startDate && day !in 100..200){
                                    id = 1
                                    val targetPageView = container.getChildAt(id)
                                    val targetTextView = targetPageView.findViewById<TextView>(day)
                                    targetTextView.setBackgroundResource(R.drawable.background_range)

                                }
                                if(day in (endDate!!/ 100) * 100..endDate!! && day != endDate && day != startDate && day in 100..200){
                                    id = 0
                                    val targetPageView = container.getChildAt(id)
                                    val targetTextView = targetPageView.findViewById<TextView>(day)
                                    targetTextView.setBackgroundResource(R.drawable.background_range)

                                }
                            }

                            resultTextView.text = formatDates(startDate!!, endDate!!)

                            onTextViewClickListener.onTextViewClick(selectedRange)

                            notifyDataSetChanged()
                        }
                        // set end date and save selected range to array

                    }
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

    fun formatDates(start: Int, end: Int): String {
        val startMonth = getMonthName(start / 100)
        val startDay = start % 100
        val endMonth = getMonthName(end / 100)
        val endDay = end % 100
        return "$startMonth $startDay - $endMonth $endDay"
    }

    fun getMonthName(month: Int): String {
        return when (month) {
            1 -> "Jan"
            2 -> "Feb"
            3 -> "Mar"
            4 -> "Apr"
            5 -> "May"
            6 -> "Jun"
            7 -> "Jul"
            8 -> "Aug"
            9 -> "Sep"
            10 -> "Oct"
            11 -> "Nov"
            12 -> "Dec"
            else -> throw IllegalArgumentException("Invalid month value: $month")
        }
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
        fun onTextViewClick(textViewId: MutableList<Int>)
    }
    fun clearSelectedRange() {
        selectedRange.clear()
        notifyDataSetChanged()
    }
}
