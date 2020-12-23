package com.wyj.widget.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.base.utils.clickDelay
import com.wyj.base.startAction
import com.wyj.widget.R
import com.wyj.widget.decoration.ParallexDecoration
import kotlinx.android.synthetic.main.activity_recycler_parallex.*

/**
 * 作者：王颜军 on 2020/12/23 10:14
 * 邮箱：3183424727@qq.com
 */
class ParallexRecyclerActivity : AppCompatActivity() {
    private var orientation = 0

    private val mHorizontalBgs = intArrayOf(
        R.drawable.rd_gua_seed_1, R.drawable.rd_gua_seed_2, R.drawable.rd_gua_seed_3,
        R.drawable.rd_gua_seed_4, R.drawable.rd_gua_seed_5, R.drawable.rd_gua_seed_6
    )
    private val mVerticalBgs = intArrayOf(
        R.drawable.rd_gua_seed_v_1, R.drawable.rd_gua_seed_v_2, R.drawable.rd_gua_seed_v_3,
        R.drawable.rd_gua_seed_v_4, R.drawable.rd_gua_seed_v_5, R.drawable.rd_gua_seed_v_6
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycler_parallex)
        initValue()
        initView()
    }


    private fun initValue() {

        orientation = intent.getIntExtra("orientation", 0)
    }

    private fun initView() {
        if (orientation == 1) {
            recycler.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            recycler.addItemDecoration(ParallexDecoration(this).apply {
                setUpResorce(mHorizontalBgs.asList())
                parallax = 0.35f

            })
            recycler.adapter = MyAdapter()
            btn_horizontal.visibility = View.GONE
            btn_vertical.visibility = View.GONE
        } else if (orientation == 2) {
            recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            recycler.addItemDecoration(ParallexDecoration(this).apply {
                setUpResorce(mVerticalBgs.asList())
                parallax = 0.35f
            })
            recycler.adapter = MyAdapter()

            btn_horizontal.visibility = View.GONE
            btn_vertical.visibility = View.GONE
        }

        btn_horizontal.clickDelay {
            startActivity(Intent(this, ParallexRecyclerActivity::class.java).apply {
                putExtra("orientation", 1)
            })
        }
        btn_vertical.clickDelay {
            startActivity(Intent(this, ParallexRecyclerActivity::class.java).apply {
                putExtra("orientation", 2)
            })
        }
    }


    inner class MyAdapter : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {
        inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val mTvContent = itemView.findViewById<AppCompatTextView>(R.id.tv_content)
            fun bind(string: String) {
                mTvContent.text = string
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            MyViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.adapter_parallex, parent, false)
            )

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.bind("Item ---> $position")
        }

        override fun getItemCount() = 100
    }
}