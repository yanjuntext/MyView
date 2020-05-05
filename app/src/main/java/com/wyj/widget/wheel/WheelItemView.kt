package com.wyj.widget.wheel

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration
import android.view.animation.LinearInterpolator
import android.widget.OverScroller
import androidx.annotation.ColorInt
import com.wyj.widget.R

/**
 *
 *@author abc
 *@time 2019/10/24 15:15
 */
class WheelItemView : View, IWheelViewSetting, View.OnTouchListener {


    private val TAG by lazy { WheelItemView::class.java.simpleName }
    private val MIN_SHOW_COUNT = 5
    private val DEFAULT_ROTATION_X = 45.0f

    private val DEFAULT_VELOCITY_UNITS = 600
    /**字体画笔*/
    private val mTextPaint by lazy {
        TextPaint(Paint.ANTI_ALIAS_FLAG)
    }
    private val mSelectTextPaint by lazy {
        TextPaint(Paint.ANTI_ALIAS_FLAG)
    }
    /**或轮子*/
    private val mCamera: Camera by lazy { Camera() }
    private val mMatrix: Matrix by lazy { Matrix() }

    private var textBaseLine = 0f

    /**数据*/
    private val items: MutableList<IWheel> by lazy { mutableListOf<IWheel>() }

    private var textColor = Color.BLACK
    private var textSize = 0.0f
    private var mSelectTextColor = Color.BLUE
    private var mSelectTestSize = 0.0f
    private var totalOffsetX = 0

    /**the average pixel length of show text.*/
    private var averageShowTextLength = 0f

    private var showCount = 5

    private var drawCount: Int = showCount + 2
    private var defaultRectArray: Array<Rect>? = null
    private var drawRectArray: Array<Rect>? = null
    private var offsetY = 0
    private var totalMoveY = 0//
    private var wheelRotationX = 0f
    private var velocityUnits = 0

    private var itemVerticalSpace = 0

    private var itemHeight = 0
    private var lastX = 0.0f
    private var lastY = 0.0f
    private val calculateResult = IntArray(2)//for saving the calculate result.
    private var selectedIndex = 0//the selected index position
    private var onSelectedListener: OnSelectedListener? = null
    private var animator: ValueAnimator? = null
    private var isScrolling = false
    private var isAnimatorCanceledForwardly = false//whether cancel auto scroll animation forwardly

    private val CLICK_EVENT_INTERNAL_TIME: Long = 1000
    private val rectF = RectF()
    private var touchDownTimeStamp: Long = 0

    //about fling action
    private var mMinimumVelocity: Int = 0
    private var mMaximumVelocity: Int = 0
    private var scaledTouchSlop: Int = 0
    private var mVelocityTracker: VelocityTracker? = null
    private var mOverScroller: OverScroller? = null
    private var flingDirection = 0//-1向上、1向下

    constructor(context: Context) : super(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(context, attrs, defStyle)
    }

    fun init(context: Context, attrs: AttributeSet?, defStyle: Int) {
        mOverScroller = OverScroller(context)
        val viewConfiguration = ViewConfiguration.get(context)
        mMinimumVelocity = viewConfiguration.scaledMinimumFlingVelocity
        mMaximumVelocity = viewConfiguration.scaledMaximumFlingVelocity
        scaledTouchSlop = viewConfiguration.scaledTouchSlop

        attrs?.let {
            val typedArray =
                context.obtainStyledAttributes(it, R.styleable.WheelItemView, defStyle, 0)
            val defaultTextSize = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                14f,
                resources.displayMetrics
            )
            val defaultSelectTextSize =
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    15f,
                    resources.displayMetrics
                )

            textColor = typedArray.getColor(R.styleable.WheelItemView_wheelTextColor, -0xcccccd)
            textSize =
                typedArray.getDimension(R.styleable.WheelItemView_wheelTextSize, defaultTextSize)

            mSelectTextColor = typedArray.getColor(
                R.styleable.WheelItemView_wheelSelectTextColor,
                mSelectTextColor
            )
            mSelectTestSize =
                typedArray.getDimension(
                    R.styleable.WheelItemView_wheelSelectTextSize,
                    defaultSelectTextSize
                )

            showCount = typedArray.getInt(R.styleable.WheelItemView_wheelShowCount, showCount)
            totalOffsetX =
                typedArray.getDimensionPixelSize(R.styleable.WheelItemView_wheelTotalOffsetX, 0)
            itemVerticalSpace = typedArray.getDimensionPixelSize(
                R.styleable.WheelItemView_wheelItemVerticalSpace,
                32
            )
            wheelRotationX =
                typedArray.getFloat(R.styleable.WheelItemView_wheelRotationX, DEFAULT_ROTATION_X)
            velocityUnits = typedArray.getInteger(
                R.styleable.WheelItemView_wheelVelocityUnits,
                DEFAULT_VELOCITY_UNITS
            )
            if (velocityUnits < 0) {
                velocityUnits = Math.abs(velocityUnits)
            }
            typedArray.recycle()
        }
        setOnTouchListener(this)
        initConfig()
    }

    private fun initConfig() {
        mTextPaint.let {
            it.color = textColor
            it.textSize = textSize
        }
        mSelectTextPaint.let {
            it.color = mSelectTextColor
            it.textSize = mSelectTestSize
        }
        updateItemHeight()
        if (showCount < MIN_SHOW_COUNT) {
            showCount = MIN_SHOW_COUNT
        }
        if (showCount % 2 == 0) showCount++
        drawCount = showCount + 2
        defaultRectArray = Array(drawCount) { Rect() }
        drawRectArray = Array(drawCount) { Rect() }
    }

    //更新ITEM高度
    private fun updateItemHeight() {
        if (items.isNotEmpty()) {
            val fontMetrics = mSelectTextPaint.fontMetrics
            val rect = Rect()
            val label = items[0].getShowText()
            mSelectTextPaint.getTextBounds(label, 0, label.length, rect)
            itemHeight = rect.height() + itemVerticalSpace
            textBaseLine =
                -itemHeight / 2.0f + (itemHeight - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top
            Log.e(
                "WheelItemView",
                "textBaseLine[${textBaseLine}],rectH[${rect.height()}],t[${fontMetrics.bottom - fontMetrics.top},top[${fontMetrics.top}],bottom[${fontMetrics.bottom}]]"
            )
        }
        averageShowTextLength = calAverageShowTextLength()
    }

    //计算字体的平局宽度像素
    private fun calAverageShowTextLength(): Float {
        var totalLength = 0f
        var lable: String? = null
        if (items.isNotEmpty()) {
            items.forEach {
                lable = it.getShowText()
                if (!lable.isNullOrEmpty()) {
                    totalLength += mSelectTextPaint.measureText(lable)
                }
            }
        }
        return if (getItemCount() == 0) 0f else totalLength / getItemCount()
    }

    private fun getItemCount() = items.size


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var top = -itemHeight
        for (i in 0 until drawCount) {
            defaultRectArray?.get(i)?.set(0, top, 0, top + itemHeight)
            top += itemHeight
        }
        val heigheSize =
            View.MeasureSpec.makeMeasureSpec(itemHeight * showCount, View.MeasureSpec.EXACTLY)
        super.onMeasure(widthMeasureSpec, heigheSize)
    }


    override fun setTextSize(textSize: Float) {
        this.textSize = textSize
        initConfig()
        requestLayout()
    }

    override fun setTestColor(textColor: Int) {
        this.textColor = textColor
        mTextPaint.color = textColor
        invalidate()
    }

    override fun setSelectTextSize(textSize: Float) {
        this.mSelectTestSize = textSize
        initConfig()
        requestLayout()
    }

    override fun setSelectTextColor(@ColorInt textColor: Int) {
        this.mSelectTextColor = textColor
        mSelectTextPaint.color = textColor
        invalidate()
    }

    override fun setShowCount(showCount: Int) {
        this.showCount = showCount
        initConfig()
        requestLayout()
    }

    override fun setTotalOffsetX(totalOffsetX: Int) {
        this.totalOffsetX = totalOffsetX
        invalidate()
    }

    override fun setItemVerticalSpace(itemVerticalSpace: Int) {
        this.itemVerticalSpace = itemVerticalSpace
        initConfig()
        requestLayout()
    }

    override fun setItems(items: MutableList<IWheel>) {
        this.items.clear()
        this.items.addAll(items)
        initConfig()
        requestLayout()
    }

    override fun getSelectedIndex(): Int = selectedIndex

    override fun setSelectedIndex(targetIndexPosition: Int) {
        setSelectedIndex(targetIndexPosition, true)
    }

    override fun setSelectedIndex(targetIndexPosition: Int, withAnimation: Boolean) {
        if (targetIndexPosition < 0 || targetIndexPosition >= getItemCount())
            throw IndexOutOfBoundsException("Out of array bounds. cound[${getItemCount()}],target[${targetIndexPosition}]")
        if (withAnimation) {
            executeAnimation(totalMoveY, 0 - itemHeight * targetIndexPosition)
        } else {
            totalMoveY = 0 - itemHeight * targetIndexPosition
            selectedIndex = targetIndexPosition
            offsetY = 0
            invalidate()
            if (onSelectedListener != null)
                onSelectedListener?.onSelected(context, selectedIndex)
        }
    }

    override fun setSelectedLabel(targerLabel: String) {
        for (i in 0 until items.size) {
            if (items[i].getShowText() == targerLabel) {
                setSelectedIndex(i)
                continue
            }
        }
    }

    override fun setSelectedLabel(targerLabel: String, withAnimation: Boolean) {
        for (i in 0 until items.size) {
            if (items[i].getShowText() == targerLabel) {
                setSelectedIndex(i, withAnimation)
                continue
            }
        }
    }

    override fun isScrolling(): Boolean = isScrolling

    private fun executeAnimation(vararg values: Int) {
        //if it's invalid animation, call back immediately.
        if (invalidAnimation(*values)) {
            if (onSelectedListener != null)
                onSelectedListener?.onSelected(context, selectedIndex)
            return
        }
        var duration = 0
        for (i in values.indices) {
            if (i > 0) {
                duration += Math.abs(values[i] - values[i - 1])
            }
        }
        if (duration == 0) {
            if (onSelectedListener != null)
                onSelectedListener?.onSelected(context, selectedIndex)
            return
        }
        createAnimatorIfNecessary()
        if (animator?.isRunning == true) {
            isAnimatorCanceledForwardly = true
            animator?.cancel()
        }
        animator?.setIntValues(*values)
        animator?.duration = calSuitableDuration(duration).toLong()
        animator?.start()
    }

    private fun createAnimatorIfNecessary() {
        if (animator == null) {
            animator = ValueAnimator().also {
                it.addUpdateListener { animation ->
                    val tempTotalMoveY = animation.animatedValue as Int
                    updateByTotalMoveY(tempTotalMoveY, 0)
                }
                it.interpolator = LinearInterpolator()
                it.addListener(object : Animator.AnimatorListener {

                    override fun onAnimationStart(animation: Animator) {
                        isScrolling = true
                    }

                    override fun onAnimationEnd(animation: Animator) {
                        isScrolling = false
                        //Cancel animation forwardly.
                        if (isAnimatorCanceledForwardly) {
                            isAnimatorCanceledForwardly = false
                            return
                        }

                        if (onSelectedListener != null)
                            onSelectedListener?.onSelected(context, selectedIndex)
                    }

                    override fun onAnimationCancel(animation: Animator) {

                    }

                    override fun onAnimationRepeat(animation: Animator) {

                    }
                })
            }

        }
    }

    private fun updateByTotalMoveY(totalMoveY: Int, direction: Int) {
        calculateSelectedIndex(totalMoveY, direction)
        this.totalMoveY = totalMoveY
        this.selectedIndex = calculateResult[0]
        this.offsetY = calculateResult[1]
        invalidate()
    }

    private fun calculateSelectedIndex(totalMoveY: Int, direction: Int) {
        var selectedIndex = totalMoveY / (0 - itemHeight)
        var rest = totalMoveY % (0 - itemHeight)
        if (direction > 0 && rest != 0) {
            selectedIndex++
            rest = itemHeight - Math.abs(rest)
        }
        //move up
        if (direction < 0 && Math.abs(rest) >= itemHeight / 2) {
            selectedIndex++
        }
        //move down
        if (direction > 0 && Math.abs(rest) >= itemHeight / 2) {
            selectedIndex--
        }

        selectedIndex = Math.max(selectedIndex, 0)
        selectedIndex = Math.min(selectedIndex, getItemCount() - 1)
        val offsetY = 0 - selectedIndex * itemHeight - totalMoveY
        calculateResult[0] = selectedIndex
        calculateResult[1] = offsetY
    }

    private fun invalidAnimation(vararg values: Int): Boolean {
        if (values == null || values.size < 2)
            return true
        val firstValue = values[0]
        for (value in values) {
            if (firstValue != value)
                return false
        }
        return true
    }

    private fun calSuitableDuration(duration: Int): Int {
        var result = duration
        while (result > 1200) {
            result /= 2
        }
        return result
    }

    override fun OnSelectedListener(onSelectedListener: OnSelectedListener?) {
        this.onSelectedListener = onSelectedListener
    }

    private fun isEmpty() = items.isEmpty()

    fun getItemHeight(): Int {
        return itemHeight
    }

    fun getShowCount(): Int {
        return showCount
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (isEmpty()) return super.onTouchEvent(event)
        initVelocityTrackerIfNotExists()
        run outside@{
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    mVelocityTracker?.clear()
                    flingDirection = 0
                    mOverScroller?.forceFinished(true)
                    if (animator?.isRunning == true) {
                        isAnimatorCanceledForwardly = true
                        animator?.cancel()
                    }
                    lastX = event.x
                    lastY = event.y

                    touchDownTimeStamp = System.currentTimeMillis()
                }
                MotionEvent.ACTION_MOVE -> {
                    mVelocityTracker?.addMovement(event)
                    val currentX = event.x
                    val currentY = event.y
                    val distance = (currentY - lastY).toInt()

                    var direction = 0
                    if (distance != 0) {
                        //if moved, cancel click event
                        touchDownTimeStamp = 0
                        direction = distance / Math.abs(distance)

                        //initialize touch area
                        rectF.set(0f, 0f, width.toFloat(), height.toFloat())
                        if (rectF.contains(currentX, currentY)) {
                            //inside touch area, execute move event.
                            lastX = currentX
                            lastY = currentY
                            updateByTotalMoveY(totalMoveY + distance, direction)
                        }
                    }

                }
                MotionEvent.ACTION_UP,
                MotionEvent.ACTION_CANCEL -> {
                    if (System.currentTimeMillis() - touchDownTimeStamp <= CLICK_EVENT_INTERNAL_TIME) {
                        //it's a click event, do it
                        executeClickEvent(event.x, event.y)
                        return@outside
                    }

                    //calculate current velocity
                    val velocityTracker = mVelocityTracker
                    velocityTracker?.computeCurrentVelocity(
                        velocityUnits,
                        mMaximumVelocity.toFloat()
                    )
                    val currentVelocity = velocityTracker?.yVelocity ?: 0f
                    recycleVelocityTracker()

                    val tempFlingDirection =
                        if (currentVelocity == 0f) 0 else if (currentVelocity < 0) -1 else 1
                    if (Math.abs(currentVelocity) >= mMinimumVelocity) {
                        //it's a fling event.
                        flingDirection = tempFlingDirection
                        mOverScroller?.fling(
                            0,
                            totalMoveY,
                            0,
                            currentVelocity.toInt(),
                            0,
                            0,
                            -(getItemCount() + showCount / 2) * itemHeight,
                            showCount / 2 * itemHeight,
                            0,
                            0
                        )
                        invalidate()
                    } else {
                        calculateSelectedIndex(totalMoveY, tempFlingDirection)
                        selectedIndex = calculateResult[0]
                        offsetY = calculateResult[1]
                        //execute rebound animation
                        executeAnimation(
                            totalMoveY,
                            0 - selectedIndex * itemHeight
                        )
                    }
                }
            }
        }
        return true
    }

    override fun computeScroll() {
        if (mOverScroller?.computeScrollOffset() == true) {
            totalMoveY = mOverScroller?.currY ?: 0
            updateByTotalMoveY(totalMoveY, 0)
            invalidate()
            return
        }

        if (flingDirection != 0) {
            val flingDirectionCopy = flingDirection
            flingDirection = 0
            calculateSelectedIndex(totalMoveY, flingDirectionCopy)
            selectedIndex = calculateResult[0]
            offsetY = calculateResult[1]
            //execute rebound animation
            executeAnimation(
                totalMoveY,
                0 - selectedIndex * itemHeight
            )
        }

    }

    private fun initVelocityTrackerIfNotExists() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain()
        }
    }

    private fun recycleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker?.recycle()
            mVelocityTracker = null
        }
    }

    /**
     * Execute click event.
     *
     * @return true, valid click event, else invalid.
     */
    private fun executeClickEvent(upX: Float, upY: Float) {
        var isValidTempSelectedIndex = false
        var tempSelectedIndex = selectedIndex - drawCount / 2
        for (i in 0 until drawCount) {
            rectF.set(drawRectArray?.get(i))
            if (rectF.contains(upX, upY)) {
                isValidTempSelectedIndex = true
                break
            }
            tempSelectedIndex++
        }
        if (isValidTempSelectedIndex
            && tempSelectedIndex >= 0
            && tempSelectedIndex < getItemCount()
        ) {
            //Move to target selected index
            setSelectedIndex(tempSelectedIndex)
        }
    }


    override fun onDraw(canvas: Canvas?) {
        if (isEmpty()) return
        var tempStartSelectedIndex = selectedIndex - drawCount / 2
        for (i in 0 until drawCount) {
            drawRectArray?.get(i)?.also {
                it.set(defaultRectArray?.get(i))
                it.left = 0
                it.right = width
                if (tempStartSelectedIndex >= 0 && tempStartSelectedIndex < getItemCount()) {
                    drawItem(
                        canvas,
                        it,
                        getItemAt(tempStartSelectedIndex),
                        -offsetY,
                        tempStartSelectedIndex
                    )
                }
                tempStartSelectedIndex++
            }

        }
    }

    private fun getItemAt(position: Int): IWheel? {
        return if (isEmpty() || position < 0 || position >= getItemCount()) null else items[position]
    }

    private fun drawItem(
        canvas: Canvas?,
        rect: Rect?,
        item: IWheel?,
        offsetY: Int,
        currentPositin: Int
    ) {
        val lable = item?.getShowText()
        if (lable.isNullOrEmpty()) return

        rect?.offset(0, offsetY)
        val offsetX = if (currentPositin == selectedIndex) {
            Log.i(TAG, "offsetY[$offsetY].flingDirection[$flingDirection]")
            if (totalOffsetX == 0) 0 else calOffsetX(totalOffsetX, rect)
        } else {
            mTextPaint.alpha = calAlpha(rect)
            if (totalOffsetX == 0) 0 else calOffsetX(totalOffsetX, rect)
        }

        val w = if (currentPositin == selectedIndex) mSelectTextPaint.measureText(lable)
        else mTextPaint.measureText(lable)
        var startX = when {
            totalOffsetX > 0 -> {
                //show text align right
                val rightAlignPosition = (width + averageShowTextLength) / 2.0f
                rightAlignPosition - w + offsetX
            }
            totalOffsetX < 0 -> {
                //show text align left
                val leftAlignPosition = (width - averageShowTextLength) / 2.0f
                leftAlignPosition + offsetX
            }
            else -> //show text align center_horizontal
                (width - w) / 2.0f + offsetX
        }

        var centerX = width / 2.0f
        val centerY = rect?.exactCenterY() ?: 0f
        val baseLine = centerY + textBaseLine

        Log.e("WheelItemView", "baseLine[${baseLine}]")

        mMatrix.reset()
        mCamera.save()
        Log.e(
            TAG,
            "calRotationX[${calRotationX(rect, wheelRotationX)}],wheelRotationX[$wheelRotationX]"
        )
        mCamera.rotateX(calRotationX(rect, wheelRotationX))
        mCamera.getMatrix(mMatrix)
        mCamera.restore()
        mMatrix.preTranslate(-centerX, -centerY)
        mMatrix.postTranslate(centerX, centerY)
        if (totalOffsetX > 0) {
            val skewX = 0 - calSkewX(rect)
            centerX = (startX + w) / 2.0f
            mMatrix.setSkew(skewX, 0f, centerX, centerY)
        } else if (totalOffsetX < 0) {
            val skewX = calSkewX(rect)
            centerX = (startX + w) / 2.0f
            mMatrix.setSkew(skewX, 0f, centerX, centerY)
        }
        canvas?.save()
        canvas?.concat(mMatrix)
        if (currentPositin == selectedIndex) {
            canvas?.drawText(
                lable,
                startX,
                baseLine,
                if (currentPositin == selectedIndex) mSelectTextPaint else mTextPaint
            )
//            }else{
//
//                val left = startX.toInt()
////                val top = (rect?.top ?: 0) + offsetY
//                val top = (rect?.top ?: 0)
//                val right = (startX + (rect?.right ?: 0)).toInt()
////                val bottom = (rect?.top ?: 0) + offsetY
//                val bottom = (rect?.bottom ?: 0)
//                canvas?.clipRect(left, top, right, bottom)
//                mSelectTextPaint.color = textColor
//                canvas?.drawText(lable, startX, baseLine, mSelectTextPaint)
//                canvas?.restore()
//
//                canvas?.save()
//                canvas?.concat(mMatrix)
//                val sLeft =  startX.toInt()
////                val sTop = (rect?.top ?: 0) + offsetY
//                val sTop = (rect?.top ?: 0)
//                val sRight =(startX + (rect?.right ?: 0)).toInt()
////                val sBottom = (rect?.bottom ?: 0)
//                val sBottom = (rect?.top ?: 0) + offsetY
//                canvas?.clipRect(sLeft, sTop, sRight, sBottom)
//                mSelectTextPaint.color = mSelectTextColor
//                canvas?.drawText(lable, startX, baseLine, mSelectTextPaint)
//            }


        } else {
            canvas?.drawText(
                lable,
                startX,
                baseLine,
                if (currentPositin == selectedIndex) mSelectTextPaint else mTextPaint
            )
        }
        canvas?.restore()
    }


    private fun calAlpha(rect: Rect?): Int {
        rect?.let {
            val centerY = height / 2
            val distance = Math.abs(centerY - it.centerY())
            val totalDistance = itemHeight * (showCount / 2)
            val alpha = 0.6f * distance / totalDistance
            return ((1 - alpha) * 0xFF).toInt()
        }
        return 1
    }

    private fun calOffsetX(totalOffsetX: Int, rect: Rect?): Int {
        val centerY = height / 2
        val distance = Math.abs(centerY - (rect?.centerY() ?: 0))
        val totalDistance = itemHeight * (showCount / 2)
        return totalOffsetX * distance / totalDistance
    }

    private fun calRotationX(rect: Rect?, baseRotationX: Float): Float {
        val centerY = height / 2
        val distance = centerY - (rect?.centerY() ?: 0)
        val totalDistance = itemHeight * (showCount / 2)
        return baseRotationX * distance.toFloat() * 1.0f / totalDistance
    }

    private fun calSkewX(rect: Rect?): Float {
        val centerY = height / 2
        val distance = centerY - (rect?.centerY() ?: 0)
        val totalDistance = itemHeight * (showCount / 2)
        return 0.3f * distance / totalDistance
    }


    interface OnSelectedListener {
        fun onSelected(context: Context, selectedIndex: Int)
    }

}