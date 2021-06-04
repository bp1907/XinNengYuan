package com.wanma.xinnengyuan.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wanma.xinnengyuan.R
import com.wanma.xinnengyuan.bean.NoListItem

class NoListAdapter(val context: Context, val list: ArrayList<NoListItem>) : RecyclerView.Adapter<NoListAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val no = view.findViewById<TextView>(R.id.no)
        val name = view.findViewById<TextView>(R.id.name)
        val img = view.findViewById<ImageButton>(R.id.show_detail_img)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_show_detail_item, parent, false)
        val viewHolder = ViewHolder(view)
        viewHolder.itemView.setOnClickListener {
            //TODO 跳转到详情页面
        }
        return viewHolder
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val noListItem = list[position]
        holder.no.text = noListItem.mainNO
        holder.name.text = noListItem.secondaryName
    }
}