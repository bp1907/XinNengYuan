package com.wanma.xinnengyuan.adapter

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.wanma.xinnengyuan.R
import com.wanma.xinnengyuan.`interface`.OnRemoveListener
import com.wanma.xinnengyuan.bean.LineListItem
import com.wanma.xinnengyuan.utils.Config
import com.wanma.xinnengyuan.utils.PhotoViewUtil

class LineItemAdapter(val context: Context, val list: ArrayList<LineListItem>, val onRemoveListener: OnRemoveListener)
    : RecyclerView.Adapter<LineItemAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val et = view.findViewById<EditText>(R.id.charging_add_ccu_no)
        val recyclerView = view.findViewById<RecyclerView>(R.id.chargingAddItemRecyclerView)
        val deleteBtn = view.findViewById<ImageButton>(R.id.charging_add_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        when(viewType) {
//            Config.VIEW_SHOW -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_list_image_item, parent, false)
                val viewHolder = ViewHolder(view)
                viewHolder.deleteBtn.setOnClickListener {
                    val position = viewHolder.adapterPosition
                    onRemoveListener.onRemove(position)
                }

                return viewHolder
//            }
//            Config.VIEW_NOT_SHOW -> {
//                val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_not_show_item, parent, false)
//                return ViewHolder(view)
//            }
//            else -> return ViewHolder(View(context))
//        }
    }

    override fun getItemCount() = list.size

//    override fun getItemViewType(position: Int) = if(list[position].flag) Config.VIEW_SHOW
//    else Config.VIEW_NOT_SHOW


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        if(holder.itemViewType == Config.VIEW_SHOW) {
            val adapter = list[position].adapter
            holder.et.setText(list[position].noStr)
            PhotoViewUtil.initWidget(holder.recyclerView, context, adapter, 3)

            if(holder.et.tag is TextWatcher) {
                holder.et.removeTextChangedListener(holder.et.tag as TextWatcher)
            }
            val watch = object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {
                    list[position].noStr = p0.toString()

                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }
            }
            holder.et.addTextChangedListener(watch)
            holder.et.tag = watch
//        }
    }
}