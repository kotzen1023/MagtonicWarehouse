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

class ReturnOfGoodsItemAdapter (context: Context?, resource: Int, objects: ArrayList<ReturnOfGoodsItem>) :
    ArrayAdapter<ReturnOfGoodsItem>(context as Context, resource, objects) {

    private val layoutResourceId: Int = resource

    private var inflater : LayoutInflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private val items: ArrayList<ReturnOfGoodsItem> = objects
    //private val mContext = context

    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): ReturnOfGoodsItem? {
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


        val returnOfGoodsItem = items[position]

        if (returnOfGoodsItem.getIsSigned()) {
            //holder.itemSigned.visibility = View.VISIBLE
            view.setBackgroundColor(Color.YELLOW)
        } else
            view.setBackgroundColor(Color.TRANSPARENT)
        //if (receiptDetailItem != null) {
        /*val workOrderSize = IssuanceLookupItem.getData2().length
        //val partNpSize = outsourcedProcessOrderDetailItem.getData3().length
        val combine: String = IssuanceLookupItem.getData2()+"\n"+outsourcedProcessOrderDetailItem.getData3()
        val spannable = SpannableStringBuilder(combine)
        spannable.setSpan(
            ForegroundColorSpan(Color.RED),
            workOrderSize, // start
            combine.length, // end
            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
        )

        spannable.setSpan(
            StyleSpan(Typeface.BOLD_ITALIC),
            workOrderSize, // start
            combine.length, // end
            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
        )*/



        /*var partNo: String = getColoredSpanned(outsourcedProcessOrderDetailItem.getData3(), "#D81B60") as String
        Log.e(mTAG, "partNo = $partNo")
        if (Build.VERSION.SDK_INT >= 24) {
            combine = outsourcedProcessOrderDetailItem.getData2()+"\n"+ Html.fromHtml(partNo, FROM_HTML_MODE_LEGACY)
        } else {
            combine = outsourcedProcessOrderDetailItem.getData2()+"\n"+ Html.fromHtml(partNo)
        }*/
        //combine = outsourcedProcessOrderDetailItem.getData2()+"\n"+ Html.fromHtml("<font color='#D81B60'>"+outsourcedProcessOrderDetailItem.getData3()+"</font>")

        holder.itemHeader.text = returnOfGoodsItem.getData2()
        holder.itemContent.text = returnOfGoodsItem.getData3()
        //holder.itemQuantity.text = outsourcedProcessOrderDetailItem.getData4()




        return view
    }

    /*private fun getColoredSpanned(text: String, color: String): String? {
        return "<font color='$color'>$text</font>"
    }*/

    class ViewHolder (view: View) {
        var itemHeader: TextView = view.findViewById(R.id.returnOfGoodsItemHeader)
        var itemContent: TextView = view.findViewById(R.id.returnOfGoodsItemHContent)
        //var itemQuantity: TextView = view.findViewById(R.id.outSourcedProcessOrderDetailQuantity)
    }
}