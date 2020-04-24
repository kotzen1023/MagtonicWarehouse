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
import java.util.ArrayList

class OutsourcedProcessLowerPartItemAdapter(context: Context?, resource: Int, objects: ArrayList<OutsourcedProcessLowerPartItem>) :
    ArrayAdapter<OutsourcedProcessLowerPartItem>(context as Context, resource, objects) {
    private val mTAG = OutsourcedProcessLowerPartItemAdapter::class.java.name
    private val layoutResourceId: Int = resource

    private var inflater : LayoutInflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private val items: ArrayList<OutsourcedProcessLowerPartItem> = objects
    private val mContext = context


    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): OutsourcedProcessLowerPartItem? {
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


        val outsourcedProcessLowerPartItem = items[position]
        //if (receiptDetailItem != null) {
        holder.itemHeader.text = outsourcedProcessLowerPartItem.getHeader()
        holder.itemContentStatic.text = outsourcedProcessLowerPartItem.getContentStatic()
        holder.itemContentDynamic.text = outsourcedProcessLowerPartItem.getContentDynamic()
        holder.itemEdit.setText(outsourcedProcessLowerPartItem.getContentDynamic())

        outsourcedProcessLowerPartItem.setTextViewStatic(holder.itemContentStatic)
        outsourcedProcessLowerPartItem.setTextViewDynamic(holder.itemContentDynamic)
        outsourcedProcessLowerPartItem.setLinearLayout(holder.itemLayout)
        outsourcedProcessLowerPartItem.setEditText(holder.itemEdit)
        outsourcedProcessLowerPartItem.setBtnOk(holder.itemBtnOk)

        if (outsourcedProcessLowerPartItem.getChange()) {
            holder.itemContentDynamic.setTextColor(Color.BLUE)
        } else {
            holder.itemContentDynamic.setTextColor(Color.GRAY)
        }

        if (outsourcedProcessLowerPartItem.getChecked()) {
            holder.itemHeader.setTextColor(Color.rgb(0xf9, 0xa8, 0x25))
        } else {
            holder.itemHeader.setTextColor(Color.BLACK)
        }

        holder.itemEdit.inputType = (InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL)



        holder.itemBtnOk.setOnClickListener {
            Log.e(mTAG, "adapter click $position")

            val oldString: String = holder.itemContentDynamic.text.toString()
            val newString: String = holder.itemEdit.text.toString()

            if (!oldString.contentEquals(newString)) {
                //content changed
                outsourcedProcessLowerPartItem.setContentDynamic(holder.itemEdit.text.toString())
                outsourcedProcessLowerPartItem.getEditText()!!.setText(holder.itemEdit.text.toString())
                outsourcedProcessLowerPartItem.setChange(true)

                val modifyIntent = Intent()
                modifyIntent.action = Constants.ACTION.ACTION_OUTSOURCED_PROCESS_MODIFY_CHANGED
                modifyIntent.putExtra("INDEX", position)
                modifyIntent.putExtra("CONTENT", outsourcedProcessLowerPartItem.getContentDynamic())
                mContext!!.sendBroadcast(modifyIntent)
            } else {

                val noModifyIntent = Intent()
                noModifyIntent.action = Constants.ACTION.ACTION_OUTSOURCED_PROCESS_NO_CHANGED
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

        /*holder.itemEdit.setOnFocusChangeListener{ _, hasFocus ->
            if (!hasFocus) {
                Log.e(mTAG, "position $position focus = false")
            } else {
                Log.e(mTAG, "position $position focus = true")


                holder.itemEdit.setSelection(holder.itemEdit.text.length)

                val sendMoveIntent = Intent()
                sendMoveIntent.action = Constants.ACTION.ACTION_OUTSOURCED_PROCESS_MOVE_TO_POSITION
                sendMoveIntent.putExtra("INDEX", position)
                mContext!!.sendBroadcast(sendMoveIntent)
            }
        }*/

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
        var itemHeader: TextView = view.findViewById(R.id.outSourcedProcessLowerItemDetailHeader)
        var itemContentStatic: TextView = view.findViewById(R.id.outSourcedProcessLowerItemDetailContentStatic)
        var itemContentDynamic: TextView = view.findViewById(R.id.outSourcedProcessLowerItemDetailContentDynamic)
        var itemLayout: LinearLayout = view.findViewById(R.id.outSourcedProcessLowerItemDetailLayout)
        var itemEdit: EditText = view.findViewById(R.id.outSourcedProcessLowerItemDetailEdit)
        var itemBtnOk: Button = view.findViewById(R.id.outSourcedProcessLowerItemDetailBtnOk)
    }
}