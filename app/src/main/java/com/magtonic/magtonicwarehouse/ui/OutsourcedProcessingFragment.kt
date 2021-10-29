package com.magtonic.magtonicwarehouse.ui


import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import android.widget.AdapterView
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleObserver
import com.magtonic.magtonicwarehouse.MainActivity
import com.magtonic.magtonicwarehouse.MainActivity.Companion.isKeyBoardShow
import com.magtonic.magtonicwarehouse.MainActivity.Companion.isOutSourcedInDetail
import com.magtonic.magtonicwarehouse.MainActivity.Companion.outsourcedProcessOrderList
import com.magtonic.magtonicwarehouse.MainActivity.Companion.outsourcedProcessOrderListBySupplier
import com.magtonic.magtonicwarehouse.MainActivity.Companion.outsourcedSupplierHashMap
import com.magtonic.magtonicwarehouse.MainActivity.Companion.outsourcedSupplierNameList
import com.magtonic.magtonicwarehouse.R
import com.magtonic.magtonicwarehouse.SignActivity
import com.magtonic.magtonicwarehouse.data.*
import java.util.*
import kotlin.collections.ArrayList



class OutsourcedProcessingFragment : Fragment(), LifecycleObserver {
    private val mTAG = OutsourcedProcessingFragment::class.java.name
    private var outsourcedProcessContext: Context? = null

    //private var outsourcedProcessLowerPartItemAdapter: OutsourcedProcessLowerPartItemAdapter? = null
    //private var outsourcedProcessTopItemAdapter: OutsourcedProcessTopItemAdapter? = null
    private var outsourcedProcessDetailItemAdapter: OutsourcedProcessDetailItemAdapter? = null
    private var outsourcedProcessSupplierItemAdapter: OutsourcedProcessSupplierItemAdapter? = null
    private var outsourcedProcessMoreDetailAdapter: OutsourcedProcessMoreDetailAdapter? = null
    //private var btnOutsourcedProcessMain: Button? = null
    //private var btnOutsourcedProcessLower: Button? = null

    //private var btnSign: Button? = null

    private var textViewSupplier: TextView? = null
    private var barcodeInput: EditText? = null
    private var linearLayout: LinearLayout? = null
    private var linearLayoutSupplierHeader: LinearLayout? = null
    private var linearLayoutDetailHeader: LinearLayout? = null
    private var viewLine: View?=null
    //private var layoutLowerItemHeader: LinearLayout? = null
    //private var listView: ListView? = null
    private var listViewBySupplier: ListView? = null
    private var listViewDetail: ListView? = null
    private var listViewMoreDetail: ListView? = null
    private var progressBar: ProgressBar? = null
    private var relativeLayout: RelativeLayout? = null
    private var imageViewPrev: ImageView? = null

    //var outsourcedProcessTopList = ArrayList<OutsourcedProcessTopItem>()
    //var outsourcedProcessLowerList = ArrayList<OutsourcedProcessLowerPartItem>()
    private var outsourcedProcessDetailList = ArrayList<OutsourcedProcessDetailItem>()
    private var outsourcedProcessMoreDetailList = ArrayList<OutsourcedProcessMoreDetailItem>()
    private var outsourcedProcessListBySupplier = ArrayList<OutsourcedProcessSupplierItem>()

    //private var listViewTop: ListView?= null
    //private var listViewLower: ListView?= null
    private var mReceiver: BroadcastReceiver? = null
    private var isRegister = false

    //private var currentClickItem: Int = 0
    private var toastHandle: Toast? = null

    private val colorCodePink = Color.parseColor("#D81B60")
    //private val colorCodeBlue = Color.parseColor("#1976D2")

    private var currentSendOrder: String = ""
    private var currentSelectSendOrder: Int = -1

    //private val outsourcedSupplierHashMap = HashMap<String, String>()
    //private val outsourcedSupplierNameList = ArrayList<String>()
    var currentSelectedSupplier: Int = 0

    private val storageList = ArrayList<String>()
    private val storageWarehouseList = ArrayList<String>()

    private var storageFilter = ""
    //private var warehouseFilter = ""

    var outsourcedProcessDetailFilterList = ArrayList<OutsourcedProcessDetailItem>()
    private var currentWarehouse: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.e(mTAG, "onCreate")

        outsourcedProcessContext = context

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        Log.d(mTAG, "onCreateView")

        outsourcedProcessOrderListBySupplier.clear()

        val view = inflater.inflate(R.layout.fragment_outsourced_process, container, false)

        relativeLayout = view.findViewById(R.id.outsourced_process_container)
        linearLayout = view.findViewById(R.id.linearLayoutOutsourcedProcess)
        linearLayoutSupplierHeader = view.findViewById(R.id.linearLayoutSupplierHeader)
        linearLayoutDetailHeader = view.findViewById(R.id.linearLayoutDetailHeader)
        viewLine = view.findViewById(R.id.viewLine)
        imageViewPrev = view.findViewById(R.id.imageViewPrev)
        //layoutLowerItemHeader = view.findViewById(R.id.layoutLowerItemHeader)
        progressBar = ProgressBar(outsourcedProcessContext, null, android.R.attr.progressBarStyleLarge)
        val params = RelativeLayout.LayoutParams(MainActivity.screenHeight / 4, MainActivity.screenWidth / 4)
        params.addRule(RelativeLayout.CENTER_IN_PARENT)

        val localRelativeLayout: RelativeLayout? = relativeLayout
        if (localRelativeLayout != null) {
            localRelativeLayout.addView(progressBar, params)
        } else {
            Log.e(mTAG, "localRelativeLayout = null")
        }
        progressBar!!.visibility = View.GONE
        textViewSupplier = view.findViewById(R.id.textViewOutsourcedProcess)
        barcodeInput = view.findViewById(R.id.editTextOutsourcedProcess)
        //btnOutsourcedProcessMain = view.findViewById(R.id.btnOutsourcedProcessMain)
        //btnOutsourcedProcessLower = view.findViewById(R.id.btnOutsourcedProcessLower)
        //btnUpload = view.findViewById(R.id.btnUpload)
        //buttonTextColor = btnUpload!!.textColors
        //btnSign = view.findViewById(R.id.btnOutsourcedProcessSign)
        //listViewTop = view!!.findViewById(R.id.listViewOutsourcedProcessTop)

        listViewBySupplier = view!!.findViewById(R.id.listViewOutsourceListBySupplier)
        listViewDetail = view.findViewById(R.id.listViewOutsourceDetail)
        listViewMoreDetail = view.findViewById(R.id.listViewOutsourceMoreDetail)

        listViewBySupplier!!.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            Log.d(mTAG, "click $position")

            if (!outsourcedProcessListBySupplier[position].getIsSigned()) {
                currentSelectSendOrder = position

                currentSendOrder = outsourcedProcessListBySupplier[position].getData2()

                progressBar!!.indeterminateTintList = ColorStateList.valueOf(colorCodePink)
                progressBar!!.visibility = View.VISIBLE

                if (outsourcedProcessDetailList.size > 0) {
                    outsourcedProcessDetailList.clear()
                }

                if (outsourcedProcessDetailItemAdapter != null) {
                    outsourcedProcessDetailItemAdapter!!.notifyDataSetChanged()
                }

                val searchIntent = Intent()
                searchIntent.action = Constants.ACTION.ACTION_OUTSOURCED_PROCESS_GET_DETAIL_BY_SEND_ORDER
                searchIntent.putExtra("SEND_ORDER", outsourcedProcessListBySupplier[position].getData2())
                outsourcedProcessContext?.sendBroadcast(searchIntent)
            } else {
                toast(getString(R.string.outsourced_process_sign_is_signed_already))
            }


        }

        listViewBySupplier!!.onItemLongClickListener = AdapterView.OnItemLongClickListener { _, _, position, _ -> // Process the long-click

            Log.d(mTAG, "long click $position")



            true
        }

        listViewDetail!!.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            Log.d(mTAG, "click $position")

            val moreDetailIntent = Intent()
            moreDetailIntent.action = Constants.ACTION.ACTION_OUTSOURCED_PROCESS_FRAGMENT_MORE_DETAIL_REFRESH
            moreDetailIntent.putExtra("SEND_ORDER", currentSendOrder)
            moreDetailIntent.putExtra("INDEX", position.toString())
            outsourcedProcessContext?.sendBroadcast(moreDetailIntent)
        }


        val storageSpinner = view.findViewById<Spinner>(R.id.storageSpinner)
        val storageAdapter: ArrayAdapter<String> = ArrayAdapter(outsourcedProcessContext as Context, R.layout.myspinner, storageWarehouseList)
        storageSpinner.adapter = storageAdapter

        storageSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                Log.e(mTAG, "position = $position")

                outsourcedProcessDetailFilterList.clear()
                //if (outsourcedProcessDetailItemAdapter != null) {
                //    outsourcedProcessDetailItemAdapter?.notifyDataSetChanged()
                //}


                storageFilter = if (position == 0) {
                    ""
                } else {
                    storageList[position]
                }
                currentWarehouse = storageFilter
                if (storageFilter != "") {


                    for (data in outsourcedProcessDetailList) {
                        if (data.getData9() == storageFilter) {
                            outsourcedProcessDetailFilterList.add(data)
                        }
                    }

                    outsourcedProcessDetailItemAdapter = OutsourcedProcessDetailItemAdapter(outsourcedProcessContext, R.layout.fragment_outsourced_process_detail_item, outsourcedProcessDetailFilterList)
                    listViewDetail!!.adapter = outsourcedProcessDetailItemAdapter
                } else {
                    outsourcedProcessDetailItemAdapter = OutsourcedProcessDetailItemAdapter(outsourcedProcessContext, R.layout.fragment_outsourced_process_detail_item, outsourcedProcessDetailList)
                    listViewDetail!!.adapter = outsourcedProcessDetailItemAdapter
                }


            }

        }

        /*val warehouseSpinner = view.findViewById<Spinner>(R.id.warehouseSpinner)
        val warehouseAdapter: ArrayAdapter<String> = ArrayAdapter(outsourcedProcessContext as Context, R.layout.myspinner, warehouseList)
        warehouseSpinner.adapter = warehouseAdapter

        warehouseSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                Log.e(mTAG, "position = $position")
                warehouseFilter = if (position == 0) {
                    ""
                } else {
                    warehouseList[position]
                }

                if (storageFilter != "" && warehouseFilter != "") {
                    outsourcedProcessDetailFilterList.clear()
                    for (data in outsourcedProcessDetailList) {
                        if (data.getData9() == storageFilter && data.getData10() == warehouseFilter ) {
                            outsourcedProcessDetailFilterList.add(data)
                        }
                    }

                    outsourcedProcessDetailItemAdapter = OutsourcedProcessDetailItemAdapter(outsourcedProcessContext, R.layout.fragment_outsourced_process_detail_item, outsourcedProcessDetailFilterList)
                    listViewDetail!!.adapter = outsourcedProcessDetailItemAdapter
                }
            }

        }*/
        //listViewLower = view.findViewById(R.id.listViewOutsourcedProcessLower)
        //val header = layoutInflater.inflate(R.layout.fragment_outsourced_process_lower_header, null) as View
        //listViewLower!!.addHeaderView(header)
        /*listViewLower!!.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            Log.d(mTAG, "click $position")

            if (outsourcedProcessLowerPartItemAdapter != null) {

                outsourcedProcessLowerList[position].setChecked(true)

                listViewLower!!.invalidateViews()
            }

            var found = false

            for (i in 0 until outsourcedProcessLowerList.size) {
                if (!outsourcedProcessLowerList[i].getChecked()) { //find out not checked
                    found = true
                }
            }

            if (!found)
                btnSign!!.visibility = View.VISIBLE
            else {
                btnSign!!.visibility = View.GONE
            }



        }

        listViewLower!!.onItemLongClickListener = AdapterView.OnItemLongClickListener { _, _, position, _ -> // Process the long-click

            Log.d(mTAG, "long click $position")

            showUploadToERPDialog(outsourcedProcessLowerList[position].getHeader() as String, position)

            true
        }*/


        if (outsourcedProcessContext != null) {

            
            outsourcedProcessSupplierItemAdapter = OutsourcedProcessSupplierItemAdapter(outsourcedProcessContext, R.layout.fragment_outsourced_process_supplier_item, outsourcedProcessListBySupplier)
            listViewBySupplier!!.adapter = outsourcedProcessSupplierItemAdapter
            
            //detail
            outsourcedProcessDetailItemAdapter = OutsourcedProcessDetailItemAdapter(outsourcedProcessContext, R.layout.fragment_outsourced_process_detail_item, outsourcedProcessDetailList)
            listViewDetail!!.adapter = outsourcedProcessDetailItemAdapter

            //more detail
            outsourcedProcessMoreDetailAdapter = OutsourcedProcessMoreDetailAdapter(outsourcedProcessContext, R.layout.fragment_outsourced_process_more_detail_item, outsourcedProcessMoreDetailList)
            listViewMoreDetail!!.adapter = outsourcedProcessMoreDetailAdapter
        }

        //detect soft keyboard




        /*linearLayout!!.viewTreeObserver.addOnGlobalLayoutListener(
            object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    val r = Rect()
                    linearLayout!!.getWindowVisibleDisplayFrame(r)
                    val screenHeight = linearLayout!!.rootView.height
                    val keypadHeight = screenHeight - r.bottom
                    isKeyBoardShow = (keypadHeight > screenHeight * 0.15)

                    if (isKeyBoardShow) {
                        if (receiptDetailList.size > 0) {
                            receiptDetailList[currentClickItem].getEditText()!!.requestFocus()
                        }

                    }
                }
            }
        )*/
        linearLayout!!.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            linearLayout!!.getWindowVisibleDisplayFrame(r)
            val screenHeight = linearLayout!!.rootView.height
            val keypadHeight = screenHeight - r.bottom
            //val temp = screenHeight * 0.15
            //Log.e(mTAG, "keypadHeight = $keypadHeight, screenHeight =$screenHeight, screenHeight * 0.15 = $temp")
            isKeyBoardShow = (keypadHeight > screenHeight * 0.15)



            /*if (isKeyBoardShow) {
                if (!barcodeInput!!.isFocused) {
                    val hideIntent = Intent()
                    hideIntent.action = Constants.ACTION.ACTION_HIDE_KEYBOARD
                    outsourcedProcessContext!!.sendBroadcast(hideIntent)
                }

            }*/
        }





        barcodeInput!!.setOnEditorActionListener { _, actionId, _ ->

            when(actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    Log.e(mTAG, "IME_ACTION_DONE")
                    outsourcedProcessOrderListBySupplier.clear()

                    linearLayoutSupplierHeader!!.visibility = View.INVISIBLE
                    linearLayoutDetailHeader!!.visibility = View.GONE
                    viewLine!!.visibility = View.GONE

                    progressBar!!.indeterminateTintList = ColorStateList.valueOf(colorCodePink)
                    progressBar!!.visibility = View.VISIBLE

                    /*outsourcedProcessDetailList.clear()
                    if (outsourcedProcessOrderDetailItemAdapter != null) {
                        outsourcedProcessOrderDetailItemAdapter?.notifyDataSetChanged()
                    }*/
                    outsourcedProcessListBySupplier.clear()
                    if (outsourcedProcessSupplierItemAdapter != null) {
                        outsourcedProcessSupplierItemAdapter?.notifyDataSetChanged()
                    }

                    outsourcedProcessDetailList.clear()
                    if (outsourcedProcessDetailItemAdapter != null) {
                        outsourcedProcessDetailItemAdapter?.notifyDataSetChanged()
                    }

                    outsourcedProcessMoreDetailList.clear()
                    if (outsourcedProcessMoreDetailAdapter != null) {
                        outsourcedProcessMoreDetailAdapter?.notifyDataSetChanged()
                    }

                    listViewBySupplier!!.visibility = View.VISIBLE
                    listViewDetail!!.visibility = View.GONE
                    listViewMoreDetail!!.visibility = View.GONE

                    isOutSourcedInDetail = 0

                    val searchIntent = Intent()
                    searchIntent.action = Constants.ACTION.ACTION_USER_INPUT_SEARCH
                    searchIntent.putExtra("INPUT_NO",
                        barcodeInput!!.text.toString().uppercase(Locale.getDefault())
                    )
                    outsourcedProcessContext?.sendBroadcast(searchIntent)

                    //val hideIntent = Intent()
                    //hideIntent.action = Constants.ACTION.ACTION_OUTSOURCED_PROCESS_HIDE_FAB_BACK
                    //outsourcedProcessContext?.sendBroadcast(hideIntent)

                    true
                }

                EditorInfo.IME_ACTION_GO -> {
                    Log.e(mTAG, "IME_ACTION_GO")
                    true
                }

                EditorInfo.IME_ACTION_NEXT -> {
                    Log.e(mTAG, "IME_ACTION_NEXT")
                    outsourcedProcessOrderListBySupplier.clear()

                    linearLayoutSupplierHeader!!.visibility = View.INVISIBLE
                    linearLayoutDetailHeader!!.visibility = View.GONE
                    viewLine!!.visibility = View.GONE

                    progressBar!!.indeterminateTintList = ColorStateList.valueOf(colorCodePink)
                    progressBar!!.visibility = View.VISIBLE

                    /*outsourcedProcessDetailList.clear()
                    if (outsourcedProcessOrderDetailItemAdapter != null) {
                        outsourcedProcessOrderDetailItemAdapter?.notifyDataSetChanged()
                    }*/
                    outsourcedProcessListBySupplier.clear()
                    if (outsourcedProcessSupplierItemAdapter != null) {
                        outsourcedProcessSupplierItemAdapter?.notifyDataSetChanged()
                    }

                    outsourcedProcessDetailList.clear()
                    if (outsourcedProcessDetailItemAdapter != null) {
                        outsourcedProcessDetailItemAdapter?.notifyDataSetChanged()
                    }

                    outsourcedProcessMoreDetailList.clear()
                    if (outsourcedProcessMoreDetailAdapter != null) {
                        outsourcedProcessMoreDetailAdapter?.notifyDataSetChanged()
                    }

                    listViewBySupplier!!.visibility = View.VISIBLE
                    listViewDetail!!.visibility = View.GONE
                    listViewMoreDetail!!.visibility = View.GONE

                    isOutSourcedInDetail = 0

                    val searchIntent = Intent()
                    searchIntent.action = Constants.ACTION.ACTION_USER_INPUT_SEARCH
                    searchIntent.putExtra("INPUT_NO",
                        barcodeInput!!.text.toString().uppercase(Locale.getDefault())
                    )
                    outsourcedProcessContext?.sendBroadcast(searchIntent)

                    //val hideIntent = Intent()
                    //hideIntent.action = Constants.ACTION.ACTION_OUTSOURCED_PROCESS_HIDE_FAB_BACK
                    //outsourcedProcessContext?.sendBroadcast(hideIntent)

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

        imageViewPrev!!.setOnClickListener {
            val backIntent = Intent()
            backIntent.action = Constants.ACTION.ACTION_OUTSOURCED_PROCESS_BACK_TO_SUPPLIER_LIST
            outsourcedProcessContext!!.sendBroadcast(backIntent)

            val hideIntent = Intent()
            hideIntent.action = Constants.ACTION.ACTION_OUTSOURCED_PROCESS_HIDE_FAB_BACK
            outsourcedProcessContext!!.sendBroadcast(hideIntent)
        }


        /*btnSign!!.setOnClickListener {
            val intent = Intent(outsourcedProcessContext, SignActivity::class.java)
            startActivity(intent)

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

                        //showButtonStatusByState()

                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_CONNECTION_TIMEOUT, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_CONNECTION_TIMEOUT")

                        progressBar!!.visibility = View.GONE

                        //showButtonStatusByState()

                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_OUTSOURCED_PROCESS_SCAN_BARCODE, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_OUTSOURCED_PROCESS_SCAN_BARCODE")

                        val barcode: String = intent.getStringExtra("BARCODE") as String
                        Log.e(mTAG, "barcode = $barcode")
                        barcodeInput!!.setText(barcode)


                        textViewSupplier!!.visibility = View.GONE
                        textViewSupplier!!.text = ""

                        linearLayoutSupplierHeader!!.visibility = View.INVISIBLE
                        linearLayoutDetailHeader!!.visibility = View.GONE
                        viewLine!!.visibility = View.GONE

                        progressBar!!.indeterminateTintList = ColorStateList.valueOf(colorCodePink)
                        progressBar!!.visibility = View.VISIBLE

                        outsourcedProcessListBySupplier.clear()
                        if (outsourcedProcessSupplierItemAdapter != null) {
                            outsourcedProcessSupplierItemAdapter?.notifyDataSetChanged()
                        }

                        outsourcedProcessDetailList.clear()
                        if (outsourcedProcessDetailItemAdapter != null) {
                            outsourcedProcessDetailItemAdapter?.notifyDataSetChanged()
                        }

                        outsourcedProcessMoreDetailList.clear()
                        if (outsourcedProcessMoreDetailAdapter != null) {
                            outsourcedProcessMoreDetailAdapter?.notifyDataSetChanged()
                        }

                        listViewBySupplier!!.visibility = View.VISIBLE
                        listViewDetail!!.visibility = View.GONE
                        listViewMoreDetail!!.visibility = View.GONE

                        isOutSourcedInDetail = 0

                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_SERVER_ERROR, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_SERVER_ERROR")

                        progressBar!!.visibility = View.GONE

                        //showButtonStatusByState()

                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_OUTSOURCED_PROCESS_FRAGMENT_REFRESH, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_OUTSOURCED_PROCESS_FRAGMENT_REFRESH")

                        progressBar!!.visibility = View.GONE

                        storageList.clear()
                        storageWarehouseList.clear()
                        storageAdapter.notifyDataSetChanged()

                        for (rjOutSourceProcessedBySupplier in outsourcedProcessOrderListBySupplier) {
                            var found = false
                            //found same send material order
                            for (i in 0 until outsourcedProcessListBySupplier.size) {
                                if (rjOutSourceProcessedBySupplier.data1 == outsourcedProcessListBySupplier[i].getData1() &&
                                        rjOutSourceProcessedBySupplier.data2 == outsourcedProcessListBySupplier[i].getData2()) {
                                    Log.e(mTAG, "Found !")
                                    found = true
                                    val alternateString = outsourcedProcessListBySupplier[i].getData3()+"\n"+rjOutSourceProcessedBySupplier.data3
                                    outsourcedProcessListBySupplier[i].setData3(alternateString)
                                }
                            }

                            if (!found) {
                                val outsourcedProcessSupplierItem = OutsourcedProcessSupplierItem(rjOutSourceProcessedBySupplier.data1, rjOutSourceProcessedBySupplier.data2, rjOutSourceProcessedBySupplier.data3)
                                outsourcedProcessListBySupplier.add(outsourcedProcessSupplierItem)
                            }
                            /*
                            val outsourcedProcessSupplierItem = OutsourcedProcessSupplierItem(rjOutSourceProcessedBySupplier.data1, rjOutSourceProcessedBySupplier.data2, rjOutSourceProcessedBySupplier.data3)
                            outsourcedProcessListBySupplier.add(outsourcedProcessSupplierItem)*/
                        }

                        if (outsourcedProcessListBySupplier.size > 0) {
                            linearLayoutSupplierHeader!!.visibility = View.VISIBLE
                            viewLine!!.visibility = View.VISIBLE
                        }


                        if (outsourcedProcessSupplierItemAdapter != null) {
                            outsourcedProcessSupplierItemAdapter?.notifyDataSetChanged()
                        }

                        if (isKeyBoardShow) {
                            val hideIntent = Intent()
                            hideIntent.action = Constants.ACTION.ACTION_HIDE_KEYBOARD
                            outsourcedProcessContext!!.sendBroadcast(hideIntent)
                        }

                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_OUTSOURCED_PROCESS_FRAGMENT_DETAIL_REFRESH, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_OUTSOURCED_PROCESS_FRAGMENT_DETAIL_REFRESH")

                        progressBar!!.visibility = View.GONE

                        if (isKeyBoardShow) {
                            val hideIntent = Intent()
                            hideIntent.action = Constants.ACTION.ACTION_HIDE_KEYBOARD
                            outsourcedProcessContext!!.sendBroadcast(hideIntent)
                        }
                        
                        
                        

                        for (rjOutSourceProcessed in outsourcedProcessOrderList) {

                            val outsourcedProcessDetailItem = OutsourcedProcessDetailItem(rjOutSourceProcessed.data1, rjOutSourceProcessed.data2, rjOutSourceProcessed.data3, rjOutSourceProcessed.data4,
                                rjOutSourceProcessed.data5, rjOutSourceProcessed.data6, rjOutSourceProcessed.data7, rjOutSourceProcessed.data8, rjOutSourceProcessed.data9, rjOutSourceProcessed.data10)
                            outsourcedProcessDetailList.add(outsourcedProcessDetailItem)
                        }

                        if (outsourcedProcessDetailList.size > 0) {
                            linearLayoutDetailHeader!!.visibility = View.VISIBLE
                            linearLayoutSupplierHeader!!.visibility = View.GONE
                            //viewLine!!.visibility = View.VISIBLE
                            listViewBySupplier!!.visibility = View.GONE
                            listViewDetail!!.visibility = View.VISIBLE

                            isOutSourcedInDetail = 1
                        }


                        if (outsourcedProcessDetailItemAdapter != null) {
                            outsourcedProcessDetailItemAdapter?.notifyDataSetChanged()
                        }
                        //find storage and warehouse
                        if (outsourcedProcessDetailList.size > 0) {
                            storageList.clear()
                            storageWarehouseList.clear()
                            storageList.add(getString(R.string.please_select))
                            storageWarehouseList.add(getString(R.string.please_select))

                            for (rjOutSourceProcessed in outsourcedProcessOrderList) {
                                var foundStorage = false
                                //var foundWarehouse = false
                                for (storage in storageList) {
                                    if (rjOutSourceProcessed.data9 == storage) {
                                        foundStorage = true
                                        break
                                    }
                                }

                                if (!foundStorage) {
                                    storageList.add(rjOutSourceProcessed.data9)
                                    val combineString = rjOutSourceProcessed.data9+" - "+rjOutSourceProcessed.data10
                                    storageWarehouseList.add(combineString)
                                }



                            }

                            for (storage in storageList) {
                                Log.e(mTAG, "[$storage]")
                            }

                            for (storageWarehouse in storageWarehouseList) {
                                Log.e(mTAG, "[$storageWarehouse]")
                            }

                            storageAdapter.notifyDataSetChanged()
                            //warehouseAdapter.notifyDataSetChanged()
                        }


                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_OUTSOURCED_PROCESS_FRAGMENT_MORE_DETAIL_REFRESH, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_OUTSOURCED_PROCESS_FRAGMENT_MORE_DETAIL_REFRESH")

                        val sendOrder = intent.getStringExtra("SEND_ORDER")
                        val idxString = intent.getStringExtra("INDEX")

                        val idx = idxString?.toInt()


                        if (isKeyBoardShow) {
                            val hideIntent = Intent()
                            hideIntent.action = Constants.ACTION.ACTION_HIDE_KEYBOARD
                            outsourcedProcessContext!!.sendBroadcast(hideIntent)
                        }


                        outsourcedProcessMoreDetailList.clear()

                        if (storageFilter == "") {

                            if (idx != null) {
                                val item0 =
                                    OutsourcedProcessMoreDetailItem("發料單號", sendOrder as String)
                                outsourcedProcessMoreDetailList.add(item0)
                                val item1 = OutsourcedProcessMoreDetailItem(
                                    "項次",
                                    outsourcedProcessDetailList[idx].getData1()
                                )
                                outsourcedProcessMoreDetailList.add(item1)
                                val item2 = OutsourcedProcessMoreDetailItem(
                                    "工單編號",
                                    outsourcedProcessDetailList[idx].getData2()
                                )
                                outsourcedProcessMoreDetailList.add(item2)
                                val item3 = OutsourcedProcessMoreDetailItem(
                                    "料件編號",
                                    outsourcedProcessDetailList[idx].getData3()
                                )
                                outsourcedProcessMoreDetailList.add(item3)
                                val item4 = OutsourcedProcessMoreDetailItem(
                                    "發料數量",
                                    outsourcedProcessDetailList[idx].getData4()
                                )
                                outsourcedProcessMoreDetailList.add(item4)
                                val item5 = OutsourcedProcessMoreDetailItem(
                                    "發料單位",
                                    outsourcedProcessDetailList[idx].getData5()
                                )
                                outsourcedProcessMoreDetailList.add(item5)
                                val item6 = OutsourcedProcessMoreDetailItem(
                                    "品名",
                                    outsourcedProcessDetailList[idx].getData6()
                                )
                                outsourcedProcessMoreDetailList.add(item6)
                                val item7 = OutsourcedProcessMoreDetailItem(
                                    "規格",
                                    outsourcedProcessDetailList[idx].getData7()
                                )
                                outsourcedProcessMoreDetailList.add(item7)
                                val item8 = OutsourcedProcessMoreDetailItem(
                                    "過帳否",
                                    outsourcedProcessDetailList[idx].getData8()
                                )
                                outsourcedProcessMoreDetailList.add(item8)
                                val item9 = OutsourcedProcessMoreDetailItem(
                                    "儲位",
                                    outsourcedProcessDetailList[idx].getData9()
                                )
                                outsourcedProcessMoreDetailList.add(item9)
                                val item10 = OutsourcedProcessMoreDetailItem(
                                    "倉庫",
                                    outsourcedProcessDetailList[idx].getData10()
                                )
                                outsourcedProcessMoreDetailList.add(item10)
                            }




                        } else {
                            if (idx != null) {
                                val item0 =
                                    OutsourcedProcessMoreDetailItem("發料單號", sendOrder as String)
                                outsourcedProcessMoreDetailList.add(item0)
                                val item1 = OutsourcedProcessMoreDetailItem(
                                    "項次",
                                    outsourcedProcessDetailFilterList[idx].getData1()
                                )
                                outsourcedProcessMoreDetailList.add(item1)
                                val item2 = OutsourcedProcessMoreDetailItem(
                                    "工單編號",
                                    outsourcedProcessDetailFilterList[idx].getData2()
                                )
                                outsourcedProcessMoreDetailList.add(item2)
                                val item3 = OutsourcedProcessMoreDetailItem(
                                    "料件編號",
                                    outsourcedProcessDetailFilterList[idx].getData3()
                                )
                                outsourcedProcessMoreDetailList.add(item3)
                                val item4 = OutsourcedProcessMoreDetailItem(
                                    "發料數量",
                                    outsourcedProcessDetailFilterList[idx].getData4()
                                )
                                outsourcedProcessMoreDetailList.add(item4)
                                val item5 = OutsourcedProcessMoreDetailItem(
                                    "發料單位",
                                    outsourcedProcessDetailFilterList[idx].getData5()
                                )
                                outsourcedProcessMoreDetailList.add(item5)
                                val item6 = OutsourcedProcessMoreDetailItem(
                                    "品名",
                                    outsourcedProcessDetailFilterList[idx].getData6()
                                )
                                outsourcedProcessMoreDetailList.add(item6)
                                val item7 = OutsourcedProcessMoreDetailItem(
                                    "規格",
                                    outsourcedProcessDetailFilterList[idx].getData7()
                                )
                                outsourcedProcessMoreDetailList.add(item7)
                                val item8 = OutsourcedProcessMoreDetailItem(
                                    "過帳否",
                                    outsourcedProcessDetailFilterList[idx].getData8()
                                )
                                outsourcedProcessMoreDetailList.add(item8)
                                val item9 = OutsourcedProcessMoreDetailItem(
                                    "儲位",
                                    outsourcedProcessDetailFilterList[idx].getData9()
                                )
                                outsourcedProcessMoreDetailList.add(item9)
                                val item10 = OutsourcedProcessMoreDetailItem(
                                    "倉庫",
                                    outsourcedProcessDetailFilterList[idx].getData10()
                                )
                                outsourcedProcessMoreDetailList.add(item10)
                            }
                        }
                        linearLayoutDetailHeader!!.visibility = View.INVISIBLE

                        //viewLine!!.visibility = View.VISIBLE
                        listViewDetail!!.visibility = View.GONE
                        listViewMoreDetail!!.visibility = View.VISIBLE

                        isOutSourcedInDetail = 2

                        if (outsourcedProcessMoreDetailAdapter != null) {
                            outsourcedProcessMoreDetailAdapter?.notifyDataSetChanged()
                        }
                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_OUTSOURCED_PROCESS_BACK_TO_SUPPLIER_LIST, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_OUTSOURCED_PROCESS_BACK_TO_SUPPLIER_LIST")

                        linearLayoutDetailHeader!!.visibility = View.GONE
                        linearLayoutSupplierHeader!!.visibility = View.VISIBLE
                        //viewLine!!.visibility = View.VISIBLE
                        listViewBySupplier!!.visibility = View.VISIBLE
                        listViewDetail!!.visibility = View.GONE
                        listViewMoreDetail!!.visibility = View.GONE

                        isOutSourcedInDetail = 0

                        storageWarehouseList.clear()
                        storageAdapter.notifyDataSetChanged()
                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_OUTSOURCED_PROCESS_BACK_TO_DETAIL_LIST, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_OUTSOURCED_PROCESS_BACK_TO_DETAIL_LIST")

                        linearLayoutDetailHeader!!.visibility = View.VISIBLE
                        linearLayoutSupplierHeader!!.visibility = View.GONE
                        //viewLine!!.visibility = View.VISIBLE
                        listViewBySupplier!!.visibility = View.GONE
                        listViewDetail!!.visibility = View.VISIBLE
                        listViewMoreDetail!!.visibility = View.GONE

                        isOutSourcedInDetail = 1
                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_OUTSOURCED_PROCESS_SHOW_SIGN_DIALOG_ACTION, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_OUTSOURCED_PROCESS_SHOW_SIGN_DIALOG_ACTION")

                        if (currentWarehouse == "") {
                            toast(getString(R.string.please_select))
                        } else {
                            showSignDialog()
                        }


                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_OUTSOURCED_PROCESS_SIGN_UPLOAD_SUCCESS, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_OUTSOURCED_PROCESS_SIGN_UPLOAD_SUCCESS")

                        val sendOrder = intent.getStringExtra("SEND_ORDER")

                        toast(getString(R.string.outsourced_process_sign_confirm, sendOrder))

                        Log.d(mTAG, "sendOrder = $sendOrder")


                        for (i in 0 until outsourcedProcessListBySupplier.size) {
                            Log.e(mTAG, outsourcedProcessListBySupplier[i].getData1())
                            if (outsourcedProcessListBySupplier[i].getData2() == sendOrder) {
                                outsourcedProcessListBySupplier[i].setIsSigned(true)
                            }
                            Log.e(mTAG, "outsourcedProcessListBySupplier[$i] = ${outsourcedProcessListBySupplier[i].getIsSigned()}")
                        }

                        listViewBySupplier!!.invalidateViews()

                        val backIntent = Intent()
                        backIntent.action = Constants.ACTION.ACTION_OUTSOURCED_PROCESS_BACK_TO_SUPPLIER_LIST
                        outsourcedProcessContext!!.sendBroadcast(backIntent)

                        val hideIntent = Intent()
                        hideIntent.action = Constants.ACTION.ACTION_OUTSOURCED_PROCESS_HIDE_FAB_BACK
                        outsourcedProcessContext!!.sendBroadcast(hideIntent)
                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_OUTSOURCED_PROCESS_SHOW_SUPPLIER_DIALOG, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_OUTSOURCED_PROCESS_SHOW_SUPPLIER_DIALOG")

                        showSupplierDialog()
                    }

                }
            }
        }

        if (!isRegister) {
            filter = IntentFilter()
            filter.addAction(Constants.ACTION.ACTION_BARCODE_NULL)
            filter.addAction(Constants.ACTION.ACTION_NETWORK_FAILED)
            filter.addAction(Constants.ACTION.ACTION_CONNECTION_TIMEOUT)
            filter.addAction(Constants.ACTION.ACTION_OUTSOURCED_PROCESS_SCAN_BARCODE)
            filter.addAction(Constants.ACTION.ACTION_OUTSOURCED_PROCESS_FRAGMENT_REFRESH)
            filter.addAction(Constants.ACTION.ACTION_OUTSOURCED_PROCESS_FRAGMENT_DETAIL_REFRESH)
            filter.addAction(Constants.ACTION.ACTION_OUTSOURCED_PROCESS_FRAGMENT_MORE_DETAIL_REFRESH)
            filter.addAction(Constants.ACTION.ACTION_OUTSOURCED_PROCESS_BACK_TO_SUPPLIER_LIST)
            filter.addAction(Constants.ACTION.ACTION_OUTSOURCED_PROCESS_BACK_TO_DETAIL_LIST)
            filter.addAction(Constants.ACTION.ACTION_OUTSOURCED_PROCESS_SHOW_SIGN_DIALOG_ACTION)
            filter.addAction(Constants.ACTION.ACTION_OUTSOURCED_PROCESS_SIGN_UPLOAD_SUCCESS)
            filter.addAction(Constants.ACTION.ACTION_OUTSOURCED_PROCESS_SHOW_SUPPLIER_DIALOG)
            outsourcedProcessContext?.registerReceiver(mReceiver, filter)
            isRegister = true
            Log.d(mTAG, "registerReceiver mReceiver")
        }

        return view
    }

    override fun onDestroyView() {
        Log.i(mTAG, "onDestroy")

        val hideIntent = Intent()
        hideIntent.action = Constants.ACTION.ACTION_OUTSOURCED_PROCESS_HIDE_FAB_BACK
        outsourcedProcessContext!!.sendBroadcast(hideIntent)

        if (isRegister && mReceiver != null) {
            try {
                outsourcedProcessContext!!.unregisterReceiver(mReceiver)
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            }

            isRegister = false
            mReceiver = null
            Log.d(mTAG, "unregisterReceiver mReceiver")
        }



        super.onDestroyView()
    }

    /*override fun onActivityCreated(savedInstanceState: Bundle?) {
        Log.i(mTAG, "onActivityCreated")
        super.onActivityCreated(savedInstanceState)

    }*/

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity?.lifecycle?.addObserver(this)
    }

    private fun toast(message: String) {

        if (toastHandle != null)
            toastHandle!!.cancel()

        val toast = Toast.makeText(outsourcedProcessContext, HtmlCompat.fromHtml("<h1>$message</h1>", HtmlCompat.FROM_HTML_MODE_COMPACT), Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER_HORIZONTAL or Gravity.CENTER_VERTICAL, 0, 0)

       /*val toast = Toast.makeText(outsourcedProcessContext, message, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER_HORIZONTAL or Gravity.CENTER_VERTICAL, 0, 0)
        val group = toast.view as ViewGroup
        val textView = group.getChildAt(0) as TextView
        textView.textSize = 30.0f*/
        toast.show()

        toastHandle = toast
    }

    private fun showSignDialog() {

        // get prompts.xml view
        /*LayoutInflater layoutInflater = LayoutInflater.from(Nfc_read_app.this);
        View promptView = layoutInflater.inflate(R.layout.input_dialog, null);*/
        val promptView = View.inflate(outsourcedProcessContext, R.layout.outsourced_process_sign_dialog, null)

        val alertDialogBuilder = AlertDialog.Builder(outsourcedProcessContext).create()
        alertDialogBuilder.setView(promptView)

        //final EditText editFileName = (EditText) promptView.findViewById(R.id.editFileName);
        //val textViewMsg = promptView.findViewById<TextView>(R.id.textViewOutsourcedProcessDialogMsg)
        //val textViewOutsourcedProcessSendOrderHeader = promptView.findViewById<TextView>(R.id.textViewOutsourcedProcessSendOrderHeader)
        val textViewOutsourcedProcessSendOrderContent = promptView.findViewById<TextView>(R.id.textViewOutsourcedProcessSendOrderContent)
        //val textViewOutsourcedProcessWorkOrderHeader = promptView.findViewById<TextView>(R.id.textViewOutsourcedProcessWorkOrderHeader)
        val textViewOutsourcedProcessWorkOrderContent = promptView.findViewById<TextView>(R.id.textViewOutsourcedProcessWorkOrderContent)
        val btnCancel = promptView.findViewById<Button>(R.id.btnDialogCancel)
        val btnConfirm = promptView.findViewById<Button>(R.id.btnDialogConfirm)

        Log.e(mTAG, "send No. = $currentSendOrder")

        textViewOutsourcedProcessSendOrderContent.text = currentSendOrder
        textViewOutsourcedProcessWorkOrderContent.text = outsourcedProcessListBySupplier[currentSelectSendOrder].getData3()
        //textViewShouldContent.text = outsourcedProcessLowerList[position].getContentStatic()
        //textViewActualContent.inputType = (InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL)
        //textViewActualContent.setText(outsourcedProcessLowerList[position].getContentDynamic())

        btnCancel.text = getString(R.string.cancel)
        btnConfirm.text = getString(R.string.confirm)

        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
        btnCancel!!.setOnClickListener {
            /*val noModifyIntent = Intent()
            noModifyIntent.action = Constants.ACTION.ACTION_OUTSOURCED_PROCESS_NO_CHANGED
            noModifyIntent.putExtra("INDEX", position)
            outsourcedProcessContext!!.sendBroadcast(noModifyIntent)*/

            alertDialogBuilder.dismiss()
        }
        btnConfirm!!.setOnClickListener {

            if (currentWarehouse == "") {
                toast(getString(R.string.warehouse_empty_warnning))
            } else {
                val intent = Intent(outsourcedProcessContext, SignActivity::class.java)
                intent.putExtra("SEND_ORDER", currentSendOrder)
                intent.putExtra("TITLE", getString(R.string.nav_outsourced))
                intent.putExtra("WAREHOUSE", currentWarehouse)
                intent.putExtra("SEND_FRAGMENT", "OUTSOURCED_PROCESS")
                startActivity(intent)
            }


            /*
            val oldString: String = textViewShouldContent.text.toString()
            val newString: String = textViewActualContent.text.toString()

            val oldValue = oldString.toInt()
            var newValue: Int

            if (newString == "") {
                newValue = 0
            } else {
                newValue = newString.toInt()
            }


            if (newValue == 0) {
                toast("實發數量不能為0")
            } else if (newValue <= oldValue) {
                outsourcedProcessLowerList[position].setContentDynamic(textViewActualContent.text.toString())

                if (!oldString.contentEquals(newString)) {
                    outsourcedProcessLowerList[position].setChange(true)
                } else {
                    outsourcedProcessLowerList[position].setChange(false)
                }



                listViewLower!!.invalidate()

                val modifyIntent = Intent()
                modifyIntent.action = Constants.ACTION.ACTION_OUTSOURCED_PROCESS_MODIFY_CHANGED
                modifyIntent.putExtra("INDEX", position)
                modifyIntent.putExtra("CONTENT", textViewActualContent.text.toString())
                outsourcedProcessContext!!.sendBroadcast(modifyIntent)

                alertDialogBuilder.dismiss()
            } else {
                toast("實發數量不可超過應發數量")
            }*/

            alertDialogBuilder.dismiss()
        }
        alertDialogBuilder.show()

        /*alertDialogBuilder.window?.decorView?.addOnLayoutChangeListener { v, _, _, _, _, _, _, _, _ ->
            val displayRectangle = Rect()
            val window = alertDialogBuilder.window
            v.getWindowVisibleDisplayFrame(displayRectangle)
            val maxHeight = displayRectangle.height() * 0.5f // 60%

            if (v.height > maxHeight) {
                window?.setLayout(window.attributes.width, maxHeight.toInt())
            }
        }*/
    }

    private fun showSupplierDialog() {

        // get prompts.xml view
        /*LayoutInflater layoutInflater = LayoutInflater.from(Nfc_read_app.this);
        View promptView = layoutInflater.inflate(R.layout.input_dialog, null);*/
        val promptView = View.inflate(outsourcedProcessContext, R.layout.outsourced_process_supplier_dialog, null)

        val alertDialogBuilder = AlertDialog.Builder(outsourcedProcessContext).create()
        alertDialogBuilder.setView(promptView)

        //final EditText editFileName = (EditText) promptView.findViewById(R.id.editFileName);
        //val textViewMsg = promptView.findViewById<TextView>(R.id.textViewOutsourcedProcessDialogMsg)
        //val textViewOutsourcedProcessSendOrderHeader = promptView.findViewById<TextView>(R.id.textViewOutsourcedProcessSendOrderHeader)
        val spinnerSupplier = promptView.findViewById<Spinner>(R.id.spinnerSupplier)
        //val textViewOutsourcedProcessWorkOrderHeader = promptView.findViewById<TextView>(R.id.textViewOutsourcedProcessWorkOrderHeader)
        //val textViewOutsourcedProcessWorkOrderContent = promptView.findViewById<TextView>(R.id.textViewOutsourcedProcessWorkOrderContent)
        val btnCancel = promptView.findViewById<Button>(R.id.btnOutSourcedDialogCancel)
        val btnConfirm = promptView.findViewById<Button>(R.id.btnOutSourcedDialogConfirm)
        val adapter: ArrayAdapter<String> = ArrayAdapter(outsourcedProcessContext as Context, R.layout.myspinner, outsourcedSupplierNameList)
        spinnerSupplier.adapter = adapter

        spinnerSupplier.setSelection(currentSelectedSupplier)

        spinnerSupplier?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                barcodeInput!!.setText(outsourcedSupplierHashMap[outsourcedSupplierNameList[position]])

                currentSelectedSupplier = position

            }

        }
        //Log.e(mTAG, "send No. = $currentSendOrder")

        //textViewOutsourcedProcessSendOrderContent.text = currentSendOrder
        //textViewOutsourcedProcessWorkOrderContent.text = outsourcedProcessListBySupplier[currentSelectSendOrder].getData3()
        //textViewShouldContent.text = outsourcedProcessLowerList[position].getContentStatic()
        //textViewActualContent.inputType = (InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL)
        //textViewActualContent.setText(outsourcedProcessLowerList[position].getContentDynamic())

        //btnCancel.text = getString(R.string.cancel)
        //btnConfirm.text = getString(R.string.confirm)

        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
        btnCancel!!.setOnClickListener {


            alertDialogBuilder.dismiss()
        }
        btnConfirm!!.setOnClickListener {

            outsourcedProcessOrderListBySupplier.clear()

            //if (outsourcedSupplierNameList.size > 0) {
                textViewSupplier!!.visibility = View.VISIBLE
                textViewSupplier!!.text = outsourcedSupplierNameList[currentSelectedSupplier]

                linearLayoutSupplierHeader!!.visibility = View.INVISIBLE
                linearLayoutDetailHeader!!.visibility = View.GONE
                viewLine!!.visibility = View.GONE

                progressBar!!.indeterminateTintList = ColorStateList.valueOf(colorCodePink)
                progressBar!!.visibility = View.VISIBLE

                /*outsourcedProcessDetailList.clear()
                if (outsourcedProcessOrderDetailItemAdapter != null) {
                    outsourcedProcessOrderDetailItemAdapter?.notifyDataSetChanged()
                }*/
                outsourcedProcessListBySupplier.clear()
                if (outsourcedProcessSupplierItemAdapter != null) {
                    outsourcedProcessSupplierItemAdapter?.notifyDataSetChanged()
                }

                outsourcedProcessDetailList.clear()
                if (outsourcedProcessDetailItemAdapter != null) {
                    outsourcedProcessDetailItemAdapter?.notifyDataSetChanged()
                }

                outsourcedProcessMoreDetailList.clear()
                if (outsourcedProcessMoreDetailAdapter != null) {
                    outsourcedProcessMoreDetailAdapter?.notifyDataSetChanged()
                }

                outsourcedProcessDetailFilterList.clear()


                listViewBySupplier!!.visibility = View.VISIBLE
                listViewDetail!!.visibility = View.GONE
                listViewMoreDetail!!.visibility = View.GONE

                isOutSourcedInDetail = 0

                val searchIntent = Intent()
                searchIntent.action = Constants.ACTION.ACTION_USER_INPUT_SEARCH
                searchIntent.putExtra("INPUT_NO",
                    barcodeInput!!.text.toString().uppercase(Locale.getDefault())
                )
                outsourcedProcessContext?.sendBroadcast(searchIntent)
            //}




            alertDialogBuilder.dismiss()
        }
        alertDialogBuilder.show()


    }
}