package com.magtonic.magtonicwarehouse.data

import android.content.Context
import android.content.Intent

import android.graphics.Color
import android.util.Log

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.magtonic.magtonicwarehouse.R

class StorageDetailItemAdapter(context: Context?, resource: Int, objects: ArrayList<StorageDetailItem>) :
    ArrayAdapter<StorageDetailItem>(context as Context, resource, objects)  {
    private val mTAG = StorageDetailItemAdapter::class.java.name
    private val layoutResourceId: Int = resource

    private var inflater : LayoutInflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private val items: java.util.ArrayList<StorageDetailItem> = objects
    private val mContext = context

    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): StorageDetailItem? {
        return items[position]
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        //Log.e(mTAG, "getView = "+ position);
        val view: View
        val holder: ViewHolder
        if (convertView == null || convertView.tag == null) {

            view = inflater.inflate(layoutResourceId, null)
            holder = ViewHolder(view)
            //holder.checkbox.setVisibility(View.INVISIBLE);
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        val storageDetailItem = items[position]
        //if (receiptDetailItem != null) {
        holder.itemHeader.text = storageDetailItem.getHeader()
        holder.itemContent.text = storageDetailItem.getContent()
        holder.itemEdit.setText(storageDetailItem.getContent())

        if (storageDetailItem.getHeader().equals("入庫單號") ||
            storageDetailItem.getHeader().equals("允收量")) {
            holder.itemContent.setTextColor(Color.RED)
        }

        storageDetailItem.setTextView(holder.itemContent)
        storageDetailItem.setLinearLayout(holder.itemLayout)
        storageDetailItem.setEditText(holder.itemEdit)
        storageDetailItem.setBtnOk(holder.itemBtnOk)

        if (storageDetailItem.getChange()) {
            holder.itemContent.setTextColor(Color.BLUE)
        } else {
            holder.itemContent.setTextColor(Color.GRAY)
        }

        /*
        if (position == 12) { //rva06
            holder.itemHeader.setBackgroundColor(Color.YELLOW)
            holder.itemContent.setBackgroundColor(Color.YELLOW)
        }*/


        holder.itemBtnOk.setOnClickListener {
            Log.e(mTAG, "adapter click $position")

            val oldString: String = holder.itemContent.text.toString()
            val newString: String = holder.itemEdit.text.toString()

            if (!oldString.contentEquals(newString)) {
                //content changed
                storageDetailItem.setContent(holder.itemEdit.text.toString())
                storageDetailItem.getEditText()!!.setText(holder.itemEdit.text.toString())
                storageDetailItem.setChange(true)

                val modifyIntent = Intent()
                modifyIntent.action = Constants.ACTION.ACTION_STORAGE_MODIFY_CHANGED
                modifyIntent.putExtra("INDEX", position)
                modifyIntent.putExtra("CONTENT", storageDetailItem.getContent())
                mContext!!.sendBroadcast(modifyIntent)
            } else {

                val noModifyIntent = Intent()
                noModifyIntent.action = Constants.ACTION.ACTION_STORAGE_MODIFY_NO_CHANGED
                noModifyIntent.putExtra("INDEX", position)
                mContext!!.sendBroadcast(noModifyIntent)
            }



        }

        holder.itemEdit.setOnFocusChangeListener{ _, hasFocus ->
            if (!hasFocus) {
                Log.e(mTAG, "focus = false")
            } else {
                Log.e(mTAG, "focus = true")
                holder.itemEdit.setSelection(holder.itemEdit.text.length)
            }
        }

        return view
    }

    class ViewHolder (view: View) {
        var itemHeader: TextView = view.findViewById(R.id.storageItemDetailHeader)
        var itemContent: TextView = view.findViewById(R.id.storageItemDetailContent)
        var itemLayout: LinearLayout = view.findViewById(R.id.storageItemDetailLayout)
        var itemEdit: EditText = view.findViewById(R.id.storageItemDetailEdit)
        var itemBtnOk: Button = view.findViewById(R.id.storageItemDetailBtnOk)



        /*init {
            this.itemHeader = view.findViewById(R.id.receiptItemDetailHeader)
            this.itemContent = view.findViewById(R.id.receiptItemDetailContent)
            this.itemLayout = view.findViewById(R.id.receiptItemDetailLayout)
            this.itemEdit = view.findViewById(R.id.receiptItemDetailEdit)
            this.itemBtnOk = view.findViewById(R.id.receiptItemDetailBtnOk)
        }*/
    }
}