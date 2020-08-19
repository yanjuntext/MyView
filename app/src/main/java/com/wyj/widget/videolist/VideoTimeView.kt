package com.wyj.widget.videolist

import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.*
import android.view.animation.Interpolator
import android.widget.OverScroller
import androidx.core.view.ViewCompat
import com.base.utils.DisplayHelper
import com.base.utils.TimeHelper
import com.wyj.base.log
import com.wyj.widget.ruler.DateUtil
import com.wyj.widget.ruler.RulerEnum
import java.util.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

/**
 * 作者：王颜军 on 2020/8/13 17:09
 * 邮箱：3183424727@qq.com
 *
 * 视频时间轴
 */
class VideoTimeView : View {

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

    private val mScaleGestureListener by lazy { MyScaleGestureListener() }
    private lateinit var mScaleGestureDetector: ScaleGestureDetector


    private val mHourTextPaint by lazy {
        TextPaint().apply {
            isAntiAlias = true
            textSize = 40f
            color = Color.RED
            style = Paint.Style.FILL
        }
    }

    private val mTimeLineRectF by lazy { RectF() }
    private val mTimeLinePaint by lazy {
        Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
            color = Color.parseColor("#FFF2F2F2")
        }
    }


    private var mTimeLineWidth = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        5F,
        resources.displayMetrics
    )


    private val lineWidth = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        1f,
        resources.displayMetrics
    )
    private val mRulerPaint by lazy {
        Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
            color = Color.parseColor("#DDDDDD")
            strokeWidth = lineWidth
        }
    }


    /**
     * 单位秒  以两小时进行分割
     */
    private val timePerSecond = 60 * 60 * 2

    private val margin by lazy {
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            15F,
            resources.displayMetrics
        )
    }

    private val mMaxTextWidth by lazy { getTextWidth("24:00", mHourTextPaint) }

    //距离顶部
    private val mTopMargin = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        80F,
        resources.displayMetrics
    )
    private var mBottomMargin = 0f

    //开始时间 距离1970/01/01 00:00:00 的秒数  时间戳
    private var startTime = 0L

    //当天开始时间 时间戳 如 2020/08/18 00:00:00
    private var endTime = 0L

    //当天当前时间总秒数
    private var totleTime = 0L

    //当前选择的时间 时间戳 如 2020/08/18 10:50:55
    private var selTime = 0L

    // 系统最小滑动距离
    private val mTouchSlop by lazy { ViewConfiguration.get(context).scaledTouchSlop }


    private var mVelocityTracker: VelocityTracker? = null

    private val mQuinticInterpolator by lazy {
        Interpolator { it ->
            val t = it - 1.0f
            t * t * t * t * t + 1.0f
        }
    }

    private var mLastTouchY = 0f
    private var isScaling = false
    private var isFrist = true
    private val mMaxFlingVelocity by lazy { ViewConfiguration.get(context).scaledMaximumFlingVelocity }
    private val mMinFlingVelocity by lazy { ViewConfiguration.get(context).scaledMinimumFlingVelocity }
    private val mViewFlinger by lazy {
        ViewFlinger(
            context,
            OverScroller(context, mQuinticInterpolator)
        )
    }

    private var mTimeBarListener: OnTimeBarListener? = null

    //视频
    private var mVideoList = mutableListOf<VideoItem>()
    private val mVideoRectFs = mutableListOf<RectF>()
    private val mVideoPaint by lazy {
        Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
            color = Color.RED
        }
    }

    constructor(context: Context) : super(context, null) {
        initView()
    }

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


    private fun initView() {
        mScaleGestureDetector = ScaleGestureDetector(context, mScaleGestureListener)
    }


    fun setTimeBarListener(listener: OnTimeBarListener?) {
        mTimeBarListener = listener
    }

    fun start() {
        with(Calendar.getInstance()) {
            startTime = System.currentTimeMillis() / 1000

            endTime = DateUtil.getCurrentDayStartTime(startTime * 1000)

            selTime = startTime
            totleTime =
                get(Calendar.HOUR_OF_DAY) * 3600L + get(Calendar.MINUTE) * 60 + get(Calendar.SECOND)

            this@VideoTimeView.log(
                "startTime[${DateUtil.formatHourMinute(startTime * 1000)}],endTime[${DateUtil.formatHourMinute(
                    endTime * 1000
                )}]," +
                        "[${TimeHelper.getTimeStr(startTime * 1000)}],[${TimeHelper.getTimeStr(
                            endTime * 1000
                        )}]"
            )
        }
        postInvalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.let {
            drawTimeLine(it)
            drawVideos(it)
        }

    }


    //画时间轴
    private fun drawTimeLine(canvas: Canvas) {

        val space = (timePerSecond * 1.0f / rulerSpacing)//每秒的高度

        val left = mMaxTextWidth + margin * 2
        val top = 0f
        val right = left + mTimeLineWidth

        val timeLineHeight =
            totleTime / timePerSecond * rulerSpacing + (totleTime % timePerSecond) * 1.0f / space

        val bottom = timeLineHeight + mTopMargin + mBottomMargin
        mTimeLineRectF.set(left, top, right, bottom)

        mTimeLinePaint.color = Color.parseColor("#FFF2F2F2")
        canvas.drawRect(mTimeLineRectF, mTimeLinePaint)

        mTimeLinePaint.color = Color.RED
        mTimeLineRectF.set(left, top, right, mTopMargin)
        canvas.drawRect(mTimeLineRectF, mTimeLinePaint)


        val sen: Int = DateUtil.getSecond(startTime * 1000)
        val rTime = startTime - sen


        val senHeight = sen / space

        val total = (totleTime - sen) / 60 + 1

        log("total[$total],timeLineHeight[$timeLineHeight],rulerSpace[$rulerSpacing],[$mMinHourRulerSpace]")

        val margintTop = mTopMargin + senHeight

        val minuterSpace = 60 / space//每分钟的高度
        (1..total).forEachIndexed { index, _ ->
            val start = rTime - 60 * index

            val rminute = DateUtil.getMinute(start * 1000)

            val time = DateUtil.formatHourMinute(start * 1000)
            log("draw time[$time]")
            val textHeight = getTextHeight(time, mHourTextPaint)
//            if (sDay != cDay) return@forEachIndexed
            val h = margintTop + minuterSpace * index
            when (mCurrentRulerEnum) {
                RulerEnum.ONE_MINUTER -> {
                    canvas.drawText(
                        time,
                        margin,
                        h + textHeight / 2f,
                        mHourTextPaint
                    )
                    canvas.drawLine(
                        margin + margin / 2 + mMaxTextWidth,
                        h + lineWidth / 2f,
                        margin * 2 + mMaxTextWidth,
                        h + lineWidth / 2f,
                        mRulerPaint
                    )
                }
                RulerEnum.TEN_MINUTER -> {

                    canvas.drawLine(
                        if (rminute % 10 == 0) margin + margin / 2 + mMaxTextWidth else margin + margin * 2 / 3 + mMaxTextWidth,
                        h + lineWidth / 2f,
                        margin * 2 + mMaxTextWidth,
                        h + lineWidth / 2f,
                        mRulerPaint
                    )


                    if (rminute % 10 == 0) {


                        canvas.drawText(
                            time,
                            margin,
                            h + textHeight / 2f,
                            mHourTextPaint
                        )
                    }


                }
                RulerEnum.HALF_HOUR -> {
                    if (rminute % 10 == 0) {
                        canvas.drawLine(
                            if (rminute % 30 == 0) margin + margin / 2 + mMaxTextWidth else margin + margin * 2 / 3 + mMaxTextWidth,
                            h + lineWidth / 2f,
                            margin * 2 + mMaxTextWidth,
                            h + lineWidth / 2f,
                            mRulerPaint
                        )
                    }

                    if (rminute % 30 == 0) {

                        canvas.drawText(
                            time,
                            margin,
                            h + textHeight / 2f,
                            mHourTextPaint
                        )


                    }
                }
                RulerEnum.HOUR -> {
                    if (rminute % 10 == 0) {
                        canvas.drawLine(
                            if (rminute % 60 == 0) margin + margin / 2 + mMaxTextWidth else margin + margin * 2 / 3 + mMaxTextWidth,
                            h + lineWidth / 2f,
                            margin * 2 + mMaxTextWidth,
                            h + lineWidth / 2f,
                            mRulerPaint
                        )
                    }
                    if (rminute % 60 == 0) {
                        canvas.drawText(
                            time,
                            margin,
                            h + textHeight / 2f,
                            mHourTextPaint
                        )
                    }

                }
                else -> {
                    val hour = DateUtil.getHour(start * 1000)
                    if (rminute == 0) {
                        canvas.drawLine(
                            if (hour % 2 == 0) margin + margin / 2 + mMaxTextWidth else margin + margin * 2 / 3 + mMaxTextWidth,
                            h + lineWidth / 2f,
                            margin * 2 + mMaxTextWidth,
                            h + lineWidth / 2f,
                            mRulerPaint
                        )
                    }

                    if (hour % 2 == 0 && rminute == 0) {

                        canvas.drawText(
                            time,
                            margin,
                            h + textHeight / 2f,
                            mHourTextPaint
                        )


                    }

                }

            }
        }


    }

    //画视频区域
    private fun drawVideos(canvas: Canvas) {
        //mTimeLinePaint.color = Color.parseColor("#FFF2F2F2")
        //canvas.drawRect(mTimeLineRectF, mTimeLinePaint)

        if (mVideoList.isEmpty()) return
        if (isScaling || isFrist) {
            isFrist = false
            mTimeBarListener?.onVideos(getTotalHeight(), rulerSpacing)
//            mTimeBarListener?.onTimeBarScale(rulerSpacing,scrollY)
        }
        mVideoRectFs.forEach {
            canvas.drawRect(it, mVideoPaint)
        }

//        val space = timePerSecond * 1.0f / rulerSpacing
//
//        mVideoList.forEachIndexed { index, it ->
//
//
//            val duration = startTime - it.getFirstStartTime() + 1
//
//            val left = mMaxTextWidth + margin * 2
//            val top = duration * 1.0f / space + mTopMargin
//            val right = left + mTimeLineWidth
//
//            val time = it.getDurationTime()
//            val bottom = time * 1.0f / space + top
//
//            mTimeLineRectF.set(left, top, right, bottom)
//            it.setTimeLineTopY(top)
//            canvas.drawRect(mTimeLineRectF, mVideoPaint)
//        }
//        if(isScaling || isFrist){
//            isFrist = false
//            mTimeBarListener?.onVideos(getTotalHeight(),rulerSpacing)
////            mTimeBarListener?.onTimeBarScale(rulerSpacing,scrollY)
//        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mBottomMargin = height - mTopMargin
    }

    private fun getTextWidth(text: String, paint: TextPaint) = paint.measureText(text)


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        mScaleGestureDetector.onTouchEvent(event)
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain()
        }
        var eventAddedToVelocityTracker = false

        val vtev = MotionEvent.obtain(event)

        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                mTimeBarListener?.onTimeBarCurrentTime(selTime)
                mViewFlinger.stop()
                mLastTouchY = event.y
            }
            MotionEvent.ACTION_POINTER_DOWN -> {

            }
            MotionEvent.ACTION_MOVE -> {
                if (!isScaling) {
                    var dy = mLastTouchY - event.y

                    if (abs(dy) > abs(mTouchSlop)) {
                        if (dy > 0) dy -= mTouchSlop else dy += mTouchSlop
                    }

                    mLastTouchY = event.y
                    constrainScrollBy(0, dy.toInt())
                }
            }
            MotionEvent.ACTION_UP -> {
                if (!isScaling) {
                    mVelocityTracker?.addMovement(vtev)
                    eventAddedToVelocityTracker = true
                    mVelocityTracker?.computeCurrentVelocity(1000, mMaxFlingVelocity.toFloat())
                    var yVelocity = -(mVelocityTracker?.yVelocity ?: 0f)

                    yVelocity = if (abs(yVelocity) < mMinFlingVelocity.toFloat()) 0F else max(
                        -mMaxFlingVelocity.toFloat(),
                        min(yVelocity, mMaxFlingVelocity.toFloat())
                    )
                    if (yVelocity != 0f) {
                        mViewFlinger.fling(yVelocity.toInt())
                    }
                }
                isScaling = false

            }
            MotionEvent.ACTION_CANCEL -> {
                isScaling = false
            }
        }

        if (!eventAddedToVelocityTracker) {
            mVelocityTracker?.addMovement(vtev)
        }
        vtev.recycle()
        return true
    }

    //滑动出路
    private fun constrainScrollBy(dx: Int, dy: Int) {
        val totalHeight =
            totleTime / timePerSecond * rulerSpacing + (totleTime % timePerSecond) * 1.0f / (timePerSecond * 1.0f / rulerSpacing)

        val scrollY = scrollY
        val sDy = when {
            scrollY + dy > totalHeight -> {
                (totalHeight - scrollY + 0.5f).toInt()
            }
            scrollY + dy < 0 -> {
                -scrollY
            }
            else -> dy
        }
        scrollBy(dx, sDy)
    }

    //计算当前选中时间
    override fun scrollBy(x: Int, y: Int) {
        super.scrollBy(x, y)
        val time: Long = (timePerSecond * 1.0f / rulerSpacing * scrollY).toLong()
        selTime = startTime - time
        if (selTime < endTime) selTime = endTime
        log("scrollBy time[${DateUtil.formatHourMinute(selTime * 1000)}]")
        mTimeBarListener?.onTimeBarCurrentTime(selTime)
        mTimeBarListener?.onScrollY(y)

        log("scrollY scrollBy[${y}],[$scrollY]")
    }

    private fun updateLinePosition() {
        val height =
            ((startTime - selTime) * 1.0f / (timePerSecond * 1.0f / rulerSpacing) + 0.5f).toInt()
        scrollY = height
//        scrollTo(0, height)
        mTimeBarListener?.onTimeBarScale(rulerSpacing, height)

        this@VideoTimeView.log("onScaleEnd updateLinePosition")
        this@VideoTimeView.log("calculationVideoRectF  scrollY[$scrollY],height[$height]")
        log("scrollY updateLinePosition[${height}],[$scrollY]")
    }

    fun getTopMargin() = mTopMargin

    fun getLeftMargin() = margin * 2 + mMaxTextWidth + mTimeLineWidth

    fun getBottomMargin() = mBottomMargin

    fun getTotalHeight() = totleTime * 1.0f / getHeightPerSecond() + mTopMargin + mBottomMargin

    //每秒高度
    fun getHeightPerSecond() = timePerSecond * 1.0f / rulerSpacing

    //设置视频集合
    fun setVideoList(list: MutableList<VideoItem>) {
        mVideoList.clear()
        mVideoList.addAll(list)
        calculationVideoRectF()
        postInvalidate()
    }

    private fun calculationVideoRectF() {
        val space = timePerSecond * 1.0f / rulerSpacing

        mVideoList.forEachIndexed { index, it ->
            val rect = if (index >= mVideoRectFs.size) {
                RectF().apply {
                    mVideoRectFs.add(this)
                }
            } else mVideoRectFs[index]

            val duration = startTime - it.getFirstStartTime() + 1

            val left = mMaxTextWidth + margin * 2
            val top = duration * 1.0f / space + mTopMargin
            val right = left + mTimeLineWidth

            val time = it.getDurationTime()
            val bottom = time * 1.0f / space + top

            rect.set(left, top, right, bottom)
            it.setTimeLineTopY(top)
            log("calculationVideoRectF VideoItem[${it.getTimeLineTopY()}]")
        }
        if (isScaling) {
            mTimeBarListener?.onVideos(getTotalHeight(), rulerSpacing)
        }

    }

    private fun getTextHeight(text: String, paint: TextPaint): Float {
        val rect = Rect()
        paint.getTextBounds(text, 0, text.length, rect)
        return rect.height().toFloat()
    }

    //惯性滑动
    inner class ViewFlinger(val context: Context, val mScroller: OverScroller) : Runnable {

        private var mLastFlingY = 0
        private var mEatRunOnAnimationRequest = false
        private var mReSchedulePostAnimationCallback = false

        override fun run() {
            disableRunOnAnimationRequests()
            if (mScroller.computeScrollOffset()) {
                val y = mScroller.currY
                val dy = y - mLastFlingY
                mLastFlingY = y
                constrainScrollBy(0, dy)
                postOnAnimation()
            }
            enableRunOnAnimationRequests()
        }

        fun fling(valocityY: Int) {
            mLastFlingY = 0
            mScroller.fling(
                0,
                0,
                0,
                valocityY,
                Int.MIN_VALUE,
                Int.MAX_VALUE,
                Int.MIN_VALUE,
                Int.MAX_VALUE
            )
            postOnAnimation()
        }

        fun stop() {
            removeCallbacks(this)
            mScroller.abortAnimation()
        }

        private fun disableRunOnAnimationRequests() {
            mReSchedulePostAnimationCallback = false
            mEatRunOnAnimationRequest = true
        }

        private fun enableRunOnAnimationRequests() {
            mEatRunOnAnimationRequest = false
            if (mReSchedulePostAnimationCallback) {
                postOnAnimation()
            }
        }

        fun postOnAnimation() {
            if (mEatRunOnAnimationRequest) {
                mReSchedulePostAnimationCallback = true
            } else {
                removeCallbacks(this)
                ViewCompat.postOnAnimation(this@VideoTimeView, this)
            }
        }

    }

    //缩放
    inner class MyScaleGestureListener : ScaleGestureDetector.OnScaleGestureListener {

        private var mLastScale = -1f

        override fun onScaleBegin(detector: ScaleGestureDetector?): Boolean {
            this@VideoTimeView.log("onScaleBegin")
            isScaling = true
            return true
        }

        override fun onScaleEnd(detector: ScaleGestureDetector?) {
            mLastScale = -1f
            this@VideoTimeView.log("onScaleEnd")
            mTimeBarListener?.onVideos(getTotalHeight(), rulerSpacing)
            updateLinePosition()
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
                            92f
                        )
                    } else {
                        if (currDirection > 0) rulerSpacing += DisplayHelper.dp2px(162f) else rulerSpacing -= DisplayHelper.dp2px(
                            324f
                        )
                    }

                } else {
                    calculationVideoRectF()
                    postInvalidate()
                    updateLinePosition()
                    return true
                }
                if (rulerSpacing < mMinRulerSpace) rulerSpacing =
                    mMinRulerSpace else if (rulerSpacing > mMaxRulerSpace)
                    rulerSpacing = mMaxRulerSpace

                mCurrentRulerEnum = when {
                    rulerSpacing < mMinRulerSpace + (mMinHourRulerSpace - mMinRulerSpace) / 2 -> RulerEnum.TWO_HOUR
                    rulerSpacing < mMinHourRulerSpace + (mMinHalfHourRulerSpace - mMinHourRulerSpace) / 2 -> RulerEnum.HOUR
                    rulerSpacing < mMinHalfHourRulerSpace + (mMinTenMinuterRulerSpace - mMinHalfHourRulerSpace) / 2 -> RulerEnum.HALF_HOUR
                    rulerSpacing < mMinTenMinuterRulerSpace + (mMaxRulerSpace - mMinTenMinuterRulerSpace) / 2f -> RulerEnum.TEN_MINUTER
                    else -> RulerEnum.ONE_MINUTER
                }
                log("mCurrentRulerEnum[$mCurrentRulerEnum],[$rulerSpacing],[${mMinTenMinuterRulerSpace + (mMaxRulerSpace - mMinTenMinuterRulerSpace) / 2f}]")
                calculationVideoRectF()
                postInvalidate()
                updateLinePosition()
            }

            return true
        }

    }


    interface OnTimeBarListener {
        fun onTimeBarScale(scaleHeight: Float, scrollY: Int)
        fun onTimeBarCurrentTime(time: Long)
        fun onScrollY(scrollY: Int)
        fun onVideos(totalHeight: Float, space: Float)
    }
}