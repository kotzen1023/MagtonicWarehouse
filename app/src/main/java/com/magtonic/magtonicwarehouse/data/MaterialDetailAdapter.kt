package com.magtonic.magtonicwarehouse.data

//import android.support.v4.view.PagerAdapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.ListView
import androidx.viewpager.widget.PagerAdapter
import com.magtonic.magtonicwarehouse.MainActivity
import com.magtonic.magtonicwarehouse.MainActivity.Companion.isKeyBoardShow
import com.magtonic.magtonicwarehouse.R
import com.magtonic.magtonicwarehouse.fragment.MaterialIssuingFragment.Companion.itemCanEdit
import com.magtonic.magtonicwarehouse.fragment.MaterialIssuingFragment.Companion.itemClick
import com.magtonic.magtonicwarehouse.model.receive.RJMaterial

class MaterialDetailAdapter(context: Context?, materialList: ArrayList<RJMaterial>, modifyList: ArrayList<Boolean>) : PagerAdapter() {
    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private val mTAG = MaterialDetailAdapter::class.java.name

    private var mContext: Context? = null
    private var materialList: ArrayList<RJMaterial> = ArrayList()
    //var materialDetailItemAdapter: MaterialDetailItemAdapter? = null
    var materialDetailItemAdapterList: ArrayList<MaterialDetailItemAdapter> = ArrayList()
    private var modifyList: ArrayList<Boolean> = ArrayList()

    var pagerListView: ListView? = null

    private var containerAll: ViewGroup? = null

    var currentItemIndexList: ArrayList<Int>? = ArrayList()

    init {
        Log.e(mTAG, "init")
        this.mContext = context
        this.materialList = materialList
        this.modifyList = modifyList

        for (i in 0 until materialList.size) {
            modifyList.add(false)

        }

    }


    override fun getCount(): Int {
        //Log.e(mTAG, "getCount = "+materialList.size)
        return materialList.size
    }

    override fun instantiateItem(container : ViewGroup, position: Int): Any
    {
        Log.e(mTAG, "instantiateItem: position = $position container size = "+container.childCount)



        containerAll = container


        val materialDetailList = ArrayList<MaterialDetailItem>()

        val view = LayoutInflater.from(mContext).inflate(R.layout.list_pager_item, container, false)

        pagerListView = view.findViewById<ListView>(R.id.pagerListView)
        val leftNav: ImageView = view.findViewById(R.id.left_nav)
        val rightNav: ImageView = view.findViewById(R.id.right_nav)

        if (position == 0) {
            if (materialList.size > 1) {
                rightNav.visibility = View.VISIBLE
            }
        } else if (position == materialList.size - 1) {
            leftNav.visibility = View.VISIBLE
        } else {
            rightNav.visibility = View.VISIBLE
            leftNav.visibility = View.VISIBLE
        }

        //linearLayout
        /*linearLayout!!.viewTreeObserver.addOnGlobalLayoutListener(
            object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    val r = Rect()
                    linearLayout!!.getWindowVisibleDisplayFrame(r)
                    val screenHeight = linearLayout!!.getRootView().getHeight()
                    val keypadHeight = screenHeight - r.bottom
                    isKeyBoardShow = (keypadHeight > screenHeight * 0.15)

                    if (isKeyBoardShow) {
                        if (materialDetailList.size > 0) {
                            materialDetailList[3].getEditText()!!.requestFocus()
                        }

                    }
                }
            }
        )*/


        //var materialDetailItemAdapter: MaterialDetailItemAdapter? = null

        //val item0 = MaterialDetailItem(mContext!!.getString(R.string.material_send_po), MainActivity.materialList[position].sfs01)
        //materialDetailList.add(item0)
        //val item0 = MaterialDetailItem(mContext!!.getString(R.string.material_send_num), MainActivity.materialList[position].sfs02)
        //materialDetailList.add(item0)
        val item11 = MaterialDetailItem(mContext!!.getString(R.string.material_wrok_order), MainActivity.materialList[position].sfs03)
        materialDetailList.add(item11)
        val item0 = MaterialDetailItem(mContext!!.getString(R.string.material_send_part_no), MainActivity.materialList[position].sfs04)
        materialDetailList.add(item0)
        val item1 = MaterialDetailItem(mContext!!.getString(R.string.material_send_name), MainActivity.materialList[position].ima02)
        materialDetailList.add(item1)
        val item2 = MaterialDetailItem(mContext!!.getString(R.string.material_send_quantity_of_actually_sent), MainActivity.materialList[position].sfs05)
        materialDetailList.add(item2)
        val item3 = MaterialDetailItem(mContext!!.getString(R.string.material_send_quantity_of_expected), MainActivity.materialList[position].sfs05)
        materialDetailList.add(item3)
        val item4 = MaterialDetailItem(mContext!!.getString(R.string.material_send_quantity_should_be_sent), MainActivity.materialList[position].sfa05)
        materialDetailList.add(item4)
        val item5 = MaterialDetailItem(mContext!!.getString(R.string.material_send_in_stock), MainActivity.materialList[position].img10)
        materialDetailList.add(item5)
        val item6 = MaterialDetailItem(mContext!!.getString(R.string.material_send_warehouse), MainActivity.materialList[position].sfs07)
        materialDetailList.add(item6)
        val item7 = MaterialDetailItem(mContext!!.getString(R.string.material_send_locate), MainActivity.materialList[position].sfs08)
        materialDetailList.add(item7)
        val item8 = MaterialDetailItem(mContext!!.getString(R.string.material_send_batch_no), MainActivity.materialList[position].sfs09)
        materialDetailList.add(item8)
        val item9 = MaterialDetailItem(mContext!!.getString(R.string.material_send_spec), MainActivity.materialList[position].ima021)
        materialDetailList.add(item9)

        /*var update_string = ""
        when (materialList[position].update) {
            1 -> {
                update_string = mContext!!.getString(R.string.material_update_failed)
            }
            2 -> {
                update_string = mContext!!.getString(R.string.material_update_success)
            }
            else -> { //0
                update_string = mContext!!.getString(R.string.material_update_yet)
            }
        }*/

        val item10 = MaterialDetailItem(mContext!!.getString(R.string.material_update_status), updateString(materialList[position].update))
        materialDetailList.add(item10)

        val materialDetailItemAdapter = MaterialDetailItemAdapter(mContext, R.layout.fragment_material_issuing_item, materialDetailList, modifyList[position])
        pagerListView!!.adapter = materialDetailItemAdapter

        //add index and adapter
        currentItemIndexList!!.add(position)
        materialDetailItemAdapterList.add(materialDetailItemAdapter)

        Log.e(mTAG, "=== currentItemIndexList start (instantiateItem) ===")
        for (i in 0 until currentItemIndexList!!.size) {
            Log.d(mTAG, "currentItemIndexList["+i+"] = "+currentItemIndexList!![i])
        }
        Log.e(mTAG, "=== currentItemIndexList end (instantiateItem) ===")

        Log.e(mTAG, "=== materialDetailItemAdapterList start (instantiateItem) ===")
        for (i in 0 until materialDetailItemAdapterList.size) {
            if (materialDetailItemAdapterList[i].count > 0) {
                Log.d(
                    mTAG,
                    "materialDetailItemAdapterList[" + i + "] = " + materialDetailItemAdapterList[i].getItem(2)!!.getContent()
                )
            }
        }
        Log.e(mTAG, "=== materialDetailItemAdapterList end (instantiateItem) ===")

        /*if (materialDetailItemAdapterList.size > 0) {
            Log.e(mTAG, "position = $position, materialDetailItemAdapterList size = "+materialDetailItemAdapterList.size)
            if (position == materialDetailItemAdapterList.size) {
                materialDetailItemAdapterList.add(materialDetailItemAdapter)
            } else {
                try {
                    materialDetailItemAdapterList.removeAt(position)
                } catch (e: IndexOutOfBoundsException) {
                    e.printStackTrace()
                }

                materialDetailItemAdapterList.add(position, materialDetailItemAdapter)
            }
        } else {
            Log.e(mTAG, "position = $position, materialDetailItemAdapterList size = "+materialDetailItemAdapterList.size)
            materialDetailItemAdapterList.add(materialDetailItemAdapter)
        }*/

        pagerListView!!.onItemClickListener = AdapterView.OnItemClickListener { _, _, item_position, _ ->
            Log.d(mTAG, "click $item_position")
            //currentClickItem = position

            if (item_position == itemCanEdit) { //sfs05
                Log.d(mTAG, "MaterialDetailAdapter->isKeyBoardShow = $isKeyBoardShow")
                if (!isKeyBoardShow) { // show keyboard

                    val hideIntent = Intent()
                    hideIntent.action = Constants.ACTION.ACTION_HIDE_KEYBOARD
                    mContext!!.sendBroadcast(hideIntent)
                } else {

                }

                materialDetailList[item_position].getTextView()!!.visibility = View.GONE
                materialDetailList[item_position].getLinearLayout()!!.visibility = View.VISIBLE

                //materialListInArrayList[position][item_position].getTextView()!!.visibility = View.GONE
                //materialListInArrayList[position][item_position].getLinearLayout()!!.visibility = View.VISIBLE
                itemClick = true
            }
        }


            //Glide.with(context).load(list[position]).into(view.imageView)
        container.addView(view)


        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, view: Any) {
        Log.e(mTAG, "destroyItem $position")

        //find index
        for (i in 0 until currentItemIndexList!!.size) {
            if (currentItemIndexList!![i] == position) {
                //found position index
                materialDetailItemAdapterList.removeAt(i)
            }
        }

        currentItemIndexList!!.removeAt(position)
        //currentItemIndexList.remove(position)

        Log.e(mTAG, "=== currentItemIndexList start (destroyItem) ===")
        for (i in 0 until currentItemIndexList!!.size) {
            Log.d(mTAG, "currentItemIndexList["+i+"] = "+currentItemIndexList!![i])
        }
        Log.e(mTAG, "=== currentItemIndexList end (destroyItem) ===")

        Log.e(mTAG, "=== materialDetailItemAdapterList start (destroyItem) ===")
        for (i in 0 until materialDetailItemAdapterList.size) {
            if (materialDetailItemAdapterList[i].count > 0) {
                Log.d(
                    mTAG,
                    "materialDetailItemAdapterList[" + i + "] = " + materialDetailItemAdapterList[i].getItem(2)!!.getContent()
                )
            }
        }
        Log.e(mTAG, "=== materialDetailItemAdapterList end (destroyItem) ===")

        container.removeView(view as View)

    }

    fun destroyAllItems() {

        if (containerAll != null) {
            if (containerAll!!.childCount > 0) {
                containerAll!!.removeAllViews()

                notifyDataSetChanged()
            }
        }

        currentItemIndexList!!.clear()
    }

    private fun updateString(state: Int): String {

        return when (state) {
            1 -> {
                mContext!!.getString(R.string.material_update_failed)
            }
            2 -> {
                mContext!!.getString(R.string.material_update_success)
            }
            else -> { //0
                mContext!!.getString(R.string.material_update_yet)
            }
        }
    }
}