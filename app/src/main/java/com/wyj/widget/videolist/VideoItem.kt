package com.wyj.widget.videolist

/**
 * 作者：王颜军 on 2020/8/18 17:32
 * 邮箱：3183424727@qq.com
 */
data class VideoItem(
    private var startTime: Long,
    private var endTime: Long,
    private var mVideoImgUrl: String
) :
    IVideoItem {

    private val mStartTimes by lazy { mutableListOf<Long>() }
    private val mEndTimes by lazy { mutableListOf<Long>() }

    private var topHeight = 0f

    private var mTopY = 0f


    override fun getFirstStartTime(): Long = startTime

    override fun getFirstEndTime(): Long = endTime

    fun clearStartTimes() {
        mStartTimes.clear()
        mEndTimes.clear()
    }

    fun addStartTimes(startTimes: Long) {
        mStartTimes.add(startTimes)
    }

    fun addEndTimes(endTime: Long) {
        mEndTimes.add(endTime)
    }

    override fun getStartTimes(): MutableList<Long> = mStartTimes

    override fun getEndTimes(): MutableList<Long> = mEndTimes

    override fun getDurationTime(): Long = startTime - endTime + 1

    fun setTopHeight(topHeight: Float) {
        this.topHeight = topHeight
    }

    override fun getTopHeight(): Float = topHeight

    override fun getVideoDuration(): Int {
        var duration = startTime - endTime + 1
        mStartTimes.forEachIndexed { index, time ->
            duration += if (index >= mEndTimes.size) 0L else mEndTimes[index] - time + 1
        }
        return duration.toInt()
    }

    override fun getVideoUrl(): MutableList<String>? = mutableListOf()
    override fun getVideoImgUrl(): String? = mVideoImgUrl
    override fun getTimeLineTopY(): Float = mTopY

    fun setTimeLineTopY(topY: Float) {
        mTopY = topY
    }
}