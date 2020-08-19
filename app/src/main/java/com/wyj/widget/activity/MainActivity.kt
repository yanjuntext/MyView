package com.wyj.widget.activity

import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.base.utils.TimeHelper
import com.base.utils.image.ImageLoader
import com.wyj.base.log
import com.wyj.widget.AudioView
import com.wyj.widget.HoodView
import com.wyj.widget.R
import com.wyj.widget.videolist.VideoTimeView
import com.wyj.widget.livedata.Test
import com.wyj.widget.livedata.TestLiveData
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.random.Random

class MainActivity : AppCompatActivity(), VideoTimeView.OnTimeBarListener {
    private var added = false
    private val hv by lazy {
        HoodView(this)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        log("onConfigurationChanged")
    }

    override fun getResources(): Resources {
        log("getResources")
        return super.getResources()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ImageLoader.init(this.application)
        setContentView(R.layout.activity_main)

        TestLiveData.getInstance().observe(this, Observer {
            log("test live data size[${it.size}]")
        })
        Test.testData.observe(this, Observer {
            log("test live data size[${it.size}] aaaa")
        })

//        dl_load.start()
        load_btn.setOnClickListener {
            audio_view.start()

            val list = mutableListOf("1", "2", "3", "4")
            Test.setData(list)
            TestLiveData.getInstance().value = list
            startActivity(Intent(this, CoorActivity::class.java))
//            headBubbles.start()
//            load_btn.startLoad()
//            startActivity(Intent(this, SlideActivity::class.java))
        }

        vsv.setOnClickListener {
            vpb.setProgress(Random.nextInt(100))

            if (!added) {
                added = true
                hv.startShow(this, vsv)
            } else {
                added = false
                hv.remove(this)
            }
        }

        audio_view.mOnAddCountListener = object : AudioView.OnAddCountListener {
            override fun onAdded() {
                iv_long.visibility = View.VISIBLE
                iv_short.visibility = View.GONE
            }
        }

//        v_ta.viewTreeObserver.addOnGlobalLayoutListener(object :
//            ViewTreeObserver.OnGlobalLayoutListener {
//            override fun onGlobalLayout() {
//                v_ta.viewTreeObserver.removeOnGlobalLayoutListener(this)
//                val params = riv_bottom.layoutParams
//                params.height = v_ta.height - DisplayHelper.dp2px(100f)
//                riv_bottom.layoutParams = params
//            }
//
//        })
        vtv.setTimeBarListener(this)
        vtv.start()

    }


    override fun onTimeBarScale(scaleHeight: Float, scrollY: Int) {

    }

    override fun onTimeBarCurrentTime(time: Long) {
        tv_time.text = TimeHelper.getTimeStr(time*1000,"HH:mm:ss")
        if(cd_time.visibility != View.VISIBLE){
            v_sel_line.visibility = View.VISIBLE
            cd_time.visibility = View.VISIBLE
        }
    }

    override fun onScrollY(scrollY: Int) {

    }

    override fun onVideos(totalHeight:Float, space:Float) {

    }


}
