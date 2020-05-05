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
import com.wyj.widget.R
import com.wyj.widget.SItemDecoration
import com.wyj.widget.dialog.base.SBuilder
import com.wyj.widget.dialog.base.SDialog
import com.base.utils.DisplayHelper
import com.base.utils.clickDelay
import kotlinx.android.synthetic.main.adapter_multiple_item.view.*
import kotlinx.android.synthetic.main.dialog_multiple.*
import kotlinx.android.synthetic.main.include_bottom.*
import kotlinx.android.synthetic.main.include_title.*

/**
 *
 *@author abc
 *@time 2019/11/21 13:59
 */
class MultipleDialog private constructor(private var builder: Builder) : SDialog(builder) {


    private var selectPositions = mutableListOf<Int>()
    private var selectLables = mutableListOf<String>()

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
            this.clickDelay {
                dismiss()
                builder.onCancleListener?.onCancle(this@MultipleDialog)
            }
        }

        with(tv_confirm) {
            this.visibility = if (builder.confirm.isNullOrEmpty()) View.GONE else View.VISIBLE
            this.text = builder.confirm ?: ""
            this.setTextColor(builder.confirmColor)
            this.clickDelay {
                dismiss()
                builder.selectedPositions.clear()
                builder.selectedPositions.addAll(selectPositions)
                builder.selectedLables.clear()
                builder.selectedLables.addAll(selectLables                                                                 )
                Log.e(
                    "MultipleDialog",
                    "${builder.onConfirmListrener == null},size[${builder.selectedPositions.size}]"
                )
                builder.onConfirmListrener?.onCaonfirm(
                    this@MultipleDialog,
                    builder.selectedPositions
                )
            }
        }

        recycler.layoutManager =
            LinearLayoutManager(builder.activity, LinearLayoutManager.VERTICAL, false)
        recycler.addItemDecoration(
            SItemDecoration(builder.activity).setDividerHeightPx(1).setDividerColorResource(
                R.color.dialog_divider
            )
        )

        val mAdapter = MultipleRvAdapter(builder.activity, builder.lables)
        selectPositions.clear()
        selectLables.clear()
        selectLables.addAll(builder.selectedLables)
        selectPositions.addAll(builder.selectedPositions)
//        mAdapter.selectedPositions = builder.selectedPositions
        mAdapter.selectedPositions = selectPositions
        mAdapter.selectedListener = object : OnSelectedListener {
            override fun selected(selectedPositions: MutableList<Int>?) {

//                if (selectedPositions.isNullOrEmpty()) {
//                    selectPositions.clear()
//                    selectLables.clear()
//                } else {
//                    selectPositions.clear()
                    selectLables.clear()
//                    selectPositions.addAll(selectedPositions)
                    selectPositions.forEach {
                        selectLables.add(builder.lables?.get(it) ?: "")
                    }
//                }
//                mAdapter.notifyDataSetChanged()
            }
        }
        recycler.adapter = mAdapter

    }

    override fun onShow(dialog: DialogInterface?) {
    }

    open class Builder(activity: FragmentActivity) : SBuilder<Builder>(activity) {

        /**数据源*/
        var lables: MutableList<String>? = null
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

        /**选中的lables*/
        var selectedLables: MutableList<String> = mutableListOf()
        /**选中的position*/
        var selectedPositions: MutableList<Int> = mutableListOf()

        private var grivity:Int = Gravity.CENTER
        private var fullWidthScreen = false

        init {
            lables = mutableListOf()
            selectTextColor = ContextCompat.getColor(activity, R.color.dialog_content)
            unSelectTextColor = ContextCompat.getColor(activity, R.color.dialog_content)
            selectLableDraw = ContextCompat.getDrawable(activity, R.drawable.iv_selecte_new)
            unselecteLableDraw = null
            maxHeight = DisplayHelper.screenHeight() / 2
            itemGrivate = Gravity.CENTER
        }

        override fun setGravity(): Int = grivity

        fun setGrivity(grivity:Int) :Builder{

            this.grivity = grivity
            return this
        }

        fun setLables(lables: MutableList<String>?): Builder {
            lables?.let {
                this.lables?.addAll(it)
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


        fun setSelectedLables(selectedLables: MutableList<String>?): Builder {
            this.selectedLables = selectedLables ?: mutableListOf()
            return this
        }

        fun setSelectedPositions(selectedPositions: MutableList<Int>?): Builder {
            this.selectedPositions = selectedPositions ?: mutableListOf()
            return this
        }

        fun setWidthScreen(fullWidthScreen:Boolean):Builder{
            this.fullWidthScreen = fullWidthScreen
            return this
        }

        override fun isFullWidthScreen(): Boolean = fullWidthScreen

        override fun builder(): Builder {
            setContentView(R.layout.dialog_multiple)
            return this
        }

        override fun show(activity: FragmentActivity): SDialog? {
            val dialog= MultipleDialog(this)
            dialog.show(activity)
            return dialog
        }

    }


    inner class MultipleRvAdapter(
        private var activity: FragmentActivity,
        private var data: MutableList<String>?
    ) : RecyclerView.Adapter<MultipleRvAdapter.ViewHolder>() {

        val inflater by lazy { LayoutInflater.from(activity) }
        var selectedPositions: MutableList<Int> = mutableListOf()
        var selectedListener: OnSelectedListener? = null

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(inflater.inflate(R.layout.adapter_multiple_item, parent, false))

        override fun getItemCount(): Int = data?.size ?: 0

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            data?.let {
                holder.bindView(it[position], position)
            }
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bindView(data: String, position: Int) {

                val selected = run outside@{
                    selectedPositions.forEach {
                        if (it == position) return@outside true
                    }
                    false
                }

                itemView.clickDelay {

                    if (selected) selectedPositions.remove(
                        position
                    ) else if (!selected) selectedPositions.add(position)
                    this@MultipleRvAdapter.notifyDataSetChanged()
                    selectedListener?.selected(selectedPositions)
                }

                itemView.tv_content.gravity = this@MultipleDialog.builder.itemGrivate
                itemView.tv_content.text = data
                itemView.tv_content.setTextColor(
                    if (selected) this@MultipleDialog.builder.selectTextColor else
                        this@MultipleDialog.builder.unSelectTextColor
                )

                itemView.iv_select.setImageDrawable(
                    if (selected) this@MultipleDialog.builder.selectLableDraw
                    else this@MultipleDialog.builder.unselecteLableDraw
                )

            }
        }
    }

    interface OnSelectedListener {
        fun selected(selectedPositions: MutableList<Int>?)
    }

}