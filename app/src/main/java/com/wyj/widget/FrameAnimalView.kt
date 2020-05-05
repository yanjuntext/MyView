package com.wyj.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.Surface
import android.view.SurfaceHolder
import android.view.TextureView
import kotlinx.coroutines.*
import kotlin.math.log

/**
 *帧动画View
 *@author abc
 *@time 2020/1/11 9:02
 */
class FrameAnimalView : TextureView, TextureView.SurfaceTextureListener, SurfaceHolder.Callback {

    private var mSurface: Surface? = null
    private var mPaint: Paint = Paint().also { it.isDither = true }
    /**图片*/
    private val mBitmapResource: MutableList<Int> = mutableListOf()
    /**时间*/
    private val mBitmapTime: MutableList<Long> = mutableListOf()

    private var mFrameAnimJob: Job? = null
    private var mFrameRect: RectF? = null

    private var mCanvas: Canvas? = null

    private var isCycle: Boolean = false
    private var isStop = false

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    init {
        //透明
        this.isOpaque = true
        surfaceTextureListener = this
    }

    fun asCycle() {
        isCycle = true
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {
    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean = false

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
        mSurface = Surface(surface)
        Log.i("FrameAnimalView", "onSurfaceTextureAvailable drawFrame[${mSurface == null}]")
        if (mBitmapResource.isNotEmpty() && mBitmapTime.isNotEmpty() && mFrameAnimJob == null) {
            startAnimal(false)
        }
    }

    fun setBitmapResource(bitmaps: MutableList<Int>?) {
        if (bitmaps != null) {
            mBitmapResource.addAll(bitmaps)
        }
    }

    fun setBitmapTime(times: MutableList<Long>?) {
        if (times != null) {
            mBitmapTime.addAll(times)
        }
    }

    fun startAnimal(start:Boolean = true) {
        stopAnimal()
        isStop = false
        drawFirstBitmap()
        if(!start) return
        if (mBitmapResource.isEmpty() || mBitmapTime.isEmpty() || mSurface == null) return
        mFrameAnimJob = GlobalScope.launch(Dispatchers.IO) {
            drawFrame()
        }
    }

    private suspend fun drawFrame() {
        Log.i("FrameAnimalView", "drawFrame[${mSurface == null}]")
        if (mSurface != null) {
            mBitmapTime.forEachIndexed { index, time ->
                val bitmap = BitmapFactory.decodeResource(resources, mBitmapResource[index])
                if (mFrameRect == null) {
                    val width = bitmap.width
                    val height = bitmap.height
                    val rW: Float = ((this.measuredWidth - width) / 2.0).toFloat()
                    val rh: Float = ((this.measuredHeight - height) / 2.0).toFloat()

                    mFrameRect = RectF(rW, rh, this.measuredWidth - rW, this.measuredHeight - rh)
                }
                try {
                    val canvas = mSurface?.lockCanvas(null)
                    canvas?.drawColor(Color.WHITE)
                    mFrameRect?.let {
                        canvas?.drawBitmap(bitmap, null, it, mPaint)
                    }
                    mSurface?.unlockCanvasAndPost(canvas)
                    bitmap.recycle()
                    delay(time)
                } catch (e: Exception) {
                }

            }
            if (isCycle && !isStop) drawFrame()
        }

    }

    private fun drawFirstBitmap(){
        if (mSurface != null && mBitmapTime.isNotEmpty()) {

            val bitmap = BitmapFactory.decodeResource(resources, mBitmapResource[0])
            if (mFrameRect == null) {
                val width = bitmap.width
                val height = bitmap.height
                val rW: Float = ((this.measuredWidth - width) / 2.0).toFloat()
                val rh: Float = ((this.measuredHeight - height) / 2.0).toFloat()

                mFrameRect = RectF(rW, rh, this.measuredWidth - rW, this.measuredHeight - rh)
            }
            try {
                val canvas = mSurface?.lockCanvas(null)
                canvas?.drawColor(Color.WHITE)
                mFrameRect?.let {
                    canvas?.drawBitmap(bitmap, null, it, mPaint)
                }
                mSurface?.unlockCanvasAndPost(canvas)
                bitmap.recycle()
            } catch (e: Exception) {
            }

        }
    }

    fun stopAnimal() {
        isStop = true
        drawFirstBitmap()
        mSurface?.unlockCanvasAndPost(mSurface?.lockCanvas(null))
        mCanvas = null
        mFrameAnimJob?.cancel()
        mFrameAnimJob = null
        mFrameRect = null
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        mFrameAnimJob?.cancel()
        mSurface?.unlockCanvasAndPost(mSurface?.lockCanvas(null))
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        mSurface = holder?.surface
        holder?.addCallback(this)
        Log.i("FrameAnimalView", "surfaceCreated drawFrame[${mSurface == null}]")
    }
}