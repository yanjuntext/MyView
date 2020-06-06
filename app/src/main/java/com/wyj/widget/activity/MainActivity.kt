package com.wyj.widget.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import com.base.utils.image.ImageLoader
import com.wyj.widget.HoodView
import com.wyj.widget.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private var added = false
    private val hv by lazy {
        HoodView(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ImageLoader.init(this.application)
        setContentView(R.layout.activity_main)

//        dl_load.start()
        load_btn.setOnClickListener {
            load_btn.startLoad()
            startActivity(Intent(this, SlideActivity::class.java))
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


    }


}
