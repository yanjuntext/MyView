package com.wyj.widget.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.wyj.widget.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        dl_load.start()
        load_btn.setOnClickListener {
            load_btn.startLoad()
            startActivity(Intent(this,SlideActivity::class.java))
        }

    }


}
