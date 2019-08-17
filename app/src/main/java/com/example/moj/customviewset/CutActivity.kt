package com.example.moj.customviewset

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_cut.*

/**
 * @author : moj
 * @date : 2019/8/17
 * @description : 简单裁剪页面
 */
class CutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cut)
        supportActionBar?.hide()
        toolbar.inflateMenu(R.menu.cut_menu)
        cutLayout.setImageResource(R.mipmap.luff)
        toolbar.setOnMenuItemClickListener {
            cutLayout.clip(this@CutActivity)
            true
        }
        toolbar.postDelayed({
            cutLayout.setImageResource(R.mipmap.ic_launcher)
        }, 5000)

    }
}