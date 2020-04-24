package com.magtonic.magtonicwarehouse.data

import android.content.Context
import android.content.Intent
import android.text.InputType


import android.util.Log
import android.view.LayoutInflater

import android.view.View
import android.view.ViewGroup

import android.widget.*
import com.magtonic.magtonicwarehouse.R
import com.magtonic.magtonicwarehouse.fragment.MaterialIssuingFragment
import com.magtonic.magtonicwarehouse.fragment.PropertyFragment.Companion.itemCanChange


import java.util.ArrayList

class PropertyDetailItemAdapter(context: Context?, resource: Int, objects: ArrayList<PropertyDetailItem>) :
    ArrayAdapter<PropertyDetailItem>(context as Context, resource, objects) {

    private val mTAG = PropertyDetailItemAdapter::class.java.name
    private val layoutResourceId: Int = resource

    private var inflater : LayoutInflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    //val items: ArrayList<MaterialDetailItem> = objects
    var items: ArrayList<PropertyDetailItem> = ArrayList()
    private var mContext : Context? = null
    //private var modify = false

    var statusList: ArrayList<String> = ArrayList()

    init {
        Log.e(mTAG, "init")
        this.mContext = context
        this.items = objects

        statusList.add("取得")
        statusList.add("資本化")
        statusList.add("折舊中")
        statusList.add("外送")
        statusList.add("折畢")
        statusList.add("出售")
        statusList.add("銷帳")
        statusList.add("折畢再提")
        statusList.add("改良")
        statusList.add("重估")
    }

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
        //if (receiptDetailItem != null) {
        holder.itemHeader.text = propertyDetailItem.getHeader()
        holder.itemContent.text = propertyDetailItem.getContent()
        holder.itemEdit.setText(propertyDetailItem.getContent())


        if (position == itemCanChange) { //quantity sfs05
            val statusAdapter = ArrayAdapter(mContext as Context, R.layout.myspinner, statusList)
            holder.itemSpinner.adapter = statusAdapter

            //val select = propertyDetailItem.getContent()!!.toInt()
            //holder.itemSpinner.setSelection(select)
        }



        propertyDetailItem.setTextView(holder.itemContent)
        propertyDetailItem.setLinearLayout(holder.itemLayout)
        propertyDetailItem.setEditText(holder.itemEdit)
        propertyDetailItem.setSpinner(holder.itemSpinner)
        propertyDetailItem.setBtnOk(holder.itemBtnOk)


        holder.itemBtnOk.text = mContext!!.getString(R.string.confirm)

        holder.itemBtnOk.setOnClickListener {
            Log.e(mTAG, "adapter click $position")



            propertyDetailItem.setContent(holder.itemSpinner.selectedItem.toString())
            propertyDetailItem.getTextView()!!.text = holder.itemSpinner.selectedItem.toString()
            propertyDetailItem.getTextView()!!.visibility = View.VISIBLE
            propertyDetailItem.getLinearLayout()!!.visibility = View.GONE
            propertyDetailItem.getSpinner()!!.visibility = View.GONE

            val modifyIntent = Intent()
            modifyIntent.action = Constants.ACTION.ACTION_PROPERTY_MODIFY_CHANGED
            modifyIntent.putExtra("INDEX", propertyDetailItem.getSpinner()!!.selectedItemPosition)
            //modifyIntent.putExtra("CONTENT", materialDetailItem.getContent())
            mContext!!.sendBroadcast(modifyIntent)


        }

        return view
    }

    class ViewHolder (view: View) {
        var itemHeader: TextView = view.findViewById(R.id.propertyItemDetailHeader)
        var itemContent: TextView = view.findViewById(R.id.propertyItemDetailContent)
        var itemLayout: LinearLayout = view.findViewById(R.id.propertyItemDetailLayout)
        var itemEdit: EditText = view.findViewById(R.id.propertyItemDetailEdit)
        var itemSpinner: Spinner = view.findViewById(R.id.propertyItemSpinner)
        var itemBtnOk: Button = view.findViewById(R.id.propertyItemDetailBtnOk)
    }
}