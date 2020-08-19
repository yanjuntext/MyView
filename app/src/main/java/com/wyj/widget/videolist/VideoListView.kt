package com.wyj.widget.videolist

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.base.utils.TimeHelper
import com.wyj.base.log
import com.wyj.widget.R
import com.wyj.widget.ruler.DateUtil
import kotlin.random.Random

/**
 * 作者：王颜军 on 2020/8/18 11:06
 * 邮箱：3183424727@qq.com
 */
class VideoListView : FrameLayout,
    VideoTimeView.OnTimeBarListener {

    private val FORMAT_TIME = "HH:mm:ss"

    private val mSelMargin by lazy {
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            5f,
            resources.displayMetrics
        )
    }


    private var mVideoTimeView: VideoTimeView? = null

    private var mVideoTimeViewGroup: ViewGroup? = null
    private var mTvTimeGroup: AppCompatTextView? = null
    private var mRecycler: MyRecycler? = null
    private var mVideoAdapter: VideoListAdapter? = null
    private var mRecyclerScrollY = 0

    private val mVideoAllList by lazy { mutableListOf<VideoItem>() }
    private val mVideoShowList by lazy { mutableListOf<VideoItem>() }

    private val mSelTimeHalfHeight = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        20f,
        resources.displayMetrics
    )


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


    private fun initView() {
        initVideoTimeView()
        initRecyclerView()
        initSelTimeView()
    }


    //初始化视频时间轴View
    private fun initVideoTimeView() {
        mVideoTimeView = VideoTimeView(context)
        addView(
            mVideoTimeView,
            LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        )
        mVideoTimeView?.setTimeBarListener(this)
        mVideoTimeView?.start()

        val start = TimeHelper.getCurrentHour() - 1
        (start downTo 0).forEach {
            val startTime = DateUtil.getDateLong(it, Random.nextInt(60))
            val endTime = startTime - Random.nextInt(120) - 10
            mVideoAllList.add(VideoItem(startTime, endTime, ""))
        }
        mVideoTimeView?.setVideoList(mVideoAllList)

    }

    //初始化图像UI控件
    private fun initRecyclerView() {
        mRecycler = MyRecycler(context)
        val manager = object : LinearLayoutManager(context) {
            override fun canScrollVertically(): Boolean = true
        }
        manager.orientation = LinearLayoutManager.VERTICAL
        mRecycler?.layoutManager = manager

        addView(
            mRecycler,
            LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        )

        val params = mRecycler?.layoutParams
        if (params is LayoutParams) {
            params.leftMargin = ((mVideoTimeView?.getLeftMargin() ?: 0f) + mSelMargin * 2).toInt()
        }
        mRecycler?.layoutParams = params

        mVideoAdapter = VideoListAdapter(context, mVideoShowList as MutableList<IVideoItem>?)
        mVideoAdapter?.bottomMargin = mVideoTimeView?.getBottomMargin() ?: 0f
        mRecycler?.adapter = mVideoAdapter

    }

    //初始化选择时间
    private fun initSelTimeView() {

        mVideoTimeViewGroup = LayoutInflater.from(context)
            .inflate(R.layout.layout_video_time, this, false) as ViewGroup
        addView(
            mVideoTimeViewGroup,
            LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        )

        val params = mVideoTimeViewGroup?.layoutParams
        if (params is LayoutParams) {
            params.topMargin =
                ((mVideoTimeView?.getTopMargin() ?: 0f) - mSelTimeHalfHeight).toInt()
            params.leftMargin = ((mVideoTimeView?.getLeftMargin() ?: 0f) + mSelMargin).toInt()
        }
        mVideoTimeViewGroup?.layoutParams = params

        mTvTimeGroup = mVideoTimeViewGroup?.findViewById(R.id.tv_time)
    }

    override fun onTimeBarScale(scaleHeight: Float, scrollY: Int) {
        log("onScaleScroll recycler scroll[${mRecyclerScrollY}],[$scrollY],rulerspace[$scaleHeight]")
        log("scrollY onTimeBarScale[${mRecyclerScrollY}],[$scrollY]")
//        onScrollY(-mRecyclerScrollY)
//        onScrollY()
        onScrollY(scrollY-mRecyclerScrollY)

    }

    override fun onTimeBarCurrentTime(time: Long) {
        mTvTimeGroup?.text = TimeHelper.getTimeStr(time * 1000, FORMAT_TIME)
    }

    override fun onScrollY(scrollY: Int) {
        mRecycler?.scrollBy(0, scrollY)
        mRecyclerScrollY += scrollY
        if(mRecyclerScrollY < 0){
            onScrollY(-mRecyclerScrollY)
        }
        log("scrollY onScrollY[${mRecyclerScrollY}],[$scrollY]")
    }

    private val VIDEO_DEFAULT_HEIGHT = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        80f,
        resources.displayMetrics
    ).toInt()


    @Synchronized
    override fun onVideos(totalHeight:Float, space:Float) {
        mRecyclerScrollY = 0
        mRecycler?.scrollToPosition(0)
        mVideoShowList.clear()
        val iterator = mVideoAllList.iterator()
        log("calculationVideoRectF VideoItem -------------------- --- [${mVideoTimeView?.scrollY}],[$mRecyclerScrollY]")
        while (iterator.hasNext()) {
            val item: VideoItem = iterator.next()
            if (mVideoShowList.isEmpty()) {
                item.clearStartTimes()
                item.setTopHeight(item.getTimeLineTopY())
                log("onVideos --- [${item.getTimeLineTopY()}]---,rulerSpacing[$space]")
                mVideoShowList.add(item)
            } else {
                val last = mVideoShowList[mVideoShowList.size - 1]
                if (last.getTimeLineTopY() + VIDEO_DEFAULT_HEIGHT >= item.getTimeLineTopY()) {
                    last.addStartTimes(item.getFirstStartTime())
                    last.addEndTimes(item.getFirstEndTime())
                } else {
                    item.clearStartTimes()
                    item.setTopHeight(item.getTimeLineTopY() - last.getTimeLineTopY() - VIDEO_DEFAULT_HEIGHT +1)
                    mVideoShowList.add(item)
                }
            }

            log("calculationVideoRectF VideoItem[${item.getTimeLineTopY()}] ---")
        }

        if (mVideoShowList.isNotEmpty()) {
            mVideoAdapter?.bottomMargin = totalHeight - mVideoShowList[mVideoShowList.size - 1].getTimeLineTopY() - VIDEO_DEFAULT_HEIGHT
        }

        mVideoAdapter?.notifyDataSetChanged()



    }

}