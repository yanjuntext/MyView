package com.wyj.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateInterpolator

/**
 * 开关按钮
 *@author abc
 *@time 2020/1/4 15:20
 */

open class SwitchButton : View {
    companion object {
        val STATE_SWITCH_OFF = 1
        val STATE_SWITCH_OFF2 = 2
        val STATE_SWITCH_ON = 3
        val STATE_SWITCH_ON2 = 4
    }


    private var mInterpolator =
        AccelerateInterpolator(2f)
    private var mPaint = Paint()
    private var mBackgroundPath = Path()
    private var mBarPath = Path()
    private var mBound = RectF()

    private var mAnim1 = 0f
    private var mAnim2 = 0f
    private var mShadowGradient: RadialGradient? = null

    /** 按钮宽高形状比率(0,1] 不推荐大幅度调整  */
    protected var mAspectRatio = 0.68f
    /** (0,1]  */
    protected var mAnimationSpeed = 0.1f

    /** 上一个选中状态  */
    private var mLastCheckedState = 0
    /** 当前的选中状态  */
    private var mCheckedState = 0

    private var isCanVisibleDrawing = false

    /** 是否显示按钮阴影  */
    private var isShadow = false
    /** 是否选中  */
    protected var mChecked = false

    /** 开启状态背景色  */
    protected var mAccentColor = -0xb4289d
    /** 开启状态按钮描边色  */
    protected var mPrimaryDarkColor = -0x404041
    /** 关闭状态描边色  */
    protected var mOffColor = -0x1c1c1d
    /** 关闭状态按钮描边色  */
    protected var mOffDarkColor = -0x404041
    /** 按钮阴影色  */
    protected var mShadowColor = -0xcccccd
    /** 监听器  */
    private var mListener: OnCheckedChangeListener? = null

    private var mRight = 0f
    private var mCenterX = 0f
    private var mCenterY = 0f
    private var mScale = 0f

    private var mOffset = 0f
    private var mRadius = 0f
    private var mStrokeWidth = 0f
    private var mWidth = 0f
    private var mLeft = 0f
    private var bRight = 0f
    private var mOnLeftX = 0f
    private var mOn2LeftX = 0f
    private var mOff2LeftX = 0f
    private var mOffLeftX = 0f

    private var mShadowReservedHeight = 0f

    constructor(context: Context) : super(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs, 0) {
        initialize(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initialize(attrs)
    }

    private fun initialize(attrs: AttributeSet?) {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
        val array =
            context.obtainStyledAttributes(attrs, R.styleable.SwitchButton)
        mChecked = array.getBoolean(R.styleable.SwitchButton_android_checked, mChecked)
        isEnabled = array.getBoolean(R.styleable.SwitchButton_android_enabled, isEnabled)

        mAccentColor = array.getColor(R.styleable.SwitchButton_selectColor, mAccentColor)
        mOffColor = array.getColor(R.styleable.SwitchButton_unSelectColor, mOffColor)
        mPrimaryDarkColor =
            array.getColor(R.styleable.SwitchButton_primaryDarkColor, mPrimaryDarkColor)
        mOffDarkColor = array.getColor(R.styleable.SwitchButton_offDarkColor, mOffDarkColor)

        mCheckedState =
            if (mChecked) STATE_SWITCH_ON else STATE_SWITCH_OFF
        mLastCheckedState = mCheckedState
        array.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var widthMeasureSpec = widthMeasureSpec
        var heightMeasureSpec = heightMeasureSpec
        when (MeasureSpec.getMode(widthMeasureSpec)) {
            MeasureSpec.AT_MOST, MeasureSpec.UNSPECIFIED -> widthMeasureSpec =
                MeasureSpec.makeMeasureSpec(
                    (TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        56f,
                        resources.displayMetrics
                    )
                            + paddingLeft + paddingRight).toInt(),
                    MeasureSpec.EXACTLY
                )
            MeasureSpec.EXACTLY -> {
            }
            else -> {
            }
        }
        when (MeasureSpec.getMode(heightMeasureSpec)) {
            MeasureSpec.AT_MOST, MeasureSpec.UNSPECIFIED -> heightMeasureSpec =
                MeasureSpec.makeMeasureSpec(
                    (MeasureSpec.getSize(widthMeasureSpec) * mAspectRatio).toInt()
                            + paddingTop + paddingBottom,
                    MeasureSpec.EXACTLY
                )
            MeasureSpec.EXACTLY -> {
            }
            else -> {
            }
        }
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        isCanVisibleDrawing =
            w > paddingLeft + paddingRight && h > paddingTop + paddingBottom
        if (isCanVisibleDrawing) {
            val actuallyDrawingAreaWidth = w - paddingLeft - paddingRight
            val actuallyDrawingAreaHeight = h - paddingTop - paddingBottom
            val actuallyDrawingAreaLeft: Int
            val actuallyDrawingAreaRight: Int
            val actuallyDrawingAreaTop: Int
            val actuallyDrawingAreaBottom: Int
            if (actuallyDrawingAreaWidth * mAspectRatio < actuallyDrawingAreaHeight) {
                actuallyDrawingAreaLeft = paddingLeft
                actuallyDrawingAreaRight = w - paddingRight
                val heightExtraSize =
                    (actuallyDrawingAreaHeight - actuallyDrawingAreaWidth * mAspectRatio).toInt()
                actuallyDrawingAreaTop = paddingTop + heightExtraSize / 2
                actuallyDrawingAreaBottom = height - paddingBottom - heightExtraSize / 2
            } else {
                val widthExtraSize =
                    (actuallyDrawingAreaWidth - actuallyDrawingAreaHeight / mAspectRatio).toInt()
                actuallyDrawingAreaLeft = paddingLeft + widthExtraSize / 2
                actuallyDrawingAreaRight = width - paddingRight - widthExtraSize / 2
                actuallyDrawingAreaTop = paddingTop
                actuallyDrawingAreaBottom = height - paddingBottom
            }
            mShadowReservedHeight =
                ((actuallyDrawingAreaBottom - actuallyDrawingAreaTop) * 0.07f).toInt().toFloat()
            val left = actuallyDrawingAreaLeft.toFloat()
            val top = actuallyDrawingAreaTop + mShadowReservedHeight
            mRight = actuallyDrawingAreaRight.toFloat()
            val bottom = actuallyDrawingAreaBottom - mShadowReservedHeight
            val sHeight = bottom - top
            mCenterX = (mRight + left) / 2
            mCenterY = (bottom + top) / 2
            mLeft = left
            mWidth = bottom - top
            bRight = left + mWidth
            // OfB
            val halfHeightOfS = mWidth / 2
            mRadius = halfHeightOfS * 0.95f
            // offset of switching
            mOffset = mRadius * 0.2f
            mStrokeWidth = (halfHeightOfS - mRadius) * 2
            mOnLeftX = mRight - mWidth
            mOn2LeftX = mOnLeftX - mOffset
            mOffLeftX = left
            mOff2LeftX = mOffLeftX + mOffset
            mScale = 1 - mStrokeWidth / sHeight
            mBackgroundPath.reset()
            val bound = RectF()
            bound.top = top
            bound.bottom = bottom
            bound.left = left
            bound.right = left + sHeight
            mBackgroundPath.arcTo(bound, 90f, 180f)
            bound.left = mRight - sHeight
            bound.right = mRight
            mBackgroundPath.arcTo(bound, 270f, 180f)
            mBackgroundPath.close()
            mBound.left = mLeft
            mBound.right = bRight
            // bTop = sTop
            mBound.top = top + mStrokeWidth / 2
            // bBottom = sBottom
            mBound.bottom = bottom - mStrokeWidth / 2
            val bCenterX = (bRight + mLeft) / 2
            val bCenterY = (bottom + top) / 2
            val red = mShadowColor shr 16 and 0xFF
            val green = mShadowColor shr 8 and 0xFF
            val blue = mShadowColor and 0xFF
            mShadowGradient = RadialGradient(
                bCenterX,
                bCenterY,
                mRadius,
                Color.argb(200, red, green, blue),
                Color.argb(25, red, green, blue),
                Shader.TileMode.CLAMP
            )
        }
    }

    private fun calcBPath(percent: Float) {
        mBarPath.reset()
        mBound.left = mLeft + mStrokeWidth / 2
        mBound.right = bRight - mStrokeWidth / 2
        mBarPath.arcTo(mBound, 90f, 180f)
        mBound.left = mLeft + percent * mOffset + mStrokeWidth / 2
        mBound.right = bRight + percent * mOffset - mStrokeWidth / 2
        mBarPath.arcTo(mBound, 270f, 180f)
        mBarPath.close()
    }

    private fun calcBTranslate(percent: Float): Float {
        var result = 0f
        when (mCheckedState - mLastCheckedState) {
            1 -> if (mCheckedState == SwitchButton.STATE_SWITCH_OFF2) { // off -> off2
                result = mOffLeftX
            } else if (mCheckedState == STATE_SWITCH_ON) { // on2 -> on
                result = mOnLeftX - (mOnLeftX - mOn2LeftX) * percent
            }
            2 -> if (mCheckedState == STATE_SWITCH_ON) { // off2 -> on
                result = mOnLeftX - (mOnLeftX - mOffLeftX) * percent
            } else if (mCheckedState == SwitchButton.STATE_SWITCH_ON2) { // off -> on2
                result = mOn2LeftX - (mOn2LeftX - mOffLeftX) * percent
            }
            3 ->  // off -> on
                result = mOnLeftX - (mOnLeftX - mOffLeftX) * percent
            -1 -> if (mCheckedState == SwitchButton.STATE_SWITCH_ON2) { // on -> on2
                result = mOn2LeftX + (mOnLeftX - mOn2LeftX) * percent
            } else if (mCheckedState == STATE_SWITCH_OFF) { // off2 -> off
                result = mOffLeftX
            }
            -2 -> if (mCheckedState == STATE_SWITCH_OFF) { // on2 -> off
                result = mOffLeftX + (mOn2LeftX - mOffLeftX) * percent
            } else if (mCheckedState == SwitchButton.STATE_SWITCH_OFF2) { // on -> off2
                result = mOff2LeftX + (mOnLeftX - mOff2LeftX) * percent
            }
            -3 ->  // on -> off
                result = mOffLeftX + (mOnLeftX - mOffLeftX) * percent
            0 -> if (mCheckedState == STATE_SWITCH_OFF) { //  off -> off
                result = mOffLeftX
            } else if (mCheckedState == STATE_SWITCH_ON) { // on -> on
                result = mOnLeftX
            }
            else -> if (mCheckedState == STATE_SWITCH_OFF) {
                result = mOffLeftX
            } else if (mCheckedState == STATE_SWITCH_ON) {
                result = mOnLeftX
            }
        }
        return result - mOffLeftX
    }

    override fun onDraw(canvas: Canvas) {
        if (!isCanVisibleDrawing) {
            return
        }
        mPaint.isAntiAlias = true
        val isOn =
            mCheckedState == STATE_SWITCH_ON || mCheckedState == SwitchButton.STATE_SWITCH_ON2
        // Draw background
        mPaint.style = Paint.Style.FILL
        mPaint.color = if (isOn) mAccentColor else mOffColor
        canvas.drawPath(mBackgroundPath, mPaint)
        mAnim1 = if (mAnim1 - mAnimationSpeed > 0) mAnim1 - mAnimationSpeed else 0f
        mAnim2 = if (mAnim2 - mAnimationSpeed > 0) mAnim2 - mAnimationSpeed else 0f
        val dsAnim = mInterpolator.getInterpolation(mAnim1)
        val dbAnim = mInterpolator.getInterpolation(mAnim2)
        // Draw background animation
        val scale = mScale * if (isOn) dsAnim else 1 - dsAnim
        val scaleOffset =
            (mRight - mCenterX - mRadius) * if (isOn) 1 - dsAnim else dsAnim
        canvas.save()
        canvas.scale(scale, scale, mCenterX + scaleOffset, mCenterY)
        mPaint.color = -0x1
        canvas.drawPath(mBackgroundPath, mPaint)
        canvas.restore()
        // To prepare center bar path
        canvas.save()
        canvas.translate(calcBTranslate(dbAnim), mShadowReservedHeight)
        val isState2 =
            mCheckedState == SwitchButton.STATE_SWITCH_ON2 || mCheckedState == SwitchButton.STATE_SWITCH_OFF2
        calcBPath(if (isState2) 1 - dbAnim else dbAnim)
        // Use center bar path to draw shadow
        if (isShadow) {
            mPaint.style = Paint.Style.FILL
            mPaint.shader = mShadowGradient
            canvas.drawPath(mBarPath, mPaint)
            mPaint.shader = null
        }
        canvas.translate(0f, -mShadowReservedHeight)
        // draw bar
        canvas.scale(0.98f, 0.98f, mWidth / 2, mWidth / 2)
        mPaint.style = Paint.Style.FILL
        mPaint.color = -0x1
        canvas.drawPath(mBarPath, mPaint)
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = mStrokeWidth * 0.5f
        mPaint.color = if (isOn) mPrimaryDarkColor else mOffDarkColor
        canvas.drawPath(mBarPath, mPaint)
        canvas.restore()
        mPaint.reset()
        if (mAnim1 > 0 || mAnim2 > 0) {
            invalidate()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        super.onTouchEvent(event)
        if (isEnabled
            && (mCheckedState == STATE_SWITCH_ON || mCheckedState == STATE_SWITCH_OFF)
            && mAnim1 * mAnim2 == 0f
        ) {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                }
                MotionEvent.ACTION_UP -> {
                    mLastCheckedState = mCheckedState
                    mAnim2 = 1f
                    when (mCheckedState) {
                        STATE_SWITCH_OFF -> {
                            setChecked(true, false)
                            if (mListener != null) {
                                mListener!!.onCheckedChanged(this, true)
                            }
                        }
                        STATE_SWITCH_ON -> {
                            setChecked(false, false)
                            if (mListener != null) {
                                mListener!!.onCheckedChanged(this, false)
                            }
                        }
                        else -> {
                        }
                    }
                }
                else -> {
                }
            }
        }
        return true
    }

    override fun onSaveInstanceState(): Parcelable? {

        return super.onSaveInstanceState()?.let {
            val state = SavedState(it)
            state.checked = mChecked
            state
        }


    }

    override fun onRestoreInstanceState(state: Parcelable) {
        val savedState = state as SavedState
        super.onRestoreInstanceState(savedState.superState)
        mChecked = savedState.checked
        mCheckedState =
            if (mChecked) STATE_SWITCH_ON else STATE_SWITCH_OFF
        invalidate()
    }

    fun setColor(newColorPrimary: Int, newColorPrimaryDark: Int) {
        setColor(newColorPrimary, newColorPrimaryDark, mOffColor, mOffDarkColor)
    }

    fun setColor(
        newColorPrimary: Int,
        newColorPrimaryDark: Int,
        newColorOff: Int,
        newColorOffDark: Int
    ) {
        setColor(newColorPrimary, newColorPrimaryDark, newColorOff, newColorOffDark, mShadowColor)
    }

    fun setColor(
        newColorPrimary: Int,
        newColorPrimaryDark: Int,
        newColorOff: Int,
        newColorOffDark: Int,
        newColorShadow: Int
    ) {
        mAccentColor = newColorPrimary
        mPrimaryDarkColor = newColorPrimaryDark
        mOffColor = newColorOff
        mOffDarkColor = newColorOffDark
        mShadowColor = newColorShadow
        invalidate()
    }

    /**
     * 设置按钮阴影开关
     */
    fun setShadow(shadow: Boolean) {
        isShadow = shadow
        invalidate()
    }

    /**
     * 当前状态是否选中
     */
    fun isChecked(): Boolean {
        return mChecked
    }

    /**
     * 设置选择状态（默认会回调监听器）
     */
    fun setChecked(checked: Boolean) { // 回调监听器
        setChecked(checked, true)
    }

    /**
     * 设置选择状态
     */
    fun setChecked(checked: Boolean, callback: Boolean) {
        val newState: Int =
            if (checked) STATE_SWITCH_ON else STATE_SWITCH_OFF
        if (newState == mCheckedState) {
            return
        }
        if (newState == STATE_SWITCH_ON && (mCheckedState == STATE_SWITCH_OFF || mCheckedState == SwitchButton.STATE_SWITCH_OFF2)
            || newState == STATE_SWITCH_OFF && (mCheckedState == STATE_SWITCH_ON || mCheckedState == SwitchButton.STATE_SWITCH_ON2)
        ) {
            mAnim1 = 1f
        }
        mAnim2 = 1f
        if (!mChecked && newState == STATE_SWITCH_ON) {
            mChecked = true
        } else if (mChecked && newState == STATE_SWITCH_OFF) {
            mChecked = false
        }
        mLastCheckedState = mCheckedState
        mCheckedState = newState
        postInvalidate()
//        if (callback && mListener != null) {
//            mListener!!.onCheckedChanged(this, checked)
//        }
    }

    /**
     * 设置选中状态改变监听
     */
    fun setOnCheckedChangeListener(listener: OnCheckedChangeListener) {
        mListener = listener
    }

    interface OnCheckedChangeListener {
        /**
         * 回调监听
         *
         * @param button            切换按钮
         * @param isChecked         是否选中
         */
        fun onCheckedChanged(button: SwitchButton?, isChecked: Boolean)
    }

    open class SavedState : BaseSavedState {

        var checked = false

        constructor(superState: Parcelable) : super(superState)
        private constructor(parcel: Parcel) : super(parcel) {
            checked = parcel.readInt() == 1
        }

        override fun writeToParcel(out: Parcel?, flags: Int) {
            super.writeToParcel(out, flags)
            out?.writeInt(if (checked) 1 else 0)
        }

        override fun describeContents(): Int {
            return 0
        }


        companion object CREATOR : Parcelable.Creator<SavedState> {
            override fun createFromParcel(parcel: Parcel): SavedState {
                return SavedState(parcel)
            }

            override fun newArray(size: Int): Array<SavedState?> {
                return arrayOfNulls(size)
            }
        }
    }
}