package com.wyj.widget.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.base.utils.clickDelay
import com.wyj.widget.R
import kotlinx.android.synthetic.main.activity_item.*

/**
 * 作者：王颜军 on 2020/12/23 09:27
 * 邮箱：3183424727@qq.com
 */
class ItemActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item)

        initView()
    }

    private fun initView() {
        tv_recycler.clickDelay {
            startActivity(Intent(this,ParallexRecyclerActivity::class.java))
        }
    }
}