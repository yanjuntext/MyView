package com.wyj.widget.dialog

import android.content.Context
import android.util.Log
import android.view.Gravity
import android.view.View
import androidx.fragment.app.FragmentActivity
import com.wyj.widget.R
import com.wyj.widget.dialog.base.SBuilder
import com.wyj.widget.dialog.base.SDialog
import com.wyj.widget.wheel.IWheel
import com.wyj.widget.wheel.WheelItem
import com.wyj.widget.wheel.WheelItemView
import com.base.utils.clickDelay
import kotlinx.android.synthetic.main.dialog_calendar.*
import kotlinx.android.synthetic.main.include_title_has_btn.*
import java.util.*

/**
 *
 *@author abc
 *@time 2020/3/3 13:50
 */
class CalendarDialog private constructor(val builder: Builder) : SDialog(builder) {

    enum class Type {
        // 五种选择模式，年${getString(R.string.picker_month)}日${getString(R.string.picker_hour)}${getString(R.string.picker_minute)}，年${getString(R.string.picker_month)}日，${getString(R.string.picker_hour)}${getString(R.string.picker_minute)}，${getString(R.string.picker_month)}日${getString(R.string.picker_hour)}${getString(R.string.picker_minute)}，年${getString(R.string.picker_month)}
        ALL,
        YEAR_MONTH_DAY,
        HOURS_MINS,
        MONTH_DAY_HOUR_MIN,
        YEAR_MONTH,
        YEAR
    }

    private val mYearList = mutableListOf<IWheel>()
    private val mMonthList = mutableListOf<IWheel>()
    private val mDayList = mutableListOf<IWheel>()
    private val mHourList = mutableListOf<IWheel>()
    private val mMinuteList = mutableListOf<IWheel>()

    private var mYearIndex = -1
    private var mMonthIndex = -1
    private var mDayIndex = -1
    private var mHourIndex = -1
    private var mMinuterIndex = -1
    private val mCurrentCalendar by lazy { Calendar.getInstance() }

    override fun initView() {

        val calendar = Calendar.getInstance()

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        Log.e("CalendarDialog", "month[$month]")

        getYear(year)
        getMonth(year, month)
        getDays(year, month + 1, day)
//        getHours(year, month, day, hour)
//        getMinutes(year, month, day, hour, minute)

        when (builder.type) {
            Type.ALL -> {
                wv_year.visibility = View.VISIBLE
                wv_month.visibility = View.VISIBLE
                wv_day.visibility = View.VISIBLE
                wv_hour.visibility = View.VISIBLE
                wv_min.visibility = View.VISIBLE
            }
            Type.YEAR_MONTH_DAY -> {
                wv_year.visibility = View.VISIBLE
                wv_month.visibility = View.VISIBLE
                wv_day.visibility = View.VISIBLE
                wv_hour.visibility = View.GONE
                wv_min.visibility = View.GONE
            }
            Type.HOURS_MINS -> {
                wv_year.visibility = View.GONE
                wv_month.visibility = View.GONE
                wv_day.visibility = View.GONE
                wv_hour.visibility = View.VISIBLE
                wv_min.visibility = View.VISIBLE
            }
            Type.MONTH_DAY_HOUR_MIN -> {
                wv_year.visibility = View.GONE
                wv_month.visibility = View.VISIBLE
                wv_day.visibility = View.VISIBLE
                wv_hour.visibility = View.VISIBLE
                wv_min.visibility = View.VISIBLE
            }
            Type.YEAR_MONTH -> {
                wv_year.visibility = View.VISIBLE
                wv_month.visibility = View.VISIBLE
                wv_day.visibility = View.GONE
                wv_hour.visibility = View.GONE
                wv_min.visibility = View.GONE
            }
            Type.YEAR -> {
                wv_year.visibility = View.VISIBLE
                wv_month.visibility = View.GONE
                wv_day.visibility = View.GONE
                wv_hour.visibility = View.GONE
                wv_min.visibility = View.GONE
            }
        }

        with(wv_year) {
            this.OnSelectedListener(object : WheelItemView.OnSelectedListener {
                override fun onSelected(context: Context, selectedIndex: Int) {
                    mYearIndex = selectedIndex
                    getMonth(
                        mYearList[selectedIndex].getShowText().replace(getString(R.string.picker_year), "").toInt(),
                        mMonthList[mMonthIndex].getShowText().replace(getString(R.string.picker_month), "").toInt() - 1
                    )
                }
            })
        }
        with(wv_month) {

            this.OnSelectedListener(object : WheelItemView.OnSelectedListener {
                override fun onSelected(context: Context, selectedIndex: Int) {
                    mMonthIndex = selectedIndex
                    getDays(
                        mYearList[mYearIndex].getShowText().replace(getString(R.string.picker_year), "").toInt(),
                        mMonthList[selectedIndex].getShowText().replace(getString(R.string.picker_month), "").toInt(),
                        mDayList[mDayIndex].getShowText().replace(getString(R.string.picker_day), "").toInt()
                    )
                }
            })
        }
        with(wv_day) {

            this.OnSelectedListener(object : WheelItemView.OnSelectedListener {
                override fun onSelected(context: Context, selectedIndex: Int) {
                    mDayIndex = selectedIndex
//                    getHours(
//                        mYearList[mYearIndex].getShowText().replace(getString(R.string.picker_year), "").toInt(),
//                        mMonthList[mMonthIndex].getShowText().replace(getString(R.string.picker_month), "").toInt(),
//                        mDayList[selectedIndex].getShowText().replace(getString(R.string.picker_day), "").toInt(),
//                        mHourList[mHourIndex].getShowText().replace(getString(R.string.picker_hour), "").toInt()
//                    )
                }
            })
        }
//        with(wv_hour) {
//
//            this.OnSelectedListener(object : WheelItemView.OnSelectedListener {
//                override fun onSelected(context: Context, selectedIndex: Int) {
//                    Log.e("CalendarDialog", "OnSelectedListener hour")
//                    mHourIndex = selectedIndex
//                    getMinutes(
//                        mYearList[mYearIndex].getShowText().replace(getString(R.string.picker_year), "").toInt(),
//                        mMonthList[mMonthIndex].getShowText().replace(getString(R.string.picker_month), "").toInt(),
//                        mDayList[mDayIndex].getShowText().replace(getString(R.string.picker_day), "").toInt(),
//                        mHourList[selectedIndex].getShowText().replace(getString(R.string.picker_hour), "").toInt(),
//                        mMinuteList[mMonthIndex].getShowText().replace(getString(R.string.picker_minute), "").toInt()
//                    )
//                }
//            })
//        }
//        with(wv_min) {
//            this.OnSelectedListener(object : WheelItemView.OnSelectedListener {
//                override fun onSelected(context: Context, selectedIndex: Int) {
//                    mMinuterIndex = selectedIndex
//                }
//            })
//        }

        tv_title.text = builder.title ?: ""
        tv_cancle.text = builder.cancle ?: ""
        tv_cancle.clickDelay {
            dismissAllowingStateLoss()
            builder.onCancleListener?.onCancle(this)
        }

        tv_sure.text = builder.confirm ?: ""
        tv_sure.clickDelay {
            dismissAllowingStateLoss()
            val sCalendar = Calendar.getInstance()
            sCalendar.clear()
            sCalendar.set(
                mYearList[mYearIndex].getShowText().replace(getString(R.string.picker_year), "").toInt(),
                mMonthList[mMonthIndex].getShowText().replace(getString(R.string.picker_month), "").toInt()-1,
                mDayList[mDayIndex].getShowText().replace(getString(R.string.picker_day), "").toInt()
            )

            builder.onTimeSelectListener?.onTimeSelect(sCalendar.timeInMillis)
        }
    }

    private fun getYear(year: Int) {
        mYearList.clear()
        var index = -1
        for (i in year - 50..year) {
            index++
            mYearList.add(WheelItem("${i}${getString(R.string.picker_year)}"))
            if (mYearIndex == -1 && i == year) mYearIndex = index
        }
        if (mYearIndex >= mYearList.size) mYearIndex - mYearList.size - 1
        wv_year.setItems(mYearList)
        wv_year.setSelectedIndex(mYearIndex, false)
    }

    private fun getMonth(year: Int, month: Int) {
        mMonthList.clear()
        if (year != getCYear()) {

            for (i in 1..12) {
                mMonthList.add(WheelItem("${format(i)}${getString(R.string.picker_month)}"))
            }
        } else {

            for (i in 1..getCMonth() + 1) {
                mMonthList.add(WheelItem("${format(i)}${getString(R.string.picker_month)}"))
            }
        }
        if (mMonthIndex == -1) mMonthIndex = month
        if (mMonthIndex >= mMonthList.size) mMonthIndex = mMonthList.size - 1
        wv_month.setItems(mMonthList)
        wv_month.setSelectedIndex(mMonthIndex, false)
    }

    private fun getDays(year: Int, month: Int, day: Int) {
        val nCalendar = Calendar.getInstance()
        nCalendar.set(year, month, 0)

        mDayList.clear()
        if (year != getCYear() || month - 1 != getCMonth()) {
            val days = nCalendar.get(Calendar.DAY_OF_MONTH)
            for (i in 1..days) {
                mDayList.add(WheelItem("${format(i)}${getString(R.string.picker_day)}"))
            }
        } else {
            for (i in 1..getCDay()) {
                mDayList.add(WheelItem("${format(i)}${getString(R.string.picker_day)}"))
            }
        }
        if (mDayIndex == -1) mDayIndex = day - 1
        else if (mDayIndex >= mDayList.size) {
            mDayIndex = mDayList.size - 1
        }

        wv_day.setItems(mDayList)
        wv_day.setSelectedIndex(mDayIndex, false)
    }

    private fun getHours(year: Int, month: Int, day: Int, hour: Int) {
        mHourList.clear()
        if (year != getCYear() || month - 1 != getCMonth() || day != getCDay()) {
            for (i in 0 until 24) {
                mHourList.add(WheelItem("${format(i)}${getString(R.string.picker_hour)}"))
            }
        } else {
            for (i in 0..getCHour()) {
                mHourList.add(WheelItem("${format(i)}${getString(R.string.picker_hour)}"))
            }
        }
        if (mHourIndex == -1) mHourIndex = hour

        if (mHourIndex >= mHourList.size) mHourIndex = mHourList.size - 1

        wv_hour.setItems(mHourList)
        wv_hour.setSelectedIndex(mHourIndex, false)

    }

    private fun getMinutes(year: Int, month: Int, day: Int, hour: Int, minuter: Int) {
        mMinuteList.clear()
        Log.e(
            "CalendarDialog",
            "nMonth[${month - 1}],c[${getCMonth()}],day[$day],m[${getCDay()}],nY[${year}],cY[${getCYear()}],y[$hour],[${getCHour()}]"
        )
        if (year != getCYear() || month - 1 != getCMonth() || day != getCDay() || hour != getCHour()) {
            for (i in 0 until 60) {
                mMinuteList.add(WheelItem("${format(i)}${getString(R.string.picker_minute)}"))
            }
            if (mMinuterIndex == -1) mMinuterIndex = minuter
        } else {
            for (i in 0..getCMinute()) {
                mMinuteList.add(WheelItem("${format(i)}${getString(R.string.picker_minute)}"))
            }
            if (mMinuterIndex == -1) mMinuterIndex = minuter
        }

        if (mMinuterIndex >= mMinuteList.size) mMinuterIndex = mMinuteList.size - 1

        wv_min.setItems(mMinuteList)
        wv_min.setSelectedIndex(mMinuterIndex, false)
    }

    private fun getCYear() = mCurrentCalendar.get(Calendar.YEAR)
    private fun getCMonth() = mCurrentCalendar.get(Calendar.MONTH)
    private fun getCDay() = mCurrentCalendar.get(Calendar.DAY_OF_MONTH)
    private fun getCHour() = mCurrentCalendar.get(Calendar.HOUR_OF_DAY)
    private fun getCMinute() = mCurrentCalendar.get(Calendar.MINUTE)

    private fun format(value: Int): String = String.format("%02d", value)

    open class Builder(activity: FragmentActivity) : SBuilder<Builder>(activity) {

        var onTimeSelectListener: OnTimeSelectListenr? = null

        var type: Type = Type.YEAR_MONTH_DAY

        fun setType(type: Type): Builder {
            this.type = type
            return this
        }

        fun setOnTimeSelectListener(onTimeSelectListener: OnTimeSelectListenr?): Builder {
            this.onTimeSelectListener = onTimeSelectListener
            return this
        }

        override fun setGravity(): Int = Gravity.BOTTOM

        override fun builder(): Builder {
            setContentView(R.layout.dialog_calendar)
            return this
        }

        override fun show(activity: FragmentActivity): SDialog? {
            return CalendarDialog(this).also {
                it.show(activity)
            }
        }

    }

    interface OnTimeSelectListenr {
        fun onTimeSelect(time: Long)
    }

}