package com.magtonic.magtonicwarehouse.data

import android.content.Context


//import android.support.v4.view.PagerAdapter

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
import com.magtonic.magtonicwarehouse.fragment.PropertyFragment.Companion.itemCanChange
import com.magtonic.magtonicwarehouse.model.receive.RJProperty

class PropertyDetailAdapter (context: Context?, propertyList: ArrayList<RJProperty>) : PagerAdapter() {
    private val mTAG = PropertyDetailAdapter::class.java.name

    private var mContext: Context? = null
    private var propertyList: ArrayList<RJProperty> = ArrayList()
    //var materialDetailItemAdapter: MaterialDetailItemAdapter? = null
    var propertyDetailItemAdapterList: ArrayList<PropertyDetailItemAdapter> = ArrayList()
    //private var modifyList: ArrayList<Boolean> = ArrayList()

    var pagerListView: ListView? = null

    private var containerAll: ViewGroup? = null

    var currentItemIndexList: ArrayList<Int>? = ArrayList()



    init {
        Log.e(mTAG, "init")
        this.mContext = context
        this.propertyList = propertyList
    }


    override fun getCount(): Int {
        //Log.e(mTAG, "getCount = "+materialList.size)
        return propertyList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container : ViewGroup, position: Int): Any
    {
        Log.e(mTAG, "instantiateItem: position = $position container size = "+container.childCount)



        containerAll = container


        val propertyDetailList = ArrayList<PropertyDetailItem>()

        val view = LayoutInflater.from(mContext).inflate(R.layout.list_pager_item, container, false)

        pagerListView = view.findViewById<ListView>(R.id.pagerListView)
        val leftNav: ImageView = view.findViewById(R.id.left_nav)
        val rightNav: ImageView = view.findViewById(R.id.right_nav)

        if (position == 0) {
            if (propertyList.size > 1) {
                rightNav.visibility = View.VISIBLE
            }
        } else if (position == propertyList.size - 1) {
            leftNav.visibility = View.VISIBLE
        } else {
            rightNav.visibility = View.VISIBLE
            leftNav.visibility = View.VISIBLE
        }


        val item0 = PropertyDetailItem(mContext!!.getString(R.string.property_serial), MainActivity.propertyList[position].faj01)
        propertyDetailList.add(item0)
        val item1 = PropertyDetailItem(mContext!!.getString(R.string.property_no), MainActivity.propertyList[position].faj02)
        propertyDetailList.add(item1)
        val item2 = PropertyDetailItem(mContext!!.getString(R.string.property_add_no), MainActivity.propertyList[position].faj022)
        propertyDetailList.add(item2)
        val item3 = PropertyDetailItem(mContext!!.getString(R.string.property_name_1), MainActivity.propertyList[position].faj06)
        propertyDetailList.add(item3)
        val item4 = PropertyDetailItem(mContext!!.getString(R.string.property_name_2), MainActivity.propertyList[position].faj061)
        propertyDetailList.add(item4)
        val item5 = PropertyDetailItem(mContext!!.getString(R.string.property_supplier), MainActivity.propertyList[position].faj10)
        propertyDetailList.add(item5)
        val item6 = PropertyDetailItem(mContext!!.getString(R.string.property_price), MainActivity.propertyList[position].faj13)
        propertyDetailList.add(item6)
        val item7 = PropertyDetailItem(mContext!!.getString(R.string.property_keeper), MainActivity.propertyList[position].faj19)
        propertyDetailList.add(item7)
        val item8 = PropertyDetailItem(mContext!!.getString(R.string.property_keep_dept), MainActivity.propertyList[position].faj20)
        propertyDetailList.add(item8)
        val item9 = PropertyDetailItem(mContext!!.getString(R.string.property_supplier_abbreviation), MainActivity.propertyList[position].pmc03)
        propertyDetailList.add(item9)
        val item10 = PropertyDetailItem(mContext!!.getString(R.string.property_emp_name), MainActivity.propertyList[position].gen02)
        propertyDetailList.add(item10)
        val item11 = PropertyDetailItem(mContext!!.getString(R.string.property_dept), MainActivity.propertyList[position].gem02)
        propertyDetailList.add(item11)


        val statusString: String =
         when(MainActivity.propertyList[position].faj43) {
             "0" -> "取得"
             "1" -> "資本化"
             "2" -> "折舊中"
             "3" -> "外送"
             "4" -> "折畢"
             "5" -> "出售"
             "6" -> "銷帳"
             "7" -> "折畢再提"
             "8" -> "改良"
             "9" -> "重估"
             else -> "取得"
        }
        val item12 = PropertyDetailItem(mContext!!.getString(R.string.property_status), statusString)
        propertyDetailList.add(item12)

        val propertyDetailItemAdapter = PropertyDetailItemAdapter(mContext, R.layout.fragment_property_item, propertyDetailList)
        pagerListView!!.adapter = propertyDetailItemAdapter

        //add index and adapter
        currentItemIndexList!!.add(position)
        propertyDetailItemAdapterList.add(propertyDetailItemAdapter)

        Log.e(mTAG, "=== currentItemIndexList start (instantiateItem) ===")
        for (i in 0 until currentItemIndexList!!.size) {
            Log.d(mTAG, "currentItemIndexList["+i+"] = "+currentItemIndexList!![i])
        }
        Log.e(mTAG, "=== currentItemIndexList end (instantiateItem) ===")

        Log.e(mTAG, "=== materialDetailItemAdapterList start (instantiateItem) ===")
        for (i in 0 until propertyDetailItemAdapterList.size) {
            if (propertyDetailItemAdapterList[i].count > 0) {
                Log.d(
                    mTAG,
                    "propertyDetailItemAdapterList[" + i + "] = " + propertyDetailItemAdapterList[i].getItem(2)!!.getContent()
                )
            }
        }
        Log.e(mTAG, "=== propertyDetailItemAdapterList end (instantiateItem) ===")

        pagerListView!!.onItemClickListener = AdapterView.OnItemClickListener { _, _, item_position, _ ->
            Log.d(mTAG, "click $item_position")
            //currentClickItem = position

            if (item_position == itemCanChange) { //sfs05
                Log.d(mTAG, "MaterialDetailAdapter->isKeyBoardShow = $isKeyBoardShow")
                /*if (!isKeyBoardShow) { // show keyboard

                    val hideIntent = Intent()
                    hideIntent.action = Constants.ACTION.ACTION_HIDE_KEYBOARD
                    mContext!!.sendBroadcast(hideIntent)
                } else {

                }*/

                propertyDetailList[item_position].getTextView()!!.visibility = View.GONE
                propertyDetailList[item_position].getLinearLayout()!!.visibility = View.VISIBLE
                propertyDetailList[item_position].getSpinner()!!.visibility = View.VISIBLE
                //materialListInArrayList[position][item_position].getTextView()!!.visibility = View.GONE
                //materialListInArrayList[position][item_position].getLinearLayout()!!.visibility = View.VISIBLE
                //itemClick = true
            }
        }

        container.addView(view)


        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, view: Any) {
        Log.e(mTAG, "destroyItem $position")

        //find index
        for (i in 0 until currentItemIndexList!!.size) {
            if (currentItemIndexList!![i] == position) {
                //found position index
                propertyDetailItemAdapterList.removeAt(i)
            }
        }

        currentItemIndexList!!.removeAt(position)

        Log.e(mTAG, "=== currentItemIndexList start (destroyItem) ===")
        for (i in 0 until currentItemIndexList!!.size) {
            Log.d(mTAG, "currentItemIndexList["+i+"] = "+currentItemIndexList!![i])
        }
        Log.e(mTAG, "=== currentItemIndexList end (destroyItem) ===")

        Log.e(mTAG, "=== propertyDetailItemAdapterList start (destroyItem) ===")
        for (i in 0 until propertyDetailItemAdapterList.size) {
            if (propertyDetailItemAdapterList[i].count > 0) {
                Log.d(
                    mTAG,
                    "propertyDetailItemAdapterList[" + i + "] = " + propertyDetailItemAdapterList[i].getItem(2)!!.getContent()
                )
            }
        }
        Log.e(mTAG, "=== propertyDetailItemAdapterList end (destroyItem) ===")

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