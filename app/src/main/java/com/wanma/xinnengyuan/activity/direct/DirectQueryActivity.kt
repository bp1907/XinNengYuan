package com.wanma.xinnengyuan.activity.direct

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.wanma.xinnengyuan.R
import com.wanma.xinnengyuan.adapter.NoListAdapter
import com.wanma.xinnengyuan.bean.NoListItem
import kotlinx.android.synthetic.main.activity_direct_query.*

class DirectQueryActivity : AppCompatActivity() {

    lateinit var list: ArrayList<NoListItem>
    lateinit var adapter: NoListAdapter
    private val tempList = ArrayList<NoListItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_direct_query)

        //获取数据
        initData()

        //数据填充到recyclerview
        initRecyclerView()

        //模糊查询
        fuzzyQuery()

    }

    private fun fuzzyQuery() {
        direct_query.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                showList(p0.toString())
            }
        })
    }

    fun showList(str: String) {
        tempList.clear()
        list.forEach {
            if(it.mainNO.contains(str)) {
                tempList.add(it)
            }
        }
        if(tempList.size < 1) {
            directRecyclerView.visibility = View.GONE
            direct_no_data.visibility = View.VISIBLE
        }else {
            directRecyclerView.visibility = View.VISIBLE
            direct_no_data.visibility = View.GONE
            adapter = NoListAdapter(this, tempList)
            directRecyclerView.adapter = adapter
            adapter.notifyDataSetChanged()
        }
    }

    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        directRecyclerView.layoutManager = layoutManager
        adapter = NoListAdapter(this, list)
        directRecyclerView.adapter = adapter
        directRecyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
    }

    private fun initData() {
        val noListItem1 = NoListItem("1124357121", "王振")
        val noListItem2 = NoListItem("1124324256", "张三")
        val noListItem3 = NoListItem("1180357796", "李四")
        val noListItem4 = NoListItem("1526355621", "王五")

        list = arrayListOf(noListItem1, noListItem2, noListItem3, noListItem4)

    }
}