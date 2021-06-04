package com.wanma.xinnengyuan.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.wanma.xinnengyuan.R
import com.wanma.xinnengyuan.activity.alternating.AlternatingAddActivity
import com.wanma.xinnengyuan.activity.alternating.AlternatingQueryActivity
import com.wanma.xinnengyuan.activity.charging.ChargingStackAddActivity
import com.wanma.xinnengyuan.activity.direct.DirectAddActivity
import com.wanma.xinnengyuan.activity.direct.DirectQueryActivity
import com.wanma.xinnengyuan.bean.HomeItem
import com.wanma.xinnengyuan.activity.fqc.FQCAddActivity
import com.wanma.xinnengyuan.activity.fqc.FQCQueryActivity
import com.wanma.xinnengyuan.activity.oqc.OQCAddActivity

class HomeAdapter(private val context: Context, private val list: List<HomeItem>, private val flag: String)
    : RecyclerView.Adapter<HomeAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val homeIv = view.findViewById<ImageView>(R.id.homeIv)
        val homeTv = view.findViewById<TextView>(R.id.homeTv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_home_item, parent, false)
        val viewHolder = ViewHolder(view)
        viewHolder.itemView.setOnClickListener {
            var intent: Intent = Intent()
            val position = viewHolder.adapterPosition
            when(list[position].text) {
                "首页" -> {

                }
                "直流测试" -> {
                    if(flag == "add") {
                        intent = Intent(context, DirectAddActivity::class.java)
                        context.startActivity(intent)
                    }else {
                        Toast.makeText(context, "暂未开通", Toast.LENGTH_SHORT).show()
                    }

                }
                "FQC检验" -> {
                    if(flag == "add") {
                        intent = Intent(context, FQCAddActivity::class.java)
                        context.startActivity(intent)
                    }else {
                        Toast.makeText(context, "暂未开通", Toast.LENGTH_SHORT).show()
                    }

                }
                "OQC检验" -> {
                    if(flag == "add") {
                        intent = Intent(context, OQCAddActivity::class.java)
                        context.startActivity(intent)
                    }else {
                        Toast.makeText(context, "暂未开通", Toast.LENGTH_SHORT).show()
                    }
                }
                "交流测试" -> {
                    if(flag == "add") {
                        intent = Intent(context, AlternatingAddActivity::class.java)
                        context.startActivity(intent)
                    }else {
                        Toast.makeText(context, "暂未开通", Toast.LENGTH_SHORT).show()
                    }
                }
                "充电堆测试" -> {
                    if(flag == "add") {
                        intent = Intent(context, ChargingStackAddActivity::class.java)
                        context.startActivity(intent)
                    }else {
                        Toast.makeText(context, "暂未开通", Toast.LENGTH_SHORT).show()
                    }
                }

            }
        }
        return viewHolder
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val homeItem = list[position]
        holder.homeIv.setImageResource(homeItem.img)
        holder.homeTv.text = homeItem.text
    }
}