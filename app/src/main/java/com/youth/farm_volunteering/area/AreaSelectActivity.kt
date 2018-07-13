package com.youth.farm_volunteering.area

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.GridLayoutManager
import com.youth.farm_volunteering.Main.*
import com.youth.farm_volunteering.R
import com.youth.farm_volunteering.R.id.recyclerview_selectarea_area
import com.youth.farm_volunteering.search.SelectAreaAdapter
import kotlinx.android.synthetic.main.activity_selectarea.*
import kotlinx.android.synthetic.main.activity_selectarea.view.*
import java.util.HashMap

class AreaSelectActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selectarea)
        var map: HashMap<Int, Boolean> = HashMap();
        enumValues<Area>().forEach { area ->
            map.put(area.code, false)
        }
        recyclerview_selectarea_area.adapter = SelectAreaAdapter(map)
        recyclerview_selectarea_area.layoutManager = GridLayoutManager(this, 4)
    }
}