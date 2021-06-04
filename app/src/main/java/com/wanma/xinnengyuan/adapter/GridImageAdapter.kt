package com.wanma.xinnengyuan.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.luck.picture.lib.entity.LocalMedia
import com.wanma.xinnengyuan.R
import com.wanma.xinnengyuan.`interface`.OnAddPicClickListener
import com.wanma.xinnengyuan.`interface`.OnItemClickListener
import com.wanma.xinnengyuan.activity.charging.ChargingStackAddActivity

class GridImageAdapter(val context: Context, var list: ArrayList<LocalMedia>,
                       val onAddPicClickListener: OnAddPicClickListener,
                       val onItemClickListener: OnItemClickListener,
                       val selectMax: Int = 9,
                       val whitch: Int = 0) : RecyclerView.Adapter<GridImageAdapter.ViewHolder>() {

    val TYPE_CAMERA = 1
    val TYPE_PICTURE = 2
    var mList = list
    val nameList: ArrayList<String> = ArrayList<String>()
    val pathList: ArrayList<String> = ArrayList<String>()

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val img = view.findViewById<ImageView>(R.id.fiv)
        val ll_del = view.findViewById<LinearLayout>(R.id.ll_del)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_image_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = if(mList.size < selectMax) mList.size + 1 else mList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(getItemViewType(position) == TYPE_CAMERA) {
            holder.img.setImageResource(R.drawable.add_img)
            holder.img.setOnClickListener {
                onAddPicClickListener.onAddPicClick(whitch)
            }
            holder.ll_del.visibility = View.INVISIBLE
        }else {
            holder.ll_del.visibility = View.VISIBLE
            holder.ll_del.setOnClickListener {
                val index = holder.absoluteAdapterPosition
                if(index != RecyclerView.NO_POSITION) {
                    mList.removeAt(index)
                    //删除选中图片
                    pathList.removeAt(index)
                    nameList.removeAt(index)
                    notifyItemRemoved(index)
                    notifyItemRangeChanged(index, mList.size)
                }
            }
            val media = mList[position]
            val mimeType = media.mimeType
            var path = ""
            path = if(media.isCut && !media.isCompressed) {
                //裁剪过
                media.cutPath
            }else if(media.isCompressed || (media.isCut && media.isCompressed)) {
                // 压缩过,或者裁剪同时压缩过,以最终压缩过图片为准
                media.compressPath
            }else {
                media.path
            }

            val options = RequestOptions()
                    .centerCrop()
                    .placeholder(R.color.color_f6)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
            Glide.with(holder.itemView.context)
                    .load(path)
                    .into(holder.img)

            //itemView的点击事件
            if(context !is ChargingStackAddActivity) {
                holder.itemView.setOnClickListener {
                    val index = holder.absoluteAdapterPosition
                    onItemClickListener.onItemClick(index, it, whitch)
                }
            }
        }
    }

    override fun getItemViewType(position: Int) = if(isShowAddItem(position)) TYPE_CAMERA else TYPE_PICTURE

    private fun isShowAddItem(position: Int) = mList.size == position

}