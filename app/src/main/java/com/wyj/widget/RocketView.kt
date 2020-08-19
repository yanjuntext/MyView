package com.wyj.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View

/**
 * 作者：王颜军 on 2020/8/7 11:09
 * 邮箱：3183424727@qq.com
 */
class RocketView : View {

    private var mRocket: Bitmap? = null

    private val mCloudList by lazy { mutableListOf<Cloud>() }

    private val strokewidth = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        2f,
        resources.displayMetrics
    )

    private val mPaint by lazy {
        Paint().apply {
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeWidth = strokewidth
            color = Color.parseColor("#06B4F9")
        }
    }

    private var mReginRectF:RectF? = null
    private var mReckotDstRectF:RectF? = null

    private val mBitmapPaint by lazy {
        Paint().apply {
            isAntiAlias = true
            color = Color.WHITE
        }
    }

    constructor(context: Context) : super(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs, 0) {
        initAttrs(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        initAttrs(context, attrs)
    }


    private fun initAttrs(context: Context, attrs: AttributeSet?) {

    }

    init {
        mRocket = BitmapFactory.decodeResource(resources, R.drawable.ic_dev_version_up, null)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        mRocket?.let {
            val bW = it.width
            val bH = it.height

            val scale = width / 2f / bW
            val matrix = Matrix()
            matrix.postScale(scale, scale)
            mRocket = Bitmap.createBitmap(it, 0, 0, bW, bH, matrix, true)
        }


    }

    private fun initCloudList() {

    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.let {
            drawCiclr(it)
            drawRegion(it)
            drawRocket(it)
        }

    }

    private fun drawRocket(canvas: Canvas) {
        mRocket?.let {
            val bW = it.width
            val bH = it.height
            if(mReckotDstRectF == null){
                mReckotDstRectF = RectF((width-bW)/2f,(height-bH)/2f ,(width-bW)/2f + bW,(width-bH)/2f+ bH)
            }
            mReckotDstRectF?.let { dst->
                canvas.drawBitmap(it,null,dst,mBitmapPaint)
            }



        }

    }

    private fun drawRegion(canvas: Canvas) {

        val space = mPaint.strokeWidth / 2f
        with(mPaint){
            style = Paint.Style.STROKE
            color = Color.parseColor("#06B4F9")
        }
        if(mReginRectF == null){
            mReginRectF = RectF(space, space, width - space, height - space)
        }
        mReginRectF?.let {
            canvas.drawArc(it, 0f, 360f, false, mPaint)
        }
    }

    private fun drawCiclr(canvas: Canvas) {
        with(mPaint){
            style = Paint.Style.FILL
            color = Color.WHITE
        }
        canvas.drawCircle(width/2f,height/2f,width/2f,mPaint)

    }


    class Cloud(var x:Int,var y:Int,var height:Int){

    }

}