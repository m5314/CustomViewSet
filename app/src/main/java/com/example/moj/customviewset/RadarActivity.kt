package com.example.moj.customviewset

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.moj.customviewset.widgets.RadarView
import kotlinx.android.synthetic.main.activity_radar.*
import java.util.*

class RadarActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_radar)
        refreshData()
        randomBtn.setOnClickListener {
            refreshData()
        }
    }

    private fun refreshData(){
        var data:ArrayList<RadarView.Ability> = ArrayList()
        for (i in 0..(Random().nextInt(6)+4)){
            data.add(RadarView.Ability("power:" + ('a' + i), (Random().nextInt(99) + 1).toDouble()))
        }
        radarView.setData(data)
    }
}
