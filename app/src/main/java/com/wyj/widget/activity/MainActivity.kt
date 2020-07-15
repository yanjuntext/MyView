package com.wyj.widget.activity

import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.base.utils.image.ImageLoader
import com.wyj.base.log
import com.wyj.widget.AudioView
import com.wyj.widget.HoodView
import com.wyj.widget.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
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

//        dl_load.start()
        load_btn.setOnClickListener {
            audio_view.start()
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


    }


}
