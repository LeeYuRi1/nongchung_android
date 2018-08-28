package com.youth.farm_volunteering

import android.content.Intent
import android.os.Parcelable
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.asksira.loopingviewpagerdemo.ApplicationController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.youth.farm_volunteering.data.BookmarkData
import com.youth.farm_volunteering.data.DetailNonghwalResponseData
import com.youth.farm_volunteering.data.FarmInfoData
import com.youth.farm_volunteering.data.HomeNonghwalData
import com.youth.farm_volunteering.login.LoginActivity
import com.youth.farm_volunteering.login.LoginDialog
import com.youth.farm_volunteering.login.LoginToken
import com.youth.farm_volunteering.signup.SignupActivity
import com.youth.farm_volunteering.signup.SignupActivity1
import com.youth.farm_volunteering.home.DetailTabAdapter
import com.youth.farm_volunteering.main.MainActivity
import kotlinx.android.synthetic.main.activity_farm_detail.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class WeekFarmAdapter(var dataListHome: List<HomeNonghwalData>) : RecyclerView.Adapter<WeekFarmItemViewHolder>() {
    var loginDialog : LoginDialog? = null

    var detailFarmInfoList: FarmInfoData? = null

    override fun getItemCount(): Int = dataListHome.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeekFarmItemViewHolder {
        val mainView: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_farm, parent, false)
        return WeekFarmItemViewHolder(mainView)
    }

    override fun onBindViewHolder(holderWeek: WeekFarmItemViewHolder, position: Int) {

        Glide.with(holderWeek.itemView.context)
                .load(dataListHome[position].img)
                .apply(RequestOptions().placeholder(R.drawable.loading_big_image))
                .into(holderWeek.pic)
        holderWeek.date.text = dataListHome[position].period
        holderWeek.title.text = dataListHome[position].name
        holderWeek.address.text = dataListHome[position].addr
        holderWeek.price.text = dataListHome[position].price.toString()
        holderWeek.star.rating = dataListHome[position].star!! / 2.0f
        if (dataListHome[position].isBooked != null) {
            when (dataListHome[position].isBooked) {
                0 -> holderWeek.isBooked.isSelected = false
                1 -> holderWeek.isBooked.isSelected = true
            }
        }
        holderWeek.starNum.text = String.format("%.1f", dataListHome[position].star!! /2.0f)

        holderWeek.isBooked.setOnClickListener {
            if(LoginToken.logined) {
                if (dataListHome[position].isBooked == 0) {
                    var bookMark = ApplicationController.instance!!.networkService!!.bookMark(Integer.parseInt(dataListHome[position].nhIdx.toString()))
                    bookMark.enqueue(object : Callback<BookmarkData> {
                        override fun onFailure(call: Call<BookmarkData>?, t: Throwable?) {
                            Toast.makeText(holderWeek.itemView.context, "bookmark request fail", Toast.LENGTH_SHORT).show()
                        }

                        override fun onResponse(call: Call<BookmarkData>?, response: Response<BookmarkData>?) {
                            Log.d("aaa", dataListHome[position].nhIdx.toString())
                            Log.d("aaa", dataListHome[position].idx.toString())
                            if (response!!.body().message == "Success to Add") {
                                holderWeek.isBooked.isSelected = true
                                dataListHome[position].isBooked = 1
                            } else if (response!!.body().message == "Already Exist") {
                                Toast.makeText(holderWeek.itemView.context, "이미 북마크에 저장하였습니다", Toast.LENGTH_SHORT).show()
                            }
                        }

                    })
                }

                if (dataListHome[position].isBooked == 1) {
                    var delete = ApplicationController.instance!!.networkService!!.delete(Integer.parseInt(dataListHome[position].nhIdx.toString()))
                    delete.enqueue(object : Callback<BookmarkData> {
                        override fun onFailure(call: Call<BookmarkData>?, t: Throwable?) {
                            Toast.makeText(holderWeek.itemView.context, "bookmark request fail", Toast.LENGTH_SHORT).show()
                        }

                        override fun onResponse(call: Call<BookmarkData>?, response: Response<BookmarkData>?) {
                            if (response!!.body().message == "Success to Delete") {
                                holderWeek.isBooked.isSelected = false
                                dataListHome[position].isBooked = 0
                            } else if (response!!.body().message == "No nonghwal activity") {
                                Toast.makeText(holderWeek.itemView.context, "에러가 발생하였습니다", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(holderWeek.itemView.context, response!!.body().message, Toast.LENGTH_SHORT).show()
                            }

                        }
                    })


                }
            } else{
                val loginOkListener = View.OnClickListener {
                    val intent = Intent(holderWeek.itemView.context, LoginActivity::class.java)
                    loginDialog!!.dismiss()
                    holderWeek.itemView.context.startActivity(intent)
                }

                val loginCancelListener = View.OnClickListener {
                    loginDialog!!.dismiss()
                }

                val signUpListener = View.OnClickListener {
                    val intent = Intent(holderWeek.itemView.context, SignupActivity1::class.java)
                    loginDialog!!.dismiss()
                    holderWeek.itemView.context.startActivity(intent)
                }

                loginDialog = LoginDialog(holderWeek.itemView.context, loginOkListener, loginCancelListener, signUpListener)
                loginDialog!!.show()
            }
        }

        holderWeek.itemView.setOnClickListener {
            val intent = Intent(holderWeek.itemView.context, FarmDetailActivity::class.java)
            intent.putExtra("populData", dataListHome[position] as Parcelable)
            
            holderWeek.itemView.context.startActivity(intent)
        }
    }


//
//    var onItemClick : View.OnClickListener? = null
//
//    override fun getItemCount(): Int = dataListHome.size
//
//    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): FarmGridViewHolder {
//        val mainView = LayoutInflater.from(parent!!.context).inflate(R.layout.item_farm, parent, false)
//        mainView.setOnClickListener(onItemClick)
//        return FarmGridViewHolder(mainView)
//    }
//
//    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//
//    fun setOnItemClickListener(l:View.OnClickListener){
//        onItemClick = l
//    }
}
