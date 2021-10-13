package com.magtonic.magtonicwarehouse.data

import android.content.Context
import android.content.Intent

import android.graphics.Color
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*

import com.magtonic.magtonicwarehouse.R

import com.magtonic.magtonicwarehouse.ui.MaterialIssuingFragment.Companion.itemCanEdit
import com.magtonic.magtonicwarehouse.ui.MaterialIssuingFragment.Companion.itemClick
import java.lang.NumberFormatException

import java.util.ArrayList

class MaterialDetailItemAdapter (context: Context?, resource: Int, objects: ArrayList<MaterialDetailItem>, modify: Boolean) :
    ArrayAdapter<MaterialDetailItem>(context as Context, resource, objects) {

    private val mTAG = MaterialDetailItemAdapter::class.java.name
    private val layoutResourceId: Int = resource

    private var inflater : LayoutInflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    //val items: ArrayList<MaterialDetailItem> = objects
    var items: ArrayList<MaterialDetailItem> = ArrayList()
    private var mContext : Context? = null
    private var modify = false
    private var predictQuantity: Int = 0
    private var quantityInStorage: Int = 0

    init {
        Log.e(mTAG, "init")
        this.mContext = context
        this.modify = modify
        this.items = objects
    }

    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): MaterialDetailItem? {
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


        val materialDetailItem = items[position]
        //if (receiptDetailItem != null) {
        holder.itemHeader.text = materialDetailItem.getHeader()
        holder.itemContent.text = materialDetailItem.getContent()
        holder.itemEdit.setText(materialDetailItem.getContent())

        materialDetailItem.setTextView(holder.itemContent)
        materialDetailItem.setLinearLayout(holder.itemLayout)
        materialDetailItem.setEditText(holder.itemEdit)
        materialDetailItem.setBtnOk(holder.itemBtnOk)
        materialDetailItem.setIcon(holder.icon)

        holder.itemBtnOk.text = mContext!!.getString(R.string.confirm)

        if (modify) {
            if (materialDetailItem.getHeader().toString() == mContext!!.getString(R.string.material_send_quantity_of_actually_sent)) {
                holder.itemContent.setTextColor(Color.BLUE)
            }
        } else {
            if (materialDetailItem.getChange()) {
                holder.itemContent.setTextColor(Color.BLUE)
            } else {
                holder.itemContent.setTextColor(Color.GRAY)
            }
        }



        if (position == itemCanEdit) { //quantity sfs05
            holder.itemEdit.inputType = (InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL)
        }

        //save predict quantity
        if (position == 4) {
           /*try {
                predictQuantity = holder.itemContent.text.toString().toInt()
            } catch (ex: NumberFormatException) {
                ex.printStackTrace()
                predictQuantity = 0
            }*/
            predictQuantity = try {
                holder.itemContent.text.toString().toInt()
            } catch (ex: NumberFormatException) {
                0
            }
        }

        if (position == 6) {
            //quantityInStorage = holder.itemContent.text.toString().toInt()
            quantityInStorage = try {
                holder.itemContent.text.toString().toInt()
            } catch (ex: NumberFormatException) {
                0
            }
        }

        if (materialDetailItem.getContent().toString() == mContext!!.getString(R.string.material_shortage)) {
            holder.icon.visibility = View.VISIBLE
            holder.icon.setImageResource(R.drawable.distribution_03)
            holder.itemContent.visibility = View.GONE
            holder.itemLayout.visibility = View.GONE
        }


        holder.itemBtnOk.setOnClickListener {
            Log.e(mTAG, "adapter click $position")


            val oldString: String = holder.itemContent.text.toString()
            val newString: String = holder.itemEdit.text.toString()

            if (quantityInStorage == 0) {
                val emptyIntent = Intent()
                emptyIntent.action = Constants.ACTION.ACTION_MATERIAL_QUANTITY_IN_STOCK_EMPTY
                mContext!!.sendBroadcast(emptyIntent)

                materialDetailItem.getTextView()!!.visibility = View.VISIBLE
                materialDetailItem.getLinearLayout()!!.visibility = View.GONE

                itemClick = false
            } else {
                if (!oldString.contentEquals(newString)) {

                    if (newString.indexOf('.') > -1) {
                        val dotIntent = Intent()
                        dotIntent.action = Constants.ACTION.ACTION_MATERIAL_QUANTITY_MUST_BE_INTEGER
                        mContext!!.sendBroadcast(dotIntent)
                    } else {
                        if (newString.toInt() > quantityInStorage) {
                            val moreIntent = Intent()
                            moreIntent.action = Constants.ACTION.ACTION_MATERIAL_REAL_SEND_CAN_NOT_MUCH_MORE_THAN_STORAGE
                            mContext!!.sendBroadcast(moreIntent)
                        } else {
                            //content changed
                            materialDetailItem.setContent(holder.itemEdit.text.toString())
                            materialDetailItem.getEditText()!!.setText(holder.itemEdit.text.toString())
                            materialDetailItem.setChange(true)

                            val modifyIntent = Intent()
                            modifyIntent.action = Constants.ACTION.ACTION_MATERIAL_MODIFY_CHANGED
                            modifyIntent.putExtra("INDEX", position)
                            modifyIntent.putExtra("CONTENT", materialDetailItem.getContent())
                            mContext!!.sendBroadcast(modifyIntent)

                            materialDetailItem.getTextView()!!.visibility = View.VISIBLE
                            materialDetailItem.getLinearLayout()!!.visibility = View.GONE

                            itemClick = false
                        }
                    }




                } else {

                    val noModifyIntent = Intent()
                    noModifyIntent.action = Constants.ACTION.ACTION_MATERIAL_MODIFY_NO_CHANGED
                    noModifyIntent.putExtra("INDEX", position)
                    mContext!!.sendBroadcast(noModifyIntent)

                    materialDetailItem.getTextView()!!.visibility = View.VISIBLE
                    materialDetailItem.getLinearLayout()!!.visibility = View.GONE

                    itemClick = false
                }
            }






        }





        holder.itemEdit.setOnFocusChangeListener{ _, hasFocus ->
            if (!hasFocus) {
                Log.e(mTAG, "position $position focus = false")
            } else {
                Log.e(mTAG, "position $position focus = true")

                holder.itemEdit.setSelection(holder.itemEdit.text.length)
            }
        }

        holder.itemEdit.setOnEditorActionListener { _, actionId, _ ->
            when(actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    Log.e(mTAG, "IME_ACTION_DONE")

                    val oldString: String = holder.itemContent.text.toString()
                    val newString: String = holder.itemEdit.text.toString()

                    if (!oldString.contentEquals(newString)) {
                        //content changed
                        materialDetailItem.setContent(holder.itemEdit.text.toString())
                        materialDetailItem.getEditText()!!.setText(holder.itemEdit.text.toString())
                        materialDetailItem.setChange(true)

                        val modifyIntent = Intent()
                        modifyIntent.action = Constants.ACTION.ACTION_MATERIAL_MODIFY_CHANGED
                        modifyIntent.putExtra("INDEX", position)
                        modifyIntent.putExtra("CONTENT", materialDetailItem.getContent())
                        mContext!!.sendBroadcast(modifyIntent)
                    } else {

                        val noModifyIntent = Intent()
                        noModifyIntent.action = Constants.ACTION.ACTION_MATERIAL_MODIFY_NO_CHANGED
                        noModifyIntent.putExtra("INDEX", position)
                        mContext!!.sendBroadcast(noModifyIntent)
                    }

                    materialDetailItem.getTextView()!!.visibility = View.VISIBLE
                    materialDetailItem.getLinearLayout()!!.visibility = View.GONE

                    itemClick = false

                    true
                }

                EditorInfo.IME_ACTION_GO -> {
                    Log.e(mTAG, "IME_ACTION_GO")
                    true
                }

                EditorInfo.IME_ACTION_NEXT -> {
                    Log.e(mTAG, "IME_ACTION_NEXT")

                    true
                }

                EditorInfo.IME_ACTION_SEND -> {
                    Log.e(mTAG, "IME_ACTION_SEND")
                    true
                }

                else -> {
                    false
                }
            }
        }

        return view
    }

    class ViewHolder (view: View) {
        var itemHeader: TextView = view.findViewById(R.id.materialItemDetailHeader)
        var itemContent: TextView = view.findViewById(R.id.materialItemDetailContent)
        var itemLayout: LinearLayout = view.findViewById(R.id.materialItemDetailLayout)
        var itemEdit: EditText = view.findViewById(R.id.materialItemDetailEdit)
        var itemBtnOk: Button = view.findViewById(R.id.materialItemDetailBtnOk)
        var icon: ImageView = view.findViewById(R.id.materialShortageIcon)



    }
}