package com.wyj.widget.videolist

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wyj.base.log
import com.wyj.widget.R
import kotlinx.android.synthetic.main.adapter_video.view.*

/**
 * 作者：王颜军 on 2020/8/18 15:16
 * 邮箱：3183424727@qq.com
 */
class VideoListAdapter(val context: Context, val data: MutableList<IVideoItem>?) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val mDefTopHeight = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        80f,
        context.resources.displayMetrics
    ).toInt()

    var bottomMargin = 0f

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        if (viewType == 0) {
            BottomViewHolder(
                LayoutInflater.from(context).inflate(R.layout.adapter_video_bottom, parent, false)
            )
        } else ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.adapter_video, parent, false)
        )

    override fun getItemCount(): Int = (data?.size ?: 0) + 1

    override fun getItemViewType(position: Int): Int {
        return if (position == itemCount - 1) 0 else 1
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        data?.let {
            if (holder is ViewHolder) {
                holder.bindView(data[position], position)
            } else if (holder is BottomViewHolder) {
                holder.bindView()
            }
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindView(date: IVideoItem, position: Int) {
            val params = itemView.v_top.layoutParams
            params.height = (date.getTopHeight()+0.5f).toInt()
            log("calculationVideoRectF adapter[${date.getTopHeight()}],positionp[$position],[${date.getTimeLineTopY() + mDefTopHeight}],[$mDefTopHeight]")
//            itemView.v_top.layoutParams = params
            itemView.v_top.requestLayout()
        }
    }

    inner class BottomViewHolder( itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindView() {
            val params = itemView.layoutParams
            params.height = (bottomMargin+0.5f).toInt()
            itemView.layoutParams = params
            itemView.requestLayout()
        }
    }


}