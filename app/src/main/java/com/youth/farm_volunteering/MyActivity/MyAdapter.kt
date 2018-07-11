package com.youth.farm_volunteering.MyActivity

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.youth.farm_volunteering.FarmItemViewHolder
import com.youth.farm_volunteering.R
import com.youth.farm_volunteering.data.NonghwalData

class MyAdapter(var myList : List<MyData>) : RecyclerView.Adapter<MyViewHolder>() {

    override fun getItemCount(): Int = myList.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MyViewHolder {
        val v : View = LayoutInflater.from(parent!!.context)
                .inflate(R.layout.fragment_myactivity, parent, false)
        return MyViewHolder(v)
    }



    override fun onBindViewHolder(holder: MyViewHolder?, position: Int) {
        Glide.with(holder!!.itemView.context)
                .load(myList[position].image)
                .into(holder.myimage)
        holder.myname.text = myList[position].nonghwalname
        holder.myplace.text = myList[position].place
        holder.myprice.text = myList[position].price
        holder.mystartdate.text = myList[position].startdate
        holder.myenddate.text = myList[position].enddate
        holder.mypeople.text = myList[position].people
        holder.mypeoplecount.text = myList[position].peoplecount
    }
}