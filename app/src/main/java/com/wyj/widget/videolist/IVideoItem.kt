package com.wyj.widget.videolist

/**
 * 作者：王颜军 on 2020/8/18 17:08
 * 邮箱：3183424727@qq.com
 * 时间从大到小
 * getStartTime() > getEndTime()
 */
interface IVideoItem {

    fun getFirstStartTime(): Long

    fun getFirstEndTime(): Long

    //开始时间 集合
    fun getStartTimes(): MutableList<Long>

    //结束时间 集合
    fun getEndTimes(): MutableList<Long>


    //开始时间和结束时间 时间差
    fun getDurationTime(): Long

    //距离顶部的高度
    fun getTopHeight(): Float

    //视频总时长
    fun getVideoDuration(): Int

    //多个视频集合
    fun getVideoUrl(): MutableList<String>?

    //视频缩略图
    fun getVideoImgUrl(): String?

    //时间轴 对应的顶部Y坐标
    fun getTimeLineTopY(): Float
}