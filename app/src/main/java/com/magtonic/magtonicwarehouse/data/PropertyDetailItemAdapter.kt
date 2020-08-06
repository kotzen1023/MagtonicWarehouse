package com.magtonic.magtonicwarehouse.data

import android.content.Context

import android.view.LayoutInflater

import android.view.View
import android.view.ViewGroup

import android.widget.*
import com.magtonic.magtonicwarehouse.R



import java.util.ArrayList

class PropertyDetailItemAdapter(context: Context?, resource: Int, objects: ArrayList<PropertyDetailItem>) :
    ArrayAdapter<PropertyDetailItem>(context as Context, resource, objects) {

    //private val mTAG = OutsourcedProcessDetailItemAdapter::class.java.name
    private val layoutResourceId: Int = resource

    private var inflater : LayoutInflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private val items: ArrayList<PropertyDetailItem> = objects
    //private val mContext = context

    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): PropertyDetailItem? {
        return items[position]
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        //Log.e(mTAG, "getView = "+ position);
        val view: View
        val holder: ViewHolder
        if (convertView == null || convertView.tag == null) {
            //Log.e(mTAG, "convertView = null");
            /*view = inflater.inflate(layoutResourceId, null);
            holder = new ViewHolder(view);
            view.setTag(holder);*/

            //LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(layoutResourceId, null)
            holder = ViewHolder(view)
            //holder.checkbox.setVisibility(View.INVISIBLE);
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        //holder.fileicon = (ImageView) view.findViewById(R.id.fd_Icon1);
        //holder.filename = (TextView) view.findViewById(R.id.fileChooseFileName);
        //holder.checkbox = (CheckBox) view.findViewById(R.id.checkBoxInRow);


        val propertyDetailItem = items[position]


        holder.itemHeader.text = propertyDetailItem.faj02
        holder.itemContent.text = propertyDetailItem.faj06





        return view
    }

    /*private fun getColoredSpanned(text: String, color: String): String? {
        return "<font color='$color'>$text</font>"
    }*/

    class ViewHolder (view: View) {
        var itemHeader: TextView = view.findViewById(R.id.propertyItemDetailHeader)
        var itemContent: TextView = view.findViewById(R.id.propertyItemDetailContent)
    }
}