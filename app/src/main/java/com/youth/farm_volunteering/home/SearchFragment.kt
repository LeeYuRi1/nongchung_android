package com.youth.farm_volunteering.home

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.asksira.loopingviewpagerdemo.ApplicationController
import com.yongbeom.aircalendar.AirCalendarDatePickerActivity
import com.youth.farm_volunteering.R
import kotlinx.android.synthetic.main.fragment_search.*
import com.yongbeom.aircalendar.core.AirCalendarIntent
import com.youth.farm_volunteering.SearchResultAdapter
import com.youth.farm_volunteering.area.Area
import com.youth.farm_volunteering.area.AreaSelectActivity
import com.youth.farm_volunteering.data.SearchResponseData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*


class SearchFragment : Fragment() {
    val searchDateFormat: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
    var registrationUserCount = 1
    var searchAreaArray: MutableList<Area> = mutableListOf(Area.ALL)
    var startDateString: String = "1992-09-12"
    var endDateString: String = "2030-09-12"

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == 42) {
            if (data != null) {
                startDateString = data.getStringExtra(AirCalendarDatePickerActivity.RESULT_SELECT_START_DATE);
                endDateString = data.getStringExtra(AirCalendarDatePickerActivity.RESULT_SELECT_END_DATE);
                textview_search_date.setText(startDateString + "~" + endDateString)
            }
        }
        if (resultCode == RESULT_OK && requestCode == 43) {
            searchAreaArray = ArrayList()
            if (data != null) {
                var selectMap = (data.getSerializableExtra("area_map") as HashMap<Int, Boolean>);
                enumValues<Area>().forEach { area ->
                    if (selectMap.get(area.code)!!) {
                        searchAreaArray.add(area)
                    }
                };
                var sb = StringBuilder()
                searchAreaArray.forEach { area ->
                    sb.append(area.regionName)
                    if (searchAreaArray.last() != area) {
                        sb.append(",")
                    }
                }
                view!!.findViewById<TextView>(R.id.textview_search_area).setText(sb.toString())
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater!!.inflate(R.layout.fragment_search, container, false)
        (activity as AppCompatActivity).supportActionBar!!.hide()
        v.findViewById<View>(R.id.layout_search_date).setOnClickListener {
            val intent = AirCalendarIntent(activity)
            intent.isBooking(false)
            intent.isSelect(false)
//            intent.setBookingDateArray('Array Dates( format: yyyy-MM-dd')
//            intent.setStartDate(yyyy, MM, dd) // int
//            intent.setEndDate(yyyy, MM, dd) // int
            intent.isMonthLabels(false)
            startActivityForResult(intent, 42)
        }
        v.findViewById<View>(R.id.layout_search_area).setOnClickListener {
            val intent = Intent(activity, AreaSelectActivity::class.java)
            startActivityForResult(intent, 43)
        }
        v.findViewById<View>(R.id.activity_search_plusButton).setOnClickListener {
            registrationUserCount = Math.min(9, registrationUserCount + 1)
            v.findViewById<TextView>(R.id.activity_search_participants).setText(registrationUserCount.toString())
        }
        v.findViewById<View>(R.id.activity_search_minusButton).setOnClickListener {
            registrationUserCount = Math.max(1, registrationUserCount - 1)
            v.findViewById<TextView>(R.id.activity_search_participants).setText(registrationUserCount.toString())
        }
        v.findViewById<View>(R.id.activity_search_button).setOnClickListener {

            //            @Query("start") startDate: String,
//            @Query("end") endDate: String,
//            @Query("person") personCount: Int,
//            @Query("scontent") content: String,
//            @Query("area") area: Int
            var sb = StringBuilder()
            sb.append("[")
            searchAreaArray.forEach { area ->
                sb.append(area.code)
                sb.append(",")
            }
            sb.append("18]")
            var areaArrayString: String = sb.toString()
            var searchCall = ApplicationController.instance!!.networkService!!.search(startDateString, endDateString, registrationUserCount, v.findViewById<EditText>(R.id.edittext_search_content).text.toString(), areaArrayString); // 서버에서 데이터 가져오는거!!
            searchCall.enqueue(object : Callback<SearchResponseData> {
                override fun onFailure(call: Call<SearchResponseData>, t: Throwable?) {
                    Toast.makeText(activity, "like request fail", Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(call: Call<SearchResponseData>, response: Response<SearchResponseData>) {

                    var searchList = response.body().data
                    var searchAdapter = SearchResultAdapter(searchList!!)
                    v.findViewById<RecyclerView>(R.id.recyclerview_search_result).adapter = searchAdapter

                    v.findViewById<RecyclerView>(R.id.recyclerview_search_result).layoutManager = LinearLayoutManager(context)

                }
            })

        }
        return v
    }

}