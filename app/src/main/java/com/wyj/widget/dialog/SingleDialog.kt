package com.wyj.widget.dialog

import android.content.DialogInterface
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wyj.widget.AutoLinearLayoutManager
import com.wyj.widget.R
import com.wyj.widget.SItemDecoration
import com.wyj.widget.dialog.base.SBuilder
import com.wyj.widget.dialog.base.SDialog
import com.base.utils.DisplayHelper
import com.base.utils.clickDelay
import kotlinx.android.synthetic.main.adapter_select_item.view.*
import kotlinx.android.synthetic.main.dialog_single.*
import kotlinx.android.synthetic.main.include_bottom.*
import kotlinx.android.synthetic.main.include_title.*

/**
 * 单选dialog
 *@author abc
 *@time 2019/11/13 15:55
 */
class SingleDialog private constructor(private var builder: Builder) :
    SDialog(builder) {


    private var mSingleAdapter: SingleRvAdapter? = null
    override fun initView() {
        with(tv_title) {
            this.visibility = if (builder.title.isNullOrEmpty()) View.GONE else View.VISIBLE
            this.text = builder.title ?: ""
            this.setTextColor(builder.titleColor)
        }

        with(tv_cancle) {
            this.visibility = if (builder.cancle.isNullOrEmpty()) {
                divider_ver.visibility = View.GONE
                View.GONE
            } else {


                divider_ver.visibility = View.VISIBLE
                View.VISIBLE
            }
            this.text = builder.cancle ?: ""
            this.setTextColor(builder.cancleColor)
        }

        with(tv_confirm) {
            this.visibility = if (builder.confirm.isNullOrEmpty()) View.GONE else View.VISIBLE
            this.text = builder.confirm ?: ""
            this.setTextColor(builder.confirmColor)
        }
//        recycler.layoutManager = AutoLinearLayoutManager(builder.activity, 10)
        recycler.layoutManager = LinearLayoutManager(builder.activity, LinearLayoutManager.VERTICAL,false)
        recycler.addItemDecoration(
            SItemDecoration(builder.activity).setDividerHeightPx(1).setDividerColorResource(
                R.color.dialog_divider
            )
        )

        if (builder.selectedDismiss) {
            divider_ver.visibility = View.GONE
            divider_hor.visibility = View.GONE
            tv_cancle.visibility = View.GONE
            tv_confirm.visibility = View.GONE
        } else {
            divider_ver.visibility = View.VISIBLE
            divider_hor.visibility = View.VISIBLE
            tv_cancle.visibility = View.VISIBLE
            tv_confirm.visibility = View.VISIBLE
        }

        mSingleAdapter = SingleRvAdapter(builder.activity, builder.lables).also {
            it.selectPosition = builder.selectPosition
            it.onItemClickListener = object : OnItemClickListener {
                override fun itemClick(data: String, position: Int) {
                    Log.d("SignalDialog", "position[${position}],data[${data}]")
                    it.selectPosition = position
                    if (builder.selectedDismiss) {
                        builder.selectPosition = position
                        builder.onConfirmListrener?.onCaonfirm(this@SingleDialog, position)
                        dismiss()
                    }
                }
            }
        }
        recycler.adapter = mSingleAdapter

        tv_cancle.clickDelay {

            dismiss()
        }
        tv_confirm.clickDelay {
            builder.selectPosition = mSingleAdapter?.selectPosition ?: -1
            builder.onConfirmListrener?.onCaonfirm(
                this@SingleDialog,
                mSingleAdapter?.selectPosition ?: -1
            )
            dismiss()
        }

    }
    override fun onShow(dialog: DialogInterface?) {
    }
    open class Builder(activity: FragmentActivity) : SBuilder<Builder>(activity) {
        /**数据源*/
        var lables: MutableList<String>? = null
        /**选中第几项*/
        var selectPosition: Int = -1

        /**选中item 颜色*/
        var selectTextColor: Int = 0
        /**未选中item 颜色*/
        var unSelectTextColor: Int = 0
        /**
         * selecter
         * 选中Lable
         * */
        var selectLableDraw: Drawable? = null
        /**未选择*/
        var unselecteLableDraw: Drawable? = null
        /**最大高度*/
        var maxHeight: Int = 0
        /**Item 显示为止*/
        var itemGrivate: Int = 0
        /**
         * 选中后是否显示
         * true 确定、取消按钮设置无效，必隐藏
         * */
        var selectedDismiss: Boolean = true

        private var sGravity:Int = Gravity.CENTER
        private var sFullWScreen = false


        override fun setGravity(): Int {
            return sGravity
        }

        override fun isFullWidthScreen() = sFullWScreen


        init {
            lables = mutableListOf()
            selectTextColor = ContextCompat.getColor(activity, R.color.dialog_content)
            unSelectTextColor = ContextCompat.getColor(activity, R.color.dialog_content)
            selectLableDraw = ContextCompat.getDrawable(activity, R.drawable.iv_selecte_new)
            unselecteLableDraw = null
            maxHeight = DisplayHelper.screenHeight() / 2
            itemGrivate = Gravity.CENTER
        }

        fun asBottom():Builder{
            sGravity = Gravity.BOTTOM
            return this
        }

        fun asWithFullScreen():Builder{
            sFullWScreen = true
            return this
        }

        fun setLables(lables: MutableList<String>?): Builder {
            lables?.let {
                this.lables?.addAll(it)
            }
            return this
        }

        fun setSelectPosition(selectPosition: Int): Builder {
            this.selectPosition = selectPosition
            return this
        }

        fun setSelectLables(selectLables: String?): Builder {
            run outside@{
                lables?.forEachIndexed { index, s ->
                    if (s == selectLables) {
                        selectPosition = index
                        return@outside
                    }
                }
            }
            return this
        }

        fun setSelectTextColor(@ColorRes selectTextColor: Int): Builder {
            this.selectTextColor = ContextCompat.getColor(activity, selectTextColor)
            return this
        }

        fun setUnselectTextColor(@ColorRes unselectTextColor: Int): Builder {
            this.unSelectTextColor = ContextCompat.getColor(activity, unselectTextColor)
            return this
        }

        fun setSelectLableDraw(@DrawableRes drawable: Int): Builder {
            selectLableDraw = ContextCompat.getDrawable(activity, drawable)
            return this
        }

        fun setUnselecteLableDraw(@DrawableRes drawable: Int?): Builder {

            unselecteLableDraw =
                if (drawable == null) null else ContextCompat.getDrawable(activity, drawable)
            return this
        }

        fun setMaxHeight(maxHeight: Int): Builder {
            this.maxHeight = maxHeight
            return this
        }

        fun setItemGravide(itemgravide: Int): Builder {
            this.itemGrivate = itemgravide
            return this
        }

        fun setSelectedDismiss(selectedDismiss: Boolean): Builder {
            this.selectedDismiss = selectedDismiss
            return this
        }

        override fun builder(): Builder {
            setContentView(R.layout.dialog_single)
            return this
        }

        override fun show(activity: FragmentActivity) : SDialog {
            val dialog= SingleDialog(this)
            dialog.show(activity)
            return dialog
        }

    }

    inner class SingleRvAdapter(
        private var activity: FragmentActivity,
        private var data: MutableList<String>?
    ) : RecyclerView.Adapter<SingleRvAdapter.ViewHolder>() {
        var selectPosition: Int = -1
            set(value) {
                field = value
                notifyDataSetChanged()
            }
        var onItemClickListener: OnItemClickListener? = null
        val inflater by lazy { LayoutInflater.from(activity) }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(inflater.inflate(R.layout.adapter_select_item, parent, false))

        override fun getItemCount(): Int = data?.size ?: 0

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            data?.let {
                holder.bindView(it[position], position, selectPosition)
            }
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bindView(data: String, position: Int, selectPosition: Int) {
                Log.e("SignalDialog", "sele[${selectPosition}],position[$position]")
                itemView.clickDelay {
                    onItemClickListener?.itemClick(data, position)
                }
                itemView.tv_content.gravity = this@SingleDialog.builder.itemGrivate
                itemView.tv_content.text = data
                itemView.tv_content.setTextColor(
                    if (position == selectPosition) this@SingleDialog.builder.selectTextColor else
                        this@SingleDialog.builder.unSelectTextColor
                )

                itemView.iv_select.setImageDrawable(
                    if (position == selectPosition) this@SingleDialog.builder.selectLableDraw
                    else this@SingleDialog.builder.unselecteLableDraw
                )


            }
        }


    }

    interface OnItemClickListener {
        fun itemClick(data: String, position: Int)
    }
}