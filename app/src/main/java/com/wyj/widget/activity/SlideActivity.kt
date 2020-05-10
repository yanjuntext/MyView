package com.wyj.widget.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.wyj.widget.R
import com.wyj.widget.SlideCloseLayout
import com.wyj.widget.adapter.SlideVpAdapter
import kotlinx.android.synthetic.main.activity_slide.*

/**
 *
 *@author abc
 *@time 2020/5/10 17:17
 */
class SlideActivity : AppCompatActivity(), SlideCloseLayout.OnLayoutCloseListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_slide)

        val data = mutableListOf(R.drawable.ic_1, R.drawable.ic_2, R.drawable.ic_3, R.drawable.ic_4)
        val adapter = SlideVpAdapter(this, data)
        vp.adapter = adapter

        slide_close.setOnLayoutCloseListener(this)
    }

    override fun onLayoutClose() {
        finish()
    }

}