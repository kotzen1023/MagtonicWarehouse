package com.magtonic.magtonicwarehouse.fragment

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
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import com.magtonic.magtonicwarehouse.MainActivity
import com.magtonic.magtonicwarehouse.MainActivity.Companion.isIssuanceLookupDetail
import com.magtonic.magtonicwarehouse.R
import com.magtonic.magtonicwarehouse.data.*
import java.util.*

class IssuanceLookupFragment : Fragment() {
    private val mTAG = IssuanceLookupFragment::class.java.name
    private var issuanceLookupContext: Context? = null

    private var progressBar: ProgressBar? = null
    private var relativeLayout: RelativeLayout? = null

    private var barcodeInput: EditText? = null
    private var linearLayoutIssuanceLookupMain: LinearLayout? = null
    private var linearLayoutIssuanceLookup: LinearLayout? = null
    private var linearLayoutIssuanceLookupHeader: LinearLayout? = null
    private var linearLayoutIssuanceLookupDetail: LinearLayout? = null
    private var viewLine: View?=null
    private var listViewIssuanceLookup: ListView?= null
    private var listViewIssuanceLookupDetail: ListView?= null


    private var issuanceLookupItemAdapter: IssuanceLookupItemAdapter? = null
    private var issuanceLookupDetailItemAdapter: IssuanceLookupDetailItemAdapter? = null

    var issuanceLookupList = ArrayList<IssuanceLookupItem>()
    var issuanceLookupDetailList = ArrayList<IssuanceLookupDetailItem>()

    private var mReceiver: BroadcastReceiver? = null
    private var isRegister = false

    private var toastHandle: Toast? = null

    private val colorCodePink = Color.parseColor("#D81B60")

    //private var currentWorkOrder: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.e(mTAG, "onCreate")

        issuanceLookupContext = context

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        Log.d(mTAG, "onCreateView")

        val view = inflater.inflate(R.layout.fragment_issuance_lookup, container, false)

        relativeLayout = view.findViewById(R.id.issuance_lookup_container)
        barcodeInput = view.findViewById(R.id.editTextIssuanceLookup)
        viewLine = view.findViewById(R.id.viewLineIssuanceLookup)
        linearLayoutIssuanceLookupMain = view.findViewById(R.id.linearLayoutIssuanceLookupMain)
        linearLayoutIssuanceLookup = view.findViewById(R.id.linearLayoutIssuanceLookup)
        linearLayoutIssuanceLookupHeader = view!!.findViewById(R.id.linearLayoutIssuanceLookupHeader)
        listViewIssuanceLookup = view.findViewById(R.id.listViewIssuanceLookup)

        linearLayoutIssuanceLookupDetail = view.findViewById(R.id.linearLayoutIssuanceLookupDetail)
        listViewIssuanceLookupDetail = view.findViewById(R.id.listViewIssuanceLookupDetail)
        //linearLayoutDetailHeader = view.findViewById(R.id.linearLayoutDetailHeader)

        //mageViewPrev = view.findViewById(R.id.imageViewPrev)
        //layoutLowerItemHeader = view.findViewById(R.id.layoutLowerItemHeader)
        progressBar = ProgressBar(issuanceLookupContext, null, android.R.attr.progressBarStyleLarge)
        val params = RelativeLayout.LayoutParams(MainActivity.screenHeight / 4, MainActivity.screenWidth / 4)
        params.addRule(RelativeLayout.CENTER_IN_PARENT)

        val localRelativeLayout: RelativeLayout? = relativeLayout
        if (localRelativeLayout != null) {
            localRelativeLayout.addView(progressBar, params)
        } else {
            Log.e(mTAG, "localRelativeLayout = null")
        }
        progressBar!!.visibility = View.GONE
        //textViewSupplier = view.findViewById(R.id.textViewOutsourcedProcess)

        //btnOutsourcedProcessMain = view.findViewById(R.id.btnOutsourcedProcessMain)
        //btnOutsourcedProcessLower = view.findViewById(R.id.btnOutsourcedProcessLower)
        //btnUpload = view.findViewById(R.id.btnUpload)
        //buttonTextColor = btnUpload!!.textColors
        //btnSign = view.findViewById(R.id.btnOutsourcedProcessSign)
        //listViewTop = view!!.findViewById(R.id.listViewOutsourcedProcessTop)

        if (issuanceLookupContext != null) {

            issuanceLookupItemAdapter = IssuanceLookupItemAdapter(issuanceLookupContext, R.layout.fragment_issuance_lookup_item, issuanceLookupList)
            listViewIssuanceLookup!!.adapter = issuanceLookupItemAdapter

            issuanceLookupDetailItemAdapter = IssuanceLookupDetailItemAdapter(issuanceLookupContext, R.layout.fragment_issuance_lookup_detail_item, issuanceLookupDetailList)
            listViewIssuanceLookupDetail!!.adapter = issuanceLookupDetailItemAdapter
        }

        listViewIssuanceLookup!!.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            Log.d(mTAG, "click $position")

            val moreDetailIntent = Intent()
            moreDetailIntent.action = Constants.ACTION.ACTION_ISSUANCE_LOOKUP_FRAGMENT_DETAIL_REFRESH
            //moreDetailIntent.putExtra("SEND_ORDER", barcodeInput!!.text.toString())
            moreDetailIntent.putExtra("INDEX", position.toString())
            issuanceLookupContext?.sendBroadcast(moreDetailIntent)
        }


        linearLayoutIssuanceLookupMain!!.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            linearLayoutIssuanceLookupMain!!.getWindowVisibleDisplayFrame(r)
            val screenHeight = linearLayoutIssuanceLookupMain!!.rootView.height
            val keypadHeight = screenHeight - r.bottom
            //val temp = screenHeight * 0.15
            //Log.e(mTAG, "keypadHeight = $keypadHeight, screenHeight =$screenHeight, screenHeight * 0.15 = $temp")
            MainActivity.isKeyBoardShow = (keypadHeight > screenHeight * 0.15)

        }





        barcodeInput!!.setOnEditorActionListener { _, actionId, _ ->

            when(actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    Log.e(mTAG, "IME_ACTION_DONE")

                    //linearLayoutIssuanceLookupHeader!!.visibility = View.INVISIBLE
                    linearLayoutIssuanceLookupHeader!!.visibility = View.GONE
                    viewLine!!.visibility = View.GONE

                    progressBar!!.indeterminateTintList = ColorStateList.valueOf(colorCodePink)
                    progressBar!!.visibility = View.VISIBLE

                    /*outsourcedProcessDetailList.clear()
                    if (outsourcedProcessOrderDetailItemAdapter != null) {
                        outsourcedProcessOrderDetailItemAdapter?.notifyDataSetChanged()
                    }*/
                    issuanceLookupList.clear()
                    if (issuanceLookupItemAdapter != null) {
                        issuanceLookupItemAdapter?.notifyDataSetChanged()
                    }



                    linearLayoutIssuanceLookup!!.visibility = View.VISIBLE
                    linearLayoutIssuanceLookupDetail!!.visibility = View.GONE


                    isIssuanceLookupDetail = 0

                    val searchIntent = Intent()
                    searchIntent.action = Constants.ACTION.ACTION_USER_INPUT_SEARCH
                    searchIntent.putExtra("INPUT_NO", barcodeInput!!.text.toString().toUpperCase(
                        Locale.getDefault()))
                    issuanceLookupContext?.sendBroadcast(searchIntent)

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

                    //linearLayoutIssuanceLookupHeader!!.visibility = View.INVISIBLE
                    //linearLayoutDetailHeader!!.visibility = View.GONE
                    linearLayoutIssuanceLookupHeader!!.visibility = View.GONE
                    viewLine!!.visibility = View.GONE

                    progressBar!!.indeterminateTintList = ColorStateList.valueOf(colorCodePink)
                    progressBar!!.visibility = View.VISIBLE

                    /*outsourcedProcessDetailList.clear()
                    if (outsourcedProcessOrderDetailItemAdapter != null) {
                        outsourcedProcessOrderDetailItemAdapter?.notifyDataSetChanged()
                    }*/
                    issuanceLookupList.clear()
                    if (issuanceLookupItemAdapter != null) {
                        issuanceLookupItemAdapter?.notifyDataSetChanged()
                    }



                    linearLayoutIssuanceLookup!!.visibility = View.VISIBLE
                    linearLayoutIssuanceLookupDetail!!.visibility = View.GONE


                    isIssuanceLookupDetail = 0

                    val searchIntent = Intent()
                    searchIntent.action = Constants.ACTION.ACTION_USER_INPUT_SEARCH
                    searchIntent.putExtra("INPUT_NO", barcodeInput!!.text.toString().toUpperCase(
                        Locale.getDefault()))
                    issuanceLookupContext?.sendBroadcast(searchIntent)

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



                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_ISSUANCE_LOOKUP_SCAN_BARCODE, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_ISSUANCE_LOOKUP_SCAN_BARCODE")


                        val barcode: String = intent.getStringExtra("BARCODE") as String
                        Log.e(mTAG, "barcode = $barcode")
                        barcodeInput!!.setText(barcode)


                        linearLayoutIssuanceLookup!!.visibility = View.VISIBLE
                        linearLayoutIssuanceLookupHeader!!.visibility = View.GONE

                        linearLayoutIssuanceLookupDetail!!.visibility = View.GONE


                        progressBar!!.indeterminateTintList = ColorStateList.valueOf(colorCodePink)
                        progressBar!!.visibility = View.VISIBLE



                        issuanceLookupList.clear()
                        if (issuanceLookupItemAdapter != null) {
                            issuanceLookupItemAdapter?.notifyDataSetChanged()
                        }

                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_ISSUANCE_LOOKUP_FRAGMENT_REFRESH, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_ISSUANCE_LOOKUP_FRAGMENT_REFRESH")


                        progressBar!!.visibility = View.GONE

                        //linearLayoutIssuanceLookupHeader!!.visibility = View.VISIBLE

                        if (MainActivity.issuanceLookupList.size == 1 ) {
                            if (MainActivity.issuanceLookupList[0].result == "1") {
                                linearLayoutIssuanceLookupHeader!!.visibility = View.GONE
                            } else {
                                linearLayoutIssuanceLookupHeader!!.visibility = View.VISIBLE
                                val rjIssuanceLookupItem =  MainActivity.issuanceLookupList[0]

                                val issuanceLookupItem = IssuanceLookupItem(rjIssuanceLookupItem.data1, rjIssuanceLookupItem.data2, rjIssuanceLookupItem.data3,
                                    rjIssuanceLookupItem.data4, rjIssuanceLookupItem.data5, rjIssuanceLookupItem.data6, rjIssuanceLookupItem.data7,
                                    rjIssuanceLookupItem.data8, rjIssuanceLookupItem.data9, rjIssuanceLookupItem.data10, rjIssuanceLookupItem.data11)
                                issuanceLookupList.add(issuanceLookupItem)
                            }


                        } else if (MainActivity.issuanceLookupList.size > 1) {
                            for (rjIssuanceLookupItem in MainActivity.issuanceLookupList) {

                                val issuanceLookupItem = IssuanceLookupItem(rjIssuanceLookupItem.data1, rjIssuanceLookupItem.data2, rjIssuanceLookupItem.data3,
                                    rjIssuanceLookupItem.data4, rjIssuanceLookupItem.data5, rjIssuanceLookupItem.data6, rjIssuanceLookupItem.data7,
                                    rjIssuanceLookupItem.data8, rjIssuanceLookupItem.data9, rjIssuanceLookupItem.data10, rjIssuanceLookupItem.data11)
                                issuanceLookupList.add(issuanceLookupItem)

                            }
                        }



                        if (issuanceLookupItemAdapter != null) {
                            issuanceLookupItemAdapter?.notifyDataSetChanged()
                        }

                        if (MainActivity.isKeyBoardShow) {
                            val hideIntent = Intent()
                            hideIntent.action = Constants.ACTION.ACTION_HIDE_KEYBOARD
                            issuanceLookupContext!!.sendBroadcast(hideIntent)
                        }

                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_ISSUANCE_LOOKUP_FRAGMENT_DETAIL_REFRESH, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_ISSUANCE_LOOKUP_FRAGMENT_DETAIL_REFRESH")

                        //val sendOrder = intent.getStringExtra("SEND_ORDER")
                        val idxString = intent.getStringExtra("INDEX")

                        val idx = idxString?.toInt()


                        if (MainActivity.isKeyBoardShow) {
                            val hideIntent = Intent()
                            hideIntent.action = Constants.ACTION.ACTION_HIDE_KEYBOARD
                            issuanceLookupContext!!.sendBroadcast(hideIntent)
                        }

                        issuanceLookupDetailList.clear()

                        if (idx != null) {
                            //val item0 = IssuanceLookupDetailItem("發料單號", sendOrder as String)
                            //issuanceLookupDetailList.add(item0)
                            val item1 = IssuanceLookupDetailItem("項次", issuanceLookupList[idx].getData1())
                            issuanceLookupDetailList.add(item1)
                            val item2 = IssuanceLookupDetailItem("工單編號", issuanceLookupList[idx].getData2())
                            issuanceLookupDetailList.add(item2)
                            val item3 = IssuanceLookupDetailItem("料件編號", issuanceLookupList[idx].getData3())
                            issuanceLookupDetailList.add(item3)
                            val item4 = IssuanceLookupDetailItem("發料數量", issuanceLookupList[idx].getData4())
                            issuanceLookupDetailList.add(item4)
                            val item5 = IssuanceLookupDetailItem("發料單位", issuanceLookupList[idx].getData5())
                            issuanceLookupDetailList.add(item5)
                            val item6 = IssuanceLookupDetailItem("品名", issuanceLookupList[idx].getData6())
                            issuanceLookupDetailList.add(item6)
                            val item7 = IssuanceLookupDetailItem("規格", issuanceLookupList[idx].getData7())
                            issuanceLookupDetailList.add(item7)
                            val item8 = IssuanceLookupDetailItem("過帳否", issuanceLookupList[idx].getData8())
                            issuanceLookupDetailList.add(item8)
                            val item9 = IssuanceLookupDetailItem("倉庫", issuanceLookupList[idx].getData9())
                            issuanceLookupDetailList.add(item9)
                            val item10 = IssuanceLookupDetailItem("儲位", issuanceLookupList[idx].getData10())
                            issuanceLookupDetailList.add(item10)
                            val item11 = IssuanceLookupDetailItem("批號", issuanceLookupList[idx].getData11())
                            issuanceLookupDetailList.add(item11)
                        }

                        //linearLayoutDetailHeader!!.visibility = View.INVISIBLE

                        //viewLine!!.visibility = View.VISIBLE
                        linearLayoutIssuanceLookup!!.visibility = View.GONE
                        linearLayoutIssuanceLookupDetail!!.visibility = View.VISIBLE

                        isIssuanceLookupDetail = 1

                        if (issuanceLookupDetailItemAdapter != null) {
                            issuanceLookupDetailItemAdapter?.notifyDataSetChanged()
                        }

                        val showIntent = Intent()
                        showIntent.action = Constants.ACTION.ACTION_ISSUANCE_LOOKUP_SHOW_FAB_BACK
                        issuanceLookupContext!!.sendBroadcast(showIntent)

                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_ISSUANCE_LOOKUP_BACK_TO_LIST, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_ISSUANCE_LOOKUP_BACK_TO_LIST")

                        linearLayoutIssuanceLookup!!.visibility = View.VISIBLE
                        linearLayoutIssuanceLookupDetail!!.visibility = View.GONE
                        //viewLine!!.visibility = View.VISIBLE


                        isIssuanceLookupDetail = 0



                    }

                }
            }
        }

        if (!isRegister) {
            filter = IntentFilter()
            filter.addAction(Constants.ACTION.ACTION_BARCODE_NULL)
            filter.addAction(Constants.ACTION.ACTION_NETWORK_FAILED)
            filter.addAction(Constants.ACTION.ACTION_CONNECTION_TIMEOUT)

            filter.addAction(Constants.ACTION.ACTION_ISSUANCE_LOOKUP_SCAN_BARCODE)
            filter.addAction(Constants.ACTION.ACTION_ISSUANCE_LOOKUP_FRAGMENT_REFRESH)
            filter.addAction(Constants.ACTION.ACTION_ISSUANCE_LOOKUP_FRAGMENT_DETAIL_REFRESH)
            filter.addAction(Constants.ACTION.ACTION_ISSUANCE_LOOKUP_BACK_TO_LIST)

            issuanceLookupContext?.registerReceiver(mReceiver, filter)
            isRegister = true
            Log.d(mTAG, "registerReceiver mReceiver")
        }

        return view
    }

    override fun onDestroyView() {
        Log.i(mTAG, "onDestroy")

        val hideIntent = Intent()
        hideIntent.action = Constants.ACTION.ACTION_OUTSOURCED_PROCESS_HIDE_FAB_BACK
        issuanceLookupContext!!.sendBroadcast(hideIntent)

        if (isRegister && mReceiver != null) {
            try {
                issuanceLookupContext!!.unregisterReceiver(mReceiver)
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

        val toast = Toast.makeText(issuanceLookupContext, HtmlCompat.fromHtml("<h1>$message</h1>", HtmlCompat.FROM_HTML_MODE_COMPACT), Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER_HORIZONTAL or Gravity.CENTER_VERTICAL, 0, 0)

        /*val toast = Toast.makeText(outsourcedProcessContext, message, Toast.LENGTH_SHORT)
         toast.setGravity(Gravity.CENTER_HORIZONTAL or Gravity.CENTER_VERTICAL, 0, 0)
         val group = toast.view as ViewGroup
         val textView = group.getChildAt(0) as TextView
         textView.textSize = 30.0f*/
        toast.show()

        toastHandle = toast
    }




}