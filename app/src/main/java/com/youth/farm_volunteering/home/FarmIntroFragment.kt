package com.youth.farm_volunteering.home
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.support.annotation.RequiresApi
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.airbnb.lottie.parser.IntegerParser
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.youth.farm_volunteering.FarmDetailActivity
import com.youth.farm_volunteering.home.FarmProfile.FarmProfileActivity
import com.youth.farm_volunteering.home.Schedule.DetailSchData
import com.youth.farm_volunteering.home.Schedule.ScheduleAdapter
import com.youth.farm_volunteering.R
import com.youth.farm_volunteering.data.FarmInfoData
import com.youth.farm_volunteering.data.FriendInfoData
import com.youth.farm_volunteering.data.NhInfoData
import com.youth.farm_volunteering.data.NonghwalData
import kotlinx.android.synthetic.main.fragment_farm_introduce.view.*


class FarmIntroFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClint: FusedLocationProviderClient
    lateinit var scheduleAdapter: ScheduleAdapter

    var DetailNonghwalList: NhInfoData? = null
    var DetailFriendInfoList: List<FriendInfoData>? = null
    var DetailFarmInfoList: FarmInfoData? = null
    var DetailScheduleList: ArrayList<DetailSchData>? = null
    var nhIdx : Int? = null

    private var introduceImage_linearLayoutManager: LinearLayoutManager? = null

    override  fun onResume(){
        super.onResume()
        var mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val myPlace = LatLng(37.512994, 127.100824)
        mMap.addMarker(MarkerOptions().position(myPlace).title("농활 장소"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPlace, 15.0f))
        mMap.uiSettings.isZoomControlsEnabled = true
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val v = inflater.inflate(R.layout.fragment_farm_introduce, container, false)

        DetailNonghwalList = arguments.getParcelable("nhInfo")
        DetailFriendInfoList = arguments.getParcelableArrayList("friendsInfo")
        DetailFarmInfoList = arguments.getParcelable("farmerInfo")
        DetailScheduleList = arguments.getParcelableArrayList("scheduleInfo")

        var builderWomanPer = StringBuilder()
        var builderManPer = StringBuilder()

        v.detail_introduce_addr.text = DetailNonghwalList!!.addr
        v.detail_introduce_name.text = DetailNonghwalList!!.name
//                setText(DetailNonghwalList!!.star.toString())
        v.detail_introduce_star.rating = (DetailNonghwalList!!.star!!/2.0f) //rating에 서버에서 float값 받아와서 생성
        v.detail_introduce_description.text = DetailNonghwalList!!.description
        v.detail_introduce_price.text = DetailNonghwalList!!.price.toString() + "원"
        v.detail_introduce_period.text = DetailNonghwalList!!.period.toString()

//        String.format("%.1f", totalScoreTemp!! / ReviewList!!.size)
        if(DetailFriendInfoList!!.get(0).attendCount!=0) {

            if(DetailFriendInfoList!!.get(0).womanCount ==0) {
                builderWomanPer.append(DetailFriendInfoList!!.get(0).womanCount!!)
                        .append("%")

                builderManPer.append(String.format("%.0f", ((DetailFriendInfoList!!.get(0).manCount!!).toFloat() / DetailFriendInfoList!!.get(0).attendCount!!.toFloat())*100))
                        .append("%")
            } else if(DetailFriendInfoList!!.get(0).manCount == 0){
                builderWomanPer.append(String.format("%.0f", ((DetailFriendInfoList!!.get(0).womanCount!!).toFloat()) / (DetailFriendInfoList!!.get(0).attendCount!!.toFloat()) * 100))
                        .append("%")

                builderManPer.append(DetailFriendInfoList!!.get(0).manCount!!)
                        .append("%")
            }else{
                builderWomanPer.append(String.format("%.0f", ((DetailFriendInfoList!!.get(0).womanCount!!).toFloat() / DetailFriendInfoList!!.get(0).attendCount!!.toFloat())*100))
                        .append("%")
                builderManPer.append(String.format("%.0f", ((DetailFriendInfoList!!.get(0).manCount!!).toFloat() / DetailFriendInfoList!!.get(0).attendCount!!.toFloat())*100))
                        .append("%")
            }

            v.textview_woman_per.text = builderWomanPer.toString()
            v.textview_man_per.text = builderManPer.toString()
        } else{
            builderWomanPer.append(DetailFriendInfoList!!.get(0).womanCount!!)
                    .append("%")
            builderManPer.append(DetailFriendInfoList!!.get(0).manCount!!)
                    .append("%")

            v.textview_woman_per.text = builderWomanPer.toString()
            v.textview_man_per.text = builderManPer.toString()
        }

        v.textview_avg_age.text = String.format("%.1f",DetailFriendInfoList!!.get(0).ageAverage)
        v.textview_cur_apply.text = DetailFriendInfoList!!.get(0).attendCount.toString()
        v.textview_max_apply.text = "/" + DetailFriendInfoList!!.get(0).personLimit.toString()

        var progress : List<String> = builderWomanPer.toString().split("%")

        v.progressbar_gender_per.progress = progress[0].toInt()

        v.farminfo_name.text = DetailFarmInfoList!!.name
        v.farminfo_comment.text = DetailFarmInfoList!!.comment
        Glide.with(activity.applicationContext)
                .load(DetailFarmInfoList!!.img)
                .into(v.farminfo_image)

        scheduleAdapter = ScheduleAdapter(DetailScheduleList!!)

        v.scheduleView_rv.layoutManager = LinearLayoutManager(activity.applicationContext)
        v.scheduleView_rv.adapter = scheduleAdapter

        v.detail_profile_watch_btn.setOnClickListener(View.OnClickListener  {
//            Toast.makeText(activity.applicationContext, "준비중입니다!", Toast.LENGTH_SHORT).show()
            var v = Intent(this.context,FarmProfileActivity::class.java)

            v.putExtra("farmIdx", DetailFarmInfoList!!.farmIdx)
//            activity.intent.putExtra("populData", nhIdx as Parcelable)

            activity.startActivity(v)
        })

        return v

    }
    @RequiresApi(Build.VERSION_CODES.CUPCAKE)

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.menu_detail_bookmark -> {
//                val intent = Intent(applicationContext, FarmDetailActivity::class.java)
//                startActivity(intent)
            }
        }

        return true
    }


//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//        IntroduceImageView_rv.layoutManager = LinearLayoutManager(context)
////
//        introduceImage_linearLayoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
//
//        IntroduceImageView_rv!!.setLayoutManager(introduceImage_linearLayoutManager)}


}


