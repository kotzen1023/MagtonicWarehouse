package com.magtonic.magtonicwarehouse.fragment

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
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import com.magtonic.magtonicwarehouse.MainActivity
import com.magtonic.magtonicwarehouse.MainActivity.Companion.isReturnOfGoodsInDetail
import com.magtonic.magtonicwarehouse.MainActivity.Companion.returnOfGoodsDetailList
import com.magtonic.magtonicwarehouse.MainActivity.Companion.returnOfGoodsListBySupplier
import com.magtonic.magtonicwarehouse.R
import com.magtonic.magtonicwarehouse.SignActivity
import com.magtonic.magtonicwarehouse.data.*
import java.util.*

class ReturnOfGoodsFragment : Fragment() {
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.e(mTAG, "onCreate")

        returnOfGoodsContext = context

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

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

            MainActivity.isKeyBoardShow = keypadHeight > screenHeight * 0.15

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

            if (!returnOfGoodsList[position].getIsSigned()) {
                currentSelectSendOrder = position

                currentSendOrder = returnOfGoodsList[position].getData2()

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
                    searchIntent.putExtra("INPUT_NO", barcodeInput!!.text.toString().toUpperCase(Locale.getDefault()))
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
                    searchIntent.putExtra("INPUT_NO", barcodeInput!!.text.toString().toUpperCase(Locale.getDefault()))
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

                        Log.e(mTAG, "returnOfGoodsListBySupplier.size = ${returnOfGoodsListBySupplier.size}")

                        if (returnOfGoodsListBySupplier.size == 1 ) {
                            if (returnOfGoodsListBySupplier[0].result == "1") {
                                linearLayoutReturnOfGoodsHeader!!.visibility = View.GONE
                            } else {
                                //linearLayoutIssuanceLookupHeader!!.visibility = View.VISIBLE
                                linearLayoutReturnOfGoodsHeader!!.visibility = View.VISIBLE

                                val rjReturnOfGoodsItem =  returnOfGoodsListBySupplier[0]

                                val returnOfGoodsItem = ReturnOfGoodsItem(rjReturnOfGoodsItem.data1, rjReturnOfGoodsItem.data2, rjReturnOfGoodsItem.data3)
                                returnOfGoodsList.add(returnOfGoodsItem)
                            }


                        } else if (returnOfGoodsListBySupplier.size > 1) {
                            linearLayoutReturnOfGoodsHeader!!.visibility = View.VISIBLE
                            for (rjReturnOfGoodsItem in returnOfGoodsListBySupplier) {

                                val returnOfGoodsItem = ReturnOfGoodsItem(rjReturnOfGoodsItem.data1, rjReturnOfGoodsItem.data2, rjReturnOfGoodsItem.data3)
                                returnOfGoodsList.add(returnOfGoodsItem)

                            }
                        } else {
                            Log.e(mTAG, "size = 0")
                        }



                        if (returnOfGoodsItemAdapter != null) {
                            returnOfGoodsItemAdapter?.notifyDataSetChanged()
                        }

                        if (MainActivity.isKeyBoardShow) {
                            val hideIntent = Intent()
                            hideIntent.action = Constants.ACTION.ACTION_HIDE_KEYBOARD
                            returnOfGoodsContext!!.sendBroadcast(hideIntent)
                        }

                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_RETURN_OF_GOODS_FRAGMENT_DETAIL_REFRESH, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_RETURN_OF_GOODS_FRAGMENT_DETAIL_REFRESH")

                        //val sendOrder = intent.getStringExtra("SEND_ORDER")
                        //val idxString = intent.getStringExtra("INDEX")

                        //val idx = idxString?.toInt()


                        if (MainActivity.isKeyBoardShow) {
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


                        for (i in 0 until returnOfGoodsList.size) {
                            Log.e(mTAG, returnOfGoodsList[i].getData1())
                            if (returnOfGoodsList[i].getData2() == sendOrder) {
                                returnOfGoodsList[i].setIsSigned(true)
                            }
                            Log.e(mTAG, "returnOfGoodsList[$i] = ${returnOfGoodsList[i].getIsSigned()}")
                        }

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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        Log.i(mTAG, "onActivityCreated")
        super.onActivityCreated(savedInstanceState)

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

        textViewReturnOfGoodsOrderContent.text = returnOfGoodsList[currentSelectSendOrder].getData2()
        textViewReturnOfGoodsTypeContent.text = returnOfGoodsList[currentSelectSendOrder].getData1()
        textViewReturnOfGoodsDateContent.text = returnOfGoodsList[currentSelectSendOrder].getData3()
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
        //val textViewOutsourcedProcessSendOrderHeader = promptView.findViewById<TextView>(R.id.textViewOutsourcedProcessSendOrderHeader)
        val spinnerSupplier = promptView.findViewById<Spinner>(R.id.spinnerSupplier)
        //val textViewOutsourcedProcessWorkOrderHeader = promptView.findViewById<TextView>(R.id.textViewOutsourcedProcessWorkOrderHeader)
        //val textViewOutsourcedProcessWorkOrderContent = promptView.findViewById<TextView>(R.id.textViewOutsourcedProcessWorkOrderContent)
        val btnCancel = promptView.findViewById<Button>(R.id.btnOutSourcedDialogCancel)
        val btnConfirm = promptView.findViewById<Button>(R.id.btnOutSourcedDialogConfirm)
        val adapter: ArrayAdapter<String> = ArrayAdapter(returnOfGoodsContext as Context, R.layout.myspinner,
            MainActivity.outsourcedSupplierNameList
        )
        spinnerSupplier.adapter = adapter

        spinnerSupplier.setSelection(currentSelectedSupplier)

        spinnerSupplier?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                barcodeInput!!.setText(MainActivity.outsourcedSupplierHashMap[MainActivity.outsourcedSupplierNameList[position]])

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
            searchIntent.putExtra("INPUT_NO", barcodeInput!!.text.toString().toUpperCase(Locale.getDefault()))
            returnOfGoodsContext?.sendBroadcast(searchIntent)



            alertDialogBuilder.dismiss()
        }
        alertDialogBuilder.show()


    }
}