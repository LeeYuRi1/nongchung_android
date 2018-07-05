package com.youth.farm_volunteering.Expanded

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ExpandableListView
import android.widget.TextView
import android.widget.Toast
import com.youth.farm_volunteering.R
import com.youth.farm_volunteering.R.id.tv_title
import kotlinx.android.synthetic.main.layout_group.view.*

class ExpandAdapter(var context: Context, var expandedView: ExpandableListView, var header: MutableList<String>, var body: MutableList<MutableList<String>>) : BaseExpandableListAdapter() {
    override fun getGroup(groupPosition: Int): String {
        return header[groupPosition]
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View? {
        var convertView = convertView
        if (convertView == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.layout_group, null)
        }
        val title = convertView!!.findViewById<TextView>(R.id.tv_title)
        title.text = getGroup(groupPosition)
        title?.setOnClickListener {
            if (expandedView.isGroupExpanded(groupPosition)) {
                expandedView.collapseGroup(groupPosition)
                //convertView!!.imageView.isPressed = false

            } else {
                expandedView.expandGroup(groupPosition)
                //convertView!!.imageView.isPressed = true
            }
            Toast.makeText(context, getGroup(groupPosition), Toast.LENGTH_SHORT).show()
        }
        if (isExpanded) {
            convertView.imageView.isPressed = true
        } else {
            convertView.imageView.isPressed = false
        }
        return convertView
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return body[groupPosition].size

    }

    override fun getChild(groupPosition: Int, childPosition: Int): String {
        return body[groupPosition][childPosition]
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup?): View? {
        var convertView = convertView



        if (convertView == null) {

            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.layout_child, null)
        }
        val title = convertView?.findViewById<TextView>(R.id.tv_title)
        title?.text = getChild(groupPosition, childPosition)
        title?.setOnClickListener {

            Toast.makeText(context, getChild(groupPosition, childPosition), Toast.LENGTH_SHORT).show()
        }

        return convertView
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun getGroupCount(): Int {
        return header.size
    }
}