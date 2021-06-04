package com.wanma.xinnengyuan

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wanma.xinnengyuan.adapter.HomeAdapter
import com.wanma.xinnengyuan.bean.HomeItem
import com.youth.banner.BannerConfig
import com.youth.banner.Transformer
import com.youth.banner.loader.ImageLoader
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var list: List<HomeItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        initBanner()
        initAdapterData()
        initRecyclerview(myRecycleView1, "add")
        initRecyclerview(myRecycleView2, "query")
    }

    private fun initAdapterData() {
        val homeItem1 = HomeItem(R.drawable.home, "首页")
        val homeItem2 = HomeItem(R.drawable.yhq, "直流测试")
        val homeItem3 = HomeItem(R.drawable.baobia, "FQC检验")
        val homeItem4 = HomeItem(R.drawable.bz, "OQC检验")
        val homeItem5 = HomeItem(R.drawable.riqi, "交流测试")
        val homeItem6 = HomeItem(R.drawable.sortall, "充电堆测试")
        list = listOf(homeItem1, homeItem2, homeItem3, homeItem4, homeItem5, homeItem6)
    }

    private fun initRecyclerview(recyclerView: RecyclerView, flag: String) {

        val layoutManager = GridLayoutManager(this, 3)
        recyclerView.layoutManager = layoutManager
        val adapter = HomeAdapter(this, list, flag)
        recyclerView.adapter = adapter
    }

    private fun initBanner() {
        val listPath:List<Int> = listOf(
                R.drawable.banner1,
                R.drawable.banner2,
                R.drawable.banner3
        )
        val listTitle:List<String> = listOf("1", "2", "3")

//设置内置样式，共六种
        myBanner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE)
                //设置图片加载器
                .setImageLoader(MyLoader())
                //设置图片网址或地址的集合
                .setImages(listPath)
                //设置轮播图的标题集合
                .setBannerTitles(listTitle)
                //设置轮播的动画效果
                .setBannerAnimation(Transformer.Default)
                //设置轮播间隔时间
                .setDelayTime(3000)
                //设置自动轮播，默认true
                .isAutoPlay(true)
                //设置不能手动影响，默认手指触摸，轮播图不能翻页
                .setViewPagerIsScroll(true)
                //设置指示器位置，小点点，左中右
                .setIndicatorGravity(BannerConfig.CENTER)
                //最后调用的方法，启动轮播图
                .start()
    }

    class MyLoader : ImageLoader() {
        override fun displayImage(context: Context, path: Any?, imageView: ImageView) {
            Glide.with(context).load(path).into(imageView)
        }

    }
}