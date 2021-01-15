package com.magtonic.magtonicwarehouse.data

import android.content.Context

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import androidx.viewpager.widget.PagerAdapter
import com.magtonic.magtonicwarehouse.R


class PropertyDetailItemPagerAdapter(context: Context?, propertyList: ArrayList<PropertyDetailItem>) : PagerAdapter() {
    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`

    }

    private val mTAG = PropertyDetailItemPagerAdapter::class.java.name

    private var mContext: Context? = null
    private var propertyList: ArrayList<PropertyDetailItem> = ArrayList()
    //var materialDetailItemAdapter: MaterialDetailItemAdapter? = null
    var propertyMoreDetailItemAdapterList: ArrayList<PropertyMoreDetailItemAdapter> = ArrayList()
    //private var modifyList: ArrayList<Boolean> = ArrayList()

    private var pagerListView: ListView? = null

    private var containerAll: ViewGroup? = null

    private var currentItemIndexList: ArrayList<Int>? = ArrayList()

    init {
        Log.e(mTAG, "init")
        this.mContext = context
        this.propertyList = propertyList
        //this.modifyList = modifyList

        /*for (i in 0 until materialList.size) {
            modifyList.add(false)

        }*/

    }


    override fun getCount(): Int {
        //Log.e(mTAG, "getCount = "+materialList.size)
        return propertyList.size
    }

    override fun instantiateItem(container : ViewGroup, position: Int): Any
    {
        Log.e(mTAG, "instantiateItem: position = $position container size = "+container.childCount)



        containerAll = container


        val propertyMoreDetailList = ArrayList<PropertyMoreDetailItem>()

        val view = LayoutInflater.from(mContext).inflate(R.layout.list_pager_item, container, false)

        pagerListView = view.findViewById(R.id.pagerListView)
        val leftNav: LinearLayout = view.findViewById(R.id.left_nav)
        val rightNav: LinearLayout = view.findViewById(R.id.right_nav)

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

        val item0 = PropertyMoreDetailItem("序號", propertyList[position].faj01 as String)
        propertyMoreDetailList.add(item0)
        val item1 = PropertyMoreDetailItem("財產編號", propertyList[position].faj02 as String)
        propertyMoreDetailList.add(item1)
        val item2 = PropertyMoreDetailItem("附號", propertyList[position].faj022 as String)
        propertyMoreDetailList.add(item2)
        val item3 = PropertyMoreDetailItem("中文名稱-1", propertyList[position].faj06 as String)
        propertyMoreDetailList.add(item3)
        val item4 = PropertyMoreDetailItem("中文名稱-2", propertyList[position].faj061 as String)
        propertyMoreDetailList.add(item4)
        val item5 = PropertyMoreDetailItem("供應廠商", propertyList[position].faj10 as String)
        propertyMoreDetailList.add(item5)
        val item6 = PropertyMoreDetailItem("本地單價", propertyList[position].faj13 as String)
        propertyMoreDetailList.add(item6)
        val item7 = PropertyMoreDetailItem("保管人員", propertyList[position].faj19 as String)
        propertyMoreDetailList.add(item7)
        val item8 = PropertyMoreDetailItem("保管部門", propertyList[position].faj20 as String)
        propertyMoreDetailList.add(item8)
        val item9 = PropertyMoreDetailItem("廠商簡稱", propertyList[position].pmc03 as String)
        propertyMoreDetailList.add(item9)
        val item10 = PropertyMoreDetailItem("員工姓名", propertyList[position].gen02 as String)
        propertyMoreDetailList.add(item10)
        val item11 = PropertyMoreDetailItem("部門名稱", propertyList[position].gem02 as String)
        propertyMoreDetailList.add(item11)
        val statusString: String =
            when(propertyList[position].faj43) {
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

        val item12 = PropertyMoreDetailItem("資產狀態",statusString)
        propertyMoreDetailList.add(item12)

        val propertyMoreDetailItemAdapter = PropertyMoreDetailItemAdapter(mContext, R.layout.fragment_property_more_detail_item, propertyMoreDetailList)
        pagerListView!!.adapter = propertyMoreDetailItemAdapter

        //add index and adapter
        currentItemIndexList!!.add(position)
        propertyMoreDetailItemAdapterList.add(propertyMoreDetailItemAdapter)

        Log.e(mTAG, "=== currentItemIndexList start (instantiateItem) ===")
        for (i in 0 until currentItemIndexList!!.size) {
            Log.d(mTAG, "currentItemIndexList["+i+"] = "+currentItemIndexList!![i])
        }
        Log.e(mTAG, "=== currentItemIndexList end (instantiateItem) ===")

        Log.e(mTAG, "=== propertyMoreDetailItemAdapterList start (instantiateItem) ===")
        for (i in 0 until propertyMoreDetailItemAdapterList.size) {
            if (propertyMoreDetailItemAdapterList[i].count > 0) {
                Log.d(
                    mTAG,
                    "propertyMoreDetailItemAdapterList[" + i + "] = " + propertyMoreDetailItemAdapterList[i].getItem(2)!!.getContent()
                )
            }
        }
        Log.e(mTAG, "=== propertyMoreDetailItemAdapterList end (instantiateItem) ===")

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

            /*if (item_position == MaterialIssuingFragment.itemCanEdit) { //sfs05
                Log.d(mTAG, "MaterialDetailAdapter->isKeyBoardShow = ${MainActivity.isKeyBoardShow}")
                if (!MainActivity.isKeyBoardShow) { // show keyboard

                    val hideIntent = Intent()
                    hideIntent.action = Constants.ACTION.ACTION_HIDE_KEYBOARD
                    mContext!!.sendBroadcast(hideIntent)
                } else {

                }

                materialDetailList[item_position].getTextView()!!.visibility = View.GONE
                materialDetailList[item_position].getLinearLayout()!!.visibility = View.VISIBLE

                //materialListInArrayList[position][item_position].getTextView()!!.visibility = View.GONE
                //materialListInArrayList[position][item_position].getLinearLayout()!!.visibility = View.VISIBLE
                MaterialIssuingFragment.itemClick = true
            }*/
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
                propertyMoreDetailItemAdapterList.removeAt(i)
            }
        }

        if (currentItemIndexList!!.size > 0)
            currentItemIndexList!!.removeAt(position)


        Log.e(mTAG, "=== currentItemIndexList start (destroyItem) ===")
        for (i in 0 until currentItemIndexList!!.size) {
            Log.d(mTAG, "currentItemIndexList["+i+"] = "+currentItemIndexList!![i])
        }
        Log.e(mTAG, "=== currentItemIndexList end (destroyItem) ===")

        Log.e(mTAG, "=== propertyMoreDetailItemAdapterList start (destroyItem) ===")
        for (i in 0 until propertyMoreDetailItemAdapterList.size) {
            if (propertyMoreDetailItemAdapterList[i].count > 0) {
                Log.d(
                    mTAG,
                    "propertyMoreDetailItemAdapterList[" + i + "] = " + propertyMoreDetailItemAdapterList[i].getItem(2)!!.getContent()
                )
            }
        }
        Log.e(mTAG, "=== propertyMoreDetailItemAdapterList end (destroyItem) ===")

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

    /*private fun updateString(state: Int): String {

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
    }*/
}