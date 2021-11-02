package com.magtonic.magtonicwarehouse.data

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.magtonic.magtonicwarehouse.R
import java.util.ArrayList

class OutsourcedProcessSupplierItemAdapter (context: Context?, resource: Int, objects: ArrayList<OutsourcedProcessSupplierItem>) :
    ArrayAdapter<OutsourcedProcessSupplierItem>(context as Context, resource, objects) {
    //private val mTAG = OutsourcedProcessSupplierItemAdapter::class.java.name
    private val layoutResourceId: Int = resource

    private var inflater : LayoutInflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private val items: ArrayList<OutsourcedProcessSupplierItem> = objects
    //private val mContext = context

    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): OutsourcedProcessSupplierItem? {
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


        val outsourcedProcessOrderItem = items[position]
        //if (receiptDetailItem != null) {
        if (outsourcedProcessOrderItem.getIsSigned()) {
            //holder.itemSigned.visibility = View.VISIBLE
            view.setBackgroundColor(Color.YELLOW)
        } else
            view.setBackgroundColor(Color.TRANSPARENT)

        if (outsourcedProcessOrderItem.getSignedNum() > 0) {
            holder.itemSigned.visibility = View.VISIBLE
            holder.itemHeader.text = outsourcedProcessOrderItem.getSignedNum().toString()
        } else {
            holder.itemSigned.visibility = View.GONE
        }
        holder.itemHeader.text = outsourcedProcessOrderItem.getData2()
        holder.itemContent.text = outsourcedProcessOrderItem.getData3()





        return view
    }

    class ViewHolder (view: View) {
        var itemSigned: TextView = view.findViewById(R.id.textViewSignedNumber)
        var itemHeader: TextView = view.findViewById(R.id.outSourcedProcessSendMaterialHeader)
        var itemContent: TextView = view.findViewById(R.id.outSourcedProcessWorkOrderContent)
    }
}