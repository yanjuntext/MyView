package com.wyj.base.adapter

import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import java.lang.ref.WeakReference

abstract class BaseViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var context: WeakReference<FragmentActivity>? = null
    var onItemClickListener: OnRvItemClickListener<T>? = null
    var onItemLongClickListener: OnRvItemLongClickListener<T>? = null
    var onItemSelectedListener: OnRvItemSelectListener<T>? = null
    var onRvMultiItemClickListener: OnRvIMultitemClickListener<T>? = null
    var changeHolderStatus:Boolean = false



    abstract fun bindView(data: T, lastData: T?, position: Int, selectPosition: Int, count: Int)


}