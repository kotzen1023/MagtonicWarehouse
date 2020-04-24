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
//import android.support.v4.view.ViewPager
import androidx.viewpager.widget.ViewPager
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.viewpager.widget.PagerAdapter
import com.magtonic.magtonicwarehouse.MainActivity
import com.magtonic.magtonicwarehouse.MainActivity.Companion.isKeyBoardShow
import com.magtonic.magtonicwarehouse.MainActivity.Companion.materialList
import com.magtonic.magtonicwarehouse.R
import com.magtonic.magtonicwarehouse.data.Constants


import com.magtonic.magtonicwarehouse.data.MaterialDetailAdapter
import com.magtonic.magtonicwarehouse.model.receive.RJMaterial
import java.util.*

import kotlin.collections.ArrayList

class MaterialIssuingFragment : Fragment(), ViewPager.OnPageChangeListener{


    private val mTAG = MaterialIssuingFragment::class.java.name

    private var materialIssuingContext: Context? = null

    private var progressBar: ProgressBar? = null
    private var relativeLayout: RelativeLayout? = null
    //private var materialDetailItemAdapter: MaterialDetailItemAdapter? = null
    private var barcodeInput: EditText? = null

    private var linearLayout: LinearLayout? = null

    //private var listView: ListView ?= null

    private var mReceiver: BroadcastReceiver? = null
    private var isRegister = false

    //private var spinnerNum: Spinner? = null
    //var spinnerAdapter: ArrayAdapter<String>? = null
    //var spinnerList = ArrayList<String>()
    //private var currentSpinnerSelect: Int = 0
    var viewPager: ViewPager? = null
    var pagerAdapter : MaterialDetailAdapter? = null

    companion object {
        @JvmStatic var currentMaterialPage: Int = 0
        @JvmStatic var itemClick: Boolean = false
        @JvmStatic val itemCanEdit: Int = 3
    }

    //var materialDetailList = ArrayList<MaterialDetailItem>()
    //private var currentClickItem: Int = 0

    private var layoutBottom: LinearLayout? = null
    private var btnClear: Button? = null
    private var btnSave: Button? = null

    private val colorCodePink = Color.parseColor("#D81B60")
    //private val colorCodeBlue = Color.parseColor("#1976D2")

    private var toastHandle: Toast? = null
    var modifyList: ArrayList<Boolean> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(mTAG, "onCreate")

        materialIssuingContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(mTAG, "onCreateView")

        val view = inflater.inflate(R.layout.fragment_material_issuing, container, false)
        relativeLayout = view.findViewById(R.id.material_issuing_container)
        progressBar = ProgressBar(materialIssuingContext, null, android.R.attr.progressBarStyleLarge)
        val params = RelativeLayout.LayoutParams(MainActivity.screenHeight / 4, MainActivity.screenWidth / 4)
        params.addRule(RelativeLayout.CENTER_IN_PARENT)

        val localRelativeLayout: RelativeLayout? = relativeLayout
        if (localRelativeLayout != null) {
            localRelativeLayout.addView(progressBar, params)
        } else {
            Log.e(mTAG, "localRelativeLayout = null")
        }
        progressBar!!.visibility = View.GONE

        layoutBottom = view.findViewById(R.id.layoutBottom)
        layoutBottom!!.visibility = View.GONE

        viewPager = view!!.findViewById(R.id.viewPager)
        if (materialIssuingContext != null) {

            materialList.clear()
            modifyList.clear()


            val item0 = RJMaterial()
            item0.sfs01 = "item0_sfs01"
            item0.sfs02 = "item0_sfs02"
            item0.sfs04 = "item0_sfs04"
            item0.sfs05 = "item0_sfs05"
            //item0.sfs05_old = "item0_sfs05_old"
            item0.sfs07 = "item0_sfs07"
            item0.sfs08 = "item0_sfs08"
            item0.sfs09 = "item0_sfs09"
            item0.sfa05 = "item0_sfa05"
            item0.ima02 = "item0_ima02"
            item0.ima021 = "item0_ima021"
            item0.img10 = "item0_img10"
            materialList.add(item0)
            val item1 = RJMaterial()
            item1.sfs01 = "item1_sfs01"
            item1.sfs02 = "item1_sfs02"
            item1.sfs04 = "item1_sfs04"
            item1.sfs05 = "item1_sfs05"
            //item1.sfs05_old = "item1_sfs05_old"
            item1.sfs07 = "item1_sfs07"
            item1.sfs08 = "item1_sfs08"
            item1.sfs09 = "item1_sfs09"
            item1.sfa05 = "item1_sfa05"
            item1.ima02 = "item1_ima02"
            item1.ima021 = "item1_ima021"
            item1.img10 = "item1_img10"
            materialList.add(item1)
            val item2 = RJMaterial()
            item2.sfs01 = "item2_sfs01"
            item2.sfs02 = "item2_sfs02"
            item2.sfs04 = "item2_sfs04"
            item2.sfs05 = "item2_sfs05"
            //item2.sfs05_old = "item2_sfs05_old"
            item2.sfs07 = "item2_sfs07"
            item2.sfs08 = "item2_sfs08"
            item2.sfs09 = "item2_sfs09"
            item2.sfa05 = "item2_sfa05"
            item2.ima02 = "item2_ima02"
            item2.ima021 = "item2_ima021"
            item2.img10 = "item2_img10"
            materialList.add(item2)

            layoutBottom!!.visibility = View.VISIBLE

            val pageString = "第 1/"+ materialList.size +" 頁"
            toastPage(pageString)

            for (i in 0 until materialList.size) {
                modifyList.add(false)
            }

            pagerAdapter = MaterialDetailAdapter(materialIssuingContext, materialList, modifyList)
            viewPager!!.adapter = pagerAdapter as PagerAdapter
        }

        viewPager!!.addOnPageChangeListener(this)


        //detect soft keyboard
        linearLayout = view.findViewById(R.id.linearLayoutMaterial)
        /*linearLayout!!.viewTreeObserver.addOnGlobalLayoutListener(
            object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    val r = Rect()
                    linearLayout!!.getWindowVisibleDisplayFrame(r)
                    val screenHeight = linearLayout!!.getRootView().getHeight()
                    val keypadHeight = screenHeight - r.bottom
                    isKeyBoardShow = (keypadHeight > screenHeight * 0.15)

                    if (isKeyBoardShow) {
                        Log.e(mTAG, "->isKeyBoardShow true")

                        //pagerAdapter!!.materialDetailItemAdapter!!.getItem(3)!!.getTextView()!!.visibility = View.GONE
                        //pagerAdapter!!.materialDetailItemAdapter!!.getItem(3)!!.getLinearLayout()!!.visibility = View.VISIBLE



                        //materialListInArrayList[currentMaterialPage][3].getEditText()!!.requestFocus()

                        if (itemClick) { //if item pressed
                            if (pagerAdapter!!.currentItemIndexList.size > 0) {

                                //found index
                                for (i in 0 until pagerAdapter!!.currentItemIndexList.size) {
                                    if (pagerAdapter!!.currentItemIndexList[i] == currentMaterialPage) {
                                        //found position index
                                        Log.e(
                                            mTAG,
                                            "found position index[$i], content = " + pagerAdapter!!.materialDetailItemAdapterList[i].getItem(
                                                itemCanEdit
                                            )!!.getContent()
                                        )
                                        pagerAdapter!!.materialDetailItemAdapterList[i].getItem(itemCanEdit)!!.getEditText()!!.requestFocus()
                                    }
                                }
                            }
                        }


                    } else {
                        Log.e(mTAG, "->isKeyBoardShow false")
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
                Log.e(mTAG, "->isKeyBoardShow true")

                //pagerAdapter!!.materialDetailItemAdapter!!.getItem(3)!!.getTextView()!!.visibility = View.GONE
                //pagerAdapter!!.materialDetailItemAdapter!!.getItem(3)!!.getLinearLayout()!!.visibility = View.VISIBLE


                //materialListInArrayList[currentMaterialPage][3].getEditText()!!.requestFocus()

                if (itemClick) { //if item pressed
                    if (pagerAdapter!!.currentItemIndexList!!.size > 0) {

                        //found index
                        for (i in 0 until pagerAdapter!!.currentItemIndexList!!.size) {
                            if (pagerAdapter!!.currentItemIndexList!![i] == currentMaterialPage) {
                                //found position index
                                Log.e(
                                    mTAG,
                                    "found position index[$i], content = " + pagerAdapter!!.materialDetailItemAdapterList[i].getItem(
                                        itemCanEdit
                                    )!!.getContent()
                                )
                                pagerAdapter!!.materialDetailItemAdapterList[i].getItem(itemCanEdit)!!.getEditText()!!.requestFocus()
                            }
                        }
                    }
                }


            } else {
                Log.e(mTAG, "->isKeyBoardShow false")
            }
        }

        barcodeInput = view.findViewById(R.id.editTextMaterial)
        barcodeInput!!.setOnEditorActionListener { _, actionId, _ ->



            when(actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    Log.e(mTAG, "IME_ACTION_DONE")

                    val hideSeekBarIntent = Intent()
                    hideSeekBarIntent.action = Constants.ACTION.ACTION_SEEK_BAR_HIDE_ACTION
                    materialIssuingContext!!.sendBroadcast(hideSeekBarIntent)

                    barcodeInput!!.setText(barcodeInput!!.text.toString().toUpperCase(Locale.getDefault()))

                    progressBar!!.indeterminateTintList = ColorStateList.valueOf(colorCodePink)
                    progressBar!!.visibility = View.VISIBLE

                    //clear
                    if (pagerAdapter != null) {
                        //pagerAdapter!!.right_nav!!.visibility = View.GONE
                        //pagerAdapter!!.left_nav!!.visibility = View.GONE

                        for (i in 0 until pagerAdapter!!.materialDetailItemAdapterList.size) {
                            pagerAdapter!!.materialDetailItemAdapterList[i].items.clear()
                            pagerAdapter!!.materialDetailItemAdapterList[i].clear()
                            pagerAdapter!!.materialDetailItemAdapterList[i].notifyDataSetChanged()
                        }
                        pagerAdapter!!.destroyAllItems()

                        modifyList.clear()

                        materialList.clear()
                        pagerAdapter!!.notifyDataSetChanged()
                    }
                    pagerAdapter = null

                    currentMaterialPage = 0

                    btnSave!!.isEnabled = false
                    layoutBottom!!.visibility = View.GONE

                    val searchIntent = Intent()
                    searchIntent.action = Constants.ACTION.ACTION_USER_INPUT_SEARCH
                    searchIntent.putExtra("INPUT_NO", barcodeInput!!.text.toString())
                    materialIssuingContext?.sendBroadcast(searchIntent)

                    true
                }

                EditorInfo.IME_ACTION_GO -> {
                    Log.e(mTAG, "IME_ACTION_GO")
                    true
                }

                EditorInfo.IME_ACTION_NEXT -> {
                    Log.e(mTAG, "IME_ACTION_NEXT")

                    val hideSeekBarIntent = Intent()
                    hideSeekBarIntent.action = Constants.ACTION.ACTION_SEEK_BAR_HIDE_ACTION
                    materialIssuingContext!!.sendBroadcast(hideSeekBarIntent)

                    barcodeInput!!.setText(barcodeInput!!.text.toString().toUpperCase(Locale.getDefault()))

                    progressBar!!.indeterminateTintList = ColorStateList.valueOf(colorCodePink)
                    progressBar!!.visibility = View.VISIBLE

                    //clear
                    if (pagerAdapter != null) {
                        //pagerAdapter!!.right_nav!!.visibility = View.GONE
                        //pagerAdapter!!.left_nav!!.visibility = View.GONE

                        for (i in 0 until pagerAdapter!!.materialDetailItemAdapterList.size) {
                            pagerAdapter!!.materialDetailItemAdapterList[i].items.clear()
                            pagerAdapter!!.materialDetailItemAdapterList[i].clear()
                            pagerAdapter!!.materialDetailItemAdapterList[i].notifyDataSetChanged()
                        }
                        pagerAdapter!!.destroyAllItems()

                        modifyList.clear()

                        materialList.clear()
                        pagerAdapter!!.notifyDataSetChanged()

                    }
                    pagerAdapter = null

                    currentMaterialPage = 0

                    btnSave!!.isEnabled = false
                    layoutBottom!!.visibility = View.GONE

                    val searchIntent = Intent()
                    searchIntent.action = Constants.ACTION.ACTION_USER_INPUT_SEARCH
                    searchIntent.putExtra("INPUT_NO", barcodeInput!!.text.toString())
                    materialIssuingContext?.sendBroadcast(searchIntent)

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

        barcodeInput!!.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                Log.e(mTAG, "barcode input = false")
            } else {
                Log.e(mTAG, "barcode input = true")
                //if barcode input click, hide current show edittext
                if (itemClick) { //if item pressed

                    if (pagerAdapter != null) {
                        if (pagerAdapter!!.materialDetailItemAdapterList.size > 0) {

                            //find index
                            for (i in 0 until pagerAdapter!!.currentItemIndexList!!.size) {
                                if (pagerAdapter!!.currentItemIndexList!![i] == currentMaterialPage) {
                                    //found position index
                                    if (pagerAdapter!!.materialDetailItemAdapterList[i].count > 0) {

                                        pagerAdapter!!.materialDetailItemAdapterList[i].getItem(itemCanEdit)!!.getTextView()!!.visibility = View.VISIBLE
                                        pagerAdapter!!.materialDetailItemAdapterList[i].getItem(itemCanEdit)!!.getLinearLayout()!!.visibility = View.GONE
                                    }
                                }
                            }


                            /*if (pagerAdapter!!.materialDetailItemAdapterList[currentMaterialPage].count > 0) {

                                pagerAdapter!!.materialDetailItemAdapterList[currentMaterialPage].getItem(itemCanEdit)!!.getTextView()!!.visibility = View.VISIBLE
                                pagerAdapter!!.materialDetailItemAdapterList[currentMaterialPage].getItem(itemCanEdit)!!.getLinearLayout()!!.visibility = View.GONE
                            }*/
                        }
                    } else {
                        Log.e(mTAG, "pagerAdapter == null")
                    }


                }

                itemClick = false
            }
        }

        btnClear = view.findViewById(R.id.btnMaterialClear)
        btnClear!!.setOnClickListener {
            barcodeInput!!.setText("")

            Log.e(mTAG, "btnClear")

            if (pagerAdapter != null) {
                //pagerAdapter!!.right_nav!!.visibility = View.GONE
                //pagerAdapter!!.left_nav!!.visibility = View.GONE

                for (i in 0 until pagerAdapter!!.materialDetailItemAdapterList.size) {
                    pagerAdapter!!.materialDetailItemAdapterList[i].items.clear()
                    pagerAdapter!!.materialDetailItemAdapterList[i].clear()
                    pagerAdapter!!.materialDetailItemAdapterList[i].notifyDataSetChanged()
                }
                pagerAdapter!!.destroyAllItems()

                modifyList.clear()

                materialList.clear()
                pagerAdapter!!.notifyDataSetChanged()
            }
            pagerAdapter = null

            currentMaterialPage = 0

            btnSave!!.isEnabled = false
            layoutBottom!!.visibility = View.GONE

            val hideSeekBarIntent = Intent()
            hideSeekBarIntent.action = Constants.ACTION.ACTION_SEEK_BAR_HIDE_ACTION
            materialIssuingContext!!.sendBroadcast(hideSeekBarIntent)

            itemClick = false
        }

        btnSave = view.findViewById(R.id.btnMaterialSave)
        btnSave!!.setOnClickListener {

            showUpdateDialog()


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

                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_CONNECTION_TIMEOUT, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_CONNECTION_TIMEOUT")

                        toast(getString(R.string.upload_receipt_connection_timeout))

                        progressBar!!.visibility = View.GONE


                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_MATERIAL_SCAN_BARCODE, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_MATERIAL_SCAN_BARCODE")


                        val barcode = intent.getStringExtra("BARCODE")

                        barcodeInput!!.setText(barcode)

                        if (pagerAdapter != null) {
                            //pagerAdapter!!.right_nav!!.visibility = View.GONE
                            //pagerAdapter!!.left_nav!!.visibility = View.GONE

                            for (i in 0 until pagerAdapter!!.materialDetailItemAdapterList.size) {
                                pagerAdapter!!.materialDetailItemAdapterList[i].items.clear()
                                pagerAdapter!!.materialDetailItemAdapterList[i].clear()
                                pagerAdapter!!.materialDetailItemAdapterList[i].notifyDataSetChanged()
                            }
                            pagerAdapter!!.destroyAllItems()

                            modifyList.clear()

                            materialList.clear()
                            pagerAdapter!!.notifyDataSetChanged()
                        }
                        pagerAdapter = null

                        currentMaterialPage = 0

                        btnSave!!.isEnabled = false
                        layoutBottom!!.visibility = View.GONE

                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_MATERIAL_NO_NOT_EXIST, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_MATERIAL_NO_NOT_EXIST")

                        progressBar!!.visibility = View.GONE



                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_MATERIAL_FRAGMENT_REFRESH, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_MATERIAL_FRAGMENT_REFRESH")

                        progressBar!!.visibility = View.GONE

                        val pageString = "第 1/"+ materialList.size +" 頁"
                        toastPage(pageString)

                        for (i in 0 until materialList.size) {
                            modifyList.add(false)
                        }


                        //pagerAdapter = MaterialDetailAdapter(materialIssuingContext, materialList, modifyList)
                        //viewPager!!.adapter = pagerAdapter
                        if (pagerAdapter != null) {
                            Log.e(mTAG, "pagerAdapter != null")
                            pagerAdapter!!.notifyDataSetChanged()
                        } else {
                            Log.e(mTAG, "pagerAdapter == null")
                            pagerAdapter = MaterialDetailAdapter(materialIssuingContext, materialList, modifyList)
                            viewPager!!.adapter = pagerAdapter
                            //viewPager!!.currentItem = 3

                            //currentMaterialPage = 3
                        }

                        layoutBottom!!.visibility = View.VISIBLE

                        val showSeekBarIntent = Intent()
                        showSeekBarIntent.action = Constants.ACTION.ACTION_SEEK_BAR_SHOW_ACTION
                        materialIssuingContext!!.sendBroadcast(showSeekBarIntent)

                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_MATERIAL_MODIFY_NO_CHANGED, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_MATERIAL_MODIFY_NO_CHANGED")

                        //val idx = intent.getIntExtra("INDEX", 0)

                        /*materialDetailList[idx].getTextView()!!.visibility = View.VISIBLE
                        materialDetailList[idx].getLinearLayout()!!.visibility = View.GONE
                        listView!!.invalidateViews()*/

                        //pagerAdapter!!.materialDetailItemAdapter!!.getItem(3)!!.getTextView()!!.visibility = View.VISIBLE
                        //pagerAdapter!!.materialDetailItemAdapter!!.getItem(3)!!.getLinearLayout()!!.visibility = View.GONE
                        pagerAdapter!!.pagerListView!!.invalidateViews()

                        if (isKeyBoardShow) {
                            val hideIntent = Intent()
                            hideIntent.action = Constants.ACTION.ACTION_HIDE_KEYBOARD
                            materialIssuingContext!!.sendBroadcast(hideIntent)
                        }

                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_MATERIAL_MODIFY_CHANGED, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_MATERIAL_MODIFY_CHANGED")

                        val idx = intent.getIntExtra("INDEX", 0)
                        val content = intent.getStringExtra("CONTENT")

                        Log.e(mTAG, "idx = $idx  content = $content")

                        if (content != null) {


                            materialList[currentMaterialPage].sfs05 = content //change actually material send
                            modifyList[currentMaterialPage] = true
                        }

                        //pagerAdapter!!.materialDetailItemAdapter!!.getItem(3)!!.getTextView()!!.visibility = View.VISIBLE
                        //pagerAdapter!!.materialDetailItemAdapter!!.getItem(3)!!.getLinearLayout()!!.visibility = View.GONE
                        pagerAdapter!!.pagerListView!!.invalidateViews()

                        if (isKeyBoardShow) {
                            val hideIntent = Intent()
                            hideIntent.action = Constants.ACTION.ACTION_HIDE_KEYBOARD
                            materialIssuingContext!!.sendBroadcast(hideIntent)
                        }

                        //enable save
                        btnSave!!.isEnabled = true
                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_MATERIAL_SEND_COMPLETE_TO_FRAGMENT, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_MATERIAL_SEND_COMPLETE_TO_FRAGMENT")
                        progressBar!!.visibility = View.GONE
                        btnSave!!.isEnabled = true
                        //listView!!.invalidateViews()
                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_MATERIAL_SEND_CHANGED_ERROR, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_MATERIAL_SEND_CHANGED_ERROR")

                        progressBar!!.visibility = View.GONE
                        btnSave!!.isEnabled = true
                        //listView!!.invalidateViews()
                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_MATERIAL_REAL_SEND_CAN_NOT_MUCH_MORE_THAN_STORAGE, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_MATERIAL_REAL_SEND_CAN_NOT_MUCH_MORE_THAN_STORAGE")

                        toast(getString(R.string.material_quantity_can_not_more_than_storage))
                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_MATERIAL_QUANTITY_MUST_BE_INTEGER, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_MATERIAL_QUANTITY_MUST_BE_INTEGER")

                        toast(getString(R.string.material_quantity_cannot_be_with_dot))
                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_MATERIAL_QUANTITY_IN_STOCK_EMPTY, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_MATERIAL_QUANTITY_IN_STOCK_EMPTY")

                        toast(getString(R.string.quantity_in_stock_empty))
                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_SEEK_BAR_SELECT_PAGE_ACTION, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_SEEK_BAR_SELECT_PAGE_ACTION")

                        val page = intent.getIntExtra("PAGE", 0)

                        Log.e(mTAG, "page = $page")

                        if (viewPager != null) {
                            viewPager!!.currentItem = page
                            currentMaterialPage = page
                        }
                    }
                }
            }
        }

        if (!isRegister) {
            filter = IntentFilter()
            filter.addAction(Constants.ACTION.ACTION_BARCODE_NULL)
            filter.addAction(Constants.ACTION.ACTION_NETWORK_FAILED)
            filter.addAction(Constants.ACTION.ACTION_CONNECTION_TIMEOUT)
            filter.addAction(Constants.ACTION.ACTION_MATERIAL_SCAN_BARCODE)
            filter.addAction(Constants.ACTION.ACTION_MATERIAL_NO_NOT_EXIST)
            filter.addAction(Constants.ACTION.ACTION_MATERIAL_FRAGMENT_REFRESH)
            filter.addAction(Constants.ACTION.ACTION_MATERIAL_MODIFY_NO_CHANGED)
            filter.addAction(Constants.ACTION.ACTION_MATERIAL_MODIFY_CHANGED)
            filter.addAction(Constants.ACTION.ACTION_MATERIAL_SEND_COMPLETE_TO_FRAGMENT)
            filter.addAction(Constants.ACTION.ACTION_MATERIAL_SEND_CHANGED_ERROR)
            filter.addAction(Constants.ACTION.ACTION_MATERIAL_REAL_SEND_CAN_NOT_MUCH_MORE_THAN_STORAGE)
            filter.addAction(Constants.ACTION.ACTION_MATERIAL_QUANTITY_MUST_BE_INTEGER)
            filter.addAction(Constants.ACTION.ACTION_MATERIAL_QUANTITY_IN_STOCK_EMPTY)
            filter.addAction(Constants.ACTION.ACTION_SEEK_BAR_SELECT_PAGE_ACTION)
            materialIssuingContext?.registerReceiver(mReceiver, filter)
            isRegister = true
            Log.d(mTAG, "registerReceiver mReceiver")
        }

        return view
    }

    override fun onDestroyView() {
        Log.i(mTAG, "onDestroy")

        if (isRegister && mReceiver != null) {
            try {
                materialIssuingContext!!.unregisterReceiver(mReceiver)
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

    fun toast(message: String) {

        if (toastHandle != null) {
            toastHandle!!.cancel()
        }

        val toast = Toast.makeText(materialIssuingContext, message, Toast.LENGTH_SHORT)
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

        val toast = Toast.makeText(materialIssuingContext, message, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM, 0, 0)
        val group = toast.view as ViewGroup
        val textView = group.getChildAt(0) as TextView
        textView.textSize = 30.0f
        toast.show()

        toastHandle = toast
    }



    //page change listener
    override fun onPageSelected(position: Int) {

        Log.e(mTAG, "onPageSelected = $position")

        currentMaterialPage = position

        if (pagerAdapter!!.materialDetailItemAdapterList.size > 0) {

            for (i in 0 until pagerAdapter!!.materialDetailItemAdapterList.size) {

                if (pagerAdapter!!.materialDetailItemAdapterList[i].getItem(itemCanEdit)!!.getTextView() != null) {
                    pagerAdapter!!.materialDetailItemAdapterList[i].getItem(itemCanEdit)!!.getTextView()!!.visibility =
                        View.VISIBLE
                    pagerAdapter!!.materialDetailItemAdapterList[i].getItem(itemCanEdit)!!.getEditText()!!.setText(pagerAdapter!!.materialDetailItemAdapterList[i].getItem(itemCanEdit)!!.getContent())
                    pagerAdapter!!.materialDetailItemAdapterList[i].getItem(itemCanEdit)!!.getLinearLayout()!!.visibility =
                        View.GONE
                }


            }
        }

        /*if (pagerAdapter!!.materialDetailItemAdapterList.size > 0) {
            if (pagerAdapter!!.materialDetailItemAdapterList[currentMaterialPage].count > 0) {

                for (i in 0 until pagerAdapter!!.materialDetailItemAdapterList.size) {
                    pagerAdapter!!.materialDetailItemAdapterList[i].getItem(itemCanEdit)!!.getTextView()!!.visibility = View.VISIBLE
                    pagerAdapter!!.materialDetailItemAdapterList[i].getItem(itemCanEdit)!!.getLinearLayout()!!.visibility = View.GONE
                }


            }
        }*/

        val page = position + 1
        //var pageString = "第 "+ page + "/"+ materialList.size +" 頁"
        val pageString = getString(R.string.material_viewpage_page, page, materialList.size)
        toastPage(pageString)
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    private fun showUpdateDialog() {

        // get prompts.xml view
        /*LayoutInflater layoutInflater = LayoutInflater.from(Nfc_read_app.this);
        View promptView = layoutInflater.inflate(R.layout.input_dialog, null);*/
        val promptView = View.inflate(materialIssuingContext, R.layout.confirm_dialog, null)

        val alertDialogBuilder = AlertDialog.Builder(materialIssuingContext).create()
        alertDialogBuilder.setView(promptView)

        //final EditText editFileName = (EditText) promptView.findViewById(R.id.editFileName);
        val textViewMsg = promptView.findViewById<TextView>(R.id.textViewDialog)
        val btnCancel = promptView.findViewById<Button>(R.id.btnDialogCancel)
        val btnConfirm = promptView.findViewById<Button>(R.id.btnDialogConfirm)

        textViewMsg.text = getString(R.string.material_update_confirm)
        btnCancel.text = getString(R.string.cancel)
        btnConfirm.text = getString(R.string.confirm)

        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
        btnCancel!!.setOnClickListener {
            alertDialogBuilder.dismiss()
        }
        btnConfirm!!.setOnClickListener {
            progressBar!!.visibility = View.VISIBLE
            btnSave!!.isEnabled = false

            val updateIntent = Intent()
            updateIntent.action = Constants.ACTION.ACTION_MATERIAL_SEND_CHANGED_UPDATE_ACTION
            materialIssuingContext!!.sendBroadcast(updateIntent)

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
}