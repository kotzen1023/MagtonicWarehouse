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
import android.text.InputType
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import com.magtonic.magtonicwarehouse.MainActivity
import com.magtonic.magtonicwarehouse.MainActivity.Companion.isKeyBoardShow
import com.magtonic.magtonicwarehouse.MainActivity.Companion.isOutSourcedInDetail
import com.magtonic.magtonicwarehouse.MainActivity.Companion.outsourcedProcessOrderList
import com.magtonic.magtonicwarehouse.MainActivity.Companion.outsourcedProcessOrderListBySupplier

import com.magtonic.magtonicwarehouse.R
import com.magtonic.magtonicwarehouse.SignActivity
import com.magtonic.magtonicwarehouse.data.*
import java.util.*
import kotlin.collections.ArrayList


class OutsourcedProcessingFragment : Fragment() {
    private val mTAG = OutsourcedProcessingFragment::class.java.name
    private var outsourcedProcessContext: Context? = null

    //private var outsourcedProcessLowerPartItemAdapter: OutsourcedProcessLowerPartItemAdapter? = null
    //private var outsourcedProcessTopItemAdapter: OutsourcedProcessTopItemAdapter? = null
    private var outsourcedProcessOrderDetailItemAdapter: OutsourcedProcessOrderDetailItemAdapter? = null
    private var outsourcedProcessSupplierItemAdapter: OutsourcedProcessSupplierItemAdapter? = null
    //private var btnOutsourcedProcessMain: Button? = null
    //private var btnOutsourcedProcessLower: Button? = null

    private var btnSign: Button? = null

    private var barcodeInput: EditText? = null
    private var linearLayout: LinearLayout? = null
    private var linearLayoutSupplierHeader: LinearLayout? = null
    private var linearLayoutDetailHeader: LinearLayout? = null
    private var viewLine: View?=null
    private var layoutLowerItemHeader: LinearLayout? = null
    //private var listView: ListView? = null
    private var listViewBySupplier: ListView? = null
    private var listViewDetail: ListView? = null
    private var progressBar: ProgressBar? = null
    private var relativeLayout: RelativeLayout? = null

    var outsourcedProcessTopList = ArrayList<OutsourcedProcessTopItem>()
    var outsourcedProcessLowerList = ArrayList<OutsourcedProcessLowerPartItem>()
    var outsourcedProcessDetailList = ArrayList<OutsourcedProcessOrderDetailItem>()
    var outsourcedProcessListBySupplier = ArrayList<OutsourcedProcessSupplierItem>()

    private var listViewTop: ListView?= null
    private var listViewLower: ListView?= null
    private var mReceiver: BroadcastReceiver? = null
    private var isRegister = false

    private var currentClickItem: Int = 0
    private var toastHandle: Toast? = null

    private val colorCodePink = Color.parseColor("#D81B60")
    private val colorCodeBlue = Color.parseColor("#1976D2")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.e(mTAG, "onCreate")

        outsourcedProcessContext = context

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Log.d(mTAG, "onCreateView")

        val view = inflater.inflate(R.layout.fragment_outsourced_process, container, false)

        relativeLayout = view.findViewById(R.id.outsourced_process_container)
        linearLayout = view.findViewById(R.id.linearLayoutOutsourcedProcess)
        linearLayoutSupplierHeader = view.findViewById(R.id.linearLayoutSupplierHeader)
        linearLayoutDetailHeader = view.findViewById(R.id.linearLayoutDetailHeader)
        viewLine = view.findViewById(R.id.viewLine)
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
        barcodeInput = view.findViewById(R.id.editTextOutsourcedProcess)
        //btnOutsourcedProcessMain = view.findViewById(R.id.btnOutsourcedProcessMain)
        //btnOutsourcedProcessLower = view.findViewById(R.id.btnOutsourcedProcessLower)
        //btnUpload = view.findViewById(R.id.btnUpload)
        //buttonTextColor = btnUpload!!.textColors
        //btnSign = view.findViewById(R.id.btnOutsourcedProcessSign)
        //listViewTop = view!!.findViewById(R.id.listViewOutsourcedProcessTop)

        listViewBySupplier = view!!.findViewById(R.id.listViewOutsourceListBySupplier)
        listViewDetail = view!!.findViewById(R.id.listViewOutsourceDetail)

        listViewBySupplier!!.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            Log.d(mTAG, "click $position")


        }

        listViewBySupplier!!.onItemLongClickListener = AdapterView.OnItemLongClickListener { _, _, position, _ -> // Process the long-click

            Log.d(mTAG, "long click $position")

            progressBar!!.indeterminateTintList = ColorStateList.valueOf(colorCodePink)
            progressBar!!.visibility = View.VISIBLE

            if (outsourcedProcessDetailList.size > 0) {
                outsourcedProcessDetailList.clear()
            }

            if (outsourcedProcessOrderDetailItemAdapter != null) {
                outsourcedProcessOrderDetailItemAdapter!!.notifyDataSetChanged()
            }

            val searchIntent = Intent()
            searchIntent.action = Constants.ACTION.ACTION_OUTSOURCED_PROCESS_GET_DETAIL_BY_SEND_ORDER
            searchIntent.putExtra("SEND_ORDER", outsourcedProcessListBySupplier[position].getData2())
            outsourcedProcessContext?.sendBroadcast(searchIntent)

            true
        }

        listViewDetail!!.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            Log.d(mTAG, "click $position")


        }

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
            outsourcedProcessOrderDetailItemAdapter = OutsourcedProcessOrderDetailItemAdapter(outsourcedProcessContext, R.layout.fragment_outsourced_process_order_item, outsourcedProcessDetailList)
            listViewDetail!!.adapter = outsourcedProcessOrderDetailItemAdapter
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

            /*if (isKeyBoardShow) {
                if (outsourcedProcessLowerList.size > 0) {
                    outsourcedProcessLowerList[currentClickItem].getEditText()!!.requestFocus()
                }

            }*/
        }





        barcodeInput!!.setOnEditorActionListener { _, actionId, _ ->

            when(actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    Log.e(mTAG, "IME_ACTION_DONE")

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
                    if (outsourcedProcessOrderDetailItemAdapter != null) {
                        outsourcedProcessOrderDetailItemAdapter?.notifyDataSetChanged()
                    }

                    listViewBySupplier!!.visibility = View.VISIBLE
                    listViewDetail!!.visibility = View.GONE

                    isOutSourcedInDetail = false

                    val searchIntent = Intent()
                    searchIntent.action = Constants.ACTION.ACTION_USER_INPUT_SEARCH
                    searchIntent.putExtra("INPUT_NO", barcodeInput!!.text.toString().toUpperCase(Locale.getDefault()))
                    outsourcedProcessContext?.sendBroadcast(searchIntent)

                    true
                }

                EditorInfo.IME_ACTION_GO -> {
                    Log.e(mTAG, "IME_ACTION_GO")
                    true
                }

                EditorInfo.IME_ACTION_NEXT -> {
                    Log.e(mTAG, "IME_ACTION_NEXT")

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
                    if (outsourcedProcessOrderDetailItemAdapter != null) {
                        outsourcedProcessOrderDetailItemAdapter?.notifyDataSetChanged()
                    }

                    listViewBySupplier!!.visibility = View.VISIBLE
                    listViewDetail!!.visibility = View.GONE

                    isOutSourcedInDetail = false

                    val searchIntent = Intent()
                    searchIntent.action = Constants.ACTION.ACTION_USER_INPUT_SEARCH
                    searchIntent.putExtra("INPUT_NO", barcodeInput!!.text.toString().toUpperCase(Locale.getDefault()))
                    outsourcedProcessContext?.sendBroadcast(searchIntent)

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

                        //showButtonStatusByState()

                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_OUTSOURCED_PROCESS_FRAGMENT_REFRESH, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_OUTSOURCED_PROCESS_FRAGMENT_REFRESH")

                        progressBar!!.visibility = View.GONE

                        if (isKeyBoardShow) {
                            val hideIntent = Intent()
                            hideIntent.action = Constants.ACTION.ACTION_HIDE_KEYBOARD
                            outsourcedProcessContext!!.sendBroadcast(hideIntent)
                        }

                        for (rjOutSourceProcessedBySupplier in outsourcedProcessOrderListBySupplier) {
                            var found: Boolean = false
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

                        }

                        if (outsourcedProcessListBySupplier.size > 0) {
                            linearLayoutSupplierHeader!!.visibility = View.VISIBLE
                            viewLine!!.visibility = View.VISIBLE
                        }


                        if (outsourcedProcessSupplierItemAdapter != null) {
                            outsourcedProcessSupplierItemAdapter?.notifyDataSetChanged()
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

                            val outsourcedProcessDetailItem = OutsourcedProcessOrderDetailItem(rjOutSourceProcessed.data1, rjOutSourceProcessed.data2, rjOutSourceProcessed.data3, rjOutSourceProcessed.data4,
                                rjOutSourceProcessed.data5, rjOutSourceProcessed.data6, rjOutSourceProcessed.data7, rjOutSourceProcessed.data8)
                            outsourcedProcessDetailList.add(outsourcedProcessDetailItem)
                        }

                        if (outsourcedProcessDetailList.size > 0) {
                            linearLayoutDetailHeader!!.visibility = View.VISIBLE
                            linearLayoutSupplierHeader!!.visibility = View.GONE
                            //viewLine!!.visibility = View.VISIBLE
                            listViewBySupplier!!.visibility = View.GONE
                            listViewDetail!!.visibility = View.VISIBLE

                            isOutSourcedInDetail = true
                        }


                        if (outsourcedProcessOrderDetailItemAdapter != null) {
                            outsourcedProcessOrderDetailItemAdapter?.notifyDataSetChanged()
                        }



                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_OUTSOURCED_PROCESS_BACK_TO_SUPPLIER_LIST, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_OUTSOURCED_PROCESS_BACK_TO_SUPPLIER_LIST")

                        linearLayoutDetailHeader!!.visibility = View.GONE
                        linearLayoutSupplierHeader!!.visibility = View.VISIBLE
                        //viewLine!!.visibility = View.VISIBLE
                        listViewBySupplier!!.visibility = View.VISIBLE
                        listViewDetail!!.visibility = View.GONE

                        isOutSourcedInDetail = false
                    }

                }
            }
        }

        if (!isRegister) {
            filter = IntentFilter()
            filter.addAction(Constants.ACTION.ACTION_BARCODE_NULL)
            filter.addAction(Constants.ACTION.ACTION_NETWORK_FAILED)
            filter.addAction(Constants.ACTION.ACTION_CONNECTION_TIMEOUT)
            filter.addAction(Constants.ACTION.ACTION_OUTSOURCED_PROCESS_FRAGMENT_REFRESH)
            filter.addAction(Constants.ACTION.ACTION_OUTSOURCED_PROCESS_FRAGMENT_DETAIL_REFRESH)
            filter.addAction(Constants.ACTION.ACTION_OUTSOURCED_PROCESS_BACK_TO_SUPPLIER_LIST)
            outsourcedProcessContext?.registerReceiver(mReceiver, filter)
            isRegister = true
            Log.d(mTAG, "registerReceiver mReceiver")
        }

        return view
    }

    override fun onDestroyView() {
        Log.i(mTAG, "onDestroy")

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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        Log.i(mTAG, "onActivityCreated")
        super.onActivityCreated(savedInstanceState)

    }

    private fun toast(message: String) {

        if (toastHandle != null)
            toastHandle!!.cancel()

        val toast = Toast.makeText(outsourcedProcessContext, message, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER_HORIZONTAL or Gravity.CENTER_VERTICAL, 0, 0)
        val group = toast.view as ViewGroup
        val textView = group.getChildAt(0) as TextView
        textView.textSize = 30.0f
        toast.show()

        toastHandle = toast
    }

    private fun showUploadToERPDialog(part_no: String, position: Int) {

        // get prompts.xml view
        /*LayoutInflater layoutInflater = LayoutInflater.from(Nfc_read_app.this);
        View promptView = layoutInflater.inflate(R.layout.input_dialog, null);*/
        val promptView = View.inflate(outsourcedProcessContext, R.layout.fragment_outsourced_process_lower_dialog, null)

        val alertDialogBuilder = AlertDialog.Builder(outsourcedProcessContext).create()
        alertDialogBuilder.setView(promptView)

        //final EditText editFileName = (EditText) promptView.findViewById(R.id.editFileName);
        val textViewMsg = promptView.findViewById<TextView>(R.id.textViewDialogLower)
        val textViewShouldHeader = promptView.findViewById<TextView>(R.id.textViewDialogLowerShouldOfferHeader)
        val textViewShouldContent = promptView.findViewById<TextView>(R.id.textViewDialogLowerShouldOfferContent)
        val textViewActualHeader = promptView.findViewById<TextView>(R.id.textViewDialogLowerActualOfferHeader)
        val textViewActualContent = promptView.findViewById<EditText>(R.id.textViewDialogLowerActualOfferContent)
        val btnCancel = promptView.findViewById<Button>(R.id.btnDialogCancel)
        val btnConfirm = promptView.findViewById<Button>(R.id.btnDialogConfirm)

        Log.e(mTAG, "getContentStatic = ${outsourcedProcessLowerList[position].getContentStatic()}")
        textViewMsg.text = part_no
        textViewShouldContent.text = outsourcedProcessLowerList[position].getContentStatic()
        textViewActualContent.inputType = (InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL)
        textViewActualContent.setText(outsourcedProcessLowerList[position].getContentDynamic())

        btnCancel.text = getString(R.string.cancel)
        btnConfirm.text = getString(R.string.confirm)

        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
        btnCancel!!.setOnClickListener {
            val noModifyIntent = Intent()
            noModifyIntent.action = Constants.ACTION.ACTION_OUTSOURCED_PROCESS_NO_CHANGED
            noModifyIntent.putExtra("INDEX", position)
            outsourcedProcessContext!!.sendBroadcast(noModifyIntent)

            alertDialogBuilder.dismiss()
        }
        btnConfirm!!.setOnClickListener {



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
            }


        }
        alertDialogBuilder.show()


    }
}