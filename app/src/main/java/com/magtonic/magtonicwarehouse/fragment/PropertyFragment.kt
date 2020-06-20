package com.magtonic.magtonicwarehouse.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
//import android.support.v4.app.Fragment
import androidx.fragment.app.Fragment
//import android.support.v4.view.ViewPager
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager

import com.magtonic.magtonicwarehouse.MainActivity
import com.magtonic.magtonicwarehouse.MainActivity.Companion.isKeyBoardShow
import com.magtonic.magtonicwarehouse.MainActivity.Companion.isPropertyInDetail
import com.magtonic.magtonicwarehouse.MainActivity.Companion.propertyList
import com.magtonic.magtonicwarehouse.R

import com.magtonic.magtonicwarehouse.data.*
import java.util.*


class PropertyFragment : Fragment(), ViewPager.OnPageChangeListener {
    private val mTAG = PropertyFragment::class.java.name
    private var propertyContext: Context? = null

    private var progressBar: ProgressBar? = null
    private var relativeLayout: RelativeLayout? = null
    //private var propertyDetailItemAdapter: PropertyDetailItemAdapter? = null



    private var barcodeInput: EditText? = null
    private var linearLayout: LinearLayout? = null

    //var propertyDetailList = ArrayList<PropertyDetailItem>()
    //private var listView: ListView ?= null

    private var mReceiver: BroadcastReceiver? = null
    private var isRegister = false

    private var propertyDetailItemAdapter: PropertyDetailItemAdapter? = null
    private var propertyMoreDetailAdapter: PropertyMoreDetailItemAdapter? = null
    //private var listViewProperty: ListView? = null
    //private var listViewPropertyDetail: ListView? = null
    var getPropertyList = ArrayList<PropertyDetailItem>()
    var getPropertyDetailList = ArrayList<PropertyMoreDetailItem>()
    var viewPager: ViewPager? = null
    var pagerAdapter : PropertyDetailItemPagerAdapter? = null

    companion object {
        @JvmStatic var currentPropertyPage: Int = 0
        @JvmStatic val itemCanChange: Int = 12
    }
    //private var layoutBottom: LinearLayout? = null
    //private var btnClear: Button? = null

    private val colorCodePink = Color.parseColor("#D81B60")

    private var toastHandle: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(mTAG, "onCreate")

        propertyContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(mTAG, "onCreateView")

        val view = inflater.inflate(R.layout.fragment_property, container, false)

        relativeLayout = view.findViewById(R.id.property_list_container)
        progressBar = ProgressBar(propertyContext, null, android.R.attr.progressBarStyleLarge)
        val params = RelativeLayout.LayoutParams(MainActivity.screenHeight / 4, MainActivity.screenWidth / 4)
        params.addRule(RelativeLayout.CENTER_IN_PARENT)

        val localRelativeLayout: RelativeLayout? = relativeLayout
        if (localRelativeLayout != null) {
            localRelativeLayout.addView(progressBar, params)
        } else {
            Log.e(mTAG, "localRelativeLayout = null")
        }
        progressBar!!.visibility = View.GONE

        //layoutBottom = view.findViewById(R.id.layoutBottomProperty)
        //layoutBottom!!.visibility = View.GONE

        viewPager = view!!.findViewById(R.id.viewPagerProperty)
        //listViewProperty = view!!.findViewById(R.id.listViewProperty)
        //listViewPropertyDetail = view!!.findViewById(R.id.listViewPropertyDetail)
        if (propertyContext != null) {

            getPropertyList.clear()
            //propertyList.clear()

            /*val item0 = RJProperty()
            item0.faj01 = "item0_faj01"
            item0.faj02 = "item0_faj02"
            item0.faj022 = "item0_faj022"
            item0.faj06 = "item0_faj06"
            item0.faj061 = "item0_faj061"
            item0.faj10 = "item0_faj10"
            item0.faj13 = "item0_faj13"
            item0.faj19 = "item0_faj19"
            item0.faj20 = "item0_faj20"
            item0.pmc03 = "item0_pmc03"
            item0.gen02 = "item0_gen02"
            item0.gem02 = "item0_gem02"
            item0.faj43 = "item0_faj43"
            propertyList.add(item0)
            val item1 = RJProperty()
            item1.faj01 = "item1_faj01"
            item1.faj02 = "item1_faj02"
            item1.faj022 = "item1_faj022"
            item1.faj06 = "item1_faj06"
            item1.faj061 = "item1_faj061"
            item1.faj10 = "item1_faj10"
            item1.faj13 = "item1_faj13"
            item1.faj19 = "item1_faj19"
            item1.faj20 = "item1_faj20"
            item1.pmc03 = "item1_pmc03"
            item1.gen02 = "item1_gen02"
            item1.gem02 = "item1_gem02"
            item1.faj43 = "item1_faj43"
            propertyList.add(item1)
            val item2 = RJProperty()
            item2.faj01 = "item2_faj01"
            item2.faj02 = "item2_faj02"
            item2.faj022 = "item2_faj022"
            item2.faj06 = "item2_faj06"
            item2.faj061 = "item2_faj061"
            item2.faj10 = "item2_faj10"
            item2.faj13 = "item2_faj13"
            item2.faj19 = "item2_faj19"
            item2.faj20 = "item2_faj20"
            item2.pmc03 = "item2_pmc03"
            item2.gen02 = "item2_gen02"
            item2.gem02 = "item2_gem02"
            item2.faj43 = "item2_faj43"
            propertyList.add(item2)
            */
            //layoutBottom!!.visibility = View.VISIBLE

            //val pageString = "第 1/"+ propertyList.size +" 頁"
            //toastPage(pageString)

            pagerAdapter = PropertyDetailItemPagerAdapter(propertyContext, getPropertyList)
            viewPager!!.adapter = pagerAdapter as PagerAdapter

            /*val showSeekBarIntent = Intent()
            showSeekBarIntent.action = Constants.ACTION.ACTION_SEEK_BAR_SHOW_ACTION
            propertyContext!!.sendBroadcast(showSeekBarIntent)

            //detail
            propertyDetailItemAdapter = PropertyDetailItemAdapter(propertyContext, R.layout.fragment_property_item, getPropertyList)
            listViewProperty!!.adapter = propertyDetailItemAdapter

            //more detail
            propertyMoreDetailAdapter = PropertyMoreDetailItemAdapter(propertyContext, R.layout.fragment_property_more_detail_item, getPropertyDetailList)
            listViewPropertyDetail!!.adapter = propertyMoreDetailAdapter*/
        }

        viewPager!!.addOnPageChangeListener(this)

        /*listViewProperty!!.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            Log.d(mTAG, "click $position")

            val moreDetailIntent = Intent()
            moreDetailIntent.action = Constants.ACTION.ACTION_PROPERTY_MORE_DETAIL_REFRESH
            moreDetailIntent.putExtra("INDEX", position.toString())
            propertyContext?.sendBroadcast(moreDetailIntent)

        }*/

        linearLayout = view.findViewById(R.id.linearLayoutProperty)

        linearLayout!!.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            linearLayout!!.getWindowVisibleDisplayFrame(r)
            val screenHeight = linearLayout!!.rootView.height
            val keypadHeight = screenHeight - r.bottom
            isKeyBoardShow = (keypadHeight > screenHeight * 0.15)

            if (isKeyBoardShow) {
                Log.e(mTAG, "->isKeyBoardShow true")
            } else {
                Log.e(mTAG, "->isKeyBoardShow false")
            }
        }

        barcodeInput = view.findViewById(R.id.editTextProperty)
        barcodeInput!!.setOnEditorActionListener { _, actionId, _ ->

            when(actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    Log.e(mTAG, "IME_ACTION_DONE")

                    //val hideSeekBarIntent = Intent()
                    //hideSeekBarIntent.action = Constants.ACTION.ACTION_MATERIAL_SEEK_BAR_HIDE_ACTION
                    //materialIssuingContext!!.sendBroadcast(hideSeekBarIntent)

                    barcodeInput!!.setText(barcodeInput!!.text.toString().toUpperCase(Locale.getDefault()))

                    progressBar!!.indeterminateTintList = ColorStateList.valueOf(colorCodePink)
                    progressBar!!.visibility = View.VISIBLE

                    //clear
                    if (pagerAdapter != null) {
                        //pagerAdapter!!.right_nav!!.visibility = View.GONE
                        //pagerAdapter!!.left_nav!!.visibility = View.GONE

                        for (i in 0 until pagerAdapter!!.propertyMoreDetailItemAdapterList.size) {
                            pagerAdapter!!.propertyMoreDetailItemAdapterList[i].clear()
                            pagerAdapter!!.propertyMoreDetailItemAdapterList[i].notifyDataSetChanged()
                        }
                        pagerAdapter!!.destroyAllItems()

                        getPropertyList.clear()

                        propertyList.clear()
                        pagerAdapter!!.notifyDataSetChanged()
                    }
                    pagerAdapter = null

                    currentPropertyPage = 0


                    //layoutBottom!!.visibility = View.GONE

                    val searchIntent = Intent()
                    searchIntent.action = Constants.ACTION.ACTION_USER_INPUT_SEARCH
                    searchIntent.putExtra("INPUT_NO", barcodeInput!!.text.toString())
                    propertyContext?.sendBroadcast(searchIntent)

                    true
                }

                EditorInfo.IME_ACTION_GO -> {
                    Log.e(mTAG, "IME_ACTION_GO")
                    true
                }

                EditorInfo.IME_ACTION_NEXT -> {
                    Log.e(mTAG, "IME_ACTION_NEXT")

                    //val hideSeekBarIntent = Intent()
                    //hideSeekBarIntent.action = Constants.ACTION.ACTION_MATERIAL_SEEK_BAR_HIDE_ACTION
                    //materialIssuingContext!!.sendBroadcast(hideSeekBarIntent)

                    barcodeInput!!.setText(barcodeInput!!.text.toString().toUpperCase(Locale.getDefault()))

                    progressBar!!.indeterminateTintList = ColorStateList.valueOf(colorCodePink)
                    progressBar!!.visibility = View.VISIBLE

                    //clear
                    if (pagerAdapter != null) {
                        //pagerAdapter!!.right_nav!!.visibility = View.GONE
                        //pagerAdapter!!.left_nav!!.visibility = View.GONE

                        for (i in 0 until pagerAdapter!!.propertyMoreDetailItemAdapterList.size) {
                            pagerAdapter!!.propertyMoreDetailItemAdapterList[i].clear()
                            pagerAdapter!!.propertyMoreDetailItemAdapterList[i].notifyDataSetChanged()
                        }
                        pagerAdapter!!.destroyAllItems()

                        getPropertyList.clear()

                        propertyList.clear()
                        pagerAdapter!!.notifyDataSetChanged()
                    }
                    pagerAdapter = null

                    currentPropertyPage = 0


                    //layoutBottom!!.visibility = View.GONE

                    val searchIntent = Intent()
                    searchIntent.action = Constants.ACTION.ACTION_USER_INPUT_SEARCH
                    searchIntent.putExtra("INPUT_NO", barcodeInput!!.text.toString())
                    propertyContext?.sendBroadcast(searchIntent)

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

        /*barcodeInput!!.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                Log.e(mTAG, "barcode input = false")
            } else {
                Log.e(mTAG, "barcode input = true")
            }
        }*/

        /*btnClear = view.findViewById(R.id.btnPropertyClear)
        btnClear!!.setOnClickListener {
            barcodeInput!!.setText("")

            Log.e(mTAG, "btnClear")

            //clear
            if (pagerAdapter != null) {
                //pagerAdapter!!.right_nav!!.visibility = View.GONE
                //pagerAdapter!!.left_nav!!.visibility = View.GONE

                for (i in 0 until pagerAdapter!!.propertyMoreDetailItemAdapterList.size) {
                    pagerAdapter!!.propertyMoreDetailItemAdapterList[i].clear()
                    pagerAdapter!!.propertyMoreDetailItemAdapterList[i].notifyDataSetChanged()
                }
                pagerAdapter!!.destroyAllItems()

                getPropertyList.clear()

                propertyList.clear()
                pagerAdapter!!.notifyDataSetChanged()
            }
            pagerAdapter = null

            currentPropertyPage = 0

            layoutBottom!!.visibility = View.GONE

            //val hideSeekBarIntent = Intent()
            //hideSeekBarIntent.action = Constants.ACTION.ACTION_MATERIAL_SEEK_BAR_HIDE_ACTION
            //materialIssuingContext!!.sendBroadcast(hideSeekBarIntent)

            //MaterialIssuingFragment.itemClick = false
        }*/

        val filter: IntentFilter

        mReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action != null) {
                    if (intent.action!!.equals(Constants.ACTION.ACTION_BARCODE_NULL, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_BARCODE_NULL")

                        toast(getString(R.string.invalid_barcode))

                        progressBar!!.visibility = View.GONE


                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_NETWORK_FAILED, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_NETWORK_FAILED")

                        progressBar!!.visibility = View.GONE

                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_CONNECTION_TIMEOUT, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_CONNECTION_TIMEOUT")

                        toast(getString(R.string.upload_receipt_connection_timeout))

                        progressBar!!.visibility = View.GONE


                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_PROPERTY_SCAN_BARCODE, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_PROPERTY_SCAN_BARCODE")


                        val barcode = intent.getStringExtra("BARCODE")

                        barcodeInput!!.setText(barcode)

                        if (pagerAdapter != null) {
                            //pagerAdapter!!.right_nav!!.visibility = View.GONE
                            //pagerAdapter!!.left_nav!!.visibility = View.GONE

                            for (i in 0 until pagerAdapter!!.propertyMoreDetailItemAdapterList.size) {
                                //pagerAdapter!!.propertyMoreDetailItemAdapterList[i].items.clear()
                                pagerAdapter!!.propertyMoreDetailItemAdapterList[i].clear()
                                pagerAdapter!!.propertyMoreDetailItemAdapterList[i].notifyDataSetChanged()
                            }
                            pagerAdapter!!.destroyAllItems()

                            getPropertyList.clear()

                            propertyList.clear()
                            pagerAdapter!!.notifyDataSetChanged()
                        }
                        pagerAdapter = null

                        currentPropertyPage = 0

                        /*if (propertyDetailItemAdapter != null) {
                            propertyDetailItemAdapter?.notifyDataSetChanged()
                        }

                        getPropertyDetailList.clear()
                        if (propertyMoreDetailAdapter != null) {
                            propertyMoreDetailAdapter?.notifyDataSetChanged()
                        }

                        listViewProperty!!.visibility = View.VISIBLE
                        listViewPropertyDetail!!.visibility = View.GONE*/

                        //currentPropertyPage = 0


                        //layoutBottom!!.visibility = View.GONE

                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_PROPERTY_NO_NOT_EXIST, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_PROPERTY_NO_NOT_EXIST")

                        progressBar!!.visibility = View.GONE



                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_PROPERTY_FRAGMENT_REFRESH, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_PROPERTY_FRAGMENT_REFRESH")

                        progressBar!!.visibility = View.GONE

                        val pageString = "第 1/"+ propertyList.size +" 頁"
                        toastPage(pageString)

                        //for (i in 0 until MainActivity.propertyList.size) {
                        //    modifyList.add(false)
                        //}
                        for (property in propertyList) {
                            val item = PropertyDetailItem(property.faj01, property.faj02, property.faj022, property.faj06, property.faj061, property.faj10, property.faj13,
                                property.faj19, property.faj20, property.pmc03, property.gen02, property.gem02, property.faj43)
                            getPropertyList.add(item)
                        }

                        /*if (propertyDetailItemAdapter != null) {
                            propertyDetailItemAdapter?.notifyDataSetChanged()
                        }*/

                        if (isKeyBoardShow) {
                            val hideIntent = Intent()
                            hideIntent.action = Constants.ACTION.ACTION_HIDE_KEYBOARD
                            propertyContext!!.sendBroadcast(hideIntent)
                        }

                        pagerAdapter = PropertyDetailItemPagerAdapter(propertyContext, getPropertyList)
                        viewPager!!.adapter = pagerAdapter
                        if (pagerAdapter != null) {
                            Log.e(mTAG, "pagerAdapter != null")
                            pagerAdapter!!.notifyDataSetChanged()
                        } else {
                            Log.e(mTAG, "pagerAdapter == null")
                            pagerAdapter = PropertyDetailItemPagerAdapter(propertyContext, getPropertyList)
                            viewPager!!.adapter = pagerAdapter
                            //viewPager!!.currentItem = 3

                            //currentPropertyPage = 3
                        }

                        //layoutBottom!!.visibility = View.VISIBLE

                        //val showSeekBarIntent = Intent()
                        //showSeekBarIntent.action = Constants.ACTION.ACTION_MATERIAL_SEEK_BAR_SHOW_ACTION
                        //materialIssuingContext!!.sendBroadcast(showSeekBarIntent)

                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_PROPERTY_MORE_DETAIL_REFRESH, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_PROPERTY_MORE_DETAIL_REFRESH")

                        progressBar!!.visibility = View.GONE

                        val idxString = intent.getStringExtra("INDEX")

                        val idx = idxString?.toInt()

                        if (isKeyBoardShow) {
                            val hideIntent = Intent()
                            hideIntent.action = Constants.ACTION.ACTION_HIDE_KEYBOARD
                            propertyContext!!.sendBroadcast(hideIntent)
                        }

                        getPropertyDetailList.clear()

                        if (idx != null) {
                            val item0 = PropertyMoreDetailItem("序號", getPropertyList[idx].faj01 as String)
                            getPropertyDetailList.add(item0)
                            val item1 = PropertyMoreDetailItem("財產編號", getPropertyList[idx].faj02 as String)
                            getPropertyDetailList.add(item1)
                            val item2 = PropertyMoreDetailItem("附號", getPropertyList[idx].faj022 as String)
                            getPropertyDetailList.add(item2)
                            val item3 = PropertyMoreDetailItem("中文名稱-1", getPropertyList[idx].faj06 as String)
                            getPropertyDetailList.add(item3)
                            val item4 = PropertyMoreDetailItem("中文名稱-2", getPropertyList[idx].faj061 as String)
                            getPropertyDetailList.add(item4)
                            val item5 = PropertyMoreDetailItem("供應廠商", getPropertyList[idx].faj10 as String)
                            getPropertyDetailList.add(item5)
                            val item6 = PropertyMoreDetailItem("本地單價", getPropertyList[idx].faj13 as String)
                            getPropertyDetailList.add(item6)
                            val item7 = PropertyMoreDetailItem("保管人員", getPropertyList[idx].faj19 as String)
                            getPropertyDetailList.add(item7)
                            val item8 = PropertyMoreDetailItem("保管部門", getPropertyList[idx].faj20 as String)
                            getPropertyDetailList.add(item8)
                            val item9 = PropertyMoreDetailItem("廠商簡稱", getPropertyList[idx].pmc03 as String)
                            getPropertyDetailList.add(item9)
                            val item10 = PropertyMoreDetailItem("員工姓名", getPropertyList[idx].gen02 as String)
                            getPropertyDetailList.add(item10)
                            val item11 = PropertyMoreDetailItem("部門名稱", getPropertyList[idx].gem02 as String)
                            getPropertyDetailList.add(item11)

                            val statusString: String =
                                when(getPropertyList[idx].faj43) {
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
                            getPropertyDetailList.add(item12)
                        }


                        //listViewProperty!!.visibility = View.GONE
                        //listViewPropertyDetail!!.visibility = View.VISIBLE

                        isPropertyInDetail = 1

                        if (propertyMoreDetailAdapter != null) {
                            propertyMoreDetailAdapter?.notifyDataSetChanged()
                        }

                        val showIntent = Intent()
                        showIntent.action = Constants.ACTION.ACTION_OUTSOURCED_PROCESS_SHOW_FAB_BACK
                        propertyContext!!.sendBroadcast(showIntent)

                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_SEEK_BAR_SELECT_PAGE_ACTION, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_SEEK_BAR_SELECT_PAGE_ACTION")

                        val page = intent.getIntExtra("PAGE", 0)

                        Log.e(mTAG, "page = $page")

                        /*if (viewPager != null) {
                            viewPager!!.currentItem = page
                            currentPropertyPage = page
                        }*/
                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_PROPERTY_MODIFY_NO_CHANGED, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_PROPERTY_MODIFY_NO_CHANGED")

                        //val idx = intent.getIntExtra("INDEX", 0)

                        /*materialDetailList[idx].getTextView()!!.visibility = View.VISIBLE
                        materialDetailList[idx].getLinearLayout()!!.visibility = View.GONE
                        listView!!.invalidateViews()*/

                        //pagerAdapter!!.materialDetailItemAdapter!!.getItem(3)!!.getTextView()!!.visibility = View.VISIBLE
                        //pagerAdapter!!.materialDetailItemAdapter!!.getItem(3)!!.getLinearLayout()!!.visibility = View.GONE
                        /*pagerAdapter!!.pagerListView!!.invalidateViews()

                        if (isKeyBoardShow) {
                            val hideIntent = Intent()
                            hideIntent.action = Constants.ACTION.ACTION_HIDE_KEYBOARD
                            propertyContext!!.sendBroadcast(hideIntent)
                        }*/

                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_PROPERTY_MODIFY_CHANGED, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_PROPERTY_MODIFY_CHANGED")

                        val idx = intent.getIntExtra("INDEX", 0)
                        //val content = intent.getStringExtra("CONTENT")

                        Log.e(mTAG, "idx = $idx")

                        //if (content != null) {



                        //}
                        propertyList[currentPropertyPage].faj43 = idx.toString() //change actually material send

                        //pagerAdapter!!.materialDetailItemAdapter!!.getItem(3)!!.getTextView()!!.visibility = View.VISIBLE
                        //pagerAdapter!!.materialDetailItemAdapter!!.getItem(3)!!.getLinearLayout()!!.visibility = View.GONE
                        /*pagerAdapter!!.pagerListView!!.invalidateViews()

                        if (isKeyBoardShow) {
                            val hideIntent = Intent()
                            hideIntent.action = Constants.ACTION.ACTION_HIDE_KEYBOARD
                            propertyContext!!.sendBroadcast(hideIntent)
                        }*/

                        //enable save
                        //btnSave!!.isEnabled = true
                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_PROPERTY_BACK_TO_LIST, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_PROPERTY_BACK_TO_LIST")

                        //listViewProperty!!.visibility = View.VISIBLE
                        //listViewPropertyDetail!!.visibility = View.GONE

                        isPropertyInDetail = 0
                    }

                }
            }
        }

        if (!isRegister) {
            filter = IntentFilter()
            filter.addAction(Constants.ACTION.ACTION_BARCODE_NULL)
            filter.addAction(Constants.ACTION.ACTION_NETWORK_FAILED)
            filter.addAction(Constants.ACTION.ACTION_CONNECTION_TIMEOUT)
            filter.addAction(Constants.ACTION.ACTION_PROPERTY_SCAN_BARCODE)
            filter.addAction(Constants.ACTION.ACTION_SERVER_ERROR)
            filter.addAction(Constants.ACTION.ACTION_RECEIPT_FRAGMENT_REFRESH)
            filter.addAction(Constants.ACTION.ACTION_PROPERTY_MORE_DETAIL_REFRESH)
            filter.addAction(Constants.ACTION.ACTION_SEEK_BAR_SELECT_PAGE_ACTION)
            filter.addAction(Constants.ACTION.ACTION_PROPERTY_MODIFY_NO_CHANGED)
            filter.addAction(Constants.ACTION.ACTION_PROPERTY_MODIFY_CHANGED)
            filter.addAction(Constants.ACTION.ACTION_PROPERTY_FRAGMENT_REFRESH)
            filter.addAction(Constants.ACTION.ACTION_PROPERTY_BACK_TO_LIST)
            propertyContext?.registerReceiver(mReceiver, filter)
            isRegister = true
            Log.d(mTAG, "registerReceiver mReceiver")
        }

        return view
    }

    override fun onDestroyView() {
        Log.i(mTAG, "onDestroy")

        if (isRegister && mReceiver != null) {
            try {
                propertyContext!!.unregisterReceiver(mReceiver)
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            }

            isRegister = false
            mReceiver = null
            Log.d(mTAG, "unregisterReceiver mReceiver")
        }

        super.onDestroyView()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        Log.i(mTAG, "onActivityCreated")
        super.onActivityCreated(savedInstanceState)

    }

    private fun toast(message: String) {

        if (toastHandle != null)
            toastHandle!!.cancel()

        val toast = Toast.makeText(propertyContext, message, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER_HORIZONTAL or Gravity.CENTER_VERTICAL, 0, 0)
        val group = toast.view as ViewGroup
        val textView = group.getChildAt(0) as TextView
        textView.textSize = 30.0f
        toast.show()

        toastHandle = toast
    }

    fun toastPage(message: String) {

        if (toastHandle != null) {
            toastHandle!!.cancel()
        }

        val toast = Toast.makeText(propertyContext, message, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM, 0, 0)
        val group = toast.view as ViewGroup
        val textView = group.getChildAt(0) as TextView
        textView.textSize = 30.0f
        toast.show()

        toastHandle = toast
    }

    override fun onPageSelected(position: Int) {

        Log.e(mTAG, "onPageSelected = $position")

        currentPropertyPage = position



        val page = position + 1
        //var pageString = "第 "+ page + "/"+ materialList.size +" 頁"
        val pageString = getString(R.string.material_viewpage_page, page, propertyList.size)
        toastPage(pageString)
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageScrollStateChanged(state: Int) {

    }
}