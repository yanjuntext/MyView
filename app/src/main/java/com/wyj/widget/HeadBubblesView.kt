package com.wyj.widget

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.graphics.*
import android.os.Looper
import android.os.Message
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.animation.doOnCancel
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnPause
import com.base.utils.DisplayHelper
import com.base.utils.image.ImageLoader
import com.wyj.base.log
import kotlinx.coroutines.Job


/**
 * 仿马蜂窝 泡泡头像
 * */
class HeadBubblesView : FrameLayout {

    private val mHeadImageLists = mutableListOf<HeadBubblesItem>()
    private val margin by lazy { DisplayHelper.dp2px(20f) }
    private val size by lazy { DisplayHelper.dp2px(40f) }
    private val mAnimatorDuration = 1500L

    private val mControlOnePoint by lazy { PointF() }
    private val mControlTwoPoint by lazy { PointF() }
    private var path: Path? = null
    private var mPathMeasure: PathMeasure? = null

    private val mPathPaint by lazy {
        Paint().apply {
            style = Paint.Style.STROKE
            color = Color.BLUE
            strokeWidth = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                5F,
                resources.displayMetrics
            )
        }
    }

    private var addCount = 0

    private val mHandler by lazy {
        object : MyHandler(context as Activity, Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                this@HeadBubblesView.log("mHandler[${msg.what}]")
                if (msg.what == 1) {
                    getHeadBubblesImage().start()
                    send()
                }
            }
        }
    }

    private fun send() {
        val duration = Math.random() * 1500
        mHandler.sendEmptyMessageDelayed(
            1,
            if (duration < 500) 500L else if (duration > 1500) 1000 else duration.toLong()
        )
    }

    private var mJob: Job? = null

    constructor(context: Context) : super(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

    }


    fun start() {
        mHandler.removeCallbacksAndMessages(null)
        mHandler.sendEmptyMessage(1)
    }


    override fun onDetachedFromWindow() {
        mJob?.cancel()
        mJob = null
        mHandler.removeCallbacksAndMessages(null)
        mHeadImageLists.forEach {
            it.remove()
        }
        super.onDetachedFromWindow()
    }

    private fun getHeadBubblesImage(): HeadBubblesItem {


        mHeadImageLists.forEach {
            log("SHeadBubbles$it")
        }
        log("SHeadBubbles---------------addCount[$addCount]")
        val freeHeadBubbles = mHeadImageLists.filter { it.end }
        return if (freeHeadBubbles.isEmpty()) {

            if (path == null) {
                path = Path()
            }
            log("addCount[$addCount]")
            if (addCount % 2 == 0) {
                val space = (height - margin - size) / 4f
                val x = width - margin - size * 3f
                val y = height - margin - size - space
                log("onAttachedToWindow x[$x],y[$y]")
                mControlOnePoint.set(x, y)
                mControlTwoPoint.set(width + margin + size * 1f, height - margin - size - space * 3)
            } else {
                val space = (height - margin - size) / 4f
                val x = width + margin + size * 1f
                val y = height - margin - size - space
                log("onAttachedToWindow x[$x],y[$y]--------")
                mControlOnePoint.set(x, y)
                mControlTwoPoint.set(width - margin - size * 3f, height - margin - size - space * 3)
            }

            path?.reset()
            path?.let {
                val y = height - margin - size / 2f
                val x = width - margin - size / 2f

                log("startX[$x],startY[$y]")

                it.moveTo(x, y)
                it.cubicTo(
                    mControlOnePoint.x,
                    mControlOnePoint.y,
                    mControlTwoPoint.x,
                    mControlTwoPoint.y,
                    width.toFloat() - margin - size / 2f,
                    0f
                )
                mPathMeasure = PathMeasure(it, false)
            }

            addCount++
            val headBubbles = AppCompatImageView(context)
            val animatorSet = getScaleAnimator(headBubbles)
            val headBubblesView =
                HeadBubblesItem(headBubbles, animatorSet, mPathMeasure, getPathMeasureAnimator())
            mHeadImageLists.add(headBubblesView)
            headBubblesView
        } else {
            freeHeadBubbles[0]
        }
    }

    private fun getScaleAnimator(headBubbles: AppCompatImageView): AnimatorSet {
        val scaleX = ObjectAnimator.ofFloat(headBubbles, "scaleX", 1f, 0f)
        val scaleY = ObjectAnimator.ofFloat(headBubbles, "scaleY", 1f, 0f)
        val alpha = ObjectAnimator.ofFloat(headBubbles, "alpha", 1f, 0f)
        return AnimatorSet().apply {
            play(scaleX).with(scaleY).with(alpha)
        }.setDuration(mAnimatorDuration)
    }

    private fun getPathMeasureAnimator(): ValueAnimator =
        ValueAnimator.ofFloat(0f, 1f).setDuration(mAnimatorDuration)

    inner class HeadBubblesItem(
        val headBubbles: AppCompatImageView,
        val animatorSet: AnimatorSet,
        val pathMeasure: PathMeasure?,
        val pathMeasureAnimator: ValueAnimator,
        var end: Boolean = false
    ) {
        private val pos: FloatArray = FloatArray(2)
        private val tan: FloatArray = FloatArray(2)

        init {
            animatorSet.doOnEnd {
                this@HeadBubblesView.log("addCound[doOnEnd]")
                this@HeadBubblesView.removeView(headBubbles)
                end = true
            }
            animatorSet.doOnCancel {
                this@HeadBubblesView.log("addCound[doOnCancel]")
                this@HeadBubblesView.removeView(headBubbles)
                end = true
            }
            animatorSet.doOnPause {
                this@HeadBubblesView.log("addCound[doOnPause]")
                this@HeadBubblesView.removeView(headBubbles)
                end = true
            }

            pathMeasureAnimator.addUpdateListener {
                val percent = it.animatedValue as Float
                pathMeasure?.let { mearsure ->
                    val length = mearsure.length * percent
                    mearsure.getPosTan(length, pos, tan)
                    headBubbles.translationX =
                        pos[0] - (this@HeadBubblesView.width - margin - size / 2f)
                    headBubbles.translationY =
                        pos[1] - (this@HeadBubblesView.height - margin - size / 2f)
                    this@HeadBubblesView.log("pos(${pos[0]},${pos[1]}),width[${this@HeadBubblesView.width}],height[${this@HeadBubblesView.height}]")
                }
            }
        }

        fun start() {
            this@HeadBubblesView.addView(
                headBubbles,
                this@HeadBubblesView.size,
                this@HeadBubblesView.size
            )
            ImageLoader.with(context)
                .load(R.drawable.ic_1)
                .circle()
                .into(headBubbles)
            headBubbles.alpha = 1f
            val params = headBubbles.layoutParams
            if (params is LayoutParams) {
                params.gravity = Gravity.BOTTOM or Gravity.END
                params.rightMargin = this@HeadBubblesView.margin
                params.bottomMargin = this@HeadBubblesView.margin
            }
            end = false
            animatorSet.start()
            pathMeasureAnimator.start()
        }

        fun remove() {
            this@HeadBubblesView.removeView(headBubbles)
            end = true
            animatorSet.cancel()
        }

        override fun toString(): String {
            return "HeadBubblesItem(end=$end)"
        }


    }


}