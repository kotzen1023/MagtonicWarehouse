package com.magtonic.magtonicwarehouse.data

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.text.InputType

import android.util.Log
import android.view.LayoutInflater

import android.view.View
import android.view.ViewGroup

import android.widget.*
import com.magtonic.magtonicwarehouse.R
import java.util.*

class ReceiptDetailItemAdapter(context: Context?, resource: Int, objects: ArrayList<ReceiptDetailItem>) :
    ArrayAdapter<ReceiptDetailItem>(context as Context, resource, objects) {
    private val mTAG = ReceiptDetailItemAdapter::class.java.name
    private val layoutResourceId: Int = resource

    private var inflater : LayoutInflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private val items: ArrayList<ReceiptDetailItem> = objects
    private val mContext = context


    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): ReceiptDetailItem? {
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


        val receiptDetailItem = items[position]
        //if (receiptDetailItem != null) {
            holder.itemHeader.text = receiptDetailItem.getHeader()
            holder.itemContent.text = receiptDetailItem.getContent()
            holder.itemEdit.setText(receiptDetailItem.getContent())

            receiptDetailItem.setTextView(holder.itemContent)
            receiptDetailItem.setLinearLayout(holder.itemLayout)
            receiptDetailItem.setEditText(holder.itemEdit)
            receiptDetailItem.setBtnOk(holder.itemBtnOk)

            if (receiptDetailItem.getChange()) {
                holder.itemContent.setTextColor(Color.BLUE)
            } else {
                holder.itemContent.setTextColor(Color.GRAY)
            }

            if (position == 2) { //quantity

                holder.itemEdit.inputType = (InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL)
            }

            /*if (position == 7) {
                holder.itemContent.setTextColor(Color.RED)
            } else {
                holder.itemContent.setTextColor(Color.BLACK)
            }*/

            if (position == 13) { //rva06 收貨日期
                holder.itemHeader.setBackgroundColor(Color.YELLOW)
                holder.itemContent.setBackgroundColor(Color.YELLOW)
            }


            holder.itemBtnOk.setOnClickListener {
                Log.e(mTAG, "adapter click $position")



                /*if (position == 2) {
                    val old: Int = holder.itemContent.text.toString().toInt()
                    try {
                        val new: Int = holder.itemEdit.text.toString().toInt()
                        Log.e(mTAG, "old = "+old+", new = "+new)

                        if (new > 0) {
                            receiptDetailItem.setContent(holder.itemEdit.text.toString())
                            receiptDetailItem.getEditText()!!.setText(holder.itemEdit.text.toString())
                        } else {
                            receiptDetailItem.getEditText()!!.setText(holder.itemContent.text.toString())
                        }


                    } catch (e: NumberFormatException) {
                        receiptDetailItem.getEditText()!!.setText(holder.itemContent.text.toString())
                        e.printStackTrace()
                    }

                } else { //position =0, 1
                    receiptDetailItem.setContent(holder.itemEdit.text.toString())
                    receiptDetailItem.getEditText()!!.setText(holder.itemEdit.text.toString())
                }*/

                val oldString: String = holder.itemContent.text.toString()
                val newString: String = holder.itemEdit.text.toString()

                if (!oldString.contentEquals(newString)) {
                    //content changed
                    receiptDetailItem.setContent(holder.itemEdit.text.toString().toUpperCase(Locale.ROOT))
                    receiptDetailItem.getEditText()!!.setText(holder.itemEdit.text.toString())
                    receiptDetailItem.setChange(true)

                    val modifyIntent = Intent()
                    modifyIntent.action = Constants.ACTION.ACTION_RECEIPT_MODIFY_CHANGED
                    modifyIntent.putExtra("INDEX", position)
                    modifyIntent.putExtra("CONTENT", receiptDetailItem.getContent())
                    mContext!!.sendBroadcast(modifyIntent)
                } else {

                    val noModifyIntent = Intent()
                    noModifyIntent.action = Constants.ACTION.ACTION_RECEIPT_MODIFY_NO_CHANGED
                    noModifyIntent.putExtra("INDEX", position)
                    mContext!!.sendBroadcast(noModifyIntent)
                }



            }



            /*holder.itemEdit.setOnEditorActionListener { _, actionId, _ ->

                when(actionId) {
                    EditorInfo.IME_ACTION_DONE -> {
                        Log.e(mTAG, "IME_ACTION_DONE")
                        val oldString: String = holder.itemContent.text.toString()
                        val newString: String = holder.itemEdit.text.toString()

                        if (!oldString.contentEquals(newString)) {
                            //content changed
                            receiptDetailItem.setContent(holder.itemEdit.text.toString())
                            receiptDetailItem.getEditText()!!.setText(holder.itemEdit.text.toString())
                            receiptDetailItem.setChange(true)

                            val modifyIntent = Intent()
                            modifyIntent.action = Constants.ACTION.ACTION_RECEIPT_MODIFY_CHANGED
                            modifyIntent.putExtra("INDEX", position)
                            modifyIntent.putExtra("CONTENT", receiptDetailItem.getContent())
                            mContext!!.sendBroadcast(modifyIntent)
                        } else {

                            val noModifyIntent = Intent()
                            noModifyIntent.action = Constants.ACTION.ACTION_RECEIPT_MODIFY_NO_CHANGED
                            noModifyIntent.putExtra("INDEX", position)
                            mContext!!.sendBroadcast(noModifyIntent)
                        }

                        true
                    }

                    else -> {
                        false
                    }
                }


            }*/

            holder.itemEdit.setOnFocusChangeListener{ _, hasFocus ->
                if (!hasFocus) {
                    Log.e(mTAG, "position $position focus = false")
                } else {
                    Log.e(mTAG, "position $position focus = true")


                    holder.itemEdit.setSelection(holder.itemEdit.text.length)
                }
            }

            /*holder.itemEdit.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                }

                override fun afterTextChanged(s: Editable) {
                    //Log.e(mTAG, "item[index: "+holder.index+", position: "+position+"] change to "+s.toString());


                }
            })*/




        //}
        return view
    }

    class ViewHolder (view: View) {
        var itemHeader: TextView = view.findViewById(R.id.receiptItemDetailHeader)
        var itemContent: TextView = view.findViewById(R.id.receiptItemDetailContent)
        var itemLayout: LinearLayout = view.findViewById(R.id.receiptItemDetailLayout)
        var itemEdit: EditText = view.findViewById(R.id.receiptItemDetailEdit)
        var itemBtnOk: Button = view.findViewById(R.id.receiptItemDetailBtnOk)



        /*init {
            this.itemHeader = view.findViewById(R.id.receiptItemDetailHeader)
            this.itemContent = view.findViewById(R.id.receiptItemDetailContent)
            this.itemLayout = view.findViewById(R.id.receiptItemDetailLayout)
            this.itemEdit = view.findViewById(R.id.receiptItemDetailEdit)
            this.itemBtnOk = view.findViewById(R.id.receiptItemDetailBtnOk)
        }*/
    }
}