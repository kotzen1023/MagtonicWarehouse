package com.magtonic.magtonicwarehouse.fragment


import android.app.AlertDialog
import android.content.*
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Rect



import android.os.Bundle
//import android.support.v4.app.Fragment
import androidx.fragment.app.Fragment



import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.*

import com.magtonic.magtonicwarehouse.MainActivity

import com.magtonic.magtonicwarehouse.MainActivity.Companion.isKeyBoardShow
import com.magtonic.magtonicwarehouse.MainActivity.Companion.isReceiptUploadAutoConfirm
import com.magtonic.magtonicwarehouse.MainActivity.Companion.isWifiConnected
import com.magtonic.magtonicwarehouse.MainActivity.Companion.itemReceipt

import com.magtonic.magtonicwarehouse.MainActivity.Companion.printerStatus

import com.magtonic.magtonicwarehouse.R
import com.magtonic.magtonicwarehouse.bluetoothchat.BluetoothChatService

import com.magtonic.magtonicwarehouse.data.Constants
import com.magtonic.magtonicwarehouse.data.ReceiptDetailItem
import com.magtonic.magtonicwarehouse.data.ReceiptDetailItemAdapter

import com.magtonic.magtonicwarehouse.model.ui.ItemReceipt
import java.util.*


class ReceiptFragment : Fragment() {
    private val mTAG = ReceiptFragment::class.java.name
    private var receiptContext: Context? = null

    //private var listView: ListView? = null
    private var progressBar: ProgressBar? = null
    private var relativeLayout: RelativeLayout? = null
    private var receiptDetailItemAdapter: ReceiptDetailItemAdapter? = null
    private var btnUpload: Button? = null
    private var btnConfim: Button? = null
    private var barcodeInput: EditText? = null
    private var linearLayout: LinearLayout? = null
    //private var isKeyBoardShow: Boolean = false

    var receiptDetailList = ArrayList<ReceiptDetailItem>()
    private var listView: ListView ?= null

    //private var currentItemReceipt: ItemReceipt? = null

    var buttonTextColor: ColorStateList? = null
    //var textViewDefailtColor: ColorStateList? = null

    private var mReceiver: BroadcastReceiver? = null
    private var isRegister = false

    private var currentClickItem: Int = 0
    private var toastHandle: Toast? = null

    private val colorCodePink = Color.parseColor("#D81B60")
    private val colorCodeBlue = Color.parseColor("#1976D2")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.e(mTAG, "onCreate")

        receiptContext = context

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Log.d(mTAG, "onCreateView")

        val view = inflater.inflate(R.layout.fragment_receipt, container, false)

        relativeLayout = view.findViewById(R.id.receipt_list_container)
        linearLayout = view.findViewById(R.id.linearLayoutReceipt)
        progressBar = ProgressBar(receiptContext, null, android.R.attr.progressBarStyleLarge)
        val params = RelativeLayout.LayoutParams(MainActivity.screenHeight / 4, MainActivity.screenWidth / 4)
        params.addRule(RelativeLayout.CENTER_IN_PARENT)

        val localRelativeLayout: RelativeLayout? = relativeLayout
        if (localRelativeLayout != null) {
            localRelativeLayout.addView(progressBar, params)
        } else {
            Log.e(mTAG, "localRelativeLayout = null")
        }
        progressBar!!.visibility = View.GONE
        barcodeInput = view.findViewById(R.id.editTextReceipt)
        btnUpload = view.findViewById(R.id.btnUpload)
        buttonTextColor = btnUpload!!.textColors
        btnConfim = view.findViewById(R.id.btnReceiptUploadConfirm)
        listView = view!!.findViewById(R.id.listViewReceipt)
        listView!!.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            Log.d(mTAG, "click $position")

            currentClickItem = position
            when(itemReceipt!!.state) {
                ItemReceipt.ItemState.INITIAL, ItemReceipt.ItemState.UPLOAD_FAILED -> {

                    if (itemReceipt!!.state == ItemReceipt.ItemState.INITIAL)
                        Log.d(mTAG, "state = INITIAL")
                    else
                        Log.d(mTAG, "state = UPLOAD_FAILED")

                    var i = 0
                    while (i < 3) {
                        if (i != position) {
                            if (receiptDetailItemAdapter != null) {
                                receiptDetailList[i].getTextView()!!.visibility = View.VISIBLE
                                receiptDetailList[i].getLinearLayout()!!.visibility = View.GONE

                            }
                        }

                        i++
                    }

                    if (position < 3) {
                        if (receiptDetailItemAdapter != null) {

                            if (!isKeyBoardShow) { // show keyboard
                                val hideIntent = Intent()
                                hideIntent.action = Constants.ACTION.ACTION_HIDE_KEYBOARD
                                receiptContext!!.sendBroadcast(hideIntent)
                            }


                            receiptDetailList[position].getTextView()!!.visibility = View.GONE
                            receiptDetailList[position].getLinearLayout()!!.visibility = View.VISIBLE



                        }
                    }



                    listView!!.invalidateViews()

                    btnUpload!!.isEnabled = false
                }

                ItemReceipt.ItemState.UPLOADED, ItemReceipt.ItemState.CONFIRM_FAILED, ItemReceipt.ItemState.CONFIRMED -> {
                    Log.d(mTAG, "state = UPLOADED")
                    Log.d(mTAG, "Receipt is uploaded. Cannot be edit")
                }


            }


            //debug
            //set other back normal
            /*var i = 0
            while (i < 3) {
                if (i != position) {
                    if (receiptDetailItemAdapter != null) {
                        receiptDetailList[i].getTextView()!!.visibility = View.VISIBLE
                        receiptDetailList[i].getLinearLayout()!!.visibility = View.GONE

                    }
                }

                i++
            }

            if (position < 3) {
                if (receiptDetailItemAdapter != null) {

                    if (!isKeyBoardShow) { // show keyboard
                        val hideIntent = Intent()
                        hideIntent.action = Constants.ACTION.ACTION_HIDE_KEYBOARD
                        receiptContext!!.sendBroadcast(hideIntent)
                    }


                    receiptDetailList[position].getTextView()!!.visibility = View.GONE
                    receiptDetailList[position].getLinearLayout()!!.visibility = View.VISIBLE



                }
            }



            listView!!.invalidateViews()

            btnUpload!!.isEnabled = false
            */
        }


        if (receiptContext != null) {

            //debug test
            /*val item0 = ReceiptDetailItem("倉庫", "A120")
            receiptDetailList.add(item0)
            val item1 = ReceiptDetailItem("儲位","E302")
            receiptDetailList.add(item1)
            val item2 = ReceiptDetailItem("數量", "1000")
            receiptDetailList.add(item2)
            val item3 = ReceiptDetailItem("供應商編號", "test")
            receiptDetailList.add(item3)
            val item4 = ReceiptDetailItem("供應商名稱", "test")
            receiptDetailList.add(item4)
            val item5 = ReceiptDetailItem("料件編號", "test")
            receiptDetailList.add(item5)
            val item6 = ReceiptDetailItem("品名", "test")
            receiptDetailList.add(item6)
            val item7 = ReceiptDetailItem("規格", "test")
            receiptDetailList.add(item7)
            val item8 = ReceiptDetailItem("單位","test")
            receiptDetailList.add(item8)
            val item9 = ReceiptDetailItem("採購單項次", "test")
            receiptDetailList.add(item9)
            val item10 = ReceiptDetailItem("採購單性質", "test")
            receiptDetailList.add(item10)
            val item11 = ReceiptDetailItem("檢驗", "test")
            receiptDetailList.add(item11)*/


            receiptDetailItemAdapter = ReceiptDetailItemAdapter(receiptContext, R.layout.fragment_receipt_item, receiptDetailList)
            //listView.setAdapter(receiptDetailItemAdapter)
            listView!!.adapter = receiptDetailItemAdapter
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
            isKeyBoardShow = (keypadHeight > screenHeight * 0.15)

            if (isKeyBoardShow) {
                if (receiptDetailList.size > 0) {
                    receiptDetailList[currentClickItem].getEditText()!!.requestFocus()
                }

            }
        }





        barcodeInput!!.setOnEditorActionListener { _, actionId, _ ->

            when(actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    Log.e(mTAG, "IME_ACTION_DONE")

                    progressBar!!.indeterminateTintList = ColorStateList.valueOf(colorCodePink)
                    progressBar!!.visibility = View.VISIBLE

                    receiptDetailList.clear()
                    if (receiptDetailItemAdapter != null) {
                        receiptDetailItemAdapter?.notifyDataSetChanged()
                    }



                    val searchIntent = Intent()
                    searchIntent.action = Constants.ACTION.ACTION_USER_INPUT_SEARCH
                    searchIntent.putExtra("INPUT_NO", barcodeInput!!.text.toString().toUpperCase(Locale.getDefault()))
                    receiptContext?.sendBroadcast(searchIntent)

                    true
                }

                EditorInfo.IME_ACTION_GO -> {
                    Log.e(mTAG, "IME_ACTION_GO")
                    true
                }

                EditorInfo.IME_ACTION_NEXT -> {
                    Log.e(mTAG, "IME_ACTION_NEXT")

                    progressBar!!.indeterminateTintList = ColorStateList.valueOf(colorCodePink)
                    progressBar!!.visibility = View.VISIBLE

                    receiptDetailList.clear()
                    if (receiptDetailItemAdapter != null) {
                        receiptDetailItemAdapter?.notifyDataSetChanged()
                    }



                    val searchIntent = Intent()
                    searchIntent.action = Constants.ACTION.ACTION_USER_INPUT_SEARCH
                    searchIntent.putExtra("INPUT_NO", barcodeInput!!.text.toString().toUpperCase(Locale.getDefault()))
                    receiptContext?.sendBroadcast(searchIntent)

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

        /*editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {

            }
        })*/


        btnUpload!!.setOnClickListener {
            showUploadToERPDialog()


        }


        btnConfim!!.setOnClickListener {
            showUploadedConfirmDialog()

        }

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

                        showButtonStatusByState()

                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_CONNECTION_TIMEOUT, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_CONNECTION_TIMEOUT")

                        progressBar!!.visibility = View.GONE

                        showButtonStatusByState()

                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_SERVER_ERROR, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_SERVER_ERROR")

                        progressBar!!.visibility = View.GONE

                        showButtonStatusByState()

                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_RECEIPT_FRAGMENT_REFRESH, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_RECEIPT_FRAGMENT_REFRESH")


                        //val rva06 = intent.getStringExtra("RVA06")
                        //hide progress bar
                        progressBar!!.visibility = View.GONE
                        //hide keyboard
                        if (isKeyBoardShow) {
                            val hideIntent = Intent()
                            hideIntent.action = Constants.ACTION.ACTION_HIDE_KEYBOARD
                            receiptContext!!.sendBroadcast(hideIntent)
                        }

                        //current item
                        //currentItemReceipt = ReceiptList.itemReceipts[0]

                        /*when (itemReceipt!!.state) {
                            ItemReceipt.ItemState.INITIAL -> {
                                Log.e(mTAG, "state==> INITIAL")
                            }
                            ItemReceipt.ItemState.UPLOADED -> {
                                Log.e(mTAG, "state==> UPLOADED")
                            }
                            ItemReceipt.ItemState.UPLOAD_FAILED -> {
                                Log.e(mTAG, "state==> UPLOAD_FAILED")
                            }
                            ItemReceipt.ItemState.CONFIRMED -> {
                                Log.e(mTAG, "state==> CONFIRMED")
                            }
                            ItemReceipt.ItemState.CONFIRM_FAILED -> {
                                Log.e(mTAG, "state==> CONFIRM_FAILED")
                            }
                        }*/
                        Log.e(mTAG, "itemReceipt status = " + itemReceipt!!.state)


                        val item0 = ReceiptDetailItem("倉庫", itemReceipt!!.rjReceipt!!.ima35)
                        receiptDetailList.add(item0)
                        val item1 = ReceiptDetailItem("儲位", itemReceipt!!.rjReceipt!!.ima36)
                        receiptDetailList.add(item1)
                        val item2 = ReceiptDetailItem("數量", itemReceipt!!.rjReceipt!!.pmn20)
                        receiptDetailList.add(item2)
                        val item3 = ReceiptDetailItem("供應商編號", itemReceipt!!.rjReceipt!!.pmm09)
                        receiptDetailList.add(item3)
                        val item4 = ReceiptDetailItem("供應商名稱", itemReceipt!!.rjReceipt!!.pmc03)
                        receiptDetailList.add(item4)
                        val item5 = ReceiptDetailItem("料件編號", itemReceipt!!.rjReceipt!!.pmn04)
                        receiptDetailList.add(item5)
                        val item6 = ReceiptDetailItem("品名", itemReceipt!!.rjReceipt!!.pmn041)
                        receiptDetailList.add(item6)
                        val item7 = ReceiptDetailItem("規格", itemReceipt!!.rjReceipt!!.ima021)
                        receiptDetailList.add(item7)
                        val item8 = ReceiptDetailItem("單位", itemReceipt!!.rjReceipt!!.pmn07)
                        receiptDetailList.add(item8)
                        val item9 = ReceiptDetailItem("採購單項次", itemReceipt!!.rjReceipt!!.pmn02)
                        receiptDetailList.add(item9)
                        val item10 = ReceiptDetailItem("採購單性質", itemReceipt!!.rjReceipt!!.pmm02)
                        receiptDetailList.add(item10)
                        val item11 = ReceiptDetailItem("檢驗", itemReceipt!!.rjReceipt!!.pmnud02)
                        receiptDetailList.add(item11)
                        /*if (rva06 != "") {
                            var item12 = ReceiptDetailItem("收貨日期", rva06)
                            receiptDetailList.add(item12)
                            //do not upload
                        }*/

                        if (receiptDetailItemAdapter != null) {
                            receiptDetailItemAdapter?.notifyDataSetChanged()
                        }

                        //set button to default
                        /*btnUpload!!.isEnabled  = true
                        btnUpload!!.text = getString(R.string.receipt_upload)
                        btnUpload!!.setTextColor(buttonTextColor)
                        btnUpload!!.setBackgroundResource(android.R.drawable.btn_default)
                        btnUpload!!.visibility = View.VISIBLE
                        //hide upload confirm button
                        btnConfim!!.visibility = View.GONE

                        when (printerStatus) {
                            BluetoothChatService.STATE_NONE, BluetoothChatService.STATE_LISTEN, BluetoothChatService.STATE_CONNECTING -> {
                                btnUpload!!.isEnabled  = false
                            }

                            BluetoothChatService.STATE_CONNECTED -> {
                                btnUpload!!.isEnabled = isWifiConnected
                            }
                        }*/
                        showButtonStatusByState()

                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_RECEIPT_NO_NOT_EXIST, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_RECEIPT_NO_NOT_EXIST")

                        progressBar!!.visibility = View.GONE

                        /*btnUpload!!.isEnabled  = false
                        btnUpload!!.text = getString(R.string.receipt_upload)
                        btnUpload!!.setTextColor(buttonTextColor)
                        btnUpload!!.setBackgroundResource(android.R.drawable.btn_default)
                        btnUpload!!.visibility = View.GONE*/
                        showButtonStatusByState()

                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_RECEIPT_SCAN_BARCODE, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_RECEIPT_SCAN_BARCODE")

                        val barcode: String = intent.getStringExtra("BARCODE") as String
                        barcodeInput!!.setText(barcode)

                        btnUpload!!.visibility = View.GONE
                        btnConfim!!.visibility = View.GONE
                        progressBar!!.visibility = View.VISIBLE

                        receiptDetailList.clear()
                        if (receiptDetailItemAdapter != null) {
                            receiptDetailItemAdapter?.notifyDataSetChanged()
                        }
                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_RECEIPT_MODIFY_NO_CHANGED, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_RECEIPT_MODIFY_NO_CHANGED")

                        val idx = intent.getIntExtra("INDEX", 0)

                        receiptDetailList[idx].getTextView()!!.visibility = View.VISIBLE
                        receiptDetailList[idx].getLinearLayout()!!.visibility = View.GONE
                        listView!!.invalidateViews()

                        if (isKeyBoardShow) {
                            val hideIntent = Intent()
                            hideIntent.action = Constants.ACTION.ACTION_HIDE_KEYBOARD
                            receiptContext!!.sendBroadcast(hideIntent)
                        }

                        when (printerStatus) {
                            BluetoothChatService.STATE_NONE, BluetoothChatService.STATE_LISTEN, BluetoothChatService.STATE_CONNECTING-> {
                                btnUpload!!.isEnabled = false
                            }

                            BluetoothChatService.STATE_CONNECTED-> {
                                btnUpload!!.isEnabled = isWifiConnected
                            }
                        }
                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_RECEIPT_MODIFY_CHANGED, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_RECEIPT_MODIFY_CHANGED")

                        val idx = intent.getIntExtra("INDEX", 0)
                        val content = intent.getStringExtra("CONTENT")

                        Log.e(mTAG, "idx = $idx  content = $content")

                        if (itemReceipt != null) {

                            if (itemReceipt!!.state == ItemReceipt.ItemState.UPLOADED || itemReceipt!!.state == ItemReceipt.ItemState.CONFIRM_FAILED || itemReceipt!!.state == ItemReceipt.ItemState.CONFIRMED) { //uploaded, confirm_failed
                                Log.e(mTAG, "Can't not edit on itemState = UPLOADED, CONFIRM_FAILED, CONFIRMED")
                                toast(getString(R.string.receipt_cannot_be_edit))
                                //no change
                                receiptDetailList[idx].getTextView()!!.visibility = View.VISIBLE
                                receiptDetailList[idx].getLinearLayout()!!.visibility = View.GONE
                                listView!!.invalidateViews()
                            } else { //initial, upload_failed
                                if (content != null) {
                                    when(idx) {
                                        0-> {
                                            itemReceipt!!.rjReceipt!!.ima35 = content
                                        }
                                        1-> {
                                            itemReceipt!!.rjReceipt!!.ima36 = content
                                        }
                                        2-> {
                                            itemReceipt!!.rjReceipt!!.pmn20 = content
                                        }
                                    }
                                }



                                Log.d(mTAG, "[new item start]")
                                Log.d(mTAG, "倉庫 = "+itemReceipt!!.rjReceipt!!.ima35)
                                Log.d(mTAG, "儲位 = "+itemReceipt!!.rjReceipt!!.ima36)
                                Log.d(mTAG, "數量 = "+itemReceipt!!.rjReceipt!!.pmn20)
                                Log.d(mTAG, "[new item end]")

                                //receiptDetailList[idx].getTextView()!!.setTextColor(Color.BLUE)
                                receiptDetailList[idx].getTextView()!!.visibility = View.VISIBLE
                                receiptDetailList[idx].getLinearLayout()!!.visibility = View.GONE
                                listView!!.invalidateViews()

                                if (isKeyBoardShow) {
                                    val hideIntent = Intent()
                                    hideIntent.action = Constants.ACTION.ACTION_HIDE_KEYBOARD
                                    receiptContext!!.sendBroadcast(hideIntent)
                                }

                                when (printerStatus) {
                                    BluetoothChatService.STATE_NONE, BluetoothChatService.STATE_LISTEN, BluetoothChatService.STATE_CONNECTING-> {
                                        btnUpload!!.isEnabled  = false
                                    }

                                    BluetoothChatService.STATE_CONNECTED-> {
                                        btnUpload!!.isEnabled = isWifiConnected
                                    }
                                }
                            }

                        } else {
                            Log.e(mTAG, "itemReceipt = null")
                        }
                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_SETTING_BLUETOOTH_STATE_CHANGE, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_SETTING_BLUETOOTH_STATE_CHANGE")

                        if (itemReceipt != null) {

                            if (itemReceipt!!.state == ItemReceipt.ItemState.UPLOADED || itemReceipt!!.state == ItemReceipt.ItemState.CONFIRM_FAILED) { //uploaded, confirm_failed
                                btnUpload!!.isEnabled  = false
                                btnUpload!!.text = getString(R.string.btn_status_uploaded)
                                btnUpload!!.setTextColor(Color.BLACK)
                                btnUpload!!.setBackgroundColor(Color.rgb(0xf9, 0xa8, 0x25)) //md_yellow_800
                                btnUpload!!.visibility = View.VISIBLE

                                //show upload confirm button
                                btnConfim!!.isEnabled = isWifiConnected
                                btnConfim!!.text = getString(R.string.receipt_upload_confirm)
                                btnConfim!!.setTextColor(buttonTextColor)
                                btnConfim!!.setBackgroundResource(android.R.drawable.btn_default)
                                btnConfim!!.visibility = View.VISIBLE
                            }  else if (itemReceipt!!.state == ItemReceipt.ItemState.CONFIRMED) {
                                btnUpload!!.visibility = View.GONE
                                //show upload confirm button
                                btnConfim!!.isEnabled = false
                                btnConfim!!.text = getString(R.string.receipt_confirm_success)
                                btnConfim!!.setTextColor(Color.BLACK)
                                btnConfim!!.setBackgroundColor(Color.rgb(0xf9, 0xa8, 0x25)) //md_yellow_800
                                btnConfim!!.visibility = View.VISIBLE
                            } else {
                                btnUpload!!.text = getString(R.string.receipt_upload)
                                btnUpload!!.setTextColor(buttonTextColor)
                                btnUpload!!.setBackgroundResource(android.R.drawable.btn_default)
                                btnUpload!!.visibility = View.VISIBLE
                                //hide upload confirm button
                                btnConfim!!.visibility = View.GONE

                                when (printerStatus) {
                                    BluetoothChatService.STATE_NONE, BluetoothChatService.STATE_LISTEN, BluetoothChatService.STATE_CONNECTING-> {
                                        btnUpload!!.isEnabled = false

                                        if (printerStatus == BluetoothChatService.STATE_LISTEN || printerStatus == BluetoothChatService.STATE_CONNECTING) {

                                            progressBar!!.indeterminateTintList = ColorStateList.valueOf(colorCodeBlue)
                                            progressBar!!.visibility = View.VISIBLE
                                        } else { //BluetoothChatService.STATE_NONE
                                            progressBar!!.visibility = View.GONE
                                        }

                                    }

                                    BluetoothChatService.STATE_CONNECTED-> {
                                        progressBar!!.visibility = View.GONE
                                        //btnUpload!!.isEnabled = true
                                        //depends on wifi is connected or not
                                        btnUpload!!.isEnabled = isWifiConnected
                                    }
                                }
                            }


                        } else {
                            btnUpload!!.visibility = View.GONE
                            btnConfim!!.visibility = View.GONE
                        }


                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_RECEIPT_UPLOADED_SEND_TO_FRAGMENT, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_RECEIPT_UPLOADED_SEND_TO_FRAGMENT(upload success)")
                        //上傳與確認合併，到此已經上傳成功

                        if (isReceiptUploadAutoConfirm) { //if auto confirm is on
                            btnUpload!!.isEnabled  = false
                            btnUpload!!.text = getString(R.string.btn_status_uploaded)
                            btnUpload!!.setTextColor(Color.BLACK)
                            btnUpload!!.setBackgroundColor(Color.rgb(0xf9, 0xa8, 0x25)) //md_yellow_800
                            btnUpload!!.visibility = View.GONE

                            btnConfim!!.isEnabled = false
                            btnConfim!!.text = getString(R.string.receipt_confirm_doing)
                            btnConfim!!.setTextColor(buttonTextColor)
                            btnConfim!!.setBackgroundResource(android.R.drawable.btn_default)
                            btnConfim!!.visibility = View.VISIBLE

                            val uploadIntent = Intent()
                            uploadIntent.action = Constants.ACTION.ACTION_RECEIPT_UPLOADED_CONFIRM_ACTION
                            receiptContext!!.sendBroadcast(uploadIntent)
                        } else { //auto confirm is off, user will confirm by himself
                            progressBar!!.visibility = View.GONE

                            showButtonStatusByState()
                        }

                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_RECEIPT_UPLOAD_FAILED_SEND_TO_FRAGMENT, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_RECEIPT_UPLOAD_FAILED_SEND_TO_FRAGMENT(upload failed)")

                        progressBar!!.visibility = View.GONE

                        showButtonStatusByState()

                        /*when (printerStatus) {
                            BluetoothChatService.STATE_NONE, BluetoothChatService.STATE_LISTEN, BluetoothChatService.STATE_CONNECTING -> {
                                btnUpload!!.isEnabled  = false
                            }

                            BluetoothChatService.STATE_CONNECTED -> {
                                btnUpload!!.isEnabled = true
                            }
                        }

                        btnUpload!!.isEnabled = isWifiConnected
                        btnUpload!!.text = getString(R.string.receipt_upload)
                        btnUpload!!.setTextColor(buttonTextColor)
                        btnUpload!!.setBackgroundResource(android.R.drawable.btn_default)
                        btnUpload!!.visibility = View.VISIBLE
                        //hide upload confirm button
                        btnConfim!!.visibility = View.GONE*/

                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_WIFI_STATE_CHANGED, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_WIFI_STATE_CHANGED")

                        showButtonStatusByState()

                        /*if (itemReceipt != null) {
                            if (itemReceipt!!.state == ItemReceipt.ItemState.UPLOADED || itemReceipt!!.state == ItemReceipt.ItemState.CONFIRM_FAILED) { //uploaded, confirm_failed

                                btnUpload!!.isEnabled = false
                                btnUpload!!.text = getString(R.string.btn_status_uploaded)
                                btnUpload!!.setTextColor(Color.BLACK)
                                btnUpload!!.setBackgroundColor(Color.rgb(0xf9, 0xa8, 0x25)) //md_yellow_800
                                btnUpload!!.visibility = View.VISIBLE

                                btnConfim!!.isEnabled = isWifiConnected
                                btnConfim!!.text = getString(R.string.receipt_upload_confirm)
                                btnConfim!!.setTextColor(buttonTextColor)
                                btnConfim!!.setBackgroundResource(android.R.drawable.btn_default)
                                btnConfim!!.visibility = View.VISIBLE


                            } else { // not upload or upload failed
                                btnUpload!!.text = getString(R.string.receipt_upload)
                                btnUpload!!.setTextColor(buttonTextColor)
                                btnUpload!!.setBackgroundResource(android.R.drawable.btn_default)
                                btnUpload!!.visibility = View.VISIBLE
                                //hide upload confirm button
                                btnConfim!!.visibility = View.GONE

                                when (printerStatus) {
                                    BluetoothChatService.STATE_NONE, BluetoothChatService.STATE_LISTEN, BluetoothChatService.STATE_CONNECTING-> {
                                        btnUpload!!.isEnabled = false
                                    }

                                    BluetoothChatService.STATE_CONNECTED-> {
                                        btnUpload!!.isEnabled = isWifiConnected
                                    }
                                }
                            }
                        } else {
                            Log.e(mTAG, "itemReceipt = null")
                            btnUpload!!.visibility = View.GONE
                            btnConfim!!.visibility = View.GONE
                        }*/


                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_RECEIPT_UPLOADED_CONFIRM_FAILED, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_RECEIPT_UPLOADED_CONFIRM_FAILED")

                        progressBar!!.visibility = View.GONE

                        showButtonStatusByState()

                        showConfirmFailedInfoDialog()

                        /*btnUpload!!.isEnabled  = false
                        btnUpload!!.text = getString(R.string.btn_status_uploaded)
                        btnUpload!!.setTextColor(Color.BLACK)
                        btnUpload!!.setBackgroundColor(Color.rgb(0xf9, 0xa8, 0x25)) //md_yellow_800
                        btnUpload!!.visibility = View.VISIBLE

                        //show upload confirm button
                        btnConfim!!.isEnabled = isWifiConnected
                        btnConfim!!.text = getString(R.string.receipt_upload_confirm)
                        btnConfim!!.setTextColor(buttonTextColor)
                        btnConfim!!.setBackgroundResource(android.R.drawable.btn_default)
                        btnConfim!!.visibility = View.VISIBLE*/

                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_RECEIPT_UPLOADED_CONFIRM_SUCCESS, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_RECEIPT_UPLOADED_CONFIRM_SUCCESS")

                        progressBar!!.visibility = View.GONE

                        showButtonStatusByState()

                        /*
                        //btnUpload!!.isEnabled  = false
                        //btnUpload!!.text = getString(R.string.btn_status_uploaded)
                        //btnUpload!!.setTextColor(Color.BLACK)
                        //btnUpload!!.setBackgroundColor(Color.rgb(0xf9, 0xa8, 0x25)) //md_yellow_800
                        btnUpload!!.visibility = View.GONE
                        //show upload confirm button
                        btnConfim!!.isEnabled = false
                        btnConfim!!.text = getString(R.string.receipt_confirm_success)
                        btnConfim!!.setTextColor(Color.BLACK)
                        btnConfim!!.setBackgroundColor(Color.rgb(0xf9, 0xa8, 0x25)) //md_yellow_800
                        btnConfim!!.visibility = View.VISIBLE*/
                    }

                }
            }
        }

        if (!isRegister) {
            filter = IntentFilter()
            filter.addAction(Constants.ACTION.ACTION_BARCODE_NULL)
            filter.addAction(Constants.ACTION.ACTION_NETWORK_FAILED)
            filter.addAction(Constants.ACTION.ACTION_CONNECTION_TIMEOUT)
            filter.addAction(Constants.ACTION.ACTION_SERVER_ERROR)
            filter.addAction(Constants.ACTION.ACTION_RECEIPT_FRAGMENT_REFRESH)
            filter.addAction(Constants.ACTION.ACTION_RECEIPT_NO_NOT_EXIST)
            filter.addAction(Constants.ACTION.ACTION_RECEIPT_SCAN_BARCODE)
            filter.addAction(Constants.ACTION.ACTION_RECEIPT_MODIFY_CHANGED)
            filter.addAction(Constants.ACTION.ACTION_RECEIPT_MODIFY_NO_CHANGED)
            filter.addAction(Constants.ACTION.ACTION_SETTING_BLUETOOTH_STATE_CHANGE)
            filter.addAction(Constants.ACTION.ACTION_RECEIPT_UPLOADED_SEND_TO_FRAGMENT)
            filter.addAction(Constants.ACTION.ACTION_RECEIPT_UPLOAD_FAILED_SEND_TO_FRAGMENT)
            filter.addAction(Constants.ACTION.ACTION_WIFI_STATE_CHANGED)
            filter.addAction(Constants.ACTION.ACTION_RECEIPT_UPLOADED_CONFIRM_FAILED)
            filter.addAction(Constants.ACTION.ACTION_RECEIPT_UPLOADED_CONFIRM_SUCCESS)

            //filter.addAction(Constants.ACTION.ACTION_RECEIPT_ALREADY_UPLOADED_SEND_TO_FRAGMENT)
            receiptContext?.registerReceiver(mReceiver, filter)
            isRegister = true
            Log.d(mTAG, "registerReceiver mReceiver")
        }

        return view
    }

    override fun onDestroyView() {
        Log.i(mTAG, "onDestroy")

        if (isRegister && mReceiver != null) {
            try {
                receiptContext!!.unregisterReceiver(mReceiver)
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

        val toast = Toast.makeText(receiptContext, message, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER_HORIZONTAL or Gravity.CENTER_VERTICAL, 0, 0)
        val group = toast.view as ViewGroup
        val textView = group.getChildAt(0) as TextView
        textView.textSize = 30.0f
        toast.show()

        toastHandle = toast
    }

    private fun showUploadToERPDialog() {

        // get prompts.xml view
        /*LayoutInflater layoutInflater = LayoutInflater.from(Nfc_read_app.this);
        View promptView = layoutInflater.inflate(R.layout.input_dialog, null);*/
        val promptView = View.inflate(receiptContext, R.layout.receipt_confirm_dialog, null)

        val alertDialogBuilder = AlertDialog.Builder(receiptContext).create()
        alertDialogBuilder.setView(promptView)

        //final EditText editFileName = (EditText) promptView.findViewById(R.id.editFileName);
        val textViewMsg = promptView.findViewById<TextView>(R.id.textViewDialog)
        val textViewNumHeader = promptView.findViewById<TextView>(R.id.textViewQuantityHeader)
        val textViewNumContent = promptView.findViewById<TextView>(R.id.textViewQuantityContent)
        val btnCancel = promptView.findViewById<Button>(R.id.btnDialogCancel)
        val btnConfirm = promptView.findViewById<Button>(R.id.btnDialogConfirm)

        textViewMsg.text = getString(R.string.receipt_upload_to_erp_msg)
        textViewNumHeader.text = "數量"
        textViewNumContent.text = itemReceipt!!.rjReceipt!!.pmn20
        textViewNumContent.setTextColor(colorCodeBlue)
        btnCancel.text = getString(R.string.cancel)
        btnConfirm.text = getString(R.string.confirm)

        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
        btnCancel!!.setOnClickListener {
            alertDialogBuilder.dismiss()
        }
        btnConfirm!!.setOnClickListener {
            progressBar!!.indeterminateTintList = ColorStateList.valueOf(colorCodePink)
            progressBar!!.visibility = View.VISIBLE

            btnUpload!!.isEnabled = false

            val uploadIntent = Intent()
            uploadIntent.action = Constants.ACTION.ACTION_RECEIPT_UPLOAD_ACTION
            receiptContext!!.sendBroadcast(uploadIntent)

            alertDialogBuilder.dismiss()
        }
        alertDialogBuilder.show()


    }

    private fun showUploadedConfirmDialog() {

        // get prompts.xml view
        /*LayoutInflater layoutInflater = LayoutInflater.from(Nfc_read_app.this);
        View promptView = layoutInflater.inflate(R.layout.input_dialog, null);*/
        val promptView = View.inflate(receiptContext, R.layout.confirm_dialog, null)

        val alertDialogBuilder = AlertDialog.Builder(receiptContext).create()
        alertDialogBuilder.setView(promptView)

        //final EditText editFileName = (EditText) promptView.findViewById(R.id.editFileName);
        val textViewMsg = promptView.findViewById<TextView>(R.id.textViewDialog)
        val btnCancel = promptView.findViewById<Button>(R.id.btnDialogCancel)
        val btnConfirm = promptView.findViewById<Button>(R.id.btnDialogConfirm)

        textViewMsg.text = getString(R.string.receipt_upload_confirm_msg)
        btnCancel.text = getString(R.string.cancel)
        btnConfirm.text = getString(R.string.confirm)

        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
        btnCancel!!.setOnClickListener {
            alertDialogBuilder.dismiss()
        }
        btnConfirm!!.setOnClickListener {
            progressBar!!.indeterminateTintList = ColorStateList.valueOf(colorCodePink)
            progressBar!!.visibility = View.VISIBLE

            btnConfim!!.isEnabled = false

            val uploadIntent = Intent()
            uploadIntent.action = Constants.ACTION.ACTION_RECEIPT_UPLOADED_CONFIRM_ACTION
            receiptContext!!.sendBroadcast(uploadIntent)

            alertDialogBuilder.dismiss()
        }
        alertDialogBuilder.show()
    }

    private fun showConfirmFailedInfoDialog() {

        // get prompts.xml view
        /*LayoutInflater layoutInflater = LayoutInflater.from(Nfc_read_app.this);
        View promptView = layoutInflater.inflate(R.layout.input_dialog, null);*/
        val promptView = View.inflate(receiptContext, R.layout.confirm_dialog, null)

        val alertDialogBuilder = AlertDialog.Builder(receiptContext).create()
        alertDialogBuilder.setView(promptView)

        //final EditText editFileName = (EditText) promptView.findViewById(R.id.editFileName);
        val textViewMsg = promptView.findViewById<TextView>(R.id.textViewDialog)
        val btnCancel = promptView.findViewById<Button>(R.id.btnDialogCancel)
        val btnConfirm = promptView.findViewById<Button>(R.id.btnDialogConfirm)

        textViewMsg.text = getString(R.string.receipt_uploaded_confirm_failed_info)
        btnCancel.text = getString(R.string.cancel)
        btnCancel.visibility = View.GONE
        btnConfirm.text = getString(R.string.btn_ok)

        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
        btnCancel!!.setOnClickListener {
            alertDialogBuilder.dismiss()
        }
        btnConfirm!!.setOnClickListener {
            /*progressBar!!.indeterminateTintList = ColorStateList.valueOf(colorCodePink)
            progressBar!!.visibility = View.VISIBLE

            btnConfim!!.isEnabled = false

            val uploadIntent = Intent()
            uploadIntent.action = Constants.ACTION.ACTION_RECEIPT_UPLOADED_CONFIRM_ACTION
            receiptContext!!.sendBroadcast(uploadIntent)*/

            alertDialogBuilder.dismiss()
        }
        alertDialogBuilder.show()
    }

    private fun showButtonStatusByState() {
        Log.d(mTAG, "=== receipt showButtonStatusByState start === ")

        if (itemReceipt != null) {

            Log.d(mTAG, "itemReceipt.state = "+ itemReceipt!!.state)

            if (itemReceipt!!.state == ItemReceipt.ItemState.UPLOADED || itemReceipt!!.state == ItemReceipt.ItemState.CONFIRM_FAILED) { //uploaded, confirm_failed
                btnUpload!!.isEnabled  = false
                btnUpload!!.text = getString(R.string.btn_status_uploaded)
                btnUpload!!.setTextColor(Color.BLACK)
                btnUpload!!.setBackgroundColor(Color.rgb(0xf9, 0xa8, 0x25)) //md_yellow_800
                btnUpload!!.visibility = View.VISIBLE

                //show upload confirm button
                btnConfim!!.isEnabled = isWifiConnected
                btnConfim!!.text = getString(R.string.receipt_upload_confirm)
                btnConfim!!.setTextColor(buttonTextColor)
                btnConfim!!.setBackgroundResource(android.R.drawable.btn_default)
                btnConfim!!.visibility = View.VISIBLE

            } else if (itemReceipt!!.state == ItemReceipt.ItemState.CONFIRMED) {
                btnUpload!!.visibility = View.GONE
                //show upload confirm button
                btnConfim!!.isEnabled = false
                btnConfim!!.text = getString(R.string.receipt_confirm_success)
                btnConfim!!.setTextColor(Color.BLACK)
                btnConfim!!.setBackgroundColor(Color.rgb(0xf9, 0xa8, 0x25)) //md_yellow_800
                btnConfim!!.visibility = View.VISIBLE
            } else { //initial, upload_failed
                when (printerStatus) {
                    BluetoothChatService.STATE_NONE, BluetoothChatService.STATE_LISTEN, BluetoothChatService.STATE_CONNECTING -> {
                        btnUpload!!.isEnabled  = false
                    }

                    BluetoothChatService.STATE_CONNECTED -> {
                        btnUpload!!.isEnabled = isWifiConnected
                    }
                }

                btnUpload!!.text = getString(R.string.receipt_upload)
                btnUpload!!.setTextColor(buttonTextColor)
                btnUpload!!.setBackgroundResource(android.R.drawable.btn_default)
                btnUpload!!.visibility = View.VISIBLE
                //hide upload confirm button
                btnConfim!!.visibility = View.GONE
            }
        } else {
            Log.e(mTAG, "itemReceipt = null")
            btnUpload!!.visibility = View.GONE
            btnConfim!!.visibility = View.GONE
        }

        Log.d(mTAG, "=== receipt showButtonStatusByState end === ")
    }
}