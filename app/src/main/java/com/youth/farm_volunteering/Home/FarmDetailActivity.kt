package com.youth.farm_volunteering

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.asksira.loopingviewpagerdemo.ApplicationController
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.youth.farm_volunteering.Expanded.ExpandFragment
import com.youth.farm_volunteering.Home.ApplicationActivity
import com.youth.farm_volunteering.Home.DetailTabAdapter
import com.youth.farm_volunteering.Home.FarmIntroFragment
import com.youth.farm_volunteering.Home.FarmReviewFragment
import com.youth.farm_volunteering.Home.Schedule.DetailSchData
import com.youth.farm_volunteering.Home.Schedule.ScheduleAdapter
import com.youth.farm_volunteering.data.*
import com.youth.farm_volunteering.login.LoginToken
import kotlinx.android.synthetic.main.activity_farm_detail.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*


class FarmDetailActivity : AppCompatActivity(), View.OnClickListener, OnMapReadyCallback {

    lateinit var applyList: ArrayList<DetailApplyData>
    lateinit var detailApplyAdapter: DetailApplyAdapter
    private lateinit var mMap: GoogleMap


    //    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var toolbar: Toolbar? = null
    var userDataList: ArrayList<UserData>? = null

    var detailNonghwalList: NhInfoData? = null
    var detailFriendInfoList: ArrayList<FriendInfoData>? = null
    var detailFarmInfoList: FarmInfoData? = null
    lateinit var detailScheduleList: ArrayList<DetailSchData>
    var detailImageList: List<String>? = null
    var detailNearestStartDate: String? = null
    var detailNearestEndDate : String? = null
    lateinit var detailAllStartDate: ArrayList<AllStData>
    var detailMyScheduleActivities: ArrayList<Int>? = null
    
    var scheIdx: Int? = null
    var fixedStartDate : String? = null
    var fixedEndDate : String? = null

    var selectedStData : AllStData? = null

    lateinit var detailTabAdapter: DetailTabAdapter
    var scheduleAdapter: ScheduleAdapter? = null

    var fragment_Array: ArrayList<Fragment>? = ArrayList()
    var tabtextArray: ArrayList<String>? = null

    var bottomSheetDialog: BottomSheetDialog? = null
    var splitDateDay : Int? = null

//    activity_main_tabViewPager.adapter = tabAdapter
//    activity_main_bottomTabLayout.setupWithViewPager(activity_main_tabViewPager)

    var formatTemp : String? = null

//    lateinit var scheduleitems: ArrayList<ScheduleData>
//    lateinit var recycleItems: ArrayList<FarmRecyData>
//    lateinit var recycleAdapter: FarmRecyAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_farm_detail)

        toolbar = findViewById(R.id.toolbarDetail)
        setSupportActionBar(toolbar)    //뒤로가기버튼생성

        var isFromSearch: Boolean = intent.getBooleanExtra("is_from_search", false)
        var populData: NonghwalData

        if (!isFromSearch) {
            populData = intent.getParcelableExtra<HomeNonghwalData>("populData")
        } else {
            populData = intent.getSerializableExtra("populData") as NonghwalData
        }
        tabtextArray = arrayListOf("농활소개", "Q & A", "후기")
        fragment_Array = arrayListOf(FarmIntroFragment(), ExpandFragment(), FarmReviewFragment())

        for (i in 0..fragment_Array!!.size - 1) {
            tablayoutDetailActivity.addTab(tablayoutDetailActivity.newTab())        //프레그먼트 갯수만큼 탭 생성
        }

        var getDetail = ApplicationController.instance!!.networkService!!.detailnonghwal(populData.getRealId()!!)
        getDetail.enqueue(object : Callback<DetailNonghwalResponseData> {
            override fun onResponse(call: Call<DetailNonghwalResponseData>?, response: Response<DetailNonghwalResponseData>?) {
                if (response!!.isSuccessful) {
                    detailNonghwalList = response.body().nhInfo             //농활소개
                    detailFriendInfoList = response.body().friendsInfo     //농활소개
                    detailFarmInfoList = response.body().farmerInfo        //농활소개
                    detailScheduleList = response.body().schedule!!           //BottomSheetDialog 신청하기
                    detailNearestStartDate = response.body().nearestStartDate!!   //BottomSheetDialog 신청하기
                    detailNearestEndDate = response.body().nearestEndDate!!
                    detailAllStartDate = response.body().allStartDate!!            //BottomSheetDialog 신청하기

//                    var dateInput : String = detailNearestStartDate!!
//                    var parsedDate : Date = Date.parse(dateInput)



                    var dateFormat = SimpleDateFormat("yyyy-MM-dd")
                    var parsedStartDate : Date = dateFormat.parse(detailNearestStartDate)        //이렇게하면 SimpleDateFormat형 변수인 dateFormat으로
                                                                                                //파싱된 paredDate라는 2018년 07월 21일이라는
                                                                                                //Date형 변수가 얻어진다
                    var parsedEndDate : Date = dateFormat.parse(detailNearestEndDate)
                    var dateFormatStart = SimpleDateFormat("yyyy년 MM월 dd일")
                    var firstStartDate = dateFormatStart.format(parsedStartDate)
                    var dateFormatEnd = SimpleDateFormat(" ~ dd일")
                    var firstEndDate = dateFormatEnd.format(parsedEndDate)

                    var dateFormatDesigned = SimpleDateFormat("yyyy년 MM월 dd일")
                    var formattedDate = dateFormatDesigned.format(parsedStartDate)
//                    var dayFormatDesigned = SimpleDateFormat("~dd일")
//                    var formattedAfterDay = dayFormatDesigned.format(parsedDate)

                    if(detailNonghwalList!!.period!="당일 치기") {
                        var splitDay: List<String> = detailNonghwalList!!.period!!.split("박")       //ex) 1박 2일을 넣고 1과 2일로 쪼갬
                        var splitDayDay = Integer.parseInt(splitDay[0])                       //     '1'과 '2일' 중에서 1을 Int형으로 저장한 변수
                        var splitDate : List<String> = detailNearestStartDate!!.split("-")      //ex) 2018-07-21
//                      var splitDateYear = Integer.parseInt(splitDate[0])                                      //    2018
//                      var splitDateMonth = Integer.parseInt(splitDate[1])                                     //    07
                        splitDateDay = Integer.parseInt(splitDate[2])                                         //    21
                        var afterDate = splitDateDay!! + splitDayDay

                        buttonApplyDate.text = firstStartDate + firstEndDate
                        formatTemp = firstStartDate + firstEndDate
                    }else{
                        buttonApplyDate.text = firstStartDate
                    }

//                    var abc : SimpleDateFormat? = null
//                    var date : Date? = null
//                    var dateToStr : String? = null
//                    abc = SimpleDateFormat("yyyy-MM-dd")

                    if (response.body().myScheduleActivities != null) {
                        detailMyScheduleActivities = response.body().myScheduleActivities!!       //BottomSheetDialog 취소 만들기위한 sche
                    }


//                    detailApplyAdapter = DetailApplyAdapter(detailScheduleList, supportFragmentManager)
                    detailTabAdapter = DetailTabAdapter(supportFragmentManager, tablayoutDetailActivity.tabCount, populData.getRealId()!!,
                            detailNonghwalList!!, detailFriendInfoList!!, detailScheduleList!!, detailFarmInfoList!!)

//                    detailTabAdapter.setNhIntroContents(detailNonghwalList!!, detailFriendInfoList!!, detailFarmInfoList!!)

                    for (i in 0..fragment_Array!!.size - 1) {
                        detailTabAdapter!!.addFragment(tabtextArray!![i], fragment_Array!![i])        //프레그먼트에 맞는 탭의 TabData를 넣음
                    }

                    viewpagerDetailBottom.adapter = detailTabAdapter
                    tablayoutDetailActivity.setupWithViewPager(viewpagerDetailBottom)

                    for (i in 0..detailTabAdapter.count - 1) tablayoutDetailActivity.getTabAt(i)!!.setText(detailTabAdapter.getDetailTabDataList(i).tabText) // 탭에 커스텀뷰 설정

                    detailImageList = response.body().image                  //상단 농활이미지
                }

            }

            override fun onFailure(call: Call<DetailNonghwalResponseData>?, t: Throwable?) {

                Toast.makeText(applicationContext, "detail request fail", Toast.LENGTH_SHORT).show()

            }

        })


        buttonApplyButton.setOnClickListener {
            if (scheIdx == null) {
                selectedStData = detailAllStartDate[0]
            }

            var getUserInfo = ApplicationController.instance!!.networkService!!.getUserInfo()
            getUserInfo.enqueue(object : Callback<UserResponseData> {
                override fun onResponse(call: Call<UserResponseData>?, response: Response<UserResponseData>?) {
                    if (LoginToken.token != null) {
                        if (response!!.isSuccessful) {
                            if (response!!.body().message == "Success To Get User Info") {
                                userDataList = response.body().data
                                val intent = Intent(applicationContext, ApplicationActivity::class.java)
                                intent.putParcelableArrayListExtra("userData", userDataList)
                                intent.putExtra("nhIdx", populData!!.getRealId())
                                intent.putExtra("nhName", populData!!.name)
                                intent.putExtra("nhAddr", populData!!.addr)
                                intent.putExtra("nhPrice", populData!!.price)
                                intent.putExtra("nhImg", populData!!.img)
                                intent.putExtra("scheDate", buttonApplyDate.text.toString())
                                intent.putExtra("scheIdx", selectedStData!!.idx)
                                intent.putExtra("selectedStData", selectedStData)
                                intent.putExtra("period", populData.period)
                                startActivityForResult(intent, applyReqCode)
                            }
                        }
                    } else {
                        Toast.makeText(applicationContext, "로그인이 필요합니다!", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<UserResponseData>?, t: Throwable?) {
//
                    Toast.makeText(applicationContext, "Please Check Network", Toast.LENGTH_SHORT).show()
                }

            })

        }

        buttonApplyDate.setOnClickListener {
            bottomSheetDialog = BottomSheetDialog.instance
            val bundle = Bundle()
            bundle.putParcelableArrayList("scheList", detailScheduleList)
            bundle.putIntegerArrayList("myScheduleActivities", detailMyScheduleActivities)
            bundle.putParcelableArrayList("allStartItems", detailAllStartDate)
            bundle.putString("nearestStartDate", detailNearestStartDate)
            var bottomSheetDialog = BottomSheetDialog.instance
            bottomSheetDialog.onDismissListener = DialogInterface.OnDismissListener {
                if (bottomSheetDialog.selectedDate == null) {
                    buttonApplyDate.text = formatTemp
                } else {
                    buttonApplyDate.text = bottomSheetDialog.selectedDate
                }
                selectedStData = bottomSheetDialog.selectedStData
                scheIdx = bottomSheetDialog.selectedIdx
            }

            bottomSheetDialog!!.arguments = bundle
            bottomSheetDialog!!.show(supportFragmentManager, "bottomSheet")
        }

        //탭레이아웃 색상 선택
        tablayoutDetailActivity.setTabTextColors(Color.parseColor("#000000"), Color.parseColor("#3470FF"))
        viewpagerDetailBottom.setCurrentItem(0)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        toolbar!!.setTitleTextColor(0xFF000000.toInt())
        toolbar!!.title = " "

        Glide.with(applicationContext)
                .load(populData.img)
                .into(imageviewCollapse)
        imageviewCollapse.scaleType = ImageView.ScaleType.FIT_XY
        viewpagerDetailBottom.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tablayoutDetailActivity))

        viewpagerDetailBottom.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageSelected(position: Int) {

            }

        })

        tablayoutDetailActivity.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewpagerDetailBottom.currentItem = tab!!.position
            }

        })

    }


    override fun onClick(v: View?) {

        when (v) {


//            farm_introduce -> {
//                clearSelected()
//                farm_introduce.isSelected = true
//                replaceFragment(FarmIntroFragment())
//
//            }
//            farm_location -> {
//                clearSelected()
//                farm_location.isSelected = true
//
//                replaceFragment(ExpandFragment())    //MapsFragment()로 바꿔서 띄우고 싶은데 잘안됩니다...
//
////                var mapFragment = FarmFAQFragment() ;
////                mapFragment.getMapAsync(this)
//
//            }
//            farm_review -> {
//                clearSelected()
//                farm_review.isSelected = true
//                replaceFragment(FarmReviewFragment())
//            }
//

        }

        //따로 스캐줄에서 더 화면을 구성한다면!!!
//        val intent : Intent = Intent(applicationContext,TestActivity::class.java)
//        startActivity(intent)

    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val myPlace = LatLng(37.498728, 127.028814)
        mMap.addMarker(MarkerOptions().position(myPlace).title("농활 장소"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPlace, 15.0f))
        mMap.uiSettings.isZoomControlsEnabled = true
    }

    // 뒤로가기 함수

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    @RequiresApi(Build.VERSION_CODES.CUPCAKE)
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        var menuInflater = getMenuInflater()
        menuInflater!!.inflate(R.menu.menu_farmdetail, menu)


        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.menu_detail_bookmark -> {
//                val intent = Intent(applicationContext, FarmDetailActivity::class.java)
//                startActivity(intent)
            }
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode==applyReqCode){
//            Toast.makeText(applicationContext, "신청 완료!", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        val applyReqCode = 101;
    }


}
