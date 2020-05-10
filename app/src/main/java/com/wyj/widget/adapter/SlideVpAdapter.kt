package com.wyj.widget.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.base.utils.DisplayHelper
import com.bumptech.glide.Glide
import com.wyj.widget.R
import kotlinx.android.synthetic.main.adapter_image.view.*

/**
 *
 *@author abc
 *@time 2020/5/10 17:11
 */
class SlideVpAdapter(val context: Context, var data: MutableList<Int>) :
    RecyclerView.Adapter<SlideVpAdapter.ViewHolder>() {
    private val inflater by lazy { LayoutInflater.from(context) }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindView(data: Int) {
            Glide.with(context)
                .load(data)
                .override(DisplayHelper.screenWidth(), DisplayHelper.screenWidth() * 9 / 16)
                .into(itemView.pv_img)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(inflater.inflate(R.layout.adapter_image, parent, false))

    override fun getItemCount(): Int = data.size * 10

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(data = data[position % data.size])
    }
}