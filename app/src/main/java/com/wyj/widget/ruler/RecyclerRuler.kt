package com.wyj.widget.ruler

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.text.TextPaint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.base.utils.DisplayHelper
import com.wyj.base.log
import java.util.*
import kotlin.math.abs

/**
 * 作者：王颜军 on 2020/8/14 17:06
 * 邮箱：3183424727@qq.com
 */
class RecyclerRuler : RecyclerView, ScaleGestureDetector.OnScaleGestureListener {

    private lateinit var manager: MyLinearLayoutManager

    /**
     * 单位秒  以两小时进行分割
     */
    private val timePerSecond = 60 * 60 * 2

    private var mCurrentRulerEnum = RulerEnum.HOUR


    private val mMinRulerSpace = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        80f,
        resources.displayMetrics
    )

    private val mMinHourRulerSpace = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        160f,
        resources.displayMetrics
    )
    private val mMinHalfHourRulerSpace = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        260f,
        resources.displayMetrics
    )
    private val mMinTenMinuterRulerSpace = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        720f,
        resources.displayMetrics
    )

    private val mMaxRulerSpace = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        7200f,
        resources.displayMetrics
    )
    private var rulerSpacing = mMinHourRulerSpace


    private var startTime = 0L
    private var totleTime = 0L
    private var selTime = 0L

    private val mScaleGestureDetector by lazy {
        ScaleGestureDetector(context, this)
    }

    private var isScale = false

    /**
     * 是否自动滚动
     */
    private var mScrollState = SCROLL_STATE_IDLE

    constructor(context: Context) : super(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs, 0) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        initView()
    }

    private val mRulerTextPaint by lazy {
        TextPaint().apply {
            isAntiAlias = true
            textSize = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                10f,
                resources.displayMetrics
            )
            color = Color.parseColor("#FF0000")
        }
    }

    override fun onDraw(c: Canvas?) {
        super.onDraw(c)

        c?.drawText("这是测试View", 100f, 50f, mRulerTextPaint)
    }

    private fun initView() {
        manager =
            MyLinearLayoutManager(context)
        manager.orientation = LinearLayoutManager.VERTICAL
        layoutManager = manager

        with(Calendar.getInstance()) {
            startTime = this.timeInMillis / 1000
            selTime = startTime
            totleTime =
                get(Calendar.HOUR_OF_DAY) * 3600L + get(Calendar.MINUTE) * 60 + get(Calendar.SECOND)

            this@RecyclerRuler.log(
                "startTime[${DateUtil.formatHourMinute(startTime * 1000)}],[${get(
                    Calendar.HOUR_OF_DAY
                )}]"
            )
        }
        adapter = MyAdapter(context)
    }

    override fun onTouchEvent(e: MotionEvent?): Boolean {
        mScaleGestureDetector.onTouchEvent(e)
        if (e?.action == MotionEvent.ACTION_POINTER_DOWN) {
            isScale = true
        }
        if (e?.action == MotionEvent.ACTION_DOWN || e?.action == MotionEvent.ACTION_UP
            || e?.action == MotionEvent.ACTION_CANCEL
        ) {
            isScale = false
        }
        return isScale or super.onTouchEvent(e)
    }

    var firstItemHeight = DisplayHelper.dp2px(100f)
    override fun onScrolled(dx: Int, dy: Int) {

        if (isScale) {
            return
        }


        if (mScrollState == SCROLL_STATE_IDLE) {
            updateLinePosition()
            return
        }


        val firstVisibleItemPosition = manager.findFirstVisibleItemPosition()
        log("onScrolled first[$firstVisibleItemPosition],[${manager.findViewByPosition(0) == null}],scrollY[$scrollY]")


        manager.findViewByPosition(firstVisibleItemPosition)?.let {
//            if (firstVisibleItemPosition != 0) {
                //获取上屏幕的偏移量
                val offsetTime: Long =
                    ((abs(it.top) / it.height.toFloat()) * timePerSecond + (firstVisibleItemPosition) * timePerSecond).toLong()
//                    ((abs(it.top) / it.height.toFloat()) * timePerSecond + (firstVisibleItemPosition-1) * timePerSecond).toLong()
                val cStartTime = startTime - offsetTime

                selTime = cStartTime
                log("selTime[$selTime],[${DateUtil.formatHourMinute(selTime*1000)}]  onScrolled")
                log(
                    "onScrolled[${DateUtil.formatHourMinute((cStartTime) * 1000)}],top[${it.top}]" +
                            ",offset[$offsetTime],cStartTime[$cStartTime],[${DateUtil.formatHourMinute(
                                cStartTime * 1000
                            )}]" +
                            ",[${DateUtil.formatHourMinute((startTime - 7200) * 1000)}],[${DateUtil.formatHourMinute(
                                (startTime) * 1000
                            )}]"
                )
//            }
        }
    }

    //更新选择时间点
    private fun updateLinePosition() {
        val index = (startTime - selTime) / timePerSecond

        val offsetTime = (startTime - selTime) % timePerSecond

        val offset = offsetTime * 1.0f / (timePerSecond * 1.0f / rulerSpacing)
        log("selTime[$selTime]  updateLinePosition,index[$index],offset[$offset],[${DateUtil.formatHourMinute(selTime*1000)}]")

        manager.scrollToPositionWithOffset(index.toInt(), -offset.toInt())
    }

    override fun onScrollStateChanged(state: Int) {
        mScrollState = state
        if (state == 0) {
//            onScrolled(0,0)
            updateLinePosition()
        }


    }


    override fun onScaleBegin(detector: ScaleGestureDetector?): Boolean {
        manager.iscanScrolllVertically = false
        return true
    }

    override fun onScaleEnd(detector: ScaleGestureDetector?) {
        manager.iscanScrolllVertically = true
    }

    override fun onScale(detector: ScaleGestureDetector?): Boolean {

        detector?.let {
            val currDirection = it.currentSpan - detector.previousSpan
            if (abs(currDirection) > 2) {
                if (rulerSpacing < mMinRulerSpace + (mMinHourRulerSpace - mMinRulerSpace) / 2) {
                    if (currDirection > 0) rulerSpacing += DisplayHelper.dp2px(2f) else rulerSpacing -= DisplayHelper.dp2px(
                        2f
                    )
                } else if (rulerSpacing < mMinHourRulerSpace + (mMinHalfHourRulerSpace - mMinHourRulerSpace) / 2) {
                    if (currDirection > 0) rulerSpacing += DisplayHelper.dp2px(2f) else rulerSpacing -= DisplayHelper.dp2px(
                        2f
                    )
                } else if (rulerSpacing < mMinHalfHourRulerSpace + (mMinTenMinuterRulerSpace - mMinHalfHourRulerSpace) / 2) {
                    if (currDirection > 0) rulerSpacing += DisplayHelper.dp2px(11f) else rulerSpacing -= DisplayHelper.dp2px(
                        11f
                    )
                } else if (rulerSpacing < mMinTenMinuterRulerSpace + (mMaxRulerSpace - mMinTenMinuterRulerSpace) / 2f) {
                    if (currDirection > 0) rulerSpacing += DisplayHelper.dp2px(162f) else rulerSpacing -= DisplayHelper.dp2px(
                        24f
                    )
                } else {
                    if (currDirection > 0) rulerSpacing += DisplayHelper.dp2px(162f) else rulerSpacing -= DisplayHelper.dp2px(
                        324f
                    )
                }

            } else {
                return true

            }
            if (rulerSpacing < mMinRulerSpace) rulerSpacing =
                mMinRulerSpace else if (rulerSpacing > mMaxRulerSpace)
                rulerSpacing = mMaxRulerSpace
//            val space = mMaxRulerSpace / 8

            mCurrentRulerEnum = when {
                rulerSpacing < mMinRulerSpace + (mMinHourRulerSpace - mMinRulerSpace) / 2 -> RulerEnum.TWO_HOUR
                rulerSpacing < mMinHourRulerSpace + (mMinHalfHourRulerSpace - mMinHourRulerSpace) / 2 -> RulerEnum.HOUR
                rulerSpacing < mMinHalfHourRulerSpace + (mMinTenMinuterRulerSpace - mMinHalfHourRulerSpace) / 2 -> RulerEnum.HALF_HOUR
                rulerSpacing < mMinTenMinuterRulerSpace + (mMaxRulerSpace - mMinTenMinuterRulerSpace) / 2f -> RulerEnum.TEN_MINUTER
                else -> RulerEnum.ONE_MINUTER
            }

//            mCurrentRulerEnum = when (rulerSpacing) {
//                in space * 7 + 0.01f..mMaxRulerSpace -> RulerEnum.ONE_MINUTER
//                in space * 5 + 0.01f..space * 7 -> RulerEnum.TEN_MINUTER
//                in space * 3f + 0.01f..space * 5 -> RulerEnum.HALF_HOUR
//                in space + 0.01f..space * 3f -> RulerEnum.HOUR
//                in mMinRulerSpace..space -> RulerEnum.TWO_HOUR
//                else -> RulerEnum.ONE_MINUTER
//            }
            log("mCurrentRulerEnum[$mCurrentRulerEnum],[$rulerSpacing],[${mMinTenMinuterRulerSpace + (mMaxRulerSpace - mMinTenMinuterRulerSpace) / 2f}]")
            adapter?.notifyDataSetChanged()
            updateLinePosition()

        }

        return true
    }

    class MyLinearLayoutManager(context: Context) : LinearLayoutManager(context) {

        var iscanScrolllVertically = true

        override fun canScrollVertically(): Boolean {
            return iscanScrolllVertically
        }


    }

    inner class MyAdapter(val context: Context) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {


        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bindView(position: Int) {

                if (itemView is RulerItemView) {
//                    if (getItemViewType(position) == 1 || getItemViewType(position) == 2) {
//
//                    } else {
//                        itemView.startTime = startTime - (position - 1) * timePerSecond
                    itemView.startTime = startTime - (position) * timePerSecond
                        itemView.ruleEnum = mCurrentRulerEnum
                        itemView.lineColor = if (position % 2 == 0) Color.parseColor("#DDDDDD")
                        else Color.parseColor("#D0D0D0")
//                    }


                }

                this@RecyclerRuler.log("startTime[${DateUtil.formatHourMinute(startTime * 1000)}],[${(startTime - position * timePerSecond) * 1000}]")

                itemView.layoutParams =
//                    if (getItemViewType(position) == 1) {
//                        LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DisplayHelper.dp2px(100f))
//                    } else if (getItemViewType(position) == 2) {
//                        LayoutParams(
//                            ViewGroup.LayoutParams.MATCH_PARENT,
//                            DisplayHelper.dp2px(1000f)
//                        )
//                    } else {
                        LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, rulerSpacing.toInt())
//                    }

            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(RulerItemView(context))

        override fun getItemCount(): Int = (totleTime / timePerSecond + 1).toInt()
//        override fun getItemCount(): Int = (totleTime / timePerSecond + 3).toInt()

        override fun getItemViewType(position: Int): Int {
            return when (position) {
                0 -> {
                    1
                }
                itemCount - 1 -> {
                    2
                }
                else -> {
                    0
                }
            }
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bindView(position)
        }


    }

}