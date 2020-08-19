package com.wyj.widget.activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.base.utils.GlobalStatusBarUtil
import com.gyf.immersionbar.ktx.immersionBar
import com.wyj.widget.R
import kotlinx.android.synthetic.main.activity_coor.*
import kotlinx.android.synthetic.main.adapter_recycler.view.*

class CoorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        GlobalStatusBarUtil.translucent(this)
        immersionBar {
//            transparentBar()
            transparentStatusBar()
        }
        setContentView(R.layout.activity_coor_new)

        recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recycler.adapter = Myadapter(this)
    }


    class Myadapter(val context: Context) : RecyclerView.Adapter<Myadapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(
                LayoutInflater.from(context).inflate(R.layout.adapter_recycler, parent, false)
            )

        override fun getItemCount(): Int = 10

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bindViewHolder(position)
        }


        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            private val list by lazy {
                arrayListOf(
                    R.drawable.ic_1,
                    R.drawable.ic_2,
                    R.drawable.ic_3,
                    R.drawable.ic_4
                )
            }

            fun bindViewHolder(position: Int) {
                itemView.iv_content.setImageResource(list[position % 4])
            }
        }

    }


}
