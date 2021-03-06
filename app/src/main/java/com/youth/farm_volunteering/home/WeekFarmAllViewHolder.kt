package com.youth.farm_volunteering.home

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.youth.farm_volunteering.R

class WeekFarmAllViewHolder(itemView : View?) : RecyclerView.ViewHolder(itemView) {

    var picall : ImageView = itemView!!.findViewById<ImageView>(R.id.farm_pic_all) as ImageView
    var dateall : TextView = itemView!!.findViewById<TextView>(R.id.farm_date_all) as TextView
    var titleall : TextView = itemView!!.findViewById<TextView>(R.id.farm_title_profile) as TextView
    var addressall : TextView = itemView!!.findViewById<TextView>(R.id.farm_address_profile) as TextView
    var priceall : TextView = itemView!!.findViewById<TextView>(R.id.farm_price_profile) as TextView
    var starall : RatingBar = itemView!!.findViewById(R.id.ratingBarFarmItem_profile)
    var isBookedall : ImageView = itemView!!.findViewById(R.id.imageViewBookmark_all)
    var starNumall : TextView = itemView!!.findViewById(R.id.textViewRating_all)

}