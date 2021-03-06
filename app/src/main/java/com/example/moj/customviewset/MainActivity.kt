package com.example.moj.customviewset

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(),AdapterView.OnItemClickListener {
    override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        when(p2){
            0 -> {startActivity(Intent(this, RadarActivity::class.java))}
            1 -> {startActivity(Intent(this, DragActivity::class.java))}
            2 -> {startActivity(Intent(this, CutActivity::class.java))}
            else -> Toast.makeText(this,"当前版本过低，请先更新app",Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val mTitles = arrayListOf("雷达图","DragHelper", "图片裁剪")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)

        recyclerView.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
                return Holder(LayoutInflater.from(this@MainActivity).inflate(R.layout.item_home,parent,false))
            }

            override fun getItemCount(): Int = mTitles.size

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
                val h:Holder = holder as Holder
                h.bindData(mTitles[position],position,this@MainActivity)
            }

        }


    }

    class Holder(itemView: View?) : RecyclerView.ViewHolder(itemView){

        fun bindData(str:String,pos:Int,listener:AdapterView.OnItemClickListener){
            val tv:TextView=itemView.findViewById(R.id.titleTv)
            tv.text = str
            itemView.setOnClickListener {
                listener.onItemClick(null,itemView,pos,0)
            }
        }
    }

}
