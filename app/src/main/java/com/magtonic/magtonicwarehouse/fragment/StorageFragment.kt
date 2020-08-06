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
//import android.support.v4.app.Fragment
import androidx.fragment.app.Fragment
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.core.text.HtmlCompat
import com.magtonic.magtonicwarehouse.MainActivity
import com.magtonic.magtonicwarehouse.MainActivity.Companion.isKeyBoardShow
import com.magtonic.magtonicwarehouse.MainActivity.Companion.isWifiConnected
import com.magtonic.magtonicwarehouse.MainActivity.Companion.itemStorage

import com.magtonic.magtonicwarehouse.R

import com.magtonic.magtonicwarehouse.data.Constants

import com.magtonic.magtonicwarehouse.data.StorageDetailItem
import com.magtonic.magtonicwarehouse.data.StorageDetailItemAdapter
import com.magtonic.magtonicwarehouse.model.ui.ItemStorage
import java.util.*
import kotlin.collections.ArrayList

class StorageFragment: Fragment() {
    private val mTAG = StorageFragment::class.java.name

    private var storageContext: Context? = null

    private var progressBar: ProgressBar? = null
    private var relativeLayout: RelativeLayout? = null
    private var storageDetailItemAdapter : StorageDetailItemAdapter? = null
    private var btnUpload: Button? = null
    private var barcodeInput: EditText? = null
    private var linearLayout: LinearLayout? = null
    private var inStockCheckIcon: ImageView? = null

    var storageDetailList = ArrayList<StorageDetailItem>()
    private var listView: ListView ?= null

    private var buttonTextColor: ColorStateList? = null

    private var mReceiver: BroadcastReceiver? = null
    private var isRegister = false

    private var currentClickItem: Int = 0
    private var toastHandle: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(mTAG, "onCreate")

        storageContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Log.d(mTAG, "onCreateView")

        val view = inflater.inflate(R.layout.fragment_storage, container, false)
        relativeLayout = view.findViewById(R.id.storage_list_container)
        linearLayout = view.findViewById(R.id.linearLayoutStorage)
        progressBar = ProgressBar(storageContext, null, android.R.attr.progressBarStyleLarge)
        val params = RelativeLayout.LayoutParams(MainActivity.screenHeight / 4, MainActivity.screenWidth / 4)
        params.addRule(RelativeLayout.CENTER_IN_PARENT)

        val localRelativeLayout: RelativeLayout? = relativeLayout
        if (localRelativeLayout != null) {
            localRelativeLayout.addView(progressBar, params)
        } else {
            Log.e(mTAG, "localRelativeLayout = null")
        }
        progressBar!!.visibility = View.GONE
        inStockCheckIcon = view.findViewById(R.id.inStockCheckIcon)
        barcodeInput = view.findViewById(R.id.editTextStorage)
        btnUpload = view.findViewById(R.id.btnStorage)

        buttonTextColor = btnUpload!!.textColors

        listView = view!!.findViewById(R.id.listViewStorage)
        listView!!.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            Log.d(mTAG, "click $position")

            currentClickItem = position



            var i = 0
            while (i < 6) {
                if (i != position) {
                    if (storageDetailItemAdapter != null) {
                        storageDetailList[i].getTextView()!!.visibility = View.VISIBLE
                        storageDetailList[i].getLinearLayout()!!.visibility = View.GONE

                    }
                }

                i++
            }

            if (position == 4 || position == 5) {
                if (storageDetailItemAdapter != null) {

                    if (!isKeyBoardShow) { // show keyboard
                        val hideIntent = Intent()
                        hideIntent.action = Constants.ACTION.ACTION_HIDE_KEYBOARD
                        storageContext!!.sendBroadcast(hideIntent)
                    }


                    storageDetailList[position].getTextView()!!.visibility = View.GONE
                    storageDetailList[position].getLinearLayout()!!.visibility = View.VISIBLE



                }
            }



            listView!!.invalidateViews()

            btnUpload!!.isEnabled = false
        }

        if (storageContext != null) {

            /*val item0 = StorageDetailItem("入庫單號", "rvv01")
            storageDetailList.add(item0)
            val item1 = StorageDetailItem("廠商", "pmc03")
            storageDetailList.add(item1)
            val item2 = StorageDetailItem("料號", "rvb05")
            storageDetailList.add(item2)
            val item3 = StorageDetailItem("品名", "rvb051")
            storageDetailList.add(item3)
            val item4 = StorageDetailItem("倉庫", "rvb36")
            storageDetailList.add(item4)
            val item5 = StorageDetailItem("儲位", "rvb37")
            storageDetailList.add(item5)
            //交貨量+單位
            val content1 = "rvb07" +"  "+"rvb90"

            val item6 = StorageDetailItem("交貨量", content1)
            storageDetailList.add(item6)
            //允收量+單位
            val content2 = "rvb33" +"  "+"rvb90"

            val item7 = StorageDetailItem("允收量", content2)
            storageDetailList.add(item7)

            val item8 = StorageDetailItem("批號", "rvb38")
            storageDetailList.add(item8)*/

            storageDetailItemAdapter = StorageDetailItemAdapter(storageContext, R.layout.fragment_storage_item, storageDetailList)
            //listView.setAdapter(receiptDetailItemAdapter)
            listView!!.adapter = storageDetailItemAdapter
        }

        //detect soft keyboard

        /*linearLayout!!.viewTreeObserver.addOnGlobalLayoutListener(
            object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    val r = Rect()
                    linearLayout!!.getWindowVisibleDisplayFrame(r)
                    //val screenHeight = linearLayout!!.getRootView().getHeight()
                    val screenHeight = linearLayout!!.rootView.height
                    val keypadHeight = screenHeight - r.bottom
                    isKeyBoardShow = (keypadHeight > screenHeight * 0.15)
                }
            }
        )*/
        linearLayout!!.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            linearLayout!!.getWindowVisibleDisplayFrame(r)
            //val screenHeight = linearLayout!!.getRootView().getHeight()
            val screenHeight = linearLayout!!.rootView.height
            val keypadHeight = screenHeight - r.bottom
            isKeyBoardShow = (keypadHeight > screenHeight * 0.15)

            Log.e(mTAG, "isKeyBoardShow = $isKeyBoardShow")
        }


        barcodeInput!!.setOnEditorActionListener { _, actionId, _ ->

            when(actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    Log.e(mTAG, "IME_ACTION_DONE")

                    progressBar!!.visibility = View.VISIBLE

                    storageDetailList.clear()
                    if (storageDetailItemAdapter != null) {
                        storageDetailItemAdapter?.notifyDataSetChanged()
                    }



                    val searchIntent = Intent()
                    searchIntent.action = Constants.ACTION.ACTION_USER_INPUT_SEARCH
                    searchIntent.putExtra("INPUT_NO", barcodeInput!!.text.toString().toUpperCase(Locale.getDefault()))
                    storageContext?.sendBroadcast(searchIntent)

                    true
                }

                EditorInfo.IME_ACTION_GO -> {
                    Log.e(mTAG, "IME_ACTION_GO")
                    true
                }

                EditorInfo.IME_ACTION_NEXT -> {
                    Log.e(mTAG, "IME_ACTION_NEXT")

                    progressBar!!.visibility = View.VISIBLE

                    storageDetailList.clear()
                    if (storageDetailItemAdapter != null) {
                        storageDetailItemAdapter?.notifyDataSetChanged()
                    }



                    val searchIntent = Intent()
                    searchIntent.action = Constants.ACTION.ACTION_USER_INPUT_SEARCH
                    searchIntent.putExtra("INPUT_NO", barcodeInput!!.text.toString().toUpperCase(Locale.getDefault()))
                    storageContext?.sendBroadcast(searchIntent)

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




        btnUpload!!.setOnClickListener {
            showUploadToERPDialog()
            /*val confirmDialog = AlertDialog.Builder(storageContext)
            confirmDialog.setIcon(R.drawable.baseline_warning_black_48)
            confirmDialog.setTitle(resources.getString(R.string.storage_upload))
            confirmDialog.setMessage(resources.getString(R.string.storage_upload_to_erp_msg))
            confirmDialog.setPositiveButton(
                resources.getString(R.string.ok)
            ) { _, _ ->
                progressBar!!.visibility = View.VISIBLE
                btnUpload!!.isEnabled = false

                val updateIntent = Intent()
                updateIntent.action = Constants.ACTION.ACTION_STORAGE_UPDATE_ACTION
                storageContext!!.sendBroadcast(updateIntent)
            }
            confirmDialog.setNegativeButton(
                resources.getString(R.string.cancel)
            ) { _, _ -> }
            confirmDialog.show()*/
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

                        toast(getString(R.string.upload_receipt_connection_timeout))

                        progressBar!!.visibility = View.GONE

                        showButtonStatusByState()
                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_STORAGE_FRAGMENT_REFRESH, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_STORAGE_FRAGMENT_REFRESH")

                        val stock = intent.getBooleanExtra("STOCK", false)
                        if (stock) {
                            inStockCheckIcon!!.visibility = View.VISIBLE
                        } else {
                            inStockCheckIcon!!.visibility = View.GONE
                        }
                        /*var stock_string = ""
                        if (stock) {
                            stock_string = getString(R.string.storage_in_storage_yes)
                        } else {
                            stock_string = getString(R.string.storage_in_storage_no)
                        }*/
                        //hide progress bar
                        progressBar!!.visibility = View.GONE
                        //hide keyboard
                        if (isKeyBoardShow) {
                            val hideIntent = Intent()
                            hideIntent.action = Constants.ACTION.ACTION_HIDE_KEYBOARD
                            storageContext!!.sendBroadcast(hideIntent)
                        }

                        val item0 = StorageDetailItem("入庫單號", itemStorage!!.rjStorage!!.rvv01)
                        storageDetailList.add(item0)
                        val item1 = StorageDetailItem("廠商", itemStorage!!.rjStorage!!.pmc03)
                        storageDetailList.add(item1)
                        val item2 = StorageDetailItem("料號", itemStorage!!.rjStorage!!.rvb05)
                        storageDetailList.add(item2)
                        val item3 = StorageDetailItem("品名", itemStorage!!.rjStorage!!.rvb051)
                        storageDetailList.add(item3)
                        val item4 = StorageDetailItem("倉庫", itemStorage!!.rjStorage!!.rvb36)
                        storageDetailList.add(item4)
                        val item5 = StorageDetailItem("儲位", itemStorage!!.rjStorage!!.rvb37)
                        storageDetailList.add(item5)
                        //交貨量+單位
                        val content1 = itemStorage!!.rjStorage!!.rvb07 +"  "+itemStorage!!.rjStorage!!.rvb90

                        val item6 = StorageDetailItem("交貨量", content1)
                        storageDetailList.add(item6)
                        //允收量+單位
                        val content2 = itemStorage!!.rjStorage!!.rvb33 +"  "+itemStorage!!.rjStorage!!.rvb90

                        val item7 = StorageDetailItem("允收量", content2)
                        storageDetailList.add(item7)

                        val item8 = StorageDetailItem("批號", itemStorage!!.rjStorage!!.rvb38)
                        storageDetailList.add(item8)

                        //val item9 = StorageDetailItem("入庫情形", stock_string)
                        //storageDetailList.add(item9)
                        /*val item1 = StorageDetailItem("收貨單", MainActivity.itemStorage!!.rjStorage!!.rvb01)
                        storageDetailList.add(item1)
                        val item2 = StorageDetailItem("項次", MainActivity.itemStorage!!.rjStorage!!.rvb02)
                        storageDetailList.add(item2)
                        val item3 = StorageDetailItem("稅率", MainActivity.itemStorage!!.rjStorage!!.pmm43)
                        storageDetailList.add(item3)

                        val item5 = StorageDetailItem("簡稱", MainActivity.itemStorage!!.rjStorage!!.pmc03)
                        storageDetailList.add(item5)

                        val item15 = StorageDetailItem("項次", MainActivity.itemStorage!!.rjStorage!!.rvv02)
                        storageDetailList.add(item15)*/


                        if (storageDetailItemAdapter != null) {
                            storageDetailItemAdapter?.notifyDataSetChanged()
                        }

                        //set button to default
                        /*btnUpload!!.isEnabled = isWifiConnected
                        btnUpload!!.text = getString(R.string.storage_upload)
                        btnUpload!!.setTextColor(buttonTextColor)
                        btnUpload!!.setBackgroundResource(android.R.drawable.btn_default)
                        btnUpload!!.visibility = View.VISIBLE*/
                        showButtonStatusByState()

                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_STORAGE_RECEIPT_NO_NOT_EXIST, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_STORAGE_RECEIPT_NO_NOT_EXIST")

                        progressBar!!.visibility = View.GONE

                        //btnUpload!!.visibility = View.GONE
                        //inStockCheckIcon!!.visibility = View.GONE
                        showButtonStatusByState()
                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_STORAGE_UPLOADED_CANNOT_LOAD, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_STORAGE_UPLOADED_CANNOT_LOAD")

                        progressBar!!.visibility = View.GONE

                        //btnUpload!!.visibility = View.GONE
                        //inStockCheckIcon!!.visibility = View.GONE
                        showButtonStatusByState()
                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_STORAGE_SCAN_BARCODE, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_STORAGE_SCAN_BARCODE")

                        val barcode: String = intent.getStringExtra("BARCODE") as String
                        barcodeInput!!.setText(barcode)

                        //hide upload button first
                        btnUpload!!.visibility = View.GONE
                        progressBar!!.visibility = View.VISIBLE
                        inStockCheckIcon!!.visibility = View.GONE

                        storageDetailList.clear()
                        if (storageDetailItemAdapter != null) {
                            storageDetailItemAdapter?.notifyDataSetChanged()
                        }
                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_STORAGE_UPLOAD_FAILED, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_STORAGE_UPLOAD_FAILED")

                        progressBar!!.visibility = View.GONE

                        //btnUpload!!.visibility = View.VISIBLE
                        //btnUpload!!.isEnabled = isWifiConnected
                        showButtonStatusByState()
                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_STORAGE_UPLOAD_SUCCESS, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_STORAGE_UPLOAD_SUCCESS")

                        val rvu01 = intent.getStringExtra("RVU01")

                        if (rvu01 != null) {
                            //in-stock no
                            storageDetailList[0].setContent(rvu01)
                            inStockCheckIcon!!.visibility = View.VISIBLE
                            //upload status
                            //storageDetailList[9].setContent(getString(R.string.storage_in_storage_yes))
                            listView!!.invalidateViews()
                        }

                        //hide progress bar
                        progressBar!!.visibility = View.GONE

                        //set button uploaded
                        /*btnUpload!!.text = getString(R.string.btn_status_uploaded)
                        btnUpload!!.setTextColor(Color.BLACK)
                        btnUpload!!.setBackgroundColor(Color.rgb(0xf9, 0xa8, 0x25)) //md_yellow_800
                        //diable button
                        btnUpload!!.isEnabled = false*/
                        showButtonStatusByState()
                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_STORAGE_UPLOAD_RETURN_EXCEPTION, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_STORAGE_UPLOAD_RETURN_EXCEPTION")

                        //hide progress bar
                        progressBar!!.visibility = View.GONE

                        //btnUpload!!.visibility = View.VISIBLE
                        //btnUpload!!.isEnabled = true
                        showButtonStatusByState()
                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_STORAGE_MODIFY_NO_CHANGED, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_STORAGE_MODIFY_NO_CHANGED")

                        val idx = intent.getIntExtra("INDEX", 0)

                        storageDetailList[idx].getTextView()!!.visibility = View.VISIBLE
                        storageDetailList[idx].getLinearLayout()!!.visibility = View.GONE
                        listView!!.invalidateViews()

                        if (isKeyBoardShow) {
                            val hideIntent = Intent()
                            hideIntent.action = Constants.ACTION.ACTION_HIDE_KEYBOARD
                            storageContext!!.sendBroadcast(hideIntent)
                        }

                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_STORAGE_MODIFY_CHANGED, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_STORAGE_MODIFY_CHANGED")

                        val idx = intent.getIntExtra("INDEX", 0)
                        val content = intent.getStringExtra("CONTENT")

                        Log.e(mTAG, "idx = $idx  content = $content")

                        if (itemStorage != null) {
                            if (content != null) {
                                when(idx) {
                                    4-> {
                                        itemStorage!!.rjStorage!!.rvb36 = content
                                    }
                                    5-> {
                                        itemStorage!!.rjStorage!!.rvb37 = content
                                    }
                                }
                            }

                            Log.d(mTAG, "[new item start]")
                            Log.d(mTAG, "倉庫 = "+itemStorage!!.rjStorage!!.rvb36)
                            Log.d(mTAG, "儲位 = "+itemStorage!!.rjStorage!!.rvb37)
                            Log.d(mTAG, "[new item end]")
                        }

                        //receiptDetailList[idx].getTextView()!!.setTextColor(Color.BLUE)
                        storageDetailList[idx].getTextView()!!.visibility = View.VISIBLE
                        storageDetailList[idx].getLinearLayout()!!.visibility = View.GONE
                        listView!!.invalidateViews()

                        if (isKeyBoardShow) {
                            val hideIntent = Intent()
                            hideIntent.action = Constants.ACTION.ACTION_HIDE_KEYBOARD
                            storageContext!!.sendBroadcast(hideIntent)
                        }
                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_WIFI_STATE_CHANGED, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_WIFI_STATE_CHANGED")

                        showButtonStatusByState()
                    }
                }
            }
        }

        if (!isRegister) {
            filter = IntentFilter()
            filter.addAction(Constants.ACTION.ACTION_BARCODE_NULL)
            filter.addAction(Constants.ACTION.ACTION_NETWORK_FAILED)
            filter.addAction(Constants.ACTION.ACTION_CONNECTION_TIMEOUT)
            filter.addAction(Constants.ACTION.ACTION_STORAGE_FRAGMENT_REFRESH)
            filter.addAction(Constants.ACTION.ACTION_STORAGE_RECEIPT_NO_NOT_EXIST)
            filter.addAction(Constants.ACTION.ACTION_STORAGE_UPLOADED_CANNOT_LOAD)
            filter.addAction(Constants.ACTION.ACTION_STORAGE_SCAN_BARCODE)
            filter.addAction(Constants.ACTION.ACTION_STORAGE_UPLOAD_FAILED)
            filter.addAction(Constants.ACTION.ACTION_STORAGE_UPLOAD_SUCCESS)
            filter.addAction(Constants.ACTION.ACTION_STORAGE_UPLOAD_RETURN_EXCEPTION)
            filter.addAction(Constants.ACTION.ACTION_STORAGE_MODIFY_CHANGED)
            filter.addAction(Constants.ACTION.ACTION_STORAGE_MODIFY_NO_CHANGED)
            filter.addAction(Constants.ACTION.ACTION_WIFI_STATE_CHANGED)
            storageContext?.registerReceiver(mReceiver, filter)
            isRegister = true
            Log.d(mTAG, "registerReceiver mReceiver")
        }

        return view
    }

    override fun onDestroyView() {
        Log.i(mTAG, "onDestroy")

        if (isRegister && mReceiver != null) {
            try {
                storageContext!!.unregisterReceiver(mReceiver)
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

        val toast = Toast.makeText(storageContext, HtmlCompat.fromHtml("<h1>$message</h1>", HtmlCompat.FROM_HTML_MODE_COMPACT), Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER_HORIZONTAL or Gravity.CENTER_VERTICAL, 0, 0)

        /*val toast = Toast.makeText(storageContext, message, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER_HORIZONTAL or Gravity.CENTER_VERTICAL, 0, 0)
        val group = toast.view as ViewGroup
        val textView = group.getChildAt(0) as TextView
        textView.textSize = 30.0f*/
        toast.show()

        toastHandle = toast
    }

    private fun showUploadToERPDialog() {

        // get prompts.xml view
        /*LayoutInflater layoutInflater = LayoutInflater.from(Nfc_read_app.this);
        View promptView = layoutInflater.inflate(R.layout.input_dialog, null);*/
        val promptView = View.inflate(storageContext, R.layout.confirm_dialog, null)

        val alertDialogBuilder = AlertDialog.Builder(storageContext).create()
        alertDialogBuilder.setView(promptView)

        //final EditText editFileName = (EditText) promptView.findViewById(R.id.editFileName);
        val textViewMsg = promptView.findViewById<TextView>(R.id.textViewDialog)
        val btnCancel = promptView.findViewById<Button>(R.id.btnDialogCancel)
        val btnConfirm = promptView.findViewById<Button>(R.id.btnDialogConfirm)

        textViewMsg.text = getString(R.string.storage_upload_to_erp_msg)
        btnCancel.text = getString(R.string.cancel)
        btnConfirm.text = getString(R.string.confirm)

        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
        btnCancel!!.setOnClickListener {
            alertDialogBuilder.dismiss()
        }
        btnConfirm!!.setOnClickListener {
            progressBar!!.visibility = View.VISIBLE
            btnUpload!!.isEnabled = false

            val updateIntent = Intent()
            updateIntent.action = Constants.ACTION.ACTION_STORAGE_UPDATE_ACTION
            storageContext!!.sendBroadcast(updateIntent)

            alertDialogBuilder.dismiss()
        }
        alertDialogBuilder.show()

        /*
        val confirmDialog = AlertDialog.Builder(storageContext)
            confirmDialog.setIcon(R.drawable.baseline_warning_black_48)
            confirmDialog.setTitle(resources.getString(R.string.storage_upload))
            confirmDialog.setMessage(resources.getString(R.string.storage_upload_to_erp_msg))
            confirmDialog.setPositiveButton(
                resources.getString(R.string.ok)
            ) { _, _ ->
                progressBar!!.visibility = View.VISIBLE
                btnUpload!!.isEnabled = false

                val updateIntent = Intent()
                updateIntent.action = Constants.ACTION.ACTION_STORAGE_UPDATE_ACTION
                storageContext!!.sendBroadcast(updateIntent)
            }
            confirmDialog.setNegativeButton(
                resources.getString(R.string.cancel)
            ) { _, _ -> }
            confirmDialog.show()
         */
    }

    private fun showButtonStatusByState() {
        Log.d(mTAG, "=== storage showButtonStatusByState start === ")

        if (itemStorage != null) {
            Log.d(mTAG, "itemStorage.state = "+itemStorage!!.state)

            if (itemStorage!!.state == ItemStorage.ItemState.INITIAL || itemStorage!!.state == ItemStorage.ItemState.UPLOAD_FAILED) {
                //set button to default
                btnUpload!!.isEnabled = isWifiConnected
                btnUpload!!.text = getString(R.string.storage_upload)
                btnUpload!!.setTextColor(buttonTextColor)
                btnUpload!!.setBackgroundResource(android.R.drawable.btn_default)
                btnUpload!!.visibility = View.VISIBLE
            } else { //uploaded
                //set button uploaded
                btnUpload!!.text = getString(R.string.btn_status_uploaded)
                btnUpload!!.setTextColor(Color.BLACK)
                btnUpload!!.setBackgroundColor(Color.rgb(0xf9, 0xa8, 0x25)) //md_yellow_800
                btnUpload!!.isEnabled = false
                btnUpload!!.visibility = View.VISIBLE
            }

        } else {
            Log.e(mTAG, "itemStorage = null")
            btnUpload!!.visibility = View.GONE
        }



        Log.d(mTAG, "=== storage showButtonStatusByState end === ")
    }
}