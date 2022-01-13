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
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleObserver
import com.magtonic.magtonicwarehouse.MainActivity
import com.magtonic.magtonicwarehouse.MainActivity.Companion.dbReturnOfGoodsSigned
import com.magtonic.magtonicwarehouse.MainActivity.Companion.isKeyBoardShow
import com.magtonic.magtonicwarehouse.MainActivity.Companion.isReturnOfGoodsInDetail
import com.magtonic.magtonicwarehouse.MainActivity.Companion.returnOfGoodsDetailList
import com.magtonic.magtonicwarehouse.MainActivity.Companion.returnOfGoodsListBySupplier
import com.magtonic.magtonicwarehouse.R
import com.magtonic.magtonicwarehouse.SignActivity
import com.magtonic.magtonicwarehouse.data.*
import com.magtonic.magtonicwarehouse.persistence.ReturnOfGoodsSignedData
import java.util.*

class ReturnOfGoodsFragment : Fragment(), LifecycleObserver {
    private val mTAG = ReturnOfGoodsFragment::class.java.name
    private var returnOfGoodsContext: Context? = null

    private var progressBar: ProgressBar? = null
    private var relativeLayout: RelativeLayout? = null

    private var textViewSupplier: TextView? = null
    private var barcodeInput: EditText? = null
    private var linearLayoutReturnOfGoodsMain: LinearLayout? = null
    private var linearLayoutReturnOfGoods: LinearLayout? = null
    private var linearLayoutReturnOfGoodsHeader: LinearLayout? = null
    private var linearLayoutReturnOfGoodsDetail: LinearLayout? = null
    private var viewLine: View?=null
    private var listViewReturnOfGoods: ListView?= null
    private var listViewReturnOfGoodsDetail: ListView?= null


    private var returnOfGoodsItemAdapter: ReturnOfGoodsItemAdapter? = null
    private var returnOfGoodsDetailItemAdapter: ReturnOfGoodsDetailItemAdapter? = null

    var returnOfGoodsList = ArrayList<ReturnOfGoodsItem>()
    //var returnOfGoodsDetailList = ArrayList<ReturnOfGoodsItemDetail>()
    var returnOfGoodsDetailShowList = ArrayList<ReturnOfGoodsDetailItem>()

    private var mReceiver: BroadcastReceiver? = null
    private var isRegister = false

    private var toastHandle: Toast? = null

    private val colorCodePink = Color.parseColor("#D81B60")

    //private var currentWorkOrder: String = ""
    private var currentSendOrder: String = ""
    private var currentSelectedSupplier: Int = 0

    private var currentSelectSendOrder: Int = -1

    private var storageSpinner: Spinner? = null
    private val storageList = ArrayList<String>()
    private val storageWarehouseList = ArrayList<String>()

    private var storageFilter = ""
    //private var warehouseFilter = ""

    var returnOfGoodsFilterList = ArrayList<ReturnOfGoodsItem>()
    private var currentWarehouse: String = ""

    private var findWarehouseCode: String = ""
    private var findWarehouseName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.e(mTAG, "onCreate")

        returnOfGoodsContext = context

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        Log.d(mTAG, "onCreateView")

        val view = inflater.inflate(R.layout.fragment_return_of_goods, container, false)

        relativeLayout = view.findViewById(R.id.return_of_goods_container)
        barcodeInput = view.findViewById(R.id.editTextReturnOfGoods)
        viewLine = view.findViewById(R.id.viewLineReturnOfGoods)
        linearLayoutReturnOfGoodsMain = view.findViewById(R.id.linearLayoutReturnOfGoodsMain)
        linearLayoutReturnOfGoods = view.findViewById(R.id.linearLayoutReturnOfGoods)
        linearLayoutReturnOfGoodsHeader = view!!.findViewById(R.id.linearLayoutReturnOfGoodsHeader)
        listViewReturnOfGoods = view.findViewById(R.id.listViewReturnOfGoods)

        linearLayoutReturnOfGoodsDetail = view.findViewById(R.id.linearLayoutReturnOfGoodsDetail)
        listViewReturnOfGoodsDetail = view.findViewById(R.id.listViewReturnOfGoodsDetail)
        //linearLayoutDetailHeader = view.findViewById(R.id.linearLayoutDetailHeader)

        //mageViewPrev = view.findViewById(R.id.imageViewPrev)
        //layoutLowerItemHeader = view.findViewById(R.id.layoutLowerItemHeader)
        progressBar = ProgressBar(returnOfGoodsContext, null, android.R.attr.progressBarStyleLarge)
        val params = RelativeLayout.LayoutParams(MainActivity.screenHeight / 4, MainActivity.screenWidth / 4)
        params.addRule(RelativeLayout.CENTER_IN_PARENT)

        val localRelativeLayout: RelativeLayout? = relativeLayout
        if (localRelativeLayout != null) {
            localRelativeLayout.addView(progressBar, params)
        } else {
            Log.e(mTAG, "localRelativeLayout = null")
        }
        progressBar!!.visibility = View.GONE
        textViewSupplier = view.findViewById(R.id.textViewReturnOfGoods)

        //btnOutsourcedProcessMain = view.findViewById(R.id.btnOutsourcedProcessMain)
        //btnOutsourcedProcessLower = view.findViewById(R.id.btnOutsourcedProcessLower)
        //btnUpload = view.findViewById(R.id.btnUpload)
        //buttonTextColor = btnUpload!!.textColors
        //btnSign = view.findViewById(R.id.btnOutsourcedProcessSign)
        //listViewTop = view!!.findViewById(R.id.listViewOutsourcedProcessTop)

        if (returnOfGoodsContext != null) {

            //linearLayoutReturnOfGoodsHeader!!.visibility = View.VISIBLE

            //val item1 = ReturnOfGoodsItem("3","AP41-20100004", "2020/10/06")
            //returnOfGoodsList.add(item1)

            returnOfGoodsItemAdapter = ReturnOfGoodsItemAdapter(returnOfGoodsContext, R.layout.fragment_return_of_goods_item, returnOfGoodsList)
            listViewReturnOfGoods!!.adapter = returnOfGoodsItemAdapter

            returnOfGoodsDetailItemAdapter = ReturnOfGoodsDetailItemAdapter(returnOfGoodsContext, R.layout.fragment_return_of_goods_detail_item, returnOfGoodsDetailShowList)
            listViewReturnOfGoodsDetail!!.adapter = returnOfGoodsDetailItemAdapter
        }

        linearLayoutReturnOfGoodsMain!!.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            linearLayoutReturnOfGoodsMain!!.getWindowVisibleDisplayFrame(r)
            val screenHeight = linearLayoutReturnOfGoodsMain!!.rootView.height
            val keypadHeight = screenHeight - r.bottom
            val temp = screenHeight * 0.15
            Log.e(mTAG, "keypadHeight = $keypadHeight, screenHeight =$screenHeight, screenHeight * 0.15 = $temp")

            isKeyBoardShow = keypadHeight > screenHeight * 0.15

            //MainActivity.isKeyBoardShow = (keypadHeight > screenHeight * 0.15)



            /*if (isKeyBoardShow) {
                if (!barcodeInput!!.isFocused) {
                    val hideIntent = Intent()
                    hideIntent.action = Constants.ACTION.ACTION_HIDE_KEYBOARD
                    outsourcedProcessContext!!.sendBroadcast(hideIntent)
                }

            }*/
        }

        listViewReturnOfGoods!!.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            Log.d(mTAG, "click $position")

            if (currentWarehouse == "") {
                toast(getString(R.string.warehouse_empty_warnning))
            } else {

                storageSpinner!!.visibility = View.INVISIBLE

                currentSelectSendOrder = position

                val moreDetailIntent = Intent()
                moreDetailIntent.action = Constants.ACTION.ACTION_RETURN_OF_GOODS_GET_DETAIL_BY_SEND_ORDER
                if (currentWarehouse != "") {
                    currentSendOrder = returnOfGoodsFilterList[position].getData2()
                    findWarehouseCode = returnOfGoodsFilterList[position].getData4()
                    findWarehouseName = returnOfGoodsFilterList[position].getData5()
                    moreDetailIntent.putExtra("SEND_ORDER", returnOfGoodsFilterList[position].getData2())

                } else {
                    currentSendOrder = returnOfGoodsList[position].getData2()
                    findWarehouseCode = returnOfGoodsList[position].getData4()
                    findWarehouseName = returnOfGoodsList[position].getData5()
                    moreDetailIntent.putExtra("SEND_ORDER", returnOfGoodsList[position].getData2())
                }
                returnOfGoodsContext?.sendBroadcast(moreDetailIntent)
            }



            /*if (!returnOfGoodsList[position].getIsSigned()) {
                currentSelectSendOrder = position

                currentSendOrder = returnOfGoodsList[position].getData2()
                findWarehouseCode = returnOfGoodsList[position].getData4()
                findWarehouseName = returnOfGoodsList[position].getData5()
                //progressBar!!.indeterminateTintList = ColorStateList.valueOf(colorCodePink)
                //progressBar!!.visibility = View.VISIBLE

                if (returnOfGoodsDetailShowList.size > 0) {
                    returnOfGoodsDetailShowList.clear()
                }

                if (returnOfGoodsDetailItemAdapter != null) {
                    returnOfGoodsDetailItemAdapter!!.notifyDataSetChanged()
                }

                val moreDetailIntent = Intent()
                moreDetailIntent.action = Constants.ACTION.ACTION_RETURN_OF_GOODS_GET_DETAIL_BY_SEND_ORDER
                //moreDetailIntent.putExtra("SEND_ORDER", barcodeInput!!.text.toString())
                moreDetailIntent.putExtra("SEND_ORDER", returnOfGoodsList[position].getData2())
                returnOfGoodsContext?.sendBroadcast(moreDetailIntent)
            } else {
                toast(getString(R.string.outsourced_process_sign_is_signed_already))
            }*/


        }

        storageSpinner = view.findViewById(R.id.returnStorageSpinner)
        val storageAdapter: ArrayAdapter<String> = ArrayAdapter(returnOfGoodsContext as Context, R.layout.myspinner, storageWarehouseList)
        storageSpinner?.adapter = storageAdapter

        storageSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                Log.e(mTAG, "onNothingSelected")
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                Log.e(mTAG, "position = $position")

                returnOfGoodsFilterList.clear()
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
                    //val outsourcedSignedDataList = dbOustsourcedSigned!!.outsourcedSignedDataDao().getOutsourcedSignedBySendOrder(currentSendOrder) as ArrayList<OutsourcedSignedData>

                    for (data in returnOfGoodsList) {
                        if (data.getData4() == storageFilter) {
                            returnOfGoodsFilterList.add(data)
                        }
                    }



                    returnOfGoodsItemAdapter = ReturnOfGoodsItemAdapter(returnOfGoodsContext, R.layout.fragment_return_of_goods_item, returnOfGoodsFilterList)
                    listViewReturnOfGoods!!.adapter = returnOfGoodsItemAdapter
                } else {
                    returnOfGoodsItemAdapter = ReturnOfGoodsItemAdapter(returnOfGoodsContext, R.layout.fragment_return_of_goods_item, returnOfGoodsList)
                    listViewReturnOfGoods!!.adapter = returnOfGoodsItemAdapter
                }


            }

        }
        /*linearLayoutIssuanceLookupMain!!.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            linearLayoutIssuanceLookupMain!!.getWindowVisibleDisplayFrame(r)
            val screenHeight = linearLayoutIssuanceLookupMain!!.rootView.height
            val keypadHeight = screenHeight - r.bottom
            //val temp = screenHeight * 0.15
            //Log.e(mTAG, "keypadHeight = $keypadHeight, screenHeight =$screenHeight, screenHeight * 0.15 = $temp")
            MainActivity.isKeyBoardShow = (keypadHeight > screenHeight * 0.15)

        }*/





        barcodeInput!!.setOnEditorActionListener { _, actionId, _ ->

            when(actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    Log.e(mTAG, "IME_ACTION_DONE")


                    viewLine!!.visibility = View.GONE

                    linearLayoutReturnOfGoodsHeader!!.visibility = View.GONE

                    progressBar!!.indeterminateTintList = ColorStateList.valueOf(colorCodePink)
                    progressBar!!.visibility = View.VISIBLE

                    /*outsourcedProcessDetailList.clear()
                    if (outsourcedProcessOrderDetailItemAdapter != null) {
                        outsourcedProcessOrderDetailItemAdapter?.notifyDataSetChanged()
                    }*/
                    returnOfGoodsList.clear()
                    if (returnOfGoodsItemAdapter != null) {
                        returnOfGoodsItemAdapter?.notifyDataSetChanged()
                    }

                    returnOfGoodsDetailShowList.clear()
                    if (returnOfGoodsDetailItemAdapter != null) {
                        returnOfGoodsDetailItemAdapter?.notifyDataSetChanged()
                    }



                    linearLayoutReturnOfGoods!!.visibility = View.VISIBLE
                    linearLayoutReturnOfGoodsDetail!!.visibility = View.GONE

                    isReturnOfGoodsInDetail = 0

                    val searchIntent = Intent()
                    searchIntent.action = Constants.ACTION.ACTION_USER_INPUT_SEARCH
                    searchIntent.putExtra("INPUT_NO",
                        barcodeInput!!.text.toString().uppercase(Locale.getDefault())
                    )
                    returnOfGoodsContext?.sendBroadcast(searchIntent)

                    true
                }

                EditorInfo.IME_ACTION_GO -> {
                    Log.e(mTAG, "IME_ACTION_GO")
                    true
                }

                EditorInfo.IME_ACTION_NEXT -> {
                    Log.e(mTAG, "IME_ACTION_NEXT")

                    viewLine!!.visibility = View.GONE

                    linearLayoutReturnOfGoodsHeader!!.visibility = View.GONE

                    progressBar!!.indeterminateTintList = ColorStateList.valueOf(colorCodePink)
                    progressBar!!.visibility = View.VISIBLE

                    /*outsourcedProcessDetailList.clear()
                    if (outsourcedProcessOrderDetailItemAdapter != null) {
                        outsourcedProcessOrderDetailItemAdapter?.notifyDataSetChanged()
                    }*/
                    returnOfGoodsList.clear()
                    if (returnOfGoodsItemAdapter != null) {
                        returnOfGoodsItemAdapter?.notifyDataSetChanged()
                    }

                    returnOfGoodsDetailShowList.clear()
                    if (returnOfGoodsDetailItemAdapter != null) {
                        returnOfGoodsDetailItemAdapter?.notifyDataSetChanged()
                    }



                    linearLayoutReturnOfGoods!!.visibility = View.VISIBLE
                    linearLayoutReturnOfGoodsDetail!!.visibility = View.GONE

                    isReturnOfGoodsInDetail = 0

                    val searchIntent = Intent()
                    searchIntent.action = Constants.ACTION.ACTION_USER_INPUT_SEARCH
                    searchIntent.putExtra("INPUT_NO",
                        barcodeInput!!.text.toString().uppercase(Locale.getDefault())
                    )
                    returnOfGoodsContext?.sendBroadcast(searchIntent)

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

        /*imageViewPrev!!.setOnClickListener {
            val backIntent = Intent()
            backIntent.action = Constants.ACTION.ACTION_OUTSOURCED_PROCESS_BACK_TO_SUPPLIER_LIST
            outsourcedProcessContext!!.sendBroadcast(backIntent)

            val hideIntent = Intent()
            hideIntent.action = Constants.ACTION.ACTION_OUTSOURCED_PROCESS_HIDE_FAB_BACK
            outsourcedProcessContext!!.sendBroadcast(hideIntent)
        }*/


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

                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_SERVER_ERROR, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_SERVER_ERROR")

                        progressBar!!.visibility = View.GONE



                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_RETURN_OF_GOODS_SCAN_BARCODE, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_RETURN_OF_GOODS_SCAN_BARCODE")


                        val barcode: String = intent.getStringExtra("BARCODE") as String
                        Log.e(mTAG, "barcode = $barcode")
                        barcodeInput!!.setText(barcode)


                        textViewSupplier!!.visibility = View.GONE
                        textViewSupplier!!.text = ""

                        linearLayoutReturnOfGoodsHeader!!.visibility = View.GONE


                        viewLine!!.visibility = View.GONE

                        progressBar!!.indeterminateTintList = ColorStateList.valueOf(colorCodePink)
                        progressBar!!.visibility = View.VISIBLE

                        returnOfGoodsList.clear()
                        if (returnOfGoodsItemAdapter != null) {
                            returnOfGoodsItemAdapter?.notifyDataSetChanged()
                        }

                        returnOfGoodsDetailShowList.clear()
                        if (returnOfGoodsDetailItemAdapter != null) {
                            returnOfGoodsDetailItemAdapter?.notifyDataSetChanged()
                        }



                        linearLayoutReturnOfGoods!!.visibility = View.VISIBLE
                        linearLayoutReturnOfGoodsDetail!!.visibility = View.GONE

                        isReturnOfGoodsInDetail = 0

                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_RETURN_OF_GOODS_FRAGMENT_REFRESH, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_RETURN_OF_GOODS_FRAGMENT_REFRESH")

                        progressBar!!.visibility = View.GONE

                        if (isKeyBoardShow) {
                            val hideIntent = Intent()
                            hideIntent.action = Constants.ACTION.ACTION_HIDE_KEYBOARD
                            returnOfGoodsContext!!.sendBroadcast(hideIntent)
                        }

                        Log.e(mTAG, "returnOfGoodsListBySupplier.size = ${returnOfGoodsListBySupplier.size}")
                        storageList.clear()
                        storageWarehouseList.clear()
                        storageAdapter.notifyDataSetChanged()

                        if (returnOfGoodsListBySupplier.size == 1 ) {
                            if (returnOfGoodsListBySupplier[0].result == "1") {
                                linearLayoutReturnOfGoodsHeader!!.visibility = View.GONE
                            } else {
                                //linearLayoutIssuanceLookupHeader!!.visibility = View.VISIBLE
                                linearLayoutReturnOfGoodsHeader!!.visibility = View.VISIBLE

                                val rjReturnOfGoodsItem =  returnOfGoodsListBySupplier[0]

                                val returnOfGoodsItem = ReturnOfGoodsItem(rjReturnOfGoodsItem.data1, rjReturnOfGoodsItem.data2, rjReturnOfGoodsItem.data3, rjReturnOfGoodsItem.data4, rjReturnOfGoodsItem.data5)
                                returnOfGoodsList.add(returnOfGoodsItem)
                            }


                        } else if (returnOfGoodsListBySupplier.size > 1) {
                            linearLayoutReturnOfGoodsHeader!!.visibility = View.VISIBLE
                            for (rjReturnOfGoodsItem in returnOfGoodsListBySupplier) {

                                val returnOfGoodsItem = ReturnOfGoodsItem(rjReturnOfGoodsItem.data1, rjReturnOfGoodsItem.data2, rjReturnOfGoodsItem.data3, rjReturnOfGoodsItem.data4, rjReturnOfGoodsItem.data5)
                                returnOfGoodsList.add(returnOfGoodsItem)

                            }
                        } else {
                            Log.e(mTAG, "size = 0")
                        }



                        if (returnOfGoodsItemAdapter != null) {
                            returnOfGoodsItemAdapter?.notifyDataSetChanged()
                        }

                        //find return goods signed
                        for (i in 0 until returnOfGoodsList.size) {
                            //val outsourcedSignedData = dbOustsourcedSigned!!.outsourcedSignedDataDao().getOutsourcedSignedBySendOrder(outsourcedProcessListBySupplier[i].getData2())
                            //var outsourcedSignedDataList: ArrayList<OutsourcedSignedData> ?= null
                            val returnOfGoodsSignedDataList = dbReturnOfGoodsSigned!!.returnOfGoodsSignedDataDao().getReturnOfGoodsSignedBySendOrder(returnOfGoodsList[i].getData2()) as ArrayList<ReturnOfGoodsSignedData>

                            Log.e(mTAG,"====>Same SendOrder but different warehouse list size = ${returnOfGoodsSignedDataList.size}")

                            var signedCount = 0
                            for (j in 0 until returnOfGoodsSignedDataList.size) {
                                if (returnOfGoodsSignedDataList[j].getSendOrder() == returnOfGoodsList[i].getData2()) {
                                    Log.e(mTAG, "sendOrder+warehouse: ${returnOfGoodsSignedDataList[j].getSendOrderWareHouse()}")
                                    returnOfGoodsList[i].setIsSigned(true)
                                    signedCount += 1
                                }
                            }
                            returnOfGoodsList[i].setSignedNum(signedCount)
                        }

                        //find storage and warehouse
                        if (returnOfGoodsList.size > 0) {
                            storageList.clear()
                            storageWarehouseList.clear()
                            storageList.add(getString(R.string.please_select))
                            storageWarehouseList.add(getString(R.string.please_select))

                            for (rjReturnOfGoods in returnOfGoodsListBySupplier) {
                                var foundStorage = false
                                //var foundWarehouse = false
                                for (storage in storageList) {
                                    if (rjReturnOfGoods.data4 == storage) {
                                        foundStorage = true
                                        break
                                    }
                                }

                                if (!foundStorage) {
                                    storageList.add(rjReturnOfGoods.data4)
                                    val combineString = rjReturnOfGoods.data4+" - "+rjReturnOfGoods.data5
                                    /*if (outsourcedSignedDataList.size > 0) {
                                        for (i in 0 until outsourcedSignedDataList.size) {
                                            if (outsourcedSignedDataList[i].getWareHouse() == rjOutSourceProcessed.data9) {
                                                combineString = getString(R.string.outsourced_signed)+"-"+combineString
                                            }
                                        }
                                    }*/
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


                        }

                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_RETURN_OF_GOODS_FRAGMENT_DETAIL_REFRESH, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_RETURN_OF_GOODS_FRAGMENT_DETAIL_REFRESH")

                        //val sendOrder = intent.getStringExtra("SEND_ORDER")
                        //val idxString = intent.getStringExtra("INDEX")

                        //val idx = idxString?.toInt()


                        if (isKeyBoardShow) {
                            val hideIntent = Intent()
                            hideIntent.action = Constants.ACTION.ACTION_HIDE_KEYBOARD
                            returnOfGoodsContext!!.sendBroadcast(hideIntent)
                        }

                        returnOfGoodsDetailShowList.clear()

                        if (returnOfGoodsDetailList.size > 0) {
                            //val item0 = IssuanceLookupDetailItem("發料單號", sendOrder as String)
                            //issuanceLookupDetailList.add(item0)
                            val item1 = ReturnOfGoodsDetailItem("退貨單項次", returnOfGoodsDetailList[0].data1)
                            returnOfGoodsDetailShowList.add(item1)
                            val item2 = ReturnOfGoodsDetailItem("採購單號", returnOfGoodsDetailList[0].data2)
                            returnOfGoodsDetailShowList.add(item2)
                            val item3 = ReturnOfGoodsDetailItem("採購單項次", returnOfGoodsDetailList[0].data3)
                            returnOfGoodsDetailShowList.add(item3)
                            val item4 = ReturnOfGoodsDetailItem("料件編號", returnOfGoodsDetailList[0].data4)
                            returnOfGoodsDetailShowList.add(item4)
                            val item5 = ReturnOfGoodsDetailItem("數量", returnOfGoodsDetailList[0].data5)
                            returnOfGoodsDetailShowList.add(item5)
                            val item6 = ReturnOfGoodsDetailItem("單位", returnOfGoodsDetailList[0].data6)
                            returnOfGoodsDetailShowList.add(item6)
                            val item7 = ReturnOfGoodsDetailItem("品名", returnOfGoodsDetailList[0].data7)
                            returnOfGoodsDetailShowList.add(item7)
                            val item8 = ReturnOfGoodsDetailItem("規格", returnOfGoodsDetailList[0].data8)
                            returnOfGoodsDetailShowList.add(item8)
                            val item9 = ReturnOfGoodsDetailItem("倉庫代號", findWarehouseCode)
                            returnOfGoodsDetailShowList.add(item9)
                            val item10 = ReturnOfGoodsDetailItem("倉庫名稱", findWarehouseName)
                            returnOfGoodsDetailShowList.add(item10)
                        }

                        //linearLayoutDetailHeader!!.visibility = View.INVISIBLE

                        //viewLine!!.visibility = View.VISIBLE
                        linearLayoutReturnOfGoods!!.visibility = View.GONE
                        linearLayoutReturnOfGoodsDetail!!.visibility = View.VISIBLE

                        isReturnOfGoodsInDetail = 1

                        if (returnOfGoodsDetailItemAdapter != null) {
                            returnOfGoodsDetailItemAdapter?.notifyDataSetChanged()
                        }

                        //val showIntent = Intent()
                        //showIntent.action = Constants.ACTION.ACTION_ISSUANCE_LOOKUP_SHOW_FAB_BACK
                        //returnOfGoodsContext!!.sendBroadcast(showIntent)

                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_RETURN_OF_GOODS_BACK_TO_LIST, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_RETURN_OF_GOODS_BACK_TO_LIST")

                        storageSpinner!!.visibility = View.VISIBLE
                        linearLayoutReturnOfGoods!!.visibility = View.VISIBLE
                        linearLayoutReturnOfGoodsDetail!!.visibility = View.GONE

                        isReturnOfGoodsInDetail = 0



                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_OUTSOURCED_PROCESS_SHOW_SUPPLIER_DIALOG, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_OUTSOURCED_PROCESS_SHOW_SUPPLIER_DIALOG")

                        showSupplierDialog()
                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_RETURN_OF_GOODS_SHOW_SIGN_DIALOG_ACTION, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_RETURN_OF_GOODS_SHOW_SIGN_DIALOG_ACTION")

                        showSignDialog()
                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_RETURN_OF_GOODS_SIGN_UPLOAD_SUCCESS, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_RETURN_OF_GOODS_SIGN_UPLOAD_SUCCESS")

                        val sendOrder = intent.getStringExtra("SEND_ORDER")

                        toast(getString(R.string.outsourced_process_sign_confirm, sendOrder))

                        Log.d(mTAG, "sendOrder = $sendOrder")

                        //add to sqlite
                        var returnOfGoodsSignedData: ReturnOfGoodsSignedData
                        if (dbReturnOfGoodsSigned != null) {
                            val combine = currentSendOrder + currentWarehouse
                            returnOfGoodsSignedData = dbReturnOfGoodsSigned!!.returnOfGoodsSignedDataDao().getReturnOfGoodsSignedBySendOrderWareHouse(combine)
                            //val c  = Calendar.getInstance(Locale.getDefault())
                            //val dateTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                            //val dateTime = SimpleDateFormat("HH:mm", Locale.getDefault())
                            //val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            //val dateString = date.format(c.time)
                            //val dateTimeString = dateTime.format(c.time)
                            //Log.e(mTAG, "outsourcedSignedData: ${outsourcedSignedData.getSendOrderWareHouse()}, ${outsourcedSignedData.getSendOrder()}, ${outsourcedSignedData.getWareHouse()}")
                            val timeStamp= System.currentTimeMillis()
                            if (returnOfGoodsSignedData != null) {
                                Log.e(mTAG, "update signed!")
                                //val combine = currentSendOrder + currentWarehouse
                                returnOfGoodsSignedData.setSendOrderWareHouse(combine)
                                returnOfGoodsSignedData.setSendOrder(currentSendOrder)
                                returnOfGoodsSignedData.setWareHouse(currentWarehouse)
                                returnOfGoodsSignedData.setTimeStamp(timeStamp)
                                dbReturnOfGoodsSigned!!.returnOfGoodsSignedDataDao().update(returnOfGoodsSignedData)
                            } else {
                                Log.e(mTAG, "add new signed!")
                                //val combine = currentSendOrder + currentWarehouse
                                returnOfGoodsSignedData = ReturnOfGoodsSignedData(combine, currentSendOrder, currentWarehouse, timeStamp)
                                dbReturnOfGoodsSigned!!.returnOfGoodsSignedDataDao().insert(returnOfGoodsSignedData)

                                val returnOfGoodsSignedDataList = dbReturnOfGoodsSigned!!.returnOfGoodsSignedDataDao().getAll() as ArrayList<ReturnOfGoodsSignedData>
                                Log.e(mTAG, "==>returnOfGoodsSignedDataList = ${returnOfGoodsSignedDataList.size}")
                                //for (i in 0 until outsourcedSignedDataList.size) {
                                //    Log.e(mTAG, "outsourcedSignedDataList[$i] = ${outsourcedSignedDataList[i].getSendOrderWareHouse()}")
                                //}
                            }

                        }

                        //update from sqlite and update view
                        for (i in 0 until returnOfGoodsList.size) {
                            //val outsourcedSignedData = dbOustsourcedSigned!!.outsourcedSignedDataDao().getOutsourcedSignedBySendOrder(outsourcedProcessListBySupplier[i].getData2())
                            //var outsourcedSignedDataList: ArrayList<OutsourcedSignedData> ?= null
                            val returnOfGoodsSignedDataList = dbReturnOfGoodsSigned!!.returnOfGoodsSignedDataDao().getReturnOfGoodsSignedBySendOrder(returnOfGoodsList[i].getData2()) as ArrayList<ReturnOfGoodsSignedData>

                            Log.e(mTAG,"====>Same SendOrder but different warehouse list size = ${returnOfGoodsSignedDataList.size}")

                            var signedCount = 0
                            for (j in 0 until returnOfGoodsSignedDataList.size) {
                                if (returnOfGoodsSignedDataList[j].getSendOrder() == returnOfGoodsList[i].getData2()) {
                                    Log.e(mTAG, "sendOrder+warehouse: ${returnOfGoodsSignedDataList[j].getSendOrderWareHouse()}")
                                    returnOfGoodsList[i].setIsSigned(true)
                                    signedCount += 1
                                }
                            }
                            returnOfGoodsList[i].setSignedNum(signedCount)
                            Log.e(mTAG, "returnOfGoodsList[$i] = ${returnOfGoodsList[i].getIsSigned()}")
                        }


                        /*for (i in 0 until returnOfGoodsList.size) {
                            Log.e(mTAG, returnOfGoodsList[i].getData1())
                            if (returnOfGoodsList[i].getData2() == sendOrder) {
                                returnOfGoodsList[i].setIsSigned(true)
                            }
                            Log.e(mTAG, "returnOfGoodsList[$i] = ${returnOfGoodsList[i].getIsSigned()}")
                        }*/

                        listViewReturnOfGoods!!.invalidateViews()

                        val backIntent = Intent()
                        backIntent.action = Constants.ACTION.ACTION_RETURN_OF_GOODS_BACK_TO_LIST
                        returnOfGoodsContext!!.sendBroadcast(backIntent)


                        val hideIntent = Intent()
                        hideIntent.action = Constants.ACTION.ACTION_OUTSOURCED_PROCESS_HIDE_FAB_BACK
                        returnOfGoodsContext!!.sendBroadcast(hideIntent)
                    }

                }
            }
        }

        if (!isRegister) {
            filter = IntentFilter()
            filter.addAction(Constants.ACTION.ACTION_BARCODE_NULL)
            filter.addAction(Constants.ACTION.ACTION_NETWORK_FAILED)
            filter.addAction(Constants.ACTION.ACTION_CONNECTION_TIMEOUT)
            filter.addAction(Constants.ACTION.ACTION_RETURN_OF_GOODS_SCAN_BARCODE)
            filter.addAction(Constants.ACTION.ACTION_RETURN_OF_GOODS_FRAGMENT_REFRESH)
            filter.addAction(Constants.ACTION.ACTION_RETURN_OF_GOODS_FRAGMENT_DETAIL_REFRESH)
            filter.addAction(Constants.ACTION.ACTION_RETURN_OF_GOODS_BACK_TO_LIST)

            filter.addAction(Constants.ACTION.ACTION_RETURN_OF_GOODS_SHOW_SIGN_DIALOG_ACTION)
            filter.addAction(Constants.ACTION.ACTION_RETURN_OF_GOODS_SIGN_UPLOAD_SUCCESS)
            filter.addAction(Constants.ACTION.ACTION_OUTSOURCED_PROCESS_SHOW_SUPPLIER_DIALOG)
            returnOfGoodsContext?.registerReceiver(mReceiver, filter)
            isRegister = true
            Log.d(mTAG, "registerReceiver mReceiver")
        }

        return view
    }

    override fun onDestroyView() {
        Log.i(mTAG, "onDestroy")

        val hideIntent = Intent()
        hideIntent.action = Constants.ACTION.ACTION_OUTSOURCED_PROCESS_HIDE_FAB_BACK
        returnOfGoodsContext!!.sendBroadcast(hideIntent)

        if (isRegister && mReceiver != null) {
            try {
                returnOfGoodsContext!!.unregisterReceiver(mReceiver)
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

        val toast = Toast.makeText(returnOfGoodsContext, HtmlCompat.fromHtml("<h1>$message</h1>", HtmlCompat.FROM_HTML_MODE_COMPACT), Toast.LENGTH_SHORT)
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
        val promptView = View.inflate(returnOfGoodsContext, R.layout.return_of_goods_sign_dialog, null)

        val alertDialogBuilder = AlertDialog.Builder(returnOfGoodsContext).create()
        alertDialogBuilder.setView(promptView)

        //final EditText editFileName = (EditText) promptView.findViewById(R.id.editFileName);
        //val textViewMsg = promptView.findViewById<TextView>(R.id.textViewOutsourcedProcessDialogMsg)
        //val textViewOutsourcedProcessSendOrderHeader = promptView.findViewById<TextView>(R.id.textViewOutsourcedProcessSendOrderHeader)
        val textViewReturnOfGoodsOrderContent = promptView.findViewById<TextView>(R.id.textViewReturnOfGoodsOrderContent)
        val textViewReturnOfGoodsTypeContent = promptView.findViewById<TextView>(R.id.textViewReturnOfGoodsTypeContent)
        val textViewReturnOfGoodsDateContent = promptView.findViewById<TextView>(R.id.textViewReturnOfGoodsDateContent)
        val btnCancel = promptView.findViewById<Button>(R.id.btnDialogCancel)
        val btnConfirm = promptView.findViewById<Button>(R.id.btnDialogConfirm)

        //Log.e(mTAG, "send No. = $currentSendOrder")

        if (currentWarehouse != "") {
            textViewReturnOfGoodsOrderContent.text = returnOfGoodsFilterList[currentSelectSendOrder].getData2()
            textViewReturnOfGoodsTypeContent.text = returnOfGoodsFilterList[currentSelectSendOrder].getData1()
            textViewReturnOfGoodsDateContent.text = returnOfGoodsFilterList[currentSelectSendOrder].getData3()

        } else {
            textViewReturnOfGoodsOrderContent.text = returnOfGoodsList[currentSelectSendOrder].getData2()
            textViewReturnOfGoodsTypeContent.text = returnOfGoodsList[currentSelectSendOrder].getData1()
            textViewReturnOfGoodsDateContent.text = returnOfGoodsList[currentSelectSendOrder].getData3()
        }


        //textViewShouldContent.text = outsourcedProcessLowerList[position].getContentStatic()
        //textViewActualContent.inputType = (InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL)
        //textViewActualContent.setText(outsourcedProcessLowerList[position].getContentDynamic())

        btnCancel.text = getString(R.string.cancel)
        btnConfirm.text = getString(R.string.confirm)

        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
        btnCancel!!.setOnClickListener {


            alertDialogBuilder.dismiss()
        }
        btnConfirm!!.setOnClickListener {

            val intent = Intent(returnOfGoodsContext, SignActivity::class.java)
            intent.putExtra("SEND_ORDER", currentSendOrder)
            intent.putExtra("TITLE", getString(R.string.nav_return_of_goods))
            intent.putExtra("WAREHOUSE", currentWarehouse)
            intent.putExtra("SEND_FRAGMENT", "RETURN_OF_GOODS")
            intent.putExtra("TYPE", textViewReturnOfGoodsTypeContent.text)
            intent.putExtra("DATE", textViewReturnOfGoodsDateContent.text)
            startActivity(intent)


            alertDialogBuilder.dismiss()
        }
        alertDialogBuilder.show()


    }

    private fun showSupplierDialog() {

        // get prompts.xml view
        /*LayoutInflater layoutInflater = LayoutInflater.from(Nfc_read_app.this);
        View promptView = layoutInflater.inflate(R.layout.input_dialog, null);*/
        val promptView = View.inflate(returnOfGoodsContext, R.layout.outsourced_process_supplier_dialog, null)

        val alertDialogBuilder = AlertDialog.Builder(returnOfGoodsContext).create()
        alertDialogBuilder.setView(promptView)

        //final EditText editFileName = (EditText) promptView.findViewById(R.id.editFileName);
        //val textViewMsg = promptView.findViewById<TextView>(R.id.textViewOutsourcedProcessDialogMsg)
        val editTextFilter = promptView.findViewById<EditText>(R.id.editTextOutsourcedSupplierDialogUserInput)
        val spinnerSupplier = promptView.findViewById<Spinner>(R.id.spinnerSupplier)
        //val textViewOutsourcedProcessWorkOrderHeader = promptView.findViewById<TextView>(R.id.textViewOutsourcedProcessWorkOrderHeader)
        //val textViewOutsourcedProcessWorkOrderContent = promptView.findViewById<TextView>(R.id.textViewOutsourcedProcessWorkOrderContent)
        val btnCancel = promptView.findViewById<Button>(R.id.btnOutSourcedDialogCancel)
        val btnConfirm = promptView.findViewById<Button>(R.id.btnOutSourcedDialogConfirm)

        var found = false
        val outsourcedSupplierNameFilterList = ArrayList<String>()

        val mTextWatcher: TextWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                //Log.e(mTAG, "afterTextChanged")
            }
            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int,
                after: Int
            ) {
                //Log.e(mTAG, "beforeTextChanged")
            }

            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int,
                count: Int
            ) {
                Log.e(mTAG, "onTextChanged")
                Log.e(mTAG, "editTextFilter.text = ${editTextFilter.text}")

                outsourcedSupplierNameFilterList.clear()

                for (i in 0 until MainActivity.outsourcedSupplierNameList.size) {
                    if (MainActivity.outsourcedSupplierNameList[i].contains(editTextFilter.text)) {
                        outsourcedSupplierNameFilterList.add(MainActivity.outsourcedSupplierNameList[i])
                        found = true
                    }
                }

                if (found) {
                    val adapter: ArrayAdapter<String> = ArrayAdapter(returnOfGoodsContext as Context, R.layout.myspinner, outsourcedSupplierNameFilterList)
                    spinnerSupplier.adapter = adapter
                } else {
                    val adapter: ArrayAdapter<String> = ArrayAdapter(returnOfGoodsContext as Context, R.layout.myspinner,
                        MainActivity.outsourcedSupplierNameList
                    )
                    spinnerSupplier.adapter = adapter
                }


            }
        }
        editTextFilter.addTextChangedListener(mTextWatcher)

        val adapter: ArrayAdapter<String> = ArrayAdapter(returnOfGoodsContext as Context, R.layout.myspinner,
            MainActivity.outsourcedSupplierNameList
        )
        spinnerSupplier.adapter = adapter

        spinnerSupplier.setSelection(currentSelectedSupplier)

        spinnerSupplier?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (found) {
                    Log.e(mTAG, "found = $found, name = ${outsourcedSupplierNameFilterList[position]}")
                    barcodeInput!!.setText(MainActivity.outsourcedSupplierHashMap[outsourcedSupplierNameFilterList[position]])

                    for (i in 0 until MainActivity.outsourcedSupplierNameList.size) {
                        if (MainActivity.outsourcedSupplierNameList[i] == outsourcedSupplierNameFilterList[position]) {
                            currentSelectedSupplier = i
                        }
                    }
                } else {
                    barcodeInput!!.setText(MainActivity.outsourcedSupplierHashMap[MainActivity.outsourcedSupplierNameList[position]])
                    currentSelectedSupplier = position
                }

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

            textViewSupplier!!.visibility = View.VISIBLE
            textViewSupplier!!.text = MainActivity.outsourcedSupplierNameList[currentSelectedSupplier]

            linearLayoutReturnOfGoodsHeader!!.visibility = View.GONE
            //linearLayoutDetailHeader!!.visibility = View.GONE
            viewLine!!.visibility = View.GONE

            progressBar!!.indeterminateTintList = ColorStateList.valueOf(colorCodePink)
            progressBar!!.visibility = View.VISIBLE


            returnOfGoodsList.clear()
            if (returnOfGoodsItemAdapter != null) {
                returnOfGoodsItemAdapter?.notifyDataSetChanged()
            }

            returnOfGoodsDetailShowList.clear()
            if (returnOfGoodsDetailItemAdapter != null) {
                returnOfGoodsDetailItemAdapter?.notifyDataSetChanged()
            }

            isReturnOfGoodsInDetail  = 0

            val searchIntent = Intent()
            searchIntent.action = Constants.ACTION.ACTION_USER_INPUT_SEARCH
            searchIntent.putExtra("INPUT_NO",
                barcodeInput!!.text.toString().uppercase(Locale.getDefault())
            )
            returnOfGoodsContext?.sendBroadcast(searchIntent)



            alertDialogBuilder.dismiss()
        }
        alertDialogBuilder.show()


    }
}