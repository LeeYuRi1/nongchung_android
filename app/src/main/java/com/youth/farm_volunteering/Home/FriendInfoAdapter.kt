package com.youth.farm_volunteering.Home

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.youth.farm_volunteering.R
import com.youth.farm_volunteering.data.FriendInfoData

//참석자 image를 나오게 하는거라고 생각하고있음!
class FriendInfoAdapter(var dataList: List<FriendInfoData>) : RecyclerView.Adapter<FriendInfoViewHolder>() {
    override fun getItemCount(): Int = dataList.size

    //private lateinit var onIntroClick: View.OnClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendInfoViewHolder {
        val mainView : View = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_friendinfo, parent, false)
        //mainView.setOnClickListener(onIntroClick)
        return FriendInfoViewHolder(mainView)
    }

    override fun onBindViewHolder(holder: FriendInfoViewHolder?, position: Int) {
        Glide.with(holder!!.itemView.context)
                .load(dataList[position].img) //String 줘서 이렇게??
                .into(holder.Friendimage)
        holder.Friendname.text = dataList[position].name
    }


}