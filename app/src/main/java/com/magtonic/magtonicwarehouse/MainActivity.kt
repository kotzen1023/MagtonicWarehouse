package com.magtonic.magtonicwarehouse

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.*
import android.util.DisplayMetrics
import android.util.Log
import android.util.Xml
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import com.magtonic.magtonicwarehouse.api.ApiFunc
import com.magtonic.magtonicwarehouse.bluetoothchat.BluetoothChatService
import com.magtonic.magtonicwarehouse.bluetoothchat.DeviceListActivity
import com.magtonic.magtonicwarehouse.bluetoothchat.printer.BluetoothPrinterFuncs
import com.magtonic.magtonicwarehouse.data.Constants
import com.magtonic.magtonicwarehouse.data.Constants.BluetoothState.Companion.MESSAGE_DEVICE_NAME
import com.magtonic.magtonicwarehouse.data.Constants.BluetoothState.Companion.MESSAGE_READ
import com.magtonic.magtonicwarehouse.data.Constants.BluetoothState.Companion.MESSAGE_STATE_CHANGE
import com.magtonic.magtonicwarehouse.data.Constants.BluetoothState.Companion.MESSAGE_TOAST
import com.magtonic.magtonicwarehouse.data.Constants.BluetoothState.Companion.MESSAGE_WRITE
import com.magtonic.magtonicwarehouse.data.ReceiptConfirmFailLog
import com.magtonic.magtonicwarehouse.fragment.*
import com.magtonic.magtonicwarehouse.fragment.MaterialIssuingFragment.Companion.currentMaterialPage
import com.magtonic.magtonicwarehouse.fragment.PropertyFragment.Companion.currentPropertyPage
import com.magtonic.magtonicwarehouse.model.receive.*
import com.magtonic.magtonicwarehouse.model.send.*
import com.magtonic.magtonicwarehouse.model.sys.ScanBarcode
import com.magtonic.magtonicwarehouse.model.sys.User
import com.magtonic.magtonicwarehouse.model.ui.*
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.*
import java.lang.Process
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executor
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val mTAG = MainActivity::class.java.name



    private val requestIdMultiplePermission = 1

    private val requestConnectDeviceSecure = 1
   
    private val requestEnableBt = 3

    private val setPrinterDev = 5
    
    //private val uploadedListMax = 512

    /*enum class ReceiptACState {
        RECEIPT_AC_STATE_INITIAL, RECEIPT_INPUT_STATE, RECEIPT_EDIT_STATE, RECEIPT_GETTING_STATE, RECEIPT_UPLOADING_STATE
    }*/

    enum class CurrentFragment {
        RECEIPT_FRAGMENT, STORAGE_FRAGMENT, MATERIAL_ISSUING_FRAGMENT, HOME_FRAGMENT, PRINTER_FRAGMENT,
        LOGIN_FRAGMENT, PROPERTY_FRAGMENT, USER_SETTING_FRAGMENT, GUEST_FRAGMENT, DRAW_FRAGMENT, OUTSOURCED_FRAGMENT
    }

    //for printer
    enum class PrintStatus {
        PRINT_SUCCESS, PRINT_ERROR//, PRINT_WAITING
    }



    //for Log
    private var process: Process? = null

    //var acState: ReceiptACState = ReceiptACState.RECEIPT_AC_STATE_INITIAL
    var currentFrag: CurrentFragment = CurrentFragment.HOME_FRAGMENT
    var printStatus: PrintStatus = PrintStatus.PRINT_SUCCESS

    var pref: SharedPreferences? = null
    var editor: SharedPreferences.Editor? = null
    private val fileName = "Preference"

    private var mContext: Context? = null

    private var imm: InputMethodManager? = null

    private var mReceiver: BroadcastReceiver? = null
    private var isRegister = false

    private var menuItemKeyboard: MenuItem? = null
    private var menuItemBluetooth: MenuItem? = null
    private var menuItemSeekBar: MenuItem? = null
    private var menuItemReceiptSetting: MenuItem? = null
    private var menuItemEraser: MenuItem? = null
    private var menuItemOutSourcedSupplier: MenuItem? = null
    private var menuItemShowReceiptConfirmFailed: MenuItem? = null
    private var menuItemReconnectPrinter: MenuItem? = null
    private var menuItemPrintAgain: MenuItem? = null

    companion object {
        @JvmStatic var screenWidth: Int = 0
        @JvmStatic var screenHeight: Int = 0
        @JvmStatic var mConnectedDeviceName: String = ""
        @JvmStatic var printerAddress: String = ""
        @JvmStatic var printerStatus: Int = BluetoothChatService.STATE_NONE
        @JvmStatic var user: User? = null
        @JvmStatic var itemReceipt: ItemReceipt? = null //for receipt
        @JvmStatic var confirmFailLogList: ArrayList<ReceiptConfirmFailLog> = ArrayList()
        //@JvmStatic var isReceiptConfirmFailedLogInDetail: Int = 0
        //@JvmStatic var itemReceiptStorage: ItemStorage? = null //for storage
        @JvmStatic var isKeyBoardShow: Boolean = false
        @JvmStatic var isWifiConnected: Boolean = false
        @JvmStatic var isReceiptUploadAutoConfirm: Boolean = true
        @JvmStatic var currentSSID: String = ""
        //@JvmStatic var rjStorageList: StorageList.itemStorages? = null
        @JvmStatic var itemStorage: ItemStorage? = null
        @JvmStatic var materialList: ArrayList<RJMaterial> = ArrayList()
        @JvmStatic var seekBarCurrentPage: Int = 0
        @JvmStatic var propertyList: ArrayList<RJProperty> = ArrayList()
        @JvmStatic var isPropertyInDetail: Int = 0
        //@JvmStatic var materialListInArrayList: ArrayList<ArrayList<MaterialDetailItem>> = ArrayList()
        @JvmStatic var isSecurityGuard: Boolean = false
        //guest
        @JvmStatic var guestListT: ArrayList<RJGuest> = ArrayList()
        @JvmStatic var guestListA: ArrayList<RJGuest> = ArrayList()
        @JvmStatic var guestListB: ArrayList<RJGuest> = ArrayList()
        @JvmStatic var currentPlant: String = "T"
        //for switch warehouse and security
        //@JvmStatic var security_mode: Boolean = false
        @JvmStatic var isEraser: Boolean = false
        @JvmStatic var penColor: Int = Color.BLACK
        @JvmStatic var penWidth: Float = 10f

        @JvmStatic var base64: String = ""
        //OutsourcedProcess
        @JvmStatic var outsourcedProcessOrderList = ArrayList<RJOutSourced>()
        @JvmStatic var outsourcedProcessOrderListBySupplier = ArrayList<RJSupplier>()
        @JvmStatic var isOutSourcedInDetail: Int = 0
        @JvmStatic var currentOutSourcedSendOrder: String = ""
        //log
        @JvmStatic var isLogEnable: Boolean = true
    }
    private var mBluetoothAdapter: BluetoothAdapter? = null
    var mChatService: BluetoothChatService? = null
    //private var mConnectedDeviceName: String? = null

    //var isLogin = false
    var account: String = ""
    var password: String = ""
    var username: String = ""



    var barcode: ScanBarcode? = null

    var textViewUserName: TextView? = null

    var navView: NavigationView? = null
    var bluetoothPrintFunc: BluetoothPrinterFuncs? = null

    //var rva06: String = ""

    //storage global
    //internal var BarCodeText = arrayOfNulls<String>(2)
    //var onlyRvb01: String = ""
    //var UpdateString : String = ""
    //var rjReceiptStorage: RJStorage = RJStorage()
    //var rjStorageList: StorageList.itemStorages? = null

    var fabPrint: FloatingActionButton? = null
    var fabPrintAgain: FloatingActionButton? = null
    var fabWifi: FloatingActionButton? = null
    var fabBack: FloatingActionButton? = null
    var fabSign: FloatingActionButton? = null


    //val receiptUploadedList = ArrayList<ItemReceipt>()
    //var receiptUploadedIndex: Int = 0
    private var toastHandle: Toast? = null
    var currentMaterialUpdateIndex = 0

    private var dialog: AlertDialog? = null
    //private var seekBarCurrentPage: Int = 0
    private var currentSelectMenuItem: MenuItem? = null
    private var isBarcodeScanning: Boolean = false
    private var receiveNumUploadSuccess: String = ""

    private var currentSearchPlant: String = "T"



    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d(mTAG, "onCreate")

        mContext = applicationContext

        //disable Scan2Key Setting
        val disableServiceIntent = Intent()
        disableServiceIntent.action = "unitech.scanservice.scan2key_setting"
        disableServiceIntent.putExtra("scan2key", false)
        sendBroadcast(disableServiceIntent)

        val displayMetrics = DisplayMetrics()
        //
        //mContext!!.display!!.getMetrics(displayMetrics)
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M)
        {
            windowManager.defaultDisplay.getMetrics(displayMetrics)
        } else {
            //mContext!!.display!!.getMetrics(displayMetrics)
            mContext!!.display!!.getRealMetrics(displayMetrics)
        }

        //Log.e(mTAG, "h = ${windowManager.currentWindowMetrics.bounds.height()} , w = ${windowManager.currentWindowMetrics.bounds.width()}")
        screenHeight = displayMetrics.heightPixels
        screenWidth = displayMetrics.widthPixels

        Log.e(mTAG, "width = $screenWidth, height = $screenHeight")

        //read user info


        pref = getSharedPreferences(fileName, Context.MODE_PRIVATE)
        account = pref!!.getString(User.USER_ACCOUNT, "") as String
        password = pref!!.getString(User.PASSWORD, "") as String
        username = pref!!.getString(User.USER_NAME, "") as String
        isReceiptUploadAutoConfirm = pref!!.getBoolean("IS_RECEIPT_UPLOAD_AUTO_CONFIRM", true)
        isLogEnable = pref!!.getBoolean("IS_LOG_ENABLE", true)

        isSecurityGuard = pref!!.getBoolean("IS_SECURITY_GUARD", false)



        // read bluetooth printer
        printerAddress = pref!!.getString("PRINTER_ADDRESS", "") as String

        // guest read current plant
        currentPlant = pref!!.getString("CURRENT_PLANT", "T") as String



        //user = User.getUser(applicationContext)
        user = User()

        user!!.userAccount = account
        user!!.password = password
        user!!.userName = username

        Log.e(mTAG, "account = "+user!!.userAccount+", password = "+user!!.password+", username = "+user!!.userName)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        //get virtual keyboard
        imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        /*val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }*/
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        Log.e(mTAG, "navView header: "+navView!!.headerCount)
        val header = navView!!.inflateHeaderView(R.layout.nav_header_main)
        textViewUserName = header.findViewById(R.id.textViewUserName)
        Log.e(mTAG, "navView header: "+navView!!.headerCount)
        navView!!.removeHeaderView(navView!!.getHeaderView(0))

        /*val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )*/

        val mDrawerToggle = object : ActionBarDrawerToggle(
            this, /* host Activity */
            drawerLayout, /* DrawerLayout object */
            toolbar, /* nav drawer icon to replace 'Up' caret */
            R.string.navigation_drawer_open, /* "open drawer" description */
            R.string.navigation_drawer_close  /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state.  */

            override fun onDrawerClosed(view: View) {
                super.onDrawerClosed(view)

                Log.d(mTAG, "onDrawerClosed")

            }

            /** Called when a drawer has settled in a completely open state.  */
            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)

                Log.d(mTAG, "onDrawerOpened")

                if (isKeyBoardShow) {
                    imm?.toggleSoftInput(InputMethodManager.RESULT_HIDDEN, 0)
                }
            }
        }

        drawerLayout.addDrawerListener(mDrawerToggle)
        mDrawerToggle.syncState()

        navView!!.setNavigationItemSelectedListener(this)

        //fab wifi
        fabWifi = findViewById(R.id.fabWifi)

        fabPrint = findViewById(R.id.fabPrint)
        fabPrint!!.setOnClickListener {
            val connectIntent = Intent()
            connectIntent.action = Constants.ACTION.ACTION_SETTING_BLUETOOTH_CONNECT_ACTION
            sendBroadcast(connectIntent)

            //fabPrint!!.visibility = View.GONE
            fabPrint!!.hide()
        }

        fabPrintAgain = findViewById(R.id.fabPrintAgain)
        fabPrintAgain!!.setOnClickListener {
            Log.d(mTAG, "===> Print it again")

            showPrintAgainConfirmDialog()

            /*if (itemReceipt != null) {

                when(printerStatus) {
                    BluetoothChatService.STATE_NONE, BluetoothChatService.STATE_LISTEN, BluetoothChatService.STATE_CONNECTING-> {
                        toast(getString(R.string.tag_printer_status_not_connected))
                    }

                    BluetoothChatService.STATE_CONNECTED-> {
                        when(printStatus) {
                            PrintStatus.PRINT_SUCCESS -> {
                                val confirmdialog = AlertDialog.Builder(this@MainActivity)
                                confirmdialog.setIcon(R.drawable.baseline_warning_black_48)
                                confirmdialog.setTitle(resources.getString(R.string.nav_printer))
                                confirmdialog.setMessage(resources.getString(R.string.fab_print_confirm))
                                confirmdialog.setPositiveButton(
                                    resources.getString(R.string.confirm)
                                ) { _, _ ->

                                    val addString = when (itemReceipt!!.receiveLine.length) {
                                        0 -> "00"
                                        1 -> "0"
                                        else -> ""
                                    }


                                    val printContent: String = itemReceipt!!.receiveNum + addString + itemReceipt!!.receiveLine

                                    //print 1
                                    val ret: Int
                                    ret = printLabel(
                                        itemReceipt!!.poNumSplit + "-" + itemReceipt!!.poLineInt,
                                        printContent,
                                        itemReceipt!!.rjReceipt!!.pmn04,
                                        itemReceipt!!.rjReceipt!!.pmn20,
                                        itemReceipt!!.rjReceipt!!.pmnud02,
                                        itemReceipt!!.rjReceipt!!.ima36,
                                        itemReceipt!!.rjReceipt!!.rvb38
                                    )

                                    when(ret) {
                                        0 -> {
                                            //itemReceipt!!.state = ItemReceipt.ItemState.PRINTED
                                            Log.d(mTAG, "(fabPrintAgain) Print 1 tag success")
                                            toast(getString(R.string.receipt_tag_printed))
                                            printStatus = PrintStatus.PRINT_SUCCESS
                                        }
                                        else -> {
                                            Log.d(mTAG, "(fabPrintAgain) tag was printed failed")
                                            toast(getString(R.string.print_error))
                                            fabPrint!!.visibility = View.VISIBLE
                                            printStatus = PrintStatus.PRINT_ERROR
                                            //itemReceipt!!.state = ItemReceipt.ItemState.PRINTED_FAILED
                                        }
                                    }

                                }
                                confirmdialog.setNegativeButton(
                                    resources.getString(R.string.cancel)
                                ) { _, _ ->

                                }
                                confirmdialog.show()
                            }

                            PrintStatus.PRINT_ERROR -> {
                                toast(getString(R.string.print_error))
                                fabPrint!!.visibility = View.VISIBLE
                                //printStatus = PrintStatus.PRINT_WAITING

                                //val connectIntent = Intent()
                                //connectIntent.action = Constants.ACTION.ACTION_PRINT_ERROR
                                //sendBroadcast(connectIntent)
                            }
                        }
                    }
                }

            } else {
                Log.e(mTAG, "itemReceipt = null")
            }*/
        }

        fabBack = findViewById(R.id.fabBack)
        fabBack!!.setOnClickListener {
            Log.d(mTAG, "===> fabBack")

            when (currentFrag) {

                CurrentFragment.OUTSOURCED_FRAGMENT -> {
                    if (isOutSourcedInDetail == 1) {
                        fabBack!!.visibility = View.GONE
                        fabSign!!.visibility = View.GONE

                        val backIntent = Intent()
                        backIntent.action = Constants.ACTION.ACTION_OUTSOURCED_PROCESS_BACK_TO_SUPPLIER_LIST
                        sendBroadcast(backIntent)
                    } else { //isOutSourcedInDetail == 2

                        val backIntent = Intent()
                        backIntent.action = Constants.ACTION.ACTION_OUTSOURCED_PROCESS_BACK_TO_DETAIL_LIST
                        sendBroadcast(backIntent)
                    }
                }
                CurrentFragment.PROPERTY_FRAGMENT -> {
                    if (isPropertyInDetail == 1) {
                        fabBack!!.visibility = View.GONE
                        fabSign!!.visibility = View.GONE

                        val backIntent = Intent()
                        backIntent.action = Constants.ACTION.ACTION_PROPERTY_BACK_TO_LIST
                        sendBroadcast(backIntent)
                    }
                }
                else -> {
                    Log.e(mTAG, "Unknown fragment")
                }
            }
        }

        fabSign = findViewById(R.id.fabSign)
        fabSign!!.setOnClickListener {
            val showIntent = Intent()
            showIntent.action = Constants.ACTION.ACTION_OUTSOURCED_PROCESS_SHOW_SIGN_DIALOG_ACTION
            sendBroadcast(showIntent)


        }
        //permission

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkAndRequestPermissions()
        } else {
            initView()
            if (isLogEnable)
                initLog()
        }

        //Log.d(mTAG, "isLogin = $isLogin")





        /*if (!isLogin) {

            var fragment: Fragment? = null
            val fragmentClass: Class<*>
            fragmentClass = LoginFragment::class.java

            try {
                fragment = fragmentClass.newInstance() as Fragment
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val fragmentManager = supportFragmentManager
            //fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment!!).commitAllowingStateLoss()

        } */

        val filter: IntentFilter
        @SuppressLint("CommitPrefEdits")
        mReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action != null) {
                    if (intent.action!!.equals(Constants.ACTION.ACTION_LOGIN_ACTION, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_LOGIN_ACTION")
                        account = intent.getStringExtra("account") as String
                        password = intent.getStringExtra("password") as String

                        Log.e(mTAG, "account = $account password $password")

                        runOnUiThread {
                            callAPILogin(account, password)
                        }







                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_LOGIN_NETWORK_ERROR, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_LOGIN_NETWORK_ERROR")


                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_LOGIN_SUCCESS, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_LOGIN_SUCCESS")

                        //isLogin = true
                        //set title

                        //title = getString(R.string.nav_receipt) +" - "+ getString(R.string.tag_printer_status_not_connected)
                        title = getString(R.string.nav_home)

                        //set username
                        //textViewUserName!!.setText(getString(R.string.nav_greeting, username))
                        textViewUserName!!.text = getString(R.string.nav_greeting, username)
                        //save to User
                        user!!.userAccount = account
                        user!!.password = password
                        user!!.userName = username
                        user!!.isLogin = true


                        navView!!.menu.getItem(0).isVisible = true //home
                        navView!!.menu.getItem(1).isVisible = true //receipt
                        navView!!.menu.getItem(2).isVisible = true //storage
                        navView!!.menu.getItem(3).isVisible = true //material
                        navView!!.menu.getItem(4).isVisible = true //outsourced
                        navView!!.menu.getItem(5).isVisible = true //property
                        navView!!.menu.getItem(6).isVisible = false //login
                        navView!!.menu.getItem(7).isVisible = true //printer
                        navView!!.menu.getItem(8).isVisible = true //setting
                        navView!!.menu.getItem(9).isVisible = true //guest
                        navView!!.menu.getItem(10).isVisible = true //about
                        navView!!.menu.getItem(11).isVisible = true //logout

                        //hide keyboard
                        imm?.toggleSoftInput(InputMethodManager.RESULT_HIDDEN, 0)

                        //save
                        editor = pref!!.edit()
                        editor!!.putString(User.USER_ACCOUNT, account)
                        editor!!.putString(User.PASSWORD, password)
                        editor!!.putString(User.USER_NAME, username)
                        editor!!.apply()

                        //start with receipt fragment
                        var fragment: Fragment? = null
                        val fragmentClass = HomeGridFragment::class.java

                        try {
                            fragment = fragmentClass.newInstance()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }


                        val fragmentManager = supportFragmentManager
                        //fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
                        fragmentManager.beginTransaction().replace(R.id.flContent, fragment!!).commitAllowingStateLoss()

                        navView!!.menu.getItem(0).isChecked = true //home
                        navView!!.menu.getItem(1).isChecked = false //receipt
                        navView!!.menu.getItem(2).isChecked = false //storage
                        navView!!.menu.getItem(3).isChecked = false //material
                        navView!!.menu.getItem(4).isChecked = false //outsourced
                        navView!!.menu.getItem(5).isChecked = false //property
                        navView!!.menu.getItem(6).isChecked = false //login
                        navView!!.menu.getItem(7).isChecked = false //printer
                        navView!!.menu.getItem(8).isChecked = false //setting
                        navView!!.menu.getItem(9).isChecked = false //guest
                        navView!!.menu.getItem(10).isChecked = false //about
                        navView!!.menu.getItem(11).isChecked = false //logout


                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_LOGIN_FAILED, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_LOGIN_FAILED")
                        //hide
                        val hideIntent = Intent()
                        hideIntent.action = Constants.ACTION.ACTION_LOGIN_FRAGMENT_LOGIN_FAILED
                        sendBroadcast(hideIntent)

                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_LOGOUT_ACTION, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_LOGOUT_ACTION")

                        //set username ""
                        //set username
                        textViewUserName!!.text = ""

                        //save to User
                        user!!.userAccount = ""
                        user!!.password = ""
                        user!!.userName = ""
                        user!!.isLogin = false
                        //saveRJStorageUpload
                        editor = pref?.edit()
                        editor?.putString(User.USER_NAME, "")
                        editor?.putString(User.PASSWORD, "")
                        editor?.putString(User.USER_ACCOUNT, "")
                        editor?.apply()

                        navView!!.menu.getItem(0).isVisible = false //home
                        navView!!.menu.getItem(1).isVisible = false //receipt
                        navView!!.menu.getItem(2).isVisible = false //storage
                        navView!!.menu.getItem(3).isVisible = false //material
                        navView!!.menu.getItem(4).isVisible = false //outsourced
                        navView!!.menu.getItem(5).isVisible = false //property
                        navView!!.menu.getItem(6).isVisible = true //login
                        navView!!.menu.getItem(7).isVisible = true //printer
                        navView!!.menu.getItem(8).isVisible = false //setting
                        navView!!.menu.getItem(9).isVisible = false //guest
                        navView!!.menu.getItem(10).isVisible = true //about
                        navView!!.menu.getItem(11).isVisible = false //logout


                        var fragment: Fragment? = null
                        val fragmentClass = LoginFragment::class.java

                        try {
                            fragment = fragmentClass.newInstance()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }


                        // Insert the fragment by replacing any existing fragment
                        val fragmentManager = supportFragmentManager
                        //fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
                        fragmentManager.beginTransaction().replace(R.id.flContent, fragment!!).commitAllowingStateLoss()

                        navView!!.menu.getItem(6).isChecked = true //login

                        title = resources.getString(R.string.nav_login)
                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_HIDE_KEYBOARD, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_HIDE_KEYBOARD")

                        imm?.toggleSoftInput(InputMethodManager.RESULT_HIDDEN, 0)
                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_USER_INPUT_SEARCH, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_USER_INPUT_SEARCH")

                        //for outsourced process
                        if (fabBack!!.visibility == View.VISIBLE) {
                            fabBack!!.visibility = View.GONE
                            fabSign!!.visibility = View.GONE
                        }

                        imm?.toggleSoftInput(InputMethodManager.RESULT_HIDDEN, 0)

                        val inputNo = intent.getStringExtra("INPUT_NO")

                        Log.e(mTAG, "inputNo = $inputNo")
                        //clear
                        //ReceiptList.removeAllItem()
                        //rva06 = ""
                        //et_Barcode.setText(text)
                        itemReceipt = null
                        itemStorage = null

                        if (inputNo != null) {

                            try {
                                barcode = ScanBarcode.setPoBarcodeByScanTransform(inputNo.trim())

                                Log.e(mTAG, "barcode = $barcode")

                                if (barcode != null) {

                                    when (currentFrag) {
                                        CurrentFragment.RECEIPT_FRAGMENT -> {
                                            //checkIfReceiptUploaded(barcode)
                                            getReceipt(barcode)
                                        }
                                        CurrentFragment.STORAGE_FRAGMENT -> {
                                            getStorage(barcode)
                                        }
                                        CurrentFragment.MATERIAL_ISSUING_FRAGMENT -> {
                                            getMaterial(barcode)
                                        }
                                        CurrentFragment.PROPERTY_FRAGMENT -> {
                                            getProperty(barcode)
                                        }
                                        CurrentFragment.HOME_FRAGMENT -> {

                                        }
                                        CurrentFragment.GUEST_FRAGMENT -> {
                                            getReceipt(barcode)
                                        }
                                        CurrentFragment.OUTSOURCED_FRAGMENT -> {
                                            getOutSourcedProcessBySupplierNo(inputNo)
                                        }

                                        else -> {
                                            Log.e(mTAG, "Unknown fragment")
                                        }
                                    }
                                } else {
                                    Log.e(mTAG, "barcode = null")
                                    val sendIntent = Intent()
                                    sendIntent.action = Constants.ACTION.ACTION_BARCODE_NULL
                                    sendBroadcast(sendIntent)
                                }
                            } catch (ex: NumberFormatException) {
                                ex.printStackTrace()
                            }


                        } else {
                            Log.e(mTAG, "inputNo = null")
                        }


                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_RECEIPT_UPLOAD_ACTION, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_RECEIPT_UPLOAD_ACTION")

                        //upload button press

                        //single

                        if (itemReceipt != null) {

                            when(printerStatus) {
                                BluetoothChatService.STATE_NONE, BluetoothChatService.STATE_LISTEN, BluetoothChatService.STATE_CONNECTING-> { //printer not connected, cancel
                                    toast(getString(R.string.tag_printer_status_not_connected))
                                }

                                BluetoothChatService.STATE_CONNECTED-> {

                                    when(itemReceipt!!.state) {
                                        ItemReceipt.ItemState.UPLOADED-> {
                                            Log.d(mTAG, "==>state UPLOADED")
                                        }
                                        ItemReceipt.ItemState.UPLOAD_FAILED-> {
                                            Log.d(mTAG, "==>state UPLOAD_FAILED")
                                            uploadReceipt()
                                        }
                                        ItemReceipt.ItemState.INITIAL-> {
                                            Log.d(mTAG, "==>state INITIAL")
                                            uploadReceipt()
                                        }
                                        ItemReceipt.ItemState.CONFIRMED-> {
                                            Log.d(mTAG, "==>state CONFIRMED")
                                        }
                                        ItemReceipt.ItemState.CONFIRM_FAILED-> {
                                            Log.d(mTAG, "==>state UPLOAD_FAILED")
                                        }
                                    }
                                }
                            }




                        } else {
                            Log.e(mTAG, "itemReceipt = null")

                            val failedIntent = Intent()
                            failedIntent.action = Constants.ACTION.ACTION_RECEIPT_UPLOAD_FAILED_SEND_TO_FRAGMENT
                            sendBroadcast(failedIntent)
                        }

                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_RECEIPT_UPLOAD_SUCCESS, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_RECEIPT_UPLOAD_SUCCESS")

                        if (itemReceipt != null) {
                            Log.d(mTAG, "============ [item start] ============")
                            Log.d(mTAG, "state : " + itemReceipt!!.state.toString())
                            Log.d(mTAG, "poNumSplit : " + itemReceipt!!.poNumSplit)
                            Log.d(mTAG, "poNumScanTotal : " + itemReceipt!!.poNumScanTotal)
                            Log.d(mTAG, "receiveLine : " + itemReceipt!!.receiveLine)
                            Log.d(mTAG, "poLineInt : " + itemReceipt!!.poLineInt)
                            Log.d(mTAG, "receiveNum : " + itemReceipt!!.receiveNum)
                            //Log.d(mTAG, "******** history *******")
                            //Log.d(mTAG, "rjHistory.result : " + itemReceipt!!.rjHistory!!.result)
                            //Log.d(mTAG, "rjHistory.rva06 : " + itemReceipt!!.rjHistory!!.rva06)
                            //Log.d(mTAG, "rjHistory.rvb01 : " + itemReceipt!!.rjHistory!!.rvb01)
                            //Log.d(mTAG, "rjHistory.rvb02 : " + itemReceipt!!.rjHistory!!.rvb02)
                            Log.d(mTAG, "******** receipt *******")
                            Log.d(mTAG, "rjReceipt.ima021 : " + itemReceipt!!.rjReceipt!!.ima021)
                            Log.d(mTAG, "rjReceipt.ima35 : " + itemReceipt!!.rjReceipt!!.ima35)
                            Log.d(mTAG, "rjReceipt.ima36 : " + itemReceipt!!.rjReceipt!!.ima36)
                            Log.d(mTAG, "rjReceipt.rvb38 : " + itemReceipt!!.rjReceipt!!.rvb38)
                            Log.d(mTAG, "rjReceipt.pmc03 : " + itemReceipt!!.rjReceipt!!.pmc03)
                            Log.d(mTAG, "rjReceipt.pmm02 : " + itemReceipt!!.rjReceipt!!.pmm02)
                            Log.d(mTAG, "rjReceipt.pmm09 : " + itemReceipt!!.rjReceipt!!.pmm09)
                            Log.d(mTAG, "rjReceipt.pmn02 : " + itemReceipt!!.rjReceipt!!.pmn02)
                            Log.d(mTAG, "rjReceipt.pmn04 : " + itemReceipt!!.rjReceipt!!.pmn04)
                            Log.d(mTAG, "rjReceipt.pmn07 : " + itemReceipt!!.rjReceipt!!.pmn07)
                            Log.d(mTAG, "rjReceipt.pmn20 : " + itemReceipt!!.rjReceipt!!.pmn20)
                            Log.d(mTAG, "rjReceipt.pmn041 : " + itemReceipt!!.rjReceipt!!.pmn041)
                            Log.d(mTAG, "rjReceipt.pmnud02 : " + itemReceipt!!.rjReceipt!!.pmnud02)

                            Log.d(mTAG, "============= [item end] =============")

                            when(printerStatus) {
                                BluetoothChatService.STATE_NONE, BluetoothChatService.STATE_LISTEN, BluetoothChatService.STATE_CONNECTING-> {
                                    toast(getString(R.string.tag_printer_status_not_connected))
                                }

                                BluetoothChatService.STATE_CONNECTED-> {
                                    //var addString: String

                                    val addString = when(itemReceipt!!.receiveLine.length) {
                                        1 -> "00"
                                        2 -> "0"
                                        else -> ""
                                    }

                                    /*if (itemReceipt!!.receiveLine.length == 1) {
                                        addString = "00"
                                    } else if (itemReceipt!!.receiveLine.length == 2) {
                                        addString = "0"
                                    } else {
                                        addString = ""
                                    }*/
                                    val printContent = itemReceipt!!.receiveNum + addString + itemReceipt!!.receiveLine
                                    //print x 2
                                    var ret: Int
                                    ret = printLabel(
                                        itemReceipt!!.poNumSplit + "-" + itemReceipt!!.poLineInt,
                                        printContent,
                                        itemReceipt!!.rjReceipt!!.pmn04,
                                        itemReceipt!!.rjReceipt!!.pmn20,
                                        itemReceipt!!.rjReceipt!!.pmnud02,
                                        itemReceipt!!.rjReceipt!!.ima36,
                                        itemReceipt!!.rjReceipt!!.rvb38
                                    )
                                    ret += printLabel(
                                        itemReceipt!!.poNumSplit + "-" + itemReceipt!!.poLineInt,
                                        printContent,
                                        itemReceipt!!.rjReceipt!!.pmn04,
                                        itemReceipt!!.rjReceipt!!.pmn20,
                                        itemReceipt!!.rjReceipt!!.pmnud02,
                                        itemReceipt!!.rjReceipt!!.ima36,
                                        itemReceipt!!.rjReceipt!!.rvb38
                                    )

                                    when(ret) {
                                        0 -> {
                                            //itemReceipt!!.state = ItemReceipt.ItemState.PRINTED
                                            Log.d(mTAG, "(upload success) Print 2 tag success")
                                            toast(getString(R.string.receipt_tag_printed))
                                            printStatus = PrintStatus.PRINT_SUCCESS
                                        }

                                        1 -> {
                                            Log.d(mTAG, "(upload success) Print 1 tag, 1 failed")
                                            //itemReceipt!!.state = ItemReceipt.ItemState.PRINTED
                                            toast(getString(R.string.print_error))
                                            fabPrint!!.visibility = View.VISIBLE
                                            printStatus = PrintStatus.PRINT_ERROR
                                        }

                                        else -> {
                                            Log.e(mTAG, "(upload success) 2 tags were printed failed")
                                            toast(getString(R.string.print_error))
                                            fabPrint!!.visibility = View.VISIBLE
                                            //itemReceipt!!.state = ItemReceipt.ItemState.PRINTED_FAILED
                                            printStatus = PrintStatus.PRINT_ERROR
                                        }
                                    }

                                }
                            }

                            //send to receiptfragment to change status
                            val sendIntent = Intent()
                            sendIntent.action = Constants.ACTION.ACTION_RECEIPT_UPLOADED_SEND_TO_FRAGMENT
                            sendBroadcast(sendIntent)

                        } else {
                            Log.e(mTAG, "itemReceipt = null")
                        }

                        //show print again button
                        fabPrintAgain!!.visibility = View.VISIBLE

                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_RECEIPT_UPLOAD_RETURN_PO_DIFF, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_RECEIPT_UPLOAD_RETURN_PO_DIFF")


                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_RECEIPT_UPLOAD_RETURN_EXCEPTION, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_RECEIPT_UPLOAD_RETURN_EXCEPTION")


                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_RECEIPT_UPLOAD_FAILED, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_RECEIPT_UPLOAD_FAILED")

                        val failedIntent = Intent()
                        failedIntent.action = Constants.ACTION.ACTION_RECEIPT_UPLOAD_FAILED_SEND_TO_FRAGMENT
                        sendBroadcast(failedIntent)

                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_RECEIPT_UPLOADED_NO, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_RECEIPT_UPLOADED_NO")

                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_RECEIPT_UPLOADED_YES, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_RECEIPT_UPLOADED_YES")

                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_RECEIPT_UPLOADED_CONFIRM_ACTION, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_RECEIPT_UPLOADED_CONFIRM_ACTION")

                        //confirmUploadReceipt()

                        if (itemReceipt != null) {
                            confirmUploadReceipt()

                        } else {
                            val failIntent = Intent()
                            failIntent.action = Constants.ACTION.ACTION_RECEIPT_UPLOADED_CONFIRM_FAILED
                            sendBroadcast(failIntent)
                            Log.e(mTAG, "itemReceipt == null")
                        }

                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_STORAGE_UPDATE_ACTION, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_STORAGE_UPDATE_ACTION")

                        if (itemStorage != null) {

                            uploadStorage()
                        } else {
                            val failIntent = Intent()
                            failIntent.action = Constants.ACTION.ACTION_STORAGE_UPLOAD_FAILED
                            sendBroadcast(failIntent)
                            Log.e(mTAG, "itemStorage == null")
                        }
                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_MATERIAL_SEND_CHANGED_UPDATE_ACTION, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_MATERIAL_SEND_CHANGED_UPDATE_ACTION")

                        if (materialList.size > 0) {
                            for (i in 0 until materialList.size) {
                                if (materialList[i].update <= 1) { //update not yet or failed
                                    currentMaterialUpdateIndex = i
                                    break
                                }
                            }

                            updateMaterialSend(materialList[currentMaterialUpdateIndex].sfs01, materialList[currentMaterialUpdateIndex].sfs02, materialList[currentMaterialUpdateIndex].sfs05)
                        } else {
                            val errorIntent = Intent()
                            errorIntent.action = Constants.ACTION.ACTION_MATERIAL_SEND_CHANGED_ERROR
                            sendBroadcast(errorIntent)
                            Log.e(mTAG, "(materialList.size = 0)")
                        }



                        //for (i in 0 until materialList.size) {
                        //    updateMaterialSend(materialList[i].sfs01, materialList[i].sfs02, materialList[i].sfs05)
                        //}

                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_MATERIAL_SEND_CHANGED_FAILED, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_MATERIAL_SEND_CHANGED_FAILED")

                        //update status
                        materialList[currentMaterialUpdateIndex].update = 1 //failed

                        if (currentMaterialUpdateIndex == (materialList.size - 1)) {
                            val completeIntent = Intent()
                            completeIntent.action = Constants.ACTION.ACTION_MATERIAL_SEND_CHANGED_COMPLETE
                            sendBroadcast(completeIntent)
                        } else {
                            currentMaterialUpdateIndex++
                            for (i in currentMaterialUpdateIndex until materialList.size) {
                                if (materialList[i].update <= 1) {
                                    currentMaterialUpdateIndex = i
                                    break
                                }
                            }


                            updateMaterialSend(materialList[currentMaterialUpdateIndex].sfs01, materialList[currentMaterialUpdateIndex].sfs02, materialList[currentMaterialUpdateIndex].sfs05)
                        }

                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_MATERIAL_SEND_CHANGED_SUCCESS, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_MATERIAL_SEND_CHANGED_SUCCESS")

                        //update status
                        materialList[currentMaterialUpdateIndex].update = 2 //success

                        if (currentMaterialUpdateIndex == (materialList.size - 1)) {
                            val completeIntent = Intent()
                            completeIntent.action = Constants.ACTION.ACTION_MATERIAL_SEND_CHANGED_COMPLETE
                            sendBroadcast(completeIntent)
                        } else {
                            currentMaterialUpdateIndex++
                            for (i in currentMaterialUpdateIndex until materialList.size) {
                                if (materialList[i].update <= 1) {
                                    currentMaterialUpdateIndex = i
                                    break
                                }
                            }
                            updateMaterialSend(materialList[currentMaterialUpdateIndex].sfs01, materialList[currentMaterialUpdateIndex].sfs02, materialList[currentMaterialUpdateIndex].sfs05)
                        }

                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_MATERIAL_SEND_CHANGED_COMPLETE, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_MATERIAL_SEND_CHANGED_COMPLETE")

                        val completeIntent = Intent()
                        completeIntent.action = Constants.ACTION.ACTION_MATERIAL_SEND_COMPLETE_TO_FRAGMENT
                        sendBroadcast(completeIntent)

                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_SEEK_BAR_HIDE_ACTION, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_SEEK_BAR_HIDE_ACTION")

                        menuItemSeekBar!!.isVisible = false

                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_SEEK_BAR_SHOW_ACTION, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_SEEK_BAR_SHOW_ACTION")

                        menuItemSeekBar!!.isVisible = true

                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_SETTING_BLUETOOTH_CONNECT_ACTION, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_SETTING_BLUETOOTH_CONNECT_ACTION")

                        if (mChatService != null) {
                            mChatService!!.stop()
                            Thread.sleep(500)

                            //connect printer
                            if (printerAddress == "") {
                                toast(getString(R.string.set_printer_first))
                            } else {
                                if (mBluetoothAdapter != null) {
                                    if (mBluetoothAdapter!!.isEnabled) {
                                        val device = mBluetoothAdapter!!.getRemoteDevice(printerAddress)
                                        toast(getString(R.string.tag_printer_status_connecting))
                                        mChatService!!.connect(device, true)
                                        //set printer
                                        bluetoothPrintFunc = BluetoothPrinterFuncs(mChatService as BluetoothChatService)
                                        if (bluetoothPrintFunc != null) {
                                            Log.e(mTAG, "Bluetooth Printer ready.")
                                        } else {
                                            Log.e(mTAG, "bluetoothPrintFunc == null")
                                        }
                                    } else {
                                        val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                                        startActivityForResult(enableIntent, requestEnableBt)
                                    }
                                } else {
                                    Log.e(mTAG, "mBluetoothAdapter = null")
                                }
                            }
                        } else{
                            Log.e(mTAG, "mChatService = null")

                            if (mBluetoothAdapter != null) {
                                //if (!mBluetoothAdapter!!.isEnabled()) {
                                if (!mBluetoothAdapter!!.isEnabled) {
                                    val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                                    startActivityForResult(enableIntent, requestEnableBt)
                                    // Otherwise, setup the chat session
                                } else {
                                    Log.d(mTAG, "===>mBluetoothAdapter is enabled")
                                    setupChat()
                                    if (mChatService != null) {
                                        // Only if the state is STATE_NONE, do we know that we haven't started already
                                        if (mChatService!!.getState() == BluetoothChatService.STATE_NONE) {
                                            Log.d(mTAG, "--->mChatService start")
                                            // Start the Bluetooth chat services
                                            mChatService!!.start()
                                        }



                                        //connect printer
                                        if (printerAddress == "") {
                                            toast(getString(R.string.set_printer_first))
                                        } else {
                                            val device = mBluetoothAdapter!!.getRemoteDevice(printerAddress)
                                            mChatService!!.connect(device, true)
                                            //set printer
                                            bluetoothPrintFunc = BluetoothPrinterFuncs(mChatService as BluetoothChatService)
                                            if (bluetoothPrintFunc != null) {
                                                Log.e(mTAG, "Bluetooth Printer ready.")
                                            } else {
                                                Log.e(mTAG, "bluetoothPrintFunc == null")
                                            }
                                        }

                                    } else {
                                        Log.e(mTAG, "mChatService = null")
                                    }
                                }
                            } else {
                                Log.e(mTAG, "mBluetoothAdapter = null")
                                toast("mBluetoothAdapter = null")
                            }

                        }


                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_SETTING_PRINTTEST_ACTION, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_SETTING_PRINTTEST_ACTION")

                        //printTest
                        val ret = printLabel("AP22-1234567890", "AP32-0123456789", "料件編號", "數量" , "N", "儲位", "批號")

                        if (ret == 0) {
                            toast(getString(R.string.receipt_tag_printed))
                        } else {
                            toast(getString(R.string.print_error))
                        }
                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_HOME_GO_TO_RECEIPT_ACTION, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_HOME_GO_TO_RECEIPT_ACTION")

                        itemReceipt = null

                        var statusTitle = ""
                        when(printerStatus) {
                            BluetoothChatService.STATE_NONE, BluetoothChatService.STATE_LISTEN-> {
                                statusTitle = getString(R.string.tag_printer_status_not_connected)
                            }
                            BluetoothChatService.STATE_CONNECTING-> {
                                statusTitle = getString(R.string.tag_printer_status_connecting)
                            }
                            BluetoothChatService.STATE_CONNECTED-> {
                                statusTitle = getString(R.string.tag_printer_status_connected)
                            }

                        }

                        title = getString(R.string.nav_receipt) +" - "+ statusTitle

                        menuItemBluetooth!!.isVisible = true
                        menuItemKeyboard!!.isVisible = true
                        menuItemReceiptSetting!!.isVisible = true
                        menuItemShowReceiptConfirmFailed!!.isVisible = true
                        menuItemReconnectPrinter!!.isVisible = true
                        menuItemPrintAgain!!.isVisible = true
                        menuItemEraser!!.isVisible = false
                        menuItemOutSourcedSupplier!!.isVisible = false

                        //start with receipt fragment
                        var fragment: Fragment? = null
                        val fragmentClass = ReceiptFragment::class.java

                        try {
                            fragment = fragmentClass.newInstance()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        val fragmentManager = supportFragmentManager
                        //fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
                        fragmentManager.beginTransaction().replace(R.id.flContent, fragment!!).commitAllowingStateLoss()

                        //show depends on bluetooth connect
                        when(printerStatus) {
                            BluetoothChatService.STATE_NONE, BluetoothChatService.STATE_LISTEN, BluetoothChatService.STATE_CONNECTING-> {
                                fabPrint!!.visibility = View.VISIBLE
                            }

                            BluetoothChatService.STATE_CONNECTED-> {
                                fabPrint!!.visibility = View.GONE
                            }
                        }

                        currentFrag = CurrentFragment.RECEIPT_FRAGMENT
                        isBarcodeScanning = false

                        //hide print again button
                        fabPrintAgain!!.hide()

                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_HOME_GO_TO_STORAGE_ACTION, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_HOME_GO_TO_STORAGE_ACTION")

                        title = getString(R.string.nav_storage)

                        menuItemBluetooth!!.isVisible = false
                        menuItemKeyboard!!.isVisible = true
                        menuItemReceiptSetting!!.isVisible = false
                        menuItemShowReceiptConfirmFailed!!.isVisible = false
                        menuItemReconnectPrinter!!.isVisible = false
                        menuItemPrintAgain!!.isVisible = false
                        menuItemEraser!!.isVisible = false
                        menuItemOutSourcedSupplier!!.isVisible = false

                        //start with receipt fragment
                        var fragment: Fragment? = null
                        val fragmentClass = StorageFragment::class.java

                        try {
                            fragment = fragmentClass.newInstance()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        val fragmentManager = supportFragmentManager
                        //fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
                        fragmentManager.beginTransaction().replace(R.id.flContent, fragment!!).commitAllowingStateLoss()

                        currentFrag = CurrentFragment.STORAGE_FRAGMENT
                        isBarcodeScanning = false

                        //hide print again button
                        fabPrintAgain!!.hide()

                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_HOME_GO_TO_MATERIAL_ACTION, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_HOME_GO_TO_MATERIAL_ACTION")

                        title = getString(R.string.nav_material_issuing)

                        menuItemBluetooth!!.isVisible = false
                        menuItemKeyboard!!.isVisible = true
                        menuItemReceiptSetting!!.isVisible = false
                        menuItemShowReceiptConfirmFailed!!.isVisible = false
                        menuItemReconnectPrinter!!.isVisible = false
                        menuItemPrintAgain!!.isVisible = false
                        menuItemEraser!!.isVisible = false
                        menuItemOutSourcedSupplier!!.isVisible = false

                        //start with receipt fragment
                        var fragment: Fragment? = null
                        val fragmentClass = MaterialIssuingFragment::class.java

                        try {
                            fragment = fragmentClass.newInstance()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        val fragmentManager = supportFragmentManager
                        //fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
                        fragmentManager.beginTransaction().replace(R.id.flContent, fragment!!).commitAllowingStateLoss()

                        currentFrag = CurrentFragment.MATERIAL_ISSUING_FRAGMENT
                        isBarcodeScanning = false

                        //hide print again button
                        fabPrintAgain!!.hide()

                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_HOME_GO_TO_PROPERTY_ACTION, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_HOME_GO_TO_PROPERTY_ACTION")

                        title = getString(R.string.nav_property)

                        menuItemBluetooth!!.isVisible = false
                        menuItemKeyboard!!.isVisible = true
                        menuItemReceiptSetting!!.isVisible = false
                        menuItemShowReceiptConfirmFailed!!.isVisible = false
                        menuItemReconnectPrinter!!.isVisible = false
                        menuItemPrintAgain!!.isVisible = false
                        menuItemEraser!!.isVisible = false
                        menuItemOutSourcedSupplier!!.isVisible = false

                        //start with receipt fragment
                        var fragment: Fragment? = null
                        val fragmentClass = PropertyFragment::class.java

                        try {
                            fragment = fragmentClass.newInstance()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        val fragmentManager = supportFragmentManager
                        //fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
                        fragmentManager.beginTransaction().replace(R.id.flContent, fragment!!).commitAllowingStateLoss()

                        currentFrag = CurrentFragment.PROPERTY_FRAGMENT
                        isBarcodeScanning = false

                        //hide print again button
                        fabPrintAgain!!.hide()

                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_HOME_GO_TO_TAG_PRINTER_ACTION, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_HOME_GO_TO_TAG_PRINTER_ACTION")
                        isBarcodeScanning = false

                        title = getString(R.string.nav_printer)

                        menuItemBluetooth!!.isVisible = true
                        menuItemKeyboard!!.isVisible = false
                        menuItemReceiptSetting!!.isVisible = false
                        menuItemShowReceiptConfirmFailed!!.isVisible = false
                        menuItemReconnectPrinter!!.isVisible = false
                        menuItemPrintAgain!!.isVisible = false
                        menuItemEraser!!.isVisible = false
                        menuItemOutSourcedSupplier!!.isVisible = false

                        //start with receipt fragment
                        var fragment: Fragment? = null
                        val fragmentClass = SettingFragment::class.java

                        try {
                            fragment = fragmentClass.newInstance()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        val fragmentManager = supportFragmentManager
                        //fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
                        fragmentManager.beginTransaction().replace(R.id.flContent, fragment!!).commitAllowingStateLoss()

                        currentFrag = CurrentFragment.PRINTER_FRAGMENT

                        //hide print again button
                        fabPrintAgain!!.hide()

                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_HOME_GO_TO_LOGOUT_ACTION, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_HOME_GO_TO_LOGOUT_ACTION")
                        isBarcodeScanning = false

                        showLogoutConfirmDialog()
                        /*val confirmDialog = AlertDialog.Builder(this@MainActivity)
                        confirmDialog.setIcon(R.drawable.baseline_warning_black_48)
                        confirmDialog.setTitle(resources.getString(R.string.logout_title))
                        confirmDialog.setMessage(resources.getString(R.string.logout_title_msg))
                        confirmDialog.setPositiveButton(
                            resources.getString(R.string.ok)
                        ) { _, _ ->

                            val logoutIntent = Intent(Constants.ACTION.ACTION_LOGOUT_ACTION)
                            mContext?.sendBroadcast(logoutIntent)
                        }
                        confirmDialog.setNegativeButton(
                            resources.getString(R.string.cancel)
                        ) { _, _ -> }
                        confirmDialog.show()*/

                        /*title = getString(R.string.nav_logout)

                        menuItemBluetooth!!.isVisible = false
                        menuItemKeyboard!!.isVisible = false

                        //start with receipt fragment
                        var fragment: Fragment? = null
                        val fragmentClass = LogoutFragment::class.java

                        try {
                            fragment = fragmentClass.newInstance()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        val fragmentManager = supportFragmentManager
                        //fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
                        fragmentManager.beginTransaction().replace(R.id.flContent, fragment!!).commitAllowingStateLoss()

                        currentFrag = CurrentFragment.LOGOUT_FRAGMENT*/

                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_HOME_GO_TO_ABOUT_ACTION, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_HOME_GO_TO_ABOUT_ACTION")
                        isBarcodeScanning = false

                        showCurrentVersionDialog()
                        /*title = getString(R.string.nav_about)

                        menuItemBluetooth!!.isVisible = false
                        menuItemKeyboard!!.isVisible = false

                        //start with receipt fragment
                        var fragment: Fragment? = null
                        val fragmentClass = AboutFragment::class.java

                        try {
                            fragment = fragmentClass.newInstance()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        val fragmentManager = supportFragmentManager
                        //fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
                        fragmentManager.beginTransaction().replace(R.id.flContent, fragment!!).commitAllowingStateLoss()

                        currentFrag = CurrentFragment.ABOUT_FRAGMENT*/
                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_HOME_GO_TO_USER_SETTING_ACTION, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_HOME_GO_TO_USER_SETTING_ACTION")
                        isBarcodeScanning = false


                        title = getString(R.string.nav_setting)

                        menuItemBluetooth!!.isVisible = false
                        menuItemKeyboard!!.isVisible = false
                        menuItemReceiptSetting!!.isVisible = false
                        menuItemShowReceiptConfirmFailed!!.isVisible = false
                        menuItemReconnectPrinter!!.isVisible = false
                        menuItemPrintAgain!!.isVisible = false
                        menuItemEraser!!.isVisible = false
                        menuItemOutSourcedSupplier!!.isVisible = false

                        //start with receipt fragment
                        var fragment: Fragment? = null
                        val fragmentClass = UserSettingFragment::class.java

                        try {
                            fragment = fragmentClass.newInstance()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        val fragmentManager = supportFragmentManager
                        //fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
                        fragmentManager.beginTransaction().replace(R.id.flContent, fragment!!).commitAllowingStateLoss()

                        currentFrag = CurrentFragment.USER_SETTING_FRAGMENT

                        //hide print again button
                        fabPrintAgain!!.hide()

                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_HOME_GO_TO_GUEST_ACTION, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_HOME_GO_TO_GUEST_ACTION")

                        isBarcodeScanning = false

                        title = getString(R.string.nav_guest)

                        menuItemBluetooth!!.isVisible = false
                        menuItemKeyboard!!.isVisible = true
                        menuItemReceiptSetting!!.isVisible = false
                        menuItemShowReceiptConfirmFailed!!.isVisible = false
                        menuItemReconnectPrinter!!.isVisible = false
                        menuItemPrintAgain!!.isVisible = false
                        menuItemEraser!!.isVisible = false
                        menuItemOutSourcedSupplier!!.isVisible = false

                        //start with receipt fragment
                        var fragment: Fragment? = null
                        val fragmentClass = GuestFragment::class.java

                        try {
                            fragment = fragmentClass.newInstance()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        val fragmentManager = supportFragmentManager
                        //fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
                        fragmentManager.beginTransaction().replace(R.id.flContent, fragment!!).commitAllowingStateLoss()

                        currentFrag = CurrentFragment.GUEST_FRAGMENT

                        //hide print again button
                        fabPrintAgain!!.hide()

                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_HOME_GO_TO_PAINT_ACTION, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_HOME_GO_TO_PAINT_ACTION")

                        isBarcodeScanning = false

                        title = getString(R.string.nav_paint)

                        menuItemBluetooth!!.isVisible = false
                        menuItemKeyboard!!.isVisible = false
                        menuItemReceiptSetting!!.isVisible = false
                        menuItemShowReceiptConfirmFailed!!.isVisible = false
                        menuItemReconnectPrinter!!.isVisible = false
                        menuItemPrintAgain!!.isVisible = false
                        menuItemEraser!!.isVisible = true
                        menuItemEraser!!.setIcon(R.drawable.eraser_white)
                        isEraser = false
                        menuItemOutSourcedSupplier!!.isVisible = false

                        //start with receipt fragment
                        var fragment: Fragment? = null
                        val fragmentClass = DrawFragment::class.java

                        try {
                            fragment = fragmentClass.newInstance()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        val fragmentManager = supportFragmentManager
                        //fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
                        fragmentManager.beginTransaction().replace(R.id.flContent, fragment!!).commitAllowingStateLoss()

                        currentFrag = CurrentFragment.DRAW_FRAGMENT


                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_HOME_GO_TO_OUTSOURCED_ACTION, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_HOME_GO_TO_OUTSOURCED_ACTION")

                        isBarcodeScanning = false

                        title = getString(R.string.nav_outsourced)

                        menuItemBluetooth!!.isVisible = false
                        menuItemKeyboard!!.isVisible = true
                        menuItemReceiptSetting!!.isVisible = false
                        menuItemShowReceiptConfirmFailed!!.isVisible = false
                        menuItemReconnectPrinter!!.isVisible = false
                        menuItemPrintAgain!!.isVisible = false
                        menuItemEraser!!.isVisible = false
                        menuItemEraser!!.setIcon(R.drawable.eraser_white)
                        isEraser = false
                        menuItemOutSourcedSupplier!!.isVisible = true

                        //start with receipt fragment
                        var fragment: Fragment? = null
                        val fragmentClass = OutsourcedProcessingFragment::class.java

                        try {
                            fragment = fragmentClass.newInstance()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        val fragmentManager = supportFragmentManager
                        //fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
                        fragmentManager.beginTransaction().replace(R.id.flContent, fragment!!).commitAllowingStateLoss()

                        currentFrag = CurrentFragment.OUTSOURCED_FRAGMENT

                        //hide print again button
                        fabPrintAgain!!.hide()

                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_SETTING_RECEIPT_AUTO_CONFIRM_UPLOADED_ON, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_SETTING_RECEIPT_AUTO_CONFIRM_UPLOADED_ON")

                        isReceiptUploadAutoConfirm = true
                        //save
                        editor = pref!!.edit()
                        editor!!.putBoolean("IS_RECEIPT_UPLOAD_AUTO_CONFIRM", isReceiptUploadAutoConfirm)
                        editor!!.apply()

                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_SETTING_RECEIPT_AUTO_CONFIRM_UPLOADED_OFF, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_SETTING_RECEIPT_AUTO_CONFIRM_UPLOADED_OFF")

                        isReceiptUploadAutoConfirm = false
                        //save
                        editor = pref!!.edit()
                        editor!!.putBoolean("IS_RECEIPT_UPLOAD_AUTO_CONFIRM", isReceiptUploadAutoConfirm)
                        editor!!.apply()
                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_SETTING_LOG_ENABLE_ON, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_SETTING_LOG_ENABLE_ON")

                        isLogEnable = true
                        //save
                        editor = pref!!.edit()
                        editor!!.putBoolean("IS_LOG_ENABLE", isLogEnable)
                        editor!!.apply()

                        initLog()

                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_SETTING_LOG_ENABLE_OFF, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_SETTING_LOG_ENABLE_OFF")

                        isLogEnable = false
                        //save
                        editor = pref!!.edit()
                        editor!!.putBoolean("IS_LOG_ENABLE", isLogEnable)
                        editor!!.apply()

                        process!!.destroy()

                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_GUEST_GET_CURRENT_PLANT_GUEST_LIST, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_GUEST_GET_CURRENT_PLANT_GUEST_LIST")



                        val plant = intent.getStringExtra("PLANT")

                        Log.e(mTAG, "plant = $plant")

                        when(plant) {
                            "A" -> guestListA.clear()
                            "B" -> guestListB.clear()
                            else -> guestListT.clear()
                        }

                        getGuestMulti(plant as String)
                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_GUEST_GET_CURRENT_PLANT_GUEST_SUCCESS, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_GUEST_GET_CURRENT_PLANT_GUEST_SUCCESS")

                        val successIntent = Intent()
                        successIntent.action = Constants.ACTION.ACTION_GUEST_FRAGMENT_REFRESH
                        mContext!!.sendBroadcast(successIntent)

                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_GUEST_IN_OR_LEAVE_ACTION, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_GUEST_IN_OR_LEAVE_ACTION")

                        val inOrOut = intent.getStringExtra("DATA1")
                        val plant = intent.getStringExtra("DATA2")
                        val guestNo = intent.getStringExtra("DATA3")
                        val pmn01 = intent.getStringExtra("DATA4")
                        val pmn02 = intent.getStringExtra("DATA5")
                        val inDate = intent.getStringExtra("DATA6")

                        guestInOrOutMulti(inOrOut as String, plant as String, guestNo as String, pmn01 as String, pmn02 as String, inDate as String)

                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_GUEST_SEARCH_GUEST_LIST_ACTION, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_GUEST_SEARCH_GUEST_LIST_ACTION")

                        //save current plant
                        editor = pref!!.edit()
                        editor!!.putString("CURRENT_PLANT", currentPlant)
                        editor!!.apply()

                        //start from T
                        currentSearchPlant = "T"
                        getGuestMulti(currentSearchPlant)

                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_GUEST_SEARCH_GUEST_NEXT_ACTION, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_GUEST_SEARCH_GUEST_NEXT_ACTION")

                        when(currentSearchPlant) {
                            "T" -> currentSearchPlant = "A"
                            "A" -> currentSearchPlant = "B"
                        }

                        getGuestMulti(currentSearchPlant)
                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_GUEST_SEARCH_GUEST_COMPLETE, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_GUEST_SEARCH_GUEST_COMPLETE")

                        val successIntent = Intent()
                        successIntent.action = Constants.ACTION.ACTION_GUEST_FRAGMENT_REFRESH
                        mContext!!.sendBroadcast(successIntent)

                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_OUTSOURCED_PROCESS_GET_DETAIL_BY_SEND_ORDER, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_OUTSOURCED_PROCESS_GET_DETAIL_BY_SEND_ORDER")

                        val sendOrder = intent.getStringExtra("SEND_ORDER")

                        getOutSourcedProcessDetail(sendOrder as String)

                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_OUTSOURCED_PROCESS_SHOW_FAB_BACK, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_OUTSOURCED_PROCESS_SHOW_FAB_BACK")

                        fabBack!!.visibility = View.VISIBLE
                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_OUTSOURCED_PROCESS_HIDE_FAB_BACK, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_OUTSOURCED_PROCESS_HIDE_FAB_BACK")

                        fabBack!!.visibility = View.GONE
                        fabSign!!.visibility = View.GONE
                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_OUTSOURCED_PROCESS_SIGN_UPLOAD_ACTION, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_OUTSOURCED_PROCESS_SIGN_UPLOAD_ACTION")

                        val sendOrder = intent.getStringExtra("SEND_ORDER")
                        val uploadSignFileName = intent.getStringExtra("SIGN_FILE_NAME")

                        currentOutSourcedSendOrder = sendOrder as String

                        confirmOutSourcedProcessSign(sendOrder , uploadSignFileName as  String, user!!.userAccount)
                    }
                }

                //detect wifi
                if ("android.net.wifi.STATE_CHANGE" == intent.action) {
                    Log.e(mTAG, "Wifi STATE_CHANGE")

                    val wifiMgr = getSystemService(Context.WIFI_SERVICE) as WifiManager

                   if (wifiMgr.isWifiEnabled) { // Wi-Fi adapter is ON
                        val wifiInfo: WifiInfo = wifiMgr.connectionInfo
                        if (wifiInfo.networkId == -1) {
                            Log.d(mTAG, "Not connected to an access point")// Not connected to an access point
                            //fabWifi!!.visibility = View.VISIBLE

                            isWifiConnected = false
                            currentSSID = ""
                            Log.e(mTAG, "info ===> not connected ")
                            toast(getString(R.string.wifi_state_disconnected))
                        } else {
                            isWifiConnected = true

                            currentSSID = wifiInfo.ssid

                            Log.e(mTAG, "currentSSID = $currentSSID")
                            toast(getString(R.string.wifi_state_connected, currentSSID))
                            //Log.d(mTAG, "Connected to ${wifiInfo.ssid}")// Not connected to an access point
                            //fabWifi!!.visibility = View.GONE
                        }
                        // Connected to an access point
                    } else {
                        Log.d(mTAG, "Wi-Fi adapter is OFF")
                       //fabWifi!!.visibility = View.VISIBLE

                       isWifiConnected = false
                       currentSSID = ""
                       Log.e(mTAG, "info ===> not connected ")
                       toast(getString(R.string.wifi_state_disconnected))
                    }

                    /*val info: NetworkInfo? = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO)

                    if (info!!.isConnected) {
                        isWifiConnected = true
                        Log.e(mTAG, "info ===> connected ")
                        val wifiManager: WifiManager = mContext!!.getSystemService(Context.WIFI_SERVICE) as WifiManager
                        val wifiInfo = wifiManager.connectionInfo

                        val rssi = wifiInfo.rssi
                        val level = WifiManager.calculateSignalLevel(rssi, 10)
                        val percentage = (level / 10.0 * 100).toInt()

                        Log.e(mTAG, "rssi = $rssi, level = %$level, percentage = $percentage")

                        currentSSID = wifiInfo.ssid

                        if (currentSSID != "\"ERP06\"") {
                            Log.e(mTAG, "currentSSID is not ERP06, is $currentSSID")
                            toast(getString(R.string.wifi_state_ssid_mismatch))
                        } else {
                            Log.e(mTAG, "currentSSID = $currentSSID")
                            toast(getString(R.string.wifi_state_connected, currentSSID))

                            //if (percentage <= 20) {
                            //    if (percentage < 10) { // 0~9
                            //        fabWifi!!.setImageDrawable(getDrawable(R.drawable.baseline_signal_wifi_0_bar_white_48))
                            //        fabWifi!!.visibility = View.VISIBLE
                            //    } else {
                            //        fabWifi!!.setImageDrawable(getDrawable(R.drawable.baseline_signal_wifi_1_bar_white_48))
                            //        fabWifi!!.visibility = View.VISIBLE
                            //    }
                            //} else {
                            //    fabWifi!!.visibility = View.GONE
                            //}



                        }
                        fabWifi!!.visibility = View.GONE

                    } else {
                        //show wifi
                        fabWifi!!.visibility = View.VISIBLE

                        isWifiConnected = false
                        currentSSID = ""
                        Log.e(mTAG, "info ===> not connected ")
                        toast(getString(R.string.wifi_state_disconnected))
                    }*/

                    val changeIntent = Intent()
                    changeIntent.action = Constants.ACTION.ACTION_WIFI_STATE_CHANGED
                    sendBroadcast(changeIntent)

                }

                if ("android.net.wifi.WIFI_STATE_CHANGED" == intent.action) {
                    Log.e(mTAG, "Wifi WIFI_STATE_CHANGED")

                    //val wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN)
                }

                if ("unitech.scanservice.data" == intent.action) {
                    val bundle = intent.extras
                    if (bundle != null) {

                        if (isWifiConnected) {
                            //detect if is scanning or not
                            if (!isBarcodeScanning) {
                                isBarcodeScanning = true

                                val text = bundle.getString("text")
                                Log.d(mTAG, "text = " + text!!)
                                //showMyToast(text, ReceiptActivity.this);



                                //hide printAgain button
                                fabPrintAgain!!.visibility = View.GONE

                                //clear
                                //ReceiptList.removeAllItem()
                                //rva06 = ""
                                itemReceipt = null

                                //et_Barcode.setText(text)

                                barcode = ScanBarcode.setPoBarcodeByScanTransform(text.toString().trim())

                                if (isWifiConnected) {
                                    val scanIntent = Intent()
                                    when (currentFrag) {
                                        CurrentFragment.RECEIPT_FRAGMENT -> {

                                            scanIntent.action = Constants.ACTION.ACTION_RECEIPT_SCAN_BARCODE
                                            scanIntent.putExtra("BARCODE", text)
                                            sendBroadcast(scanIntent)
                                            //checkIfReceiptUploaded(barcode)
                                            getReceipt(barcode)
                                        }
                                        CurrentFragment.STORAGE_FRAGMENT -> {

                                            scanIntent.action = Constants.ACTION.ACTION_STORAGE_SCAN_BARCODE
                                            scanIntent.putExtra("BARCODE", text)
                                            sendBroadcast(scanIntent)

                                            getStorage(barcode)
                                        }
                                        CurrentFragment.MATERIAL_ISSUING_FRAGMENT -> {

                                            scanIntent.action = Constants.ACTION.ACTION_MATERIAL_SCAN_BARCODE
                                            scanIntent.putExtra("BARCODE", text)
                                            sendBroadcast(scanIntent)

                                            getMaterial(barcode)
                                        }
                                        CurrentFragment.PROPERTY_FRAGMENT -> {

                                            scanIntent.action = Constants.ACTION.ACTION_PROPERTY_SCAN_BARCODE
                                            scanIntent.putExtra("BARCODE", text)
                                            sendBroadcast(scanIntent)

                                            getProperty(barcode)
                                        }
                                        CurrentFragment.GUEST_FRAGMENT -> {

                                            scanIntent.action = Constants.ACTION.ACTION_GUEST_SCAN_BARCODE
                                            scanIntent.putExtra("BARCODE", barcode!!.poBarcode)
                                            scanIntent.putExtra("LINE", barcode!!.poLine)
                                            sendBroadcast(scanIntent)
                                            getReceipt(barcode)
                                        }
                                        CurrentFragment.OUTSOURCED_FRAGMENT -> {
                                            scanIntent.action = Constants.ACTION.ACTION_OUTSOURCED_PROCESS_SCAN_BARCODE
                                            scanIntent.putExtra("BARCODE", text)
                                            sendBroadcast(scanIntent)

                                            getOutSourcedProcessBySupplierNo(text)
                                        }
                                        else -> {
                                            isBarcodeScanning = false
                                        }
                                    }
                                } else {
                                    toast(getString(R.string.get_or_send_failed_wifi_is_not_connected))
                                    isBarcodeScanning = false
                                }
                            } else {
                                Log.e(mTAG, "isBarcodeScanning = true")
                                toast(getString(R.string.barcode_scanning_get_info))
                            }
                        } else {
                            Log.e(mTAG, "Wifi is not connected. Barcode scan is useless.")
                            toast(getString(R.string.barcode_scan_off_because_wifi_is_not_connected))
                        }
                    }
                }
                if ("unitech.scanservice.datatype" == intent.action) {
                    val bundle = intent.extras
                    if (bundle != null) {
                        val type = bundle.getInt("text")

                        Log.d(mTAG, "type = $type")

                    }
                }
            }
        }


        if (!isRegister) {
            filter = IntentFilter()
            //login
            filter.addAction(Constants.ACTION.ACTION_LOGIN_ACTION)
            filter.addAction(Constants.ACTION.ACTION_LOGIN_NETWORK_ERROR)
            filter.addAction(Constants.ACTION.ACTION_LOGIN_SUCCESS)
            filter.addAction(Constants.ACTION.ACTION_LOGIN_FAILED)
            //logout
            filter.addAction(Constants.ACTION.ACTION_LOGOUT_ACTION)
            //keyboard
            filter.addAction(Constants.ACTION.ACTION_HIDE_KEYBOARD)
            //bluetooth
            filter.addAction(Constants.ACTION.ACTION_SETTING_BLUETOOTH_CONNECT_ACTION)
            filter.addAction(Constants.ACTION.ACTION_SETTING_PRINTTEST_ACTION)
            //receipt
            filter.addAction(Constants.ACTION.ACTION_USER_INPUT_SEARCH)
            filter.addAction(Constants.ACTION.ACTION_RECEIPT_UPLOAD_ACTION)
            filter.addAction(Constants.ACTION.ACTION_RECEIPT_UPLOAD_SUCCESS)
            filter.addAction(Constants.ACTION.ACTION_RECEIPT_UPLOAD_RETURN_PO_DIFF)
            filter.addAction(Constants.ACTION.ACTION_RECEIPT_UPLOAD_FAILED)
            filter.addAction(Constants.ACTION.ACTION_RECEIPT_UPLOAD_RETURN_EXCEPTION)
            filter.addAction(Constants.ACTION.ACTION_RECEIPT_UPLOADED_NO)
            filter.addAction(Constants.ACTION.ACTION_RECEIPT_UPLOADED_YES)
            filter.addAction(Constants.ACTION.ACTION_RECEIPT_UPLOADED_CONFIRM_ACTION)
            //storage
            filter.addAction(Constants.ACTION.ACTION_STORAGE_UPDATE_ACTION)
            //material
            filter.addAction(Constants.ACTION.ACTION_MATERIAL_SEND_CHANGED_UPDATE_ACTION)
            filter.addAction(Constants.ACTION.ACTION_MATERIAL_SEND_CHANGED_FAILED)
            filter.addAction(Constants.ACTION.ACTION_MATERIAL_SEND_CHANGED_SUCCESS)
            filter.addAction(Constants.ACTION.ACTION_MATERIAL_SEND_CHANGED_COMPLETE)
            filter.addAction(Constants.ACTION.ACTION_SEEK_BAR_HIDE_ACTION)
            filter.addAction(Constants.ACTION.ACTION_SEEK_BAR_SHOW_ACTION)
            //home
            filter.addAction(Constants.ACTION.ACTION_HOME_GO_TO_RECEIPT_ACTION)
            filter.addAction(Constants.ACTION.ACTION_HOME_GO_TO_STORAGE_ACTION)
            filter.addAction(Constants.ACTION.ACTION_HOME_GO_TO_MATERIAL_ACTION)
            filter.addAction(Constants.ACTION.ACTION_HOME_GO_TO_TAG_PRINTER_ACTION)
            filter.addAction(Constants.ACTION.ACTION_HOME_GO_TO_LOGOUT_ACTION)
            filter.addAction(Constants.ACTION.ACTION_HOME_GO_TO_ABOUT_ACTION)
            filter.addAction(Constants.ACTION.ACTION_HOME_GO_TO_PROPERTY_ACTION)
            filter.addAction(Constants.ACTION.ACTION_HOME_GO_TO_USER_SETTING_ACTION)
            filter.addAction(Constants.ACTION.ACTION_HOME_GO_TO_GUEST_ACTION)
            filter.addAction(Constants.ACTION.ACTION_HOME_GO_TO_PAINT_ACTION)
            filter.addAction(Constants.ACTION.ACTION_HOME_GO_TO_OUTSOURCED_ACTION)
            //user setting
            filter.addAction(Constants.ACTION.ACTION_SETTING_RECEIPT_AUTO_CONFIRM_UPLOADED_ON)
            filter.addAction(Constants.ACTION.ACTION_SETTING_RECEIPT_AUTO_CONFIRM_UPLOADED_OFF)
            filter.addAction(Constants.ACTION.ACTION_SETTING_LOG_ENABLE_ON)
            filter.addAction(Constants.ACTION.ACTION_SETTING_LOG_ENABLE_OFF)
            //guest
            filter.addAction(Constants.ACTION.ACTION_GUEST_GET_CURRENT_PLANT_GUEST_LIST)
            filter.addAction(Constants.ACTION.ACTION_GUEST_GET_CURRENT_PLANT_GUEST_SUCCESS)
            //filter.addAction(Constants.ACTION.ACTION_GUEST_GET_CURRENT_PLANT_GUEST_FAILED)
            filter.addAction(Constants.ACTION.ACTION_GUEST_IN_OR_LEAVE_ACTION)
            filter.addAction(Constants.ACTION.ACTION_GUEST_SEARCH_GUEST_LIST_ACTION)
            filter.addAction(Constants.ACTION.ACTION_GUEST_SEARCH_GUEST_NEXT_ACTION)
            filter.addAction(Constants.ACTION.ACTION_GUEST_SEARCH_GUEST_COMPLETE)
            //outsourced process
            filter.addAction(Constants.ACTION.ACTION_OUTSOURCED_PROCESS_GET_DETAIL_BY_SEND_ORDER)
            filter.addAction(Constants.ACTION.ACTION_OUTSOURCED_PROCESS_SIGN_UPLOAD_ACTION)
            //filter.addAction(Constants.ACTION.ACTION_OUTSOURCED_PROCESS_SIGN_UPLOAD_FAILED)
            //filter.addAction(Constants.ACTION.ACTION_OUTSOURCED_PROCESS_SIGN_UPLOAD_SUCCESS)

            filter.addAction(Constants.ACTION.ACTION_OUTSOURCED_PROCESS_SHOW_FAB_BACK)
            filter.addAction(Constants.ACTION.ACTION_OUTSOURCED_PROCESS_HIDE_FAB_BACK)

            filter.addAction("android.net.wifi.STATE_CHANGE")
            filter.addAction("android.net.wifi.WIFI_STATE_CHANGED")
            filter.addAction("unitech.scanservice.data")
            filter.addAction("unitech.scanservice.datatype")
            mContext!!.registerReceiver(mReceiver, filter)
            isRegister = true
            Log.d(mTAG, "registerReceiver mReceiver")
        }
    }

    override fun onDestroy() {
        Log.i(mTAG, "onDestroy")

        //enable Scan2Key Setting
        val enableServiceIntent = Intent()
        enableServiceIntent.action = "unitech.scanservice.scan2key_setting"
        enableServiceIntent.putExtra("scan2key", true)
        sendBroadcast(enableServiceIntent)

        if (mChatService != null) {
            mChatService!!.stop()
        }

        if (isRegister && mReceiver != null) {
            try {
                mContext!!.unregisterReceiver(mReceiver)
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            }

            isRegister = false
            mReceiver = null
            Log.d(mTAG, "unregisterReceiver mReceiver")
        }

        super.onDestroy()
    }

    override fun onResume() {
        Log.i(mTAG, "onResume")
        super.onResume()

        //disable Scan2Key Setting
        val disableServiceIntent = Intent()
        disableServiceIntent.action = "unitech.scanservice.scan2key_setting"
        disableServiceIntent.putExtra("scan2key", false)
        sendBroadcast(disableServiceIntent)

    }

    override fun onPause() {
        Log.i(mTAG, "onPause")
        super.onPause()

        //disable Scan2Key Setting
        val enableServiceIntent = Intent()
        enableServiceIntent.action = "unitech.scanservice.scan2key_setting"
        enableServiceIntent.putExtra("scan2key", true)
        sendBroadcast(enableServiceIntent)
    }

    override fun onBackPressed() {

        if (isOutSourcedInDetail == 1 || isPropertyInDetail == 1) { //if in outsourced detail

            fabBack!!.visibility = View.GONE
            fabSign!!.visibility = View.GONE

            if (isOutSourcedInDetail == 1) {
                val backIntent = Intent()
                backIntent.action = Constants.ACTION.ACTION_OUTSOURCED_PROCESS_BACK_TO_SUPPLIER_LIST
                sendBroadcast(backIntent)
            } else {
                val backIntent = Intent()
                backIntent.action = Constants.ACTION.ACTION_PROPERTY_BACK_TO_LIST
                sendBroadcast(backIntent)
            }

        } else if (isOutSourcedInDetail == 2) {
            val backIntent = Intent()
            backIntent.action = Constants.ACTION.ACTION_OUTSOURCED_PROCESS_BACK_TO_DETAIL_LIST
            sendBroadcast(backIntent)
        } else {
            showExitConfirmDialog()
        }



    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)

        menuItemKeyboard = menu.findItem(R.id.main_hide_or_show_keyboard)
        menuItemBluetooth = menu.findItem(R.id.main_set_bluetooth_printer)
        menuItemSeekBar = menu.findItem(R.id.main_seek_bar)
        menuItemReceiptSetting = menu.findItem(R.id.main_receipt_setting)
        menuItemEraser = menu.findItem(R.id.main_draw_pen_or_eraser)
        menuItemOutSourcedSupplier = menu.findItem(R.id.main_supplier_list)
        menuItemShowReceiptConfirmFailed = menu.findItem(R.id.main_receipt_show_confirm_failed)
        menuItemReconnectPrinter = menu.findItem(R.id.main_reconnect_printer)
        menuItemPrintAgain = menu.findItem(R.id.main_print_again)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.main_seek_bar -> {
                when(currentFrag) {
                    CurrentFragment.MATERIAL_ISSUING_FRAGMENT -> showMaterialSeekDialog()
                    CurrentFragment.PROPERTY_FRAGMENT -> showPropertySeekDialog()
                    else -> {

                    }
                }
            }

            R.id.main_hide_or_show_keyboard -> {
                imm?.toggleSoftInput(InputMethodManager.RESULT_HIDDEN, 0)
            }
            R.id.main_set_bluetooth_printer -> {
                Log.e(mTAG, "main_set_bluetooth_printer")
                val intent = Intent(this, DeviceListActivity::class.java)
                intent.putExtra("SET_DEV", setPrinterDev)
                startActivityForResult(intent, setPrinterDev)
            }

            R.id.main_receipt_setting-> {
                showSettingReceiptDialog()
            }

            R.id.main_receipt_show_confirm_failed-> {
                val intent = Intent(this, ShowReceiptConfirmFailedActivity::class.java)
                startActivity(intent)
            }

            R.id.main_draw_pen_or_eraser-> {
                Log.e(mTAG, "main_draw_pen_or_eraser: $isEraser")

                if (isEraser) { //eraser -> pen
                    Log.e(mTAG, "Color Black")
                    menuItemEraser!!.setIcon(R.drawable.eraser_white)
                    penColor = Color.BLACK
                    penWidth = 10f
                } else {
                    Log.e(mTAG, "Color White")
                    menuItemEraser!!.setIcon(R.drawable.baseline_create_white_24)
                    penColor = Color.WHITE
                    penWidth = 50f
                }

                isEraser = !isEraser


            }

            R.id.main_supplier_list-> {
                val showIntent = Intent()
                showIntent.action = Constants.ACTION.ACTION_OUTSOURCED_PROCESS_SHOW_SUPPLIER_DIALOG
                sendBroadcast(showIntent)
            }

            R.id.main_reconnect_printer-> {
                val connectIntent = Intent()
                connectIntent.action = Constants.ACTION.ACTION_SETTING_BLUETOOTH_CONNECT_ACTION
                sendBroadcast(connectIntent)

                //fabPrint!!.visibility = View.GONE
                fabPrint!!.hide()
            }

            R.id.main_print_again-> {
                Log.d(mTAG, "===> Print it again")

                showPrintAgainConfirmDialog()
            }
        }


        return true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.

        selectDrawerItem(item)

        /*when (item.itemId) {
            R.id.nav_receipt -> {
                // Handle the camera action
            }
            R.id.nav_storage -> {

            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_tools -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)*/
        return true
    }

    private fun selectDrawerItem(menuItem: MenuItem) {
        var fragment: Fragment? = null
        var fragmentClass: Class<*>? = null

        var title = ""
        //hide keyboard
        val view = currentFocus

        if (view != null) {
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }

        navView!!.menu.getItem(0).isChecked = false //home
        navView!!.menu.getItem(1).isChecked = false //receipt
        navView!!.menu.getItem(2).isChecked = false //storage
        navView!!.menu.getItem(3).isChecked = false //material
        navView!!.menu.getItem(4).isChecked = false //outsourced
        navView!!.menu.getItem(5).isChecked = false //property
        navView!!.menu.getItem(6).isChecked = false //login
        navView!!.menu.getItem(7).isChecked = false //printer
        navView!!.menu.getItem(8).isChecked = false //setting
        navView!!.menu.getItem(9).isChecked = false //guest
        navView!!.menu.getItem(10).isChecked = false //about
        navView!!.menu.getItem(11).isChecked = false //logout

        var statusTitle = ""
        when(printerStatus) {
            BluetoothChatService.STATE_NONE, BluetoothChatService.STATE_LISTEN-> {
                statusTitle = getString(R.string.tag_printer_status_not_connected)
            }
            BluetoothChatService.STATE_CONNECTING-> {
                statusTitle = getString(R.string.tag_printer_status_connecting)
            }
            BluetoothChatService.STATE_CONNECTED-> {
                statusTitle = getString(R.string.tag_printer_status_connected)
            }

        }

        currentSelectMenuItem = menuItem

        when (menuItem.itemId) {
            R.id.nav_receipt -> {
                menuItemKeyboard!!.isVisible = true
                menuItemBluetooth!!.isVisible = true
                menuItemSeekBar!!.isVisible = false
                menuItemReceiptSetting!!.isVisible = true
                menuItemShowReceiptConfirmFailed!!.isVisible = true
                menuItemReconnectPrinter!!.isVisible = true
                menuItemPrintAgain!!.isVisible = true
                menuItemEraser!!.isVisible = false
                menuItemOutSourcedSupplier!!.isVisible = false

                title = getString(R.string.nav_receipt) +" - "+ statusTitle
                fragmentClass = ReceiptFragment::class.java
                menuItem.isChecked = true
                currentFrag = CurrentFragment.RECEIPT_FRAGMENT

                //show depends on bluetooth connect
                when(printerStatus) {
                    BluetoothChatService.STATE_NONE, BluetoothChatService.STATE_LISTEN, BluetoothChatService.STATE_CONNECTING-> {
                        //fabPrint!!.visibility = View.VISIBLE
                        fabPrint!!.show()
                    }

                    BluetoothChatService.STATE_CONNECTED-> {
                        //fabPrint!!.visibility = View.GONE
                        fabPrint!!.hide()
                    }
                }
                isBarcodeScanning = false
                //hide print again button
                fabPrintAgain!!.hide()
            }
            R.id.nav_storage -> {
                menuItemKeyboard!!.isVisible = true
                menuItemBluetooth!!.isVisible = false
                menuItemSeekBar!!.isVisible = false
                menuItemReceiptSetting!!.isVisible = false
                menuItemShowReceiptConfirmFailed!!.isVisible = false
                menuItemReconnectPrinter!!.isVisible = false
                menuItemPrintAgain!!.isVisible = false
                menuItemEraser!!.isVisible = false
                menuItemOutSourcedSupplier!!.isVisible = false
                //title = getString(R.string.nav_storage) +" - "+ statusTitle
                title = getString(R.string.nav_storage)
                fragmentClass = StorageFragment::class.java
                menuItem.isChecked = true
                currentFrag = CurrentFragment.STORAGE_FRAGMENT

                //must hide fab print
                //fabPrint!!.visibility = View.GONE
                fabPrint!!.hide()
                isBarcodeScanning = false
                //hide print again button
                fabPrintAgain!!.hide()
            }
            R.id.nav_material_issuing -> {
                menuItemKeyboard!!.isVisible = true
                menuItemBluetooth!!.isVisible = false
                menuItemSeekBar!!.isVisible = false
                menuItemReceiptSetting!!.isVisible = false
                menuItemShowReceiptConfirmFailed!!.isVisible = false
                menuItemReconnectPrinter!!.isVisible = false
                menuItemPrintAgain!!.isVisible = false
                menuItemEraser!!.isVisible = false
                menuItemOutSourcedSupplier!!.isVisible = false
                title = getString(R.string.nav_material_issuing)
                fragmentClass = MaterialIssuingFragment::class.java
                menuItem.isChecked = true
                currentFrag = CurrentFragment.MATERIAL_ISSUING_FRAGMENT

                //must hide fab print
                //fabPrint!!.visibility = View.GONE
                fabPrint!!.hide()
                isBarcodeScanning = false
                //hide print again button
                fabPrintAgain!!.hide()
            }
            R.id.nav_property -> {
                menuItemKeyboard!!.isVisible = true
                menuItemBluetooth!!.isVisible = false
                menuItemSeekBar!!.isVisible = false
                menuItemReceiptSetting!!.isVisible = false
                menuItemShowReceiptConfirmFailed!!.isVisible = false
                menuItemReconnectPrinter!!.isVisible = false
                menuItemPrintAgain!!.isVisible = false
                menuItemEraser!!.isVisible = false
                menuItemOutSourcedSupplier!!.isVisible = false
                title = getString(R.string.nav_property)
                fragmentClass = PropertyFragment::class.java
                menuItem.isChecked = true
                currentFrag = CurrentFragment.PROPERTY_FRAGMENT

                //must hide fab print
                //fabPrint!!.visibility = View.GONE
                fabPrint!!.hide()
                isBarcodeScanning = false
                //hide print again button
                fabPrintAgain!!.hide()
            }
            R.id.nav_home -> {
                menuItemKeyboard!!.isVisible = false
                menuItemBluetooth!!.isVisible = false
                menuItemSeekBar!!.isVisible = false
                menuItemReceiptSetting!!.isVisible = false
                menuItemShowReceiptConfirmFailed!!.isVisible = false
                menuItemReconnectPrinter!!.isVisible = false
                menuItemPrintAgain!!.isVisible = false
                menuItemEraser!!.isVisible = false
                menuItemOutSourcedSupplier!!.isVisible = false
                title = getString(R.string.nav_home)
                fragmentClass = HomeGridFragment::class.java
                menuItem.isChecked = true
                currentFrag = CurrentFragment.HOME_FRAGMENT

                //must hide fab print
                //fabPrint!!.visibility = View.GONE
                fabPrint!!.hide()
                isBarcodeScanning = false
                //hide print again button
                fabPrintAgain!!.hide()
            }
            R.id.nav_login -> {
                menuItemKeyboard!!.isVisible = true
                menuItemBluetooth!!.isVisible = false
                menuItemSeekBar!!.isVisible = false
                menuItemReceiptSetting!!.isVisible = false
                menuItemShowReceiptConfirmFailed!!.isVisible = false
                menuItemReconnectPrinter!!.isVisible = false
                menuItemPrintAgain!!.isVisible = false
                menuItemEraser!!.isVisible = false
                menuItemOutSourcedSupplier!!.isVisible = false
                title = getString(R.string.nav_login)
                fragmentClass = LoginFragment::class.java
                menuItem.isChecked = true
                currentFrag = CurrentFragment.LOGIN_FRAGMENT

                //must hide fab print
                //fabPrint!!.visibility = View.GONE
                fabPrint!!.hide()
                isBarcodeScanning = false
                //hide print again button
                fabPrintAgain!!.hide()
            }
            R.id.nav_logout -> {
                isBarcodeScanning = false

                menuItem.isChecked = true
                showLogoutConfirmDialog()

                /*
                val confirmDialog = AlertDialog.Builder(this@MainActivity)
                confirmDialog.setIcon(R.drawable.baseline_warning_black_48)
                confirmDialog.setTitle(resources.getString(R.string.logout_title))
                confirmDialog.setMessage(resources.getString(R.string.logout_title_msg))
                confirmDialog.setPositiveButton(
                    resources.getString(R.string.ok)
                ) { _, _ ->
                    val drawer: DrawerLayout = findViewById(R.id.drawer_layout)
                    drawer.closeDrawer(GravityCompat.START)

                    val logoutIntent = Intent(Constants.ACTION.ACTION_LOGOUT_ACTION)
                    mContext?.sendBroadcast(logoutIntent)
                }
                confirmDialog.setNegativeButton(
                    resources.getString(R.string.cancel)
                ) { _, _ ->

                    menuItem.isChecked = false
                }
                confirmDialog.show()*/
            }
            R.id.nav_tag_printer -> {
                menuItemKeyboard!!.isVisible = false
                menuItemBluetooth!!.isVisible = true
                menuItemSeekBar!!.isVisible = false
                menuItemReceiptSetting!!.isVisible = false
                menuItemShowReceiptConfirmFailed!!.isVisible = false
                menuItemReconnectPrinter!!.isVisible = false
                menuItemPrintAgain!!.isVisible = false
                menuItemEraser!!.isVisible = false
                menuItemOutSourcedSupplier!!.isVisible = false
                title = getString(R.string.nav_printer)
                fragmentClass = SettingFragment::class.java
                menuItem.isChecked = true
                currentFrag = CurrentFragment.PRINTER_FRAGMENT

                isBarcodeScanning = false
                //hide print again button
                fabPrintAgain!!.hide()
            }
            R.id.nav_about -> {
                isBarcodeScanning = false
                //menuItemKeyboard!!.isVisible = false
                //menuItemBluetooth!!.isVisible = false
                //menuItemSeekBar!!.isVisible = false
                //title = getString(R.string.nav_about)
                //fragmentClass = AboutFragment::class.java
                menuItem.isChecked = true
                showCurrentVersionDialog()
                //currentFrag = CurrentFragment.ABOUT_FRAGMENT

                //must hide fab print
                //fabPrint!!.visibility = View.GONE
            }
            R.id.nav_setting -> {
                menuItemKeyboard!!.isVisible = false
                menuItemBluetooth!!.isVisible = false
                menuItemSeekBar!!.isVisible = false
                menuItemReceiptSetting!!.isVisible = false
                menuItemShowReceiptConfirmFailed!!.isVisible = false
                menuItemReconnectPrinter!!.isVisible = false
                menuItemPrintAgain!!.isVisible = false
                menuItemEraser!!.isVisible = false
                menuItemOutSourcedSupplier!!.isVisible = false
                title = getString(R.string.nav_setting)
                fragmentClass = UserSettingFragment::class.java
                menuItem.isChecked = true
                currentFrag = CurrentFragment.USER_SETTING_FRAGMENT

                //must hide fab print
                //fabPrint!!.visibility = View.GONE
                fabPrint!!.hide()

                isBarcodeScanning = false
                //hide print again button
                fabPrintAgain!!.hide()
            }
            R.id.nav_guest -> {
                menuItemKeyboard!!.isVisible = false
                menuItemBluetooth!!.isVisible = false
                menuItemSeekBar!!.isVisible = false
                menuItemReceiptSetting!!.isVisible = false
                menuItemShowReceiptConfirmFailed!!.isVisible = false
                menuItemReconnectPrinter!!.isVisible = false
                menuItemPrintAgain!!.isVisible = false
                menuItemEraser!!.isVisible = false
                menuItemOutSourcedSupplier!!.isVisible = false
                title = getString(R.string.nav_guest)
                fragmentClass = GuestFragment::class.java
                menuItem.isChecked = true
                currentFrag = CurrentFragment.GUEST_FRAGMENT

                //must hide fab print
                //fabPrint!!.visibility = View.GONE
                fabPrint!!.hide()
                isBarcodeScanning = false
                //hide print again button
                fabPrintAgain!!.hide()
            }
            R.id.nav_outsourced -> {
                menuItemKeyboard!!.isVisible = true
                menuItemBluetooth!!.isVisible = false
                menuItemSeekBar!!.isVisible = false
                menuItemReceiptSetting!!.isVisible = false
                menuItemShowReceiptConfirmFailed!!.isVisible = false
                menuItemReconnectPrinter!!.isVisible = false
                menuItemPrintAgain!!.isVisible = false
                menuItemEraser!!.isVisible = false
                menuItemOutSourcedSupplier!!.isVisible = true
                title = getString(R.string.nav_outsourced)
                fragmentClass = OutsourcedProcessingFragment::class.java
                menuItem.isChecked = true
                currentFrag = CurrentFragment.OUTSOURCED_FRAGMENT

                //must hide fab print
                //fabPrint!!.visibility = View.GONE
                fabPrint!!.hide()
                isBarcodeScanning = false
                //hide print again button
                fabPrintAgain!!.hide()
            }
        }

        if (fragmentClass != null) {
            try {
                fragment = fragmentClass.newInstance() as Fragment
            } catch (e: Exception) {
                e.printStackTrace()
            }


            // Insert the fragment by replacing any existing fragment
            val fragmentManager = supportFragmentManager
            //fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment!!).commitAllowingStateLoss()

            // Highlight the selected item has been done by NavigationView

            // Set action bar title
            if (title.isNotEmpty())
                setTitle(title)
            else
                setTitle(menuItem.title)

            // Close the navigation drawer
            val drawer: DrawerLayout = findViewById(R.id.drawer_layout)
            drawer.closeDrawer(GravityCompat.START)
        }



    }

    private fun checkAndRequestPermissions() {

        //int accessNetworkStatePermission = ContextCompat.checkSelfPermission(this,
        //        Manifest.permission.ACCESS_NETWORK_STATE);

        //int accessWiFiStatePermission = ContextCompat.checkSelfPermission(this,
        //        Manifest.permission.ACCESS_WIFI_STATE);

        val readPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        val writePermission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        val networkPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)


        val coarsePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)

        val bluetoothAdminPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN)

        val bluetoothPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH)

        val accessNetworkStatePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE)

        val accessWiFiStatePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE)

        val changeWifiStatePermissions = ContextCompat.checkSelfPermission(this, Manifest.permission.CHANGE_WIFI_STATE)
        //int cameraPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);

        val listPermissionsNeeded = ArrayList<String>()

        if (readPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        if (writePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        if (networkPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.INTERNET)
        }

        if (coarsePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }

        if (bluetoothAdminPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.BLUETOOTH_ADMIN)
        }

        if (bluetoothPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.BLUETOOTH)
        }

        if (accessNetworkStatePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_NETWORK_STATE)
        }

        if (accessWiFiStatePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_WIFI_STATE)
        }

        if (changeWifiStatePermissions != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CHANGE_WIFI_STATE)
        }
        //if (permissionSendMessage != PackageManager.PERMISSION_GRANTED) {
        //    listPermissionsNeeded.add(android.Manifest.permission.WRITE_CALENDAR);
        //}
        //if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
        //    listPermissionsNeeded.add(android.Manifest.permission.CAMERA);
        //}

        if (listPermissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                listPermissionsNeeded.toTypedArray(),
                requestIdMultiplePermission
            )
            //return false;
        } else {
            Log.e(mTAG, "All permission are granted")
            initView()
            initLog()
        }
        //return true;
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {

        Log.d(mTAG, "Permission callback called-------")
        when (requestCode) {
            requestIdMultiplePermission -> {

                val perms: HashMap<String, Int>? = HashMap()

                // Initialize the map with both permissions
                perms!![Manifest.permission.READ_EXTERNAL_STORAGE] = PackageManager.PERMISSION_GRANTED
                perms[Manifest.permission.WRITE_EXTERNAL_STORAGE] = PackageManager.PERMISSION_GRANTED
                perms[Manifest.permission.INTERNET] = PackageManager.PERMISSION_GRANTED
                perms[Manifest.permission.ACCESS_COARSE_LOCATION] = PackageManager.PERMISSION_GRANTED
                perms[Manifest.permission.BLUETOOTH_ADMIN] = PackageManager.PERMISSION_GRANTED
                perms[Manifest.permission.BLUETOOTH] = PackageManager.PERMISSION_GRANTED
                perms[Manifest.permission.ACCESS_NETWORK_STATE] = PackageManager.PERMISSION_GRANTED
                perms[Manifest.permission.ACCESS_WIFI_STATE] = PackageManager.PERMISSION_GRANTED
                perms[Manifest.permission.CHANGE_WIFI_STATE] = PackageManager.PERMISSION_GRANTED
                //perms.put(Manifest.permission.ACCESS_WIFI_STATE, PackageManager.PERMISSION_GRANTED);
                // Fill with actual results from user
                //if (grantResults.size > 0) {
                if (grantResults.isNotEmpty()) {
                    for (i in permissions.indices)
                        perms[permissions[i]] = grantResults[i]
                    // Check for both permissions
                    if (perms[Manifest.permission.READ_EXTERNAL_STORAGE] == PackageManager.PERMISSION_GRANTED
                        && perms[Manifest.permission.WRITE_EXTERNAL_STORAGE] == PackageManager.PERMISSION_GRANTED
                        && perms[Manifest.permission.INTERNET] == PackageManager.PERMISSION_GRANTED
                        && perms[Manifest.permission.ACCESS_COARSE_LOCATION] == PackageManager.PERMISSION_GRANTED
                        && perms[Manifest.permission.BLUETOOTH_ADMIN] == PackageManager.PERMISSION_GRANTED
                        && perms[Manifest.permission.BLUETOOTH] == PackageManager.PERMISSION_GRANTED
                        && perms[Manifest.permission.ACCESS_NETWORK_STATE] == PackageManager.PERMISSION_GRANTED
                        && perms[Manifest.permission.ACCESS_WIFI_STATE] == PackageManager.PERMISSION_GRANTED
                        && perms[Manifest.permission.CHANGE_WIFI_STATE] == PackageManager.PERMISSION_GRANTED
                    ) {
                        Log.d(mTAG, "write permission granted")

                        // process the normal flow
                        //else any one or both the permissions are not granted
                        //init_folder_and_files()
                        //init_setting();
                        initView()
                        initLog()
                    } else {
                        Log.d(mTAG, "Some permissions are not granted ask again ")
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
                        //                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(
                                this,
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            )
                            || ActivityCompat.shouldShowRequestPermissionRationale(
                                this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            )
                            || ActivityCompat.shouldShowRequestPermissionRationale(
                                this,
                                Manifest.permission.INTERNET
                            )
                            || ActivityCompat.shouldShowRequestPermissionRationale(
                                this,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                            || ActivityCompat.shouldShowRequestPermissionRationale(
                                this,
                                Manifest.permission.BLUETOOTH_ADMIN
                            )
                            || ActivityCompat.shouldShowRequestPermissionRationale(
                                this,
                                Manifest.permission.BLUETOOTH
                            )
                            || ActivityCompat.shouldShowRequestPermissionRationale(
                                this,
                                Manifest.permission.ACCESS_NETWORK_STATE
                            )
                            || ActivityCompat.shouldShowRequestPermissionRationale(
                                this,
                                Manifest.permission.ACCESS_WIFI_STATE
                            )
                            || ActivityCompat.shouldShowRequestPermissionRationale(
                                this,
                                Manifest.permission.CHANGE_WIFI_STATE
                            )
                        ) {
                            showDialogOK(
                                DialogInterface.OnClickListener { _, which ->
                                    when (which) {
                                        DialogInterface.BUTTON_POSITIVE -> checkAndRequestPermissions()
                                        DialogInterface.BUTTON_NEGATIVE ->
                                            // proceed with logic by disabling the related features or quit the app.
                                            finish()
                                    }
                                })
                        } else {
                            Toast.makeText(this, "Go to settings and enable permissions", Toast.LENGTH_LONG)
                                .show()
                            //                            //proceed with logic by disabling the related features or quit the app.
                        }//|| ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_NETWORK_STATE )
                        //|| ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_WIFI_STATE )
                        //permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                    }//&& perms.get(Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED &&
                    //perms.get(Manifest.permission.ACCESS_WIFI_STATE) == PackageManager.PERMISSION_GRANTED
                }
            }
        }

    }

    private fun showDialogOK(okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(this)
            .setMessage("Warning")
            .setPositiveButton("Ok", okListener)
            .setNegativeButton("Cancel", okListener)
            .create()
            .show()
    }

    private fun toast(message: String) {

        if (toastHandle != null) {
            toastHandle!!.cancel()
        }

        val toast = Toast.makeText(this, HtmlCompat.fromHtml("<h1>$message</h1>", HtmlCompat.FROM_HTML_MODE_COMPACT), Toast.LENGTH_LONG)
        toast.setGravity(Gravity.CENTER_HORIZONTAL or Gravity.CENTER_VERTICAL, 0, 0)
        /*val group = toast.view as ViewGroup
        val textView = group.getChildAt(0) as TextView
        textView.textSize = 30.0f*/
        toast.show()

        toastHandle = toast
    }

    private fun toastLong(message: String) {

        if (toastHandle != null)
            toastHandle!!.cancel()

        val toast = Toast.makeText(this, HtmlCompat.fromHtml("<h1>$message</h1>", HtmlCompat.FROM_HTML_MODE_COMPACT), Toast.LENGTH_LONG)
        toast.setGravity(Gravity.CENTER_HORIZONTAL or Gravity.CENTER_VERTICAL, 0, 0)
        /*val group = toast.view as ViewGroup
        val textView = group.getChildAt(0) as TextView
        textView.textSize = 30.0f*/
        toast.show()
        toastHandle = toast
    }

    private fun initView() {

        //show menu


        if (account.isEmpty() && password.isEmpty() && username.isEmpty()) {

            //set title
            title = getString(R.string.nav_login)

            //show login
            var fragment: Fragment? = null
            val fragmentClass: Class<*>
            fragmentClass = LoginFragment::class.java

            try {
                fragment = fragmentClass.newInstance()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val fragmentManager = supportFragmentManager
            //fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment!!).commitAllowingStateLoss()


            navView!!.menu.getItem(0).isChecked = false //home
            navView!!.menu.getItem(1).isChecked = false //receipt
            navView!!.menu.getItem(2).isChecked = false //storage
            navView!!.menu.getItem(3).isChecked = false //material
            navView!!.menu.getItem(4).isChecked = false //outsourced
            navView!!.menu.getItem(5).isChecked = false //property
            navView!!.menu.getItem(6).isChecked = true //login
            navView!!.menu.getItem(7).isChecked = false //printer
            navView!!.menu.getItem(8).isChecked = false //setting
            navView!!.menu.getItem(9).isChecked = false //guest
            navView!!.menu.getItem(10).isChecked = false //about
            navView!!.menu.getItem(11).isChecked = false //logout


        } else {
            //show home
            //set username
            if (textViewUserName != null) {
                textViewUserName!!.text = getString(R.string.nav_greeting, username)
            } else {
                Log.e(mTAG, "textViewUserName == null")
            }

            //set title
            title = getString(R.string.nav_home)

            //show menu


            navView!!.menu.getItem(0).isVisible = true //home
            navView!!.menu.getItem(1).isVisible = true //receipt
            navView!!.menu.getItem(2).isVisible = true //storage
            navView!!.menu.getItem(3).isVisible = true //material
            navView!!.menu.getItem(4).isVisible = true //outsourced
            navView!!.menu.getItem(5).isVisible = true //property
            navView!!.menu.getItem(6).isVisible = false //login
            navView!!.menu.getItem(7).isVisible = true //printer
            navView!!.menu.getItem(8).isVisible = true //setting
            navView!!.menu.getItem(9).isVisible = true //guest
            navView!!.menu.getItem(10).isVisible = true //about
            navView!!.menu.getItem(11).isVisible = true //logout



            var fragment: Fragment? = null
            val fragmentClass: Class<*>
            fragmentClass = HomeGridFragment::class.java

            try {
                fragment = fragmentClass.newInstance()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val fragmentManager = supportFragmentManager
            //fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment!!).commitAllowingStateLoss()

            navView!!.menu.getItem(0).isChecked = true //home
            navView!!.menu.getItem(1).isChecked = false //receipt
            navView!!.menu.getItem(2).isChecked = false //storage
            navView!!.menu.getItem(3).isChecked = false //material
            navView!!.menu.getItem(4).isChecked = false //outsourced
            navView!!.menu.getItem(5).isChecked = false //property
            navView!!.menu.getItem(6).isChecked = false //login
            navView!!.menu.getItem(7).isChecked = false //printer
            navView!!.menu.getItem(8).isChecked = false //setting
            navView!!.menu.getItem(9).isChecked = false //guest
            navView!!.menu.getItem(10).isChecked = false //about
            navView!!.menu.getItem(11).isChecked = false //logout


        }

        //init bluetooth
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (mBluetoothAdapter == null) {

            toastLong("Bluetooth is not available")
        } else {
            Log.d(mTAG, "Bluetooth is available")

            //check if bluetooth is enabled
            if (!mBluetoothAdapter!!.isEnabled) {
                val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableIntent, requestEnableBt)
                // Otherwise, setup the chat session
            } else {
                Log.d(mTAG, "===>mBluetoothAdapter is enabled")
                setupChat()
                if (mChatService != null) {
                    // Only if the state is STATE_NONE, do we know that we haven't started already
                    if (mChatService!!.getState() == BluetoothChatService.STATE_NONE) {
                        Log.d(mTAG, "--->mChatService start")
                        // Start the Bluetooth chat services
                        mChatService!!.start()
                    }

                    //connect printer
                    if (printerAddress == "") {
                        toast(getString(R.string.set_printer_first))
                    } else {
                        val device = mBluetoothAdapter!!.getRemoteDevice(printerAddress)
                        mChatService!!.connect(device, true)
                        //set printer
                        bluetoothPrintFunc = BluetoothPrinterFuncs(mChatService as BluetoothChatService)
                        if (bluetoothPrintFunc != null) {
                            Log.e(mTAG, "Bluetooth Printer ready.")
                        } else {
                            Log.e(mTAG, "bluetoothPrintFunc == null")
                        }
                    }

                } else {
                    Log.e(mTAG, "mChatService = null")
                }
            }

        }
    }

    private fun initLog() {
        Log.e(mTAG, "=== start log ===")
        val sdf = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
        val currentDateAndTime = sdf.format(Date())
        val logFilename = "logcat_$currentDateAndTime.txt"
        //val outputFile = File(getExternalCacheDir(), logFilename)
        val outputFile = File(externalCacheDir, logFilename)
        try {
            //process = Runtime.getRuntime().exec("logcat -d -f " + outputFile.getAbsolutePath());
            process = Runtime.getRuntime().exec("logcat -c")
            process = Runtime.getRuntime().exec("logcat -f $outputFile")

        } catch (e: IOException) {
            e.printStackTrace()
        }




    }

    //private class BluetoothHandler(activity: MainActivity) : Handler() {
    private class BluetoothHandler(activity: MainActivity) : Handler(Looper.getMainLooper()) {
        private val mActivity: WeakReference<MainActivity> = WeakReference(activity)
        private val mTAG = MainActivity::class.java.name

        override fun handleMessage(msg: Message) {

            if (mActivity.get() == null) {
                return
            }
            val activity = mActivity.get()

            when (msg.what) {
                MESSAGE_STATE_CHANGE -> when (msg.arg1) {
                    BluetoothChatService.STATE_CONNECTED -> {
                        //toast("Connected to $mConnectedDeviceName")
                        Log.e(mTAG, "Connected to $mConnectedDeviceName")
                        if (activity!!.currentFrag == CurrentFragment.RECEIPT_FRAGMENT) {
                            activity.title = activity.getString(R.string.nav_receipt) +" - "+ activity.getString(R.string.tag_printer_status_connected)
                        } else if (activity.currentFrag == CurrentFragment.STORAGE_FRAGMENT) {
                            activity.title = activity.getString(R.string.nav_storage) +" - "+ activity.getString(R.string.tag_printer_status_connected)
                        }
                        activity.printStatus = PrintStatus.PRINT_SUCCESS
                        printerStatus = BluetoothChatService.STATE_CONNECTED

                        //if there is print waiting
                        /*if (printStatus == PrintStatus.PRINT_WAITING) {
                            val waitIntent = Intent()
                            waitIntent.action = Constants.ACTION.ACTION_RECEIPT_PRINT_STATUS_WAITING_PRINT
                            sendBroadcast(waitIntent)
                        }*/

                        val statusIntent = Intent()
                        statusIntent.action = Constants.ACTION.ACTION_SETTING_BLUETOOTH_STATE_CHANGE
                        statusIntent.putExtra("STATUS", printerStatus)
                        statusIntent.putExtra("DEVICENAME", mConnectedDeviceName)
                        activity.sendBroadcast(statusIntent)

                        //activity.fabPrint!!.visibility = View.GONE
                        activity.fabPrint!!.hide()
                    }
                    BluetoothChatService.STATE_CONNECTING -> {
                        //toast(getString(R.string.title_connecting))
                        Log.e(mTAG, "connecting...")
                        if (activity!!.currentFrag == CurrentFragment.RECEIPT_FRAGMENT) {
                            activity.title = activity.getString(R.string.nav_receipt) +" - "+ activity.getString(R.string.tag_printer_status_connecting)
                        } else if (activity.currentFrag == CurrentFragment.STORAGE_FRAGMENT) {
                            activity.title = activity.getString(R.string.nav_storage) +" - "+ activity.getString(R.string.tag_printer_status_connecting)
                        }
                        printerStatus = BluetoothChatService.STATE_CONNECTING
                        val statusIntent = Intent()
                        statusIntent.action = Constants.ACTION.ACTION_SETTING_BLUETOOTH_STATE_CHANGE
                        statusIntent.putExtra("STATUS", printerStatus)
                        statusIntent.putExtra("DEVICENAME", "")
                        activity.sendBroadcast(statusIntent)

                        //activity.fabPrint!!.visibility = View.GONE
                        activity.fabPrint!!.hide()
                    }
                    BluetoothChatService.STATE_LISTEN, BluetoothChatService.STATE_NONE -> {
                        Log.e(mTAG, "not connected")
                        if (activity!!.currentFrag == CurrentFragment.RECEIPT_FRAGMENT) {
                            activity.title = activity.getString(R.string.nav_receipt) +" - "+ activity.getString(R.string.tag_printer_status_not_connected)
                        } else if (activity.currentFrag == CurrentFragment.STORAGE_FRAGMENT) {
                            activity.title = activity.getString(R.string.nav_storage) +" - "+ activity.getString(R.string.tag_printer_status_not_connected)
                        }
                        printerStatus = BluetoothChatService.STATE_NONE
                        val statusIntent = Intent()
                        statusIntent.action = Constants.ACTION.ACTION_SETTING_BLUETOOTH_STATE_CHANGE
                        statusIntent.putExtra("STATUS", printerStatus)
                        statusIntent.putExtra("DEVICENAME", "")
                        activity.sendBroadcast(statusIntent)

                        when (activity.currentFrag) {
                            CurrentFragment.RECEIPT_FRAGMENT -> {
                                //activity.fabPrint!!.visibility = View.VISIBLE
                                activity.fabPrint!!.show()
                            }
                            CurrentFragment.STORAGE_FRAGMENT -> {
                                //activity.fabPrint!!.visibility = View.GONE
                                activity.fabPrint!!.hide()
                            }
                            CurrentFragment.MATERIAL_ISSUING_FRAGMENT -> {
                                //activity.fabPrint!!.visibility = View.GONE
                                activity.fabPrint!!.hide()
                            }
                            CurrentFragment.HOME_FRAGMENT -> {

                            }
                            else -> {
                                Log.e(mTAG, "Unknown fragment")
                            }
                        }


                    }

                }

                MESSAGE_READ -> {
                }

                MESSAGE_WRITE -> {
                }


                MESSAGE_DEVICE_NAME -> {
                    // save the connected device's name
                    mConnectedDeviceName = msg.data.getString("device_name") as String
                    if (null != activity) {
                        Toast.makeText(activity, "Connected to $mConnectedDeviceName", Toast.LENGTH_SHORT).show()
                        val statusIntent = Intent()
                        statusIntent.action = Constants.ACTION.ACTION_SETTING_BLUETOOTH_STATE_CHANGE
                        statusIntent.putExtra("STATUS", BluetoothChatService.STATE_CONNECTED)
                        statusIntent.putExtra("DEVICENAME", mConnectedDeviceName)
                        activity.sendBroadcast(statusIntent)
                    }
                }
                MESSAGE_TOAST -> if (null != activity) {
                    Toast.makeText(
                        activity, msg.data.getString("toast"),
                        Toast.LENGTH_SHORT
                    ).show()


                }
            }

        }
    }

    private fun setupChat() {
        Log.e(mTAG, "setupChat()")
        //mChatService = BluetoothChatService(mContext as Context, mHandler)

        val blHandler = BluetoothHandler(this)

        mChatService = BluetoothChatService(mContext as Context, blHandler)
    }

    @SuppressLint("CommitPrefEdits")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.e(mTAG, "onActivityResult = request code = $requestCode, resultCode = $resultCode")

        when (requestCode) {
            requestConnectDeviceSecure -> {
                Log.e(mTAG, "requestConnectDeviceSecure")
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    //connectDevice(data, true)
                    Log.d(mTAG, "requestConnectDeviceSecure RESULT_OK")
                }
            }

            setPrinterDev -> {
                Log.e(mTAG, "setPrinterDev")
                if (resultCode == Activity.RESULT_OK) {
                    Log.e(mTAG, "RESULT_OK")
                    //if (data?.getExtras() != null) {
                    if (data?.extras != null) {
                        Log.e(mTAG, "data.getExtras() = " + data.extras.toString())
                        printerAddress = data.getStringExtra("PrinterAddress") as String
                        //PrinterAddres = data.getExtras()
                        //        .getString("PrinterAddress");
                        Log.e(mTAG, "PrinterAddress = $printerAddress")
                        //SaveBluetoothDev(1)

                        //save printer address
                        editor = pref!!.edit()
                        editor!!.putString("PRINTER_ADDRESS", printerAddress)
                        editor!!.apply()

                        //connect
                        if (mChatService != null) {
                            mChatService!!.stop()
                            Thread.sleep(500)

                            //connect printer
                            if (printerAddress == "") {
                                toast(getString(R.string.set_printer_first))
                            } else {
                                if (mBluetoothAdapter != null) {
                                    val device = mBluetoothAdapter!!.getRemoteDevice(printerAddress)
                                    mChatService!!.connect(device, true)
                                    //set printer
                                    bluetoothPrintFunc = BluetoothPrinterFuncs(mChatService as BluetoothChatService)
                                    if (bluetoothPrintFunc != null) {
                                        Log.e(mTAG, "Bluetooth Printer ready.")
                                    } else {
                                        Log.e(mTAG, "bluetoothPrintFunc == null")
                                    }
                                } else {
                                    Log.e(mTAG, "mBluetoothAdapter = null")
                                }
                            }
                        } else{
                            Log.e(mTAG, "mChatService = null")
                            if (!mBluetoothAdapter!!.isEnabled) {
                                val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                                startActivityForResult(enableIntent, requestEnableBt)
                                // Otherwise, setup the chat session
                            } else {
                                Log.d(mTAG, "===>mBluetoothAdapter is enabled")
                                setupChat()
                                if (mChatService != null) {
                                    // Only if the state is STATE_NONE, do we know that we haven't started already
                                    if (mChatService!!.getState() == BluetoothChatService.STATE_NONE) {
                                        Log.d(mTAG, "--->mChatService start")
                                        // Start the Bluetooth chat services
                                        mChatService!!.start()
                                    }



                                    //connect printer
                                    if (printerAddress == "") {
                                        toast(getString(R.string.set_printer_first))
                                    } else {
                                        val device = mBluetoothAdapter!!.getRemoteDevice(printerAddress)
                                        mChatService!!.connect(device, true)
                                        //set printer
                                        bluetoothPrintFunc = BluetoothPrinterFuncs(mChatService as BluetoothChatService)
                                        if (bluetoothPrintFunc != null) {
                                            Log.e(mTAG, "Bluetooth Printer ready.")
                                        } else {
                                            Log.e(mTAG, "bluetoothPrintFunc == null")
                                        }
                                    }

                                } else {
                                    Log.e(mTAG, "mChatService = null")
                                }
                            }
                        }
                    }

                }
            }
            requestEnableBt ->
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    setupChat()

                    if (mChatService != null) {
                        // Only if the state is STATE_NONE, do we know that we haven't started already
                        if (mChatService!!.getState() == BluetoothChatService.STATE_NONE) {
                            // Start the Bluetooth chat services
                            Log.d(mTAG, "--->mChatService start")
                            mChatService!!.start()
                        }



                        //connect printer
                        if (printerAddress == "") {
                            toast(getString(R.string.set_printer_first))
                        } else {
                            val device = mBluetoothAdapter!!.getRemoteDevice(printerAddress)
                            mChatService!!.connect(device, true)
                            //set printer
                            bluetoothPrintFunc = BluetoothPrinterFuncs(mChatService as BluetoothChatService)
                            if (bluetoothPrintFunc != null) {
                                Log.e(mTAG, "Bluetooth Printer ready.")
                            } else {
                                Log.e(mTAG, "bluetoothPrintFunc == null")
                            }
                        }

                    } else {
                        Log.e(mTAG, "mChatService = null")
                    }


                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(mTAG, "BT not enabled")
                    toast(getString(R.string.bt_not_enabled_leaving))
                }
        }
    }

    /*fun checkUploadSuccessList(barcode: ScanBarcode?): Boolean{
        Log.e(mTAG, "=== checkUploadSuccessList start ===")

        var ret = false

        val poLineInt = ScanBarcode.removeLeadingZeroes(barcode!!.poLine)

        if (barcode != null) {
            Log.e(mTAG, "checkUploadSuccessList poBarcode = " + barcode.poBarcode + ", poLineInt = " + poLineInt)

            /*
            para.pmn01 = barcode.poBarcode
            para.pmn02 = barcode.poLine
             */

            if (receiptUploadedList.size > 0) {

                for (i in 0 until receiptUploadedList.size) {

                    if (receiptUploadedList[i].poNumSplit == barcode.poBarcode && receiptUploadedList[i].poLineInt == poLineInt) {
                        Log.e(mTAG, "Found uploaded po")
                        itemReceipt = receiptUploadedList[i]
                        ret = true
                        break
                    }

                }
            } else {
                Log.e(mTAG, "receiptUploadedList empty")
            }
        } else {
            Log.e(mTAG, "barcode = null")
        }

        Log.e(mTAG, "=== checkUploadSuccessList end ===")

        return ret
    }*/

    /*
    fun setReceiptACState(state: ReceiptACState) {
        this.acState = state
    }//setReceiptACState
    */


    fun callAPILogin(account: String, password: String) {
        Log.e(mTAG, "callAPILogin")
        val para = HttpUserAuthPara()
        para.tc_zx101 = account
        para.tc_zx102 = password
        para.tc_zy102 = "SCM01"
        ApiFunc().login(para, loginCallback)
    }//login

    private var loginCallback: Callback = object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.e(mTAG, "err msg = $e")
            runOnUiThread(netErrRunnable)
        }

        @Throws(IOException::class)
        override fun onResponse(call: Call, response: Response) {
            val res = ReceiveTransform.restoreToJsonStr(response.body!!.string())

            runOnUiThread {
                try {
                    //val  rjUser: RJUser? = null
                    //val  rjUser: RJUser = Gson().fromJson<Any>(res, RJUser::class.javaObjectType) as RJUser
                    Log.e(mTAG, "res = $res")
                    val rjUser = Gson().fromJson<Any>(res, RJUser::class.java) as RJUser

                    //if (rjUser.result.equals("0")) {
                    if (rjUser.result == "0") {
                        //fail
                        //mLoadingView.setStatus(LoadingView.GONE)
                        // Toast.makeText(mContext,rjUser.tc_zx104,Toast.LENGTH_LONG).show();
                        //showMyToast(rjUser.tc_zx104, mContext)
                        toast(rjUser.tc_zx104)

                        val failIntent = Intent()
                        failIntent.action = Constants.ACTION.ACTION_LOGIN_FAILED
                        sendBroadcast(failIntent)
                    } else {
                        //success
                        Log.e(mTAG, "loginCallback success")

                        val successIntent = Intent()
                        successIntent.action = Constants.ACTION.ACTION_LOGIN_SUCCESS
                        sendBroadcast(successIntent)


                        username = rjUser.tc_zx104


                        Log.e(
                            mTAG,
                            "username = " + rjUser.tc_zx104 + " account = " + account + " password = " + password
                        )
                    }

                }// try
                catch (e: IOException) {
                    //mLoadingView.setStatus(LoadingView.GONE)
                    //Toast.makeText(mContext,getString(R.string.toast_server_error),Toast.LENGTH_LONG).show();
                    //showMyToast(getString(R.string.toast_server_error), mContext)
                    e.printStackTrace()

                    val failIntent = Intent()
                    failIntent.action = Constants.ACTION.ACTION_LOGIN_FAILED
                    sendBroadcast(failIntent)

                    runOnUiThread {

                        toast(getString(R.string.toast_server_error))
                    }
                }
            }
        }//response
    }

    /*fun checkIfReceiptUploaded(barcode: ScanBarcode?) {
        if (barcode != null) {
            Log.e(mTAG, "checkIfReceiptUploaded poBarcode = "+barcode.poBarcode+ ", poLine = "+barcode.poLine)
            // to call api
            //acState = ReceiptACState.RECEIPT_GETTING_STATE
            val para = HttpReceiptGetPara()
            para.pmn01 = barcode.poBarcode
            para.pmn02 = barcode.poLine

            ApiFunc().getHistory(para, checkIfReceiptUploadedCallback)

        }
    }

    private var checkIfReceiptUploadedCallback: Callback = object : Callback {

        override fun onFailure(call: Call, e: IOException) {
            Log.e(mTAG, "checkIfReceiptUploadedCallback err msg = $e")
            runOnUiThread(netErrRunnable)
        }

        @Throws(IOException::class)
        override fun onResponse(call: Call, response: Response) {
            Log.e(mTAG, "===>checkIfReceiptUploadedCallback onResponse start: ")
            Log.e(mTAG, "response.body().toString() = "+response.body.toString())
            val res = ReceiveTransform.restoreToJsonStr(response.body!!.string())
            //1.get response ,2 error or right , 3 update ui ,4. restore acState 5. update fragment detail

            runOnUiThread {
                try {
                    //val itemReceipt = ItemReceipt.trans_RJHistoryStr_To_ItemReceipt(res, barcode!!.poBarcode, barcode!!.poLine)
                    val itemHistory =
                        ItemHistory.transRJHistoryStrToItemHistory(res, barcode!!.poBarcode, barcode!!.poLine)

                    val checkUploadIntent = Intent()
                    if (itemHistory != null) {
                        Log.e(mTAG, "itemHistory.result = ${itemHistory.rjHistory!!.result}")
                        Log.e(mTAG, "pmn01: ${itemHistory.pmn01}")
                        Log.e(mTAG, "pmn02: ${itemHistory.pmn02}")
                        Log.e(mTAG, "rva06: " + itemHistory.rjHistory!!.rva06)
                        Log.e(mTAG, "rvb01: " + itemHistory.rjHistory!!.rvb01)
                        Log.e(mTAG, "rvb02: " + itemHistory.rjHistory!!.rvb02)

                        if (itemHistory.rjHistory!!.result != ItemHistory.RESULT_CORRECT) {
                            //can't receive the item
                            checkUploadIntent.action = Constants.ACTION.ACTION_RECEIPT_UPLOADED_NO

                        } else {

                            checkUploadIntent.action = Constants.ACTION.ACTION_RECEIPT_UPLOADED_YES

                            toast(getString(R.string.receipt_is_uploaded))

                        }

                    } else {
                        Log.e(mTAG, "itemHistory = null")
                        checkUploadIntent.action = Constants.ACTION.ACTION_RECEIPT_UPLOADED_NO
                    }
                    sendBroadcast(checkUploadIntent)


                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }

            }

            Log.e(mTAG, "===>checkIfReceiptUploadedCallback onResponse end: ")

        }//onResponse
    }*/


    fun getReceipt(barcode: ScanBarcode?) {
        // go for : 1.get barcode  2.call get Receipt Api ,3.update list ,4. restore input mode
        //1.

        /*runOnUiThread(Runnable {
            // hideKeyboard();
            mLoadingView.setStatus(LoadingView.LOADING)
        })*/

        if (barcode != null) {
            Log.e(mTAG, "getReceipt poBarcode = "+barcode.poBarcode+ ", poLine = "+barcode.poLine)
            // to call api
            //acState = ReceiptACState.RECEIPT_GETTING_STATE
            val para = HttpReceiptGetPara()
            para.pmn01 = barcode.poBarcode
            para.pmn02 = barcode.poLine

            ApiFunc().getReceipt(para, getReceiptCallback)

        }


    }//getReceipt


    private var getReceiptCallback: Callback = object : Callback {

        override fun onFailure(call: Call, e: IOException) {
            Log.e(mTAG, "getReceiptCallback err msg = $e")
            runOnUiThread(netErrRunnable)
        }

        @Throws(IOException::class)
        override fun onResponse(call: Call, response: Response) {
            Log.e(mTAG, "onResponse : "+response.body.toString())
            val res = ReceiveTransform.restoreToJsonStr(response.body!!.string())
            //1.get response ,2 error or right , 3 update ui ,4. restore acState 5. update fragment detail
            runOnUiThread {
                try {

                    val retItemReceipt = ItemReceipt.transRJReceiptStrToItemReceipt(res, barcode!!.poBarcode)


                    if (retItemReceipt != null) {
                        if (!retItemReceipt.rjReceipt?.result.equals(ItemReceipt.RESULT_CORRECT)) {
                            Log.e(
                                mTAG,
                                "result = " + retItemReceipt.rjReceipt?.result + " result2 = " + retItemReceipt.rjReceipt?.result2
                            )
                            //can't receive the item
                            //val mess = retItemReceipt.poNumScanTotal + " " + retItemReceipt.rjReceipt?.result2
                            val mess = retItemReceipt.rjReceipt?.result2 as String
                            toastLong(mess)


                            val receiptNoIntent = Intent()
                            receiptNoIntent.action = Constants.ACTION.ACTION_RECEIPT_NO_NOT_EXIST
                            sendBroadcast(receiptNoIntent)
                        }// result  = 0
                        else {
                            // success receive ,update list ,update fragment
                            //if(Fristpmc3.equals("") || Fristpmm02.equals("") || Fristpmc3.equals(itemReceipt.rjReceipt.pmc03) || Fristpmm02.equals(itemReceipt.rjReceipt.pmm02)) {

                            //multi
                            /*if (ReceiptList.size() > 0 ) {
                                ReceiptList.removeAllItem()
                            }

                            addResult = ReceiptList.add(retItemReceipt)
                            itemReceipt = ReceiptList.getReceiptItem(0)*/

                            //single
                            itemReceipt = retItemReceipt

                            Log.e(mTAG, "2")
                            val refreshIntent = Intent()
                            refreshIntent.action = Constants.ACTION.ACTION_RECEIPT_FRAGMENT_REFRESH
                            //refreshIntent.putExtra("RVA06", rva06)
                            mContext!!.sendBroadcast(refreshIntent)

                        }//result = 1
                    } else {
                        Log.e(mTAG, "retItemReceipt = null")

                        toast(getString(R.string.receipt_this_receipt_not_exist))

                        val receiptNoIntent = Intent()
                        receiptNoIntent.action = Constants.ACTION.ACTION_RECEIPT_NO_NOT_EXIST
                        sendBroadcast(receiptNoIntent)
                    }


                } catch (ex: Exception) {

                    Log.e(mTAG, "Server error")

                    val serverErrorIntent = Intent()
                    serverErrorIntent.action = Constants.ACTION.ACTION_SERVER_ERROR
                    sendBroadcast(serverErrorIntent)
                    //system error
                    runOnUiThread {

                        toast(getString(R.string.toast_server_error))
                    }
                }
                isBarcodeScanning = false
            }


        }//onResponse
    }

    //upload
    fun uploadReceipt() {
        Log.d(mTAG, "=== uploadReceipt start ===")

        //single
        if (itemReceipt != null) {
            val para = HttpParaUploadReceipt.itemReceiptToHttpParaUploadReceipt(
                itemReceipt as ItemReceipt, user as User
            )


            ApiFunc().uploadReceiptSingle(para, upLoadReceiptCallback)
        } else {
            Log.e(mTAG, "itemReceipt = null")
        }





        //multi
        /*val paraUploadReceiptList = ArrayList<HttpParaUploadReceipt>()
        Log.d(mTAG, "ReceiptList.getItemReceiptCount = " + ReceiptList.getItemReceiptCount())

        for (i in 0 until ReceiptList.getItemReceiptCount()) {
            val itemReceipt = ReceiptList.getReceiptItem(i)

            if (itemReceipt != null) {
                if (itemReceipt.receiveLine.length == 0) {
                    val para = HttpParaUploadReceipt.itemReceiptToHttpParaUploadReceipt(
                        itemReceipt, user as User
                    )
                    paraUploadReceiptList.add(para)
                } else {
                    //showMyToast(resources.getString(R.string.receipt_uploaded), mContext)
                    //mLoadingView.setStatus(LoadingView.GONE)
                    return
                }
            }


        }//for

        ApiFunc().uploadReceiptList(paraUploadReceiptList, upLoadReceiptCallback)
        */

        Log.d(mTAG, "=== uploadReceipt end ===")
    }

    private var upLoadReceiptCallback: Callback = object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            itemReceipt!!.state = ItemReceipt.ItemState.UPLOAD_FAILED

            Log.e(mTAG, "upLoadReceiptCallback err msg = $e")
            //itemReceiptFromScanDifferentHeader = null;
            //runOnUiThread(netErrRunnable)
            runOnUiThread {
                val failIntent = Intent()
                failIntent.action = Constants.ACTION.ACTION_CONNECTION_TIMEOUT
                sendBroadcast(failIntent)
            }
        }

        @Throws(IOException::class)
        override fun onResponse(call: Call, response: Response) {
            Log.e(mTAG, "upLoadReceiptCallback onResponse : "+response.body.toString())
            val res = ReceiveTransform.restoreToJsonStr(response.body!!.string())
            //1.get response ,2 error or right , 3 update ui ,4. restore acState 5. update fragment detail
            runOnUiThread {
                Log.e(mTAG, "===>upLoadReceiptCallback : onResponse start")
                try {   // trans to list data
                    //val jsonStr = ReceiveTransform.addToJsonArrayStr(response.body()!!.string())
                    Log.e(mTAG, "res = $res")

                    //==== receive single record start ====
                    val rjReceiptUpload: RJReceiptUpload = Gson().fromJson(res, RJReceiptUpload::class.java) as RJReceiptUpload


                    val poLineInt = Integer.valueOf(rjReceiptUpload.pmn02)
                    val po = rjReceiptUpload.pmn01
                    val uploadResult = rjReceiptUpload.result
                    val receiveNum = rjReceiptUpload.rva01// error mess when fail
                    val receiveLine = rjReceiptUpload.rvb02
                    //start find the raw data in list
                    Log.e(mTAG, "po = $po")
                    Log.e(mTAG, "uploadResult = $uploadResult")
                    Log.e(mTAG, "receiveNum = $receiveNum")
                    Log.e(mTAG, "receiveLine = $receiveLine")
                    if (itemReceipt != null) {

                        if (uploadResult == "1") { //success

                            if (itemReceipt!!.poNumSplit == po && poLineInt == itemReceipt!!.poLineInt) {
                                itemReceipt!!.state = ItemReceipt.ItemState.UPLOADED
                                itemReceipt!!.receiveNum = receiveNum
                                itemReceipt!!.receiveLine = receiveLine
                                receiveNumUploadSuccess = receiveNum
                                //ReceiptList.replaceItem(item, j)

                                val successIntent = Intent()
                                successIntent.action = Constants.ACTION.ACTION_RECEIPT_UPLOAD_SUCCESS
                                sendBroadcast(successIntent)

                            } else { //po is different
                                val poDiffIntent = Intent()
                                poDiffIntent.action = Constants.ACTION.ACTION_RECEIPT_UPLOAD_RETURN_PO_DIFF
                                sendBroadcast(poDiffIntent)

                                itemReceipt!!.state = ItemReceipt.ItemState.UPLOADED
                                Log.e(mTAG, getString(R.string.upload_receipt_failed_po_not_match))
                                toast(getString(R.string.upload_receipt_failed_po_not_match))
                            }


                        } else { // upload failed, uploadResult == 0
                            itemReceipt!!.state = ItemReceipt.ItemState.UPLOAD_FAILED

                            val errorString: String =
                                getString(R.string.receipt_upload_error) + " $receiveNum " + rjReceiptUpload.rva01
                            toast(errorString)

                            val failedIntent = Intent()
                            failedIntent.action = Constants.ACTION.ACTION_RECEIPT_UPLOAD_FAILED
                            failedIntent.putExtra("REASON", rjReceiptUpload.rva01)
                            sendBroadcast(failedIntent)
                        }// upload fail


                    } else {
                        Log.e(mTAG, "itemReceipt = null")
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.e(mTAG, "===>Exception ")
                    // itemReceiptFromScanDifferentHeader = null;
                    //String temp = ex.toString();

                    itemReceipt!!.state = ItemReceipt.ItemState.UPLOAD_FAILED

                    val exceptionIntent = Intent()
                    exceptionIntent.action = Constants.ACTION.ACTION_SERVER_ERROR
                    sendBroadcast(exceptionIntent)

                    runOnUiThread {
                        //mLoadingView.setStatus(LoadingView.GONE)
                        //setUIToEditMode()
                        toast(getString(R.string.toast_server_error))
                    }
                }

                Log.e(mTAG, "===>upLoadReceiptCallback : onResponse end")
            }


        }//onResponse
    }

    fun confirmUploadReceipt() {
        //single
        Log.d(mTAG, "=== confirmUploadReceipt start ===")
        /*val para = HttpParaConfirmReceiptUpload()
        para.rva01 = "AP32-19101114"

        ApiFunc().confirmUploadReceiptSend(para, confirmUploadReceiptCallback)*/

        if (itemReceipt != null) {
            val para = HttpParaConfirmReceiptUpload()

            when {
                itemReceipt!!.receiveNum.isNotEmpty() -> {
                    para.rva01 = itemReceipt!!.receiveNum
                    ApiFunc().confirmUploadReceiptSend(para, confirmUploadReceiptCallback)
                }
                receiveNumUploadSuccess.isNotEmpty() -> {
                    para.rva01 = receiveNumUploadSuccess
                    ApiFunc().confirmUploadReceiptSend(para, confirmUploadReceiptCallback)
                }
                else -> {
                    toastLong(getString(R.string.receipt_uploaded_confirm_rva01_lost))
                }
            }



        } else {
            Log.e(mTAG, "itemReceipt = null")
        }
        Log.d(mTAG, "=== confirmUploadReceipt end ===")
    }

    private var confirmUploadReceiptCallback: Callback = object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            itemReceipt!!.state = ItemReceipt.ItemState.CONFIRM_FAILED
            Log.e(mTAG, "confirmUploadReceiptCallback err msg = $e")
            //itemReceiptFromScanDifferentHeader = null;
            //runOnUiThread(netErrRunnable)
            runOnUiThread {
                val failIntent = Intent()
                failIntent.action = Constants.ACTION.ACTION_CONNECTION_TIMEOUT
                sendBroadcast(failIntent)
            }
        }

        @Throws(IOException::class)
        override fun onResponse(call: Call, response: Response) {

            Log.e(mTAG, "confirmUploadReceiptCallback onResponse : "+response.body.toString())
            val resXmlString = ReceiveTransform.restoreToJsonStr2(response.body!!.string()) //transfer response.body must outside of the runOnUiThread

            val currentDate: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val currentTime: String = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

            //1.get response ,2 error or right , 3 update ui ,4. restore acState 5. update fragment detail
            runOnUiThread {
                Log.e(mTAG, "===>confirmUploadReceiptCallback : onResponse start")
                try {
                    //Log.e(mTAG, "resXmlString = $resXmlString")
                    //var res = ReceiveTransform.restoreToJsonStr2(resString)
                    var res = resXmlString.replace("&lt;", "<")
                    res = res.replace("&gt;", ">")
                    Log.e(mTAG, "res = $res")



                    if (res.contentEquals("Proxy encountered error during request processing")) {
                        toast(getString(R.string.receipt_upload_confirm_retry))

                        if (itemReceipt != null)
                            itemReceipt!!.state = ItemReceipt.ItemState.CONFIRM_FAILED

                        //add this to failLogList
                        val receiptConfirmFailLog = ReceiptConfirmFailLog("Proxy Error", itemReceipt!!.receiveNum, currentDate, currentTime, "Proxy encountered error during request processing")
                        confirmFailLogList.add(receiptConfirmFailLog)

                        val failedIntent = Intent()
                        failedIntent.action = Constants.ACTION.ACTION_RECEIPT_UPLOADED_CONFIRM_FAILED
                        sendBroadcast(failedIntent)
                    } else {

                        val stream = ByteArrayInputStream(res.toByteArray())
                        val rjReceiptUploadConfirm = loadAndParseXML(stream)

                        if (rjReceiptUploadConfirm.code == "0") {
                            var successString: String = getString(R.string.receipt_confirm_success)

                            if (rjReceiptUploadConfirm.description.isNotEmpty()) {
                                successString += ": " + rjReceiptUploadConfirm.description
                            }
                            toast(successString)

                            if (itemReceipt != null)
                                itemReceipt!!.state = ItemReceipt.ItemState.CONFIRMED

                            val successIntent = Intent()
                            successIntent.action = Constants.ACTION.ACTION_RECEIPT_UPLOADED_CONFIRM_SUCCESS
                            sendBroadcast(successIntent)

                        } else {


                            //add this to failLogList
                            val receiptConfirmFailLog = ReceiptConfirmFailLog(rjReceiptUploadConfirm.code, itemReceipt!!.receiveNum, currentDate, currentTime, rjReceiptUploadConfirm.description)
                            confirmFailLogList.add(receiptConfirmFailLog)

                            var errorString: String = getString(R.string.receipt_confirm_fail) + rjReceiptUploadConfirm.code + " " + rjReceiptUploadConfirm.description

                            if (itemReceipt != null) {
                                errorString += " " + getString(R.string.receipt_confirm_receipt_no) + " " + itemReceipt!!.receiveNum
                            }



                            toast(errorString)
                            if (itemReceipt != null)
                                itemReceipt!!.state = ItemReceipt.ItemState.CONFIRM_FAILED

                            val failedIntent = Intent()
                            failedIntent.action = Constants.ACTION.ACTION_RECEIPT_UPLOADED_CONFIRM_FAILED
                            sendBroadcast(failedIntent)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()

                    toast(getString(R.string.receipt_upload_confirm_retry))

                    if (itemReceipt != null)
                        itemReceipt!!.state = ItemReceipt.ItemState.CONFIRM_FAILED

                    //add this to failLogList
                    val receiptConfirmFailLog = ReceiptConfirmFailLog("Exception", itemReceipt!!.receiveNum, currentDate, currentTime, e.toString().substring(0,64))
                    confirmFailLogList.add(receiptConfirmFailLog)

                    val failedIntent = Intent()
                    failedIntent.action = Constants.ACTION.ACTION_RECEIPT_UPLOADED_CONFIRM_FAILED
                    sendBroadcast(failedIntent)
                }


                Log.e(mTAG, "===>confirmUploadReceiptCallback : onResponse end")
            }
        }//onResponse
    }

    //storage
    fun getStorage(barcode: ScanBarcode?) {



        if (barcode != null) {
            Log.e(mTAG, "getStorage poBarcode = "+barcode.poBarcode+ ", poLine = "+barcode.poLine)


            //acState = ReceiptACState.RECEIPT_GETTING_STATE
            val para = HttpStorageGetPara()
            para.rvb01 = barcode.poBarcode
            para.rvb02 = barcode.poLine

            ApiFunc().getStorage(para, getStorageCallback)
        }


    }

    private var getStorageCallback: Callback = object : Callback {

        override fun onFailure(call: Call, e: IOException) {
            Log.e(mTAG, "getStorageCallback err msg = $e")
            runOnUiThread(netErrRunnable)
        }

        @Throws(IOException::class)
        override fun onResponse(call: Call, response: Response) {

            Log.e(mTAG, "response.body().toString() = "+response.body.toString())
            val res = ReceiveTransform.restoreToJsonStr(response.body!!.string())
            //1.get response ,2 error or right , 3 update ui ,4. restore acState 5. update fragment detail
            runOnUiThread {
                Log.e(mTAG, "===>checkRVB02Callback onResponse start: ")

                Log.e(mTAG, "res = $res")

                try {
                    //multi
                    //val jsonStr = ReceiveTransform.addToJsonArrayStr(response.body()!!.string())

                    /*if (rjReceiptStorageList != null) {
                        rjReceiptStorageList!!.dataList.clear()
                        rjReceiptStorageList = null
                    }

                    rjReceiptStorageList = Gson().fromJson(jsonStr, ReceiptStorageList.RJReceiptStorageList::class.java) as ReceiptStorageList.RJReceiptStorageList

                    Log.e(mTAG, "=== receiptStorageList start ===")

                    if (rjReceiptStorageList != null) {

                    } else {
                        Log.e(mTAG, "receiptStorageList = null")
                    }

                    var i=0
                    for (itemReceiptStorage: RJStorage in rjReceiptStorageList!!.dataList) {
                        Log.d(mTAG, "itemReceiptStorage[$i] = "+"rvb01 = "+itemReceiptStorage.rvb01+",rvb02 = "+itemReceiptStorage.rvb02)

                        if (rjReceiptStorage.p_cmd != "0") { //failed

                        }

                        i++
                    }
                    Log.e(mTAG, "=== receiptStorageList end ===")
                    */


                    //single
                    val retItemStorage = ItemStorage.transRJStorageStrToItemStorage(res)


                    if (retItemStorage != null) {
                        Log.e(
                            mTAG,
                            "p_cmd = " + retItemStorage.rjStorage!!.p_cmd + " p_cmd2 = " + retItemStorage.rjStorage!!.p_cmd2
                        )
                        //toast("p_cmd = "+retItemStorage.rjStorage!!.p_cmd+" p_cmd2 = "+retItemStorage.rjStorage!!.p_cmd2)

                        if (retItemStorage.rjStorage!!.p_cmd == "0") { //1 failed

                            //can't receive the item
                            //val mess = retItemReceipt.poNumScanTotal + " " + retItemReceipt.rjReceipt?.result2

                            //toastLong(retItemStorage.rjStorage!!.p_cmd2)

                            if (retItemStorage.rjStorage!!.p_cmd2.isNotEmpty()) {
                                //toastLong(getString(R.string.stock_in_stock_no_exist))
                                toastLong(retItemStorage.rjStorage!!.p_cmd2)

                                val uploadedIntent = Intent()
                                uploadedIntent.action = Constants.ACTION.ACTION_STORAGE_UPLOADED_CANNOT_LOAD
                                sendBroadcast(uploadedIntent)
                            } else { //not check yet

                                toastLong(retItemStorage.rjStorage!!.p_cmd2)

                                val receiptNoIntent = Intent()
                                receiptNoIntent.action = Constants.ACTION.ACTION_STORAGE_RECEIPT_NO_NOT_EXIST
                                sendBroadcast(receiptNoIntent)
                            }


                        } else { //p_cmd = 1,2

                            if (retItemStorage.rjStorage!!.p_cmd == "1" && retItemStorage.rjStorage!!.p_cmd2.isNotEmpty()) {
                                //

                                if (retItemStorage.rjStorage!!.p_cmd2 == "1") {

                                    //toastLong(getString(R.string.stock_not_in_stock_barcode))
                                    toastLong(retItemStorage.rjStorage!!.rvb01)

                                    val receiptNoIntent = Intent()
                                    receiptNoIntent.action =
                                        Constants.ACTION.ACTION_STORAGE_RECEIPT_NO_NOT_EXIST
                                    sendBroadcast(receiptNoIntent)
                                } /*else {

                                            toastLong(getString(R.string.stock_in_stock_no_exist))

                                            val uploadedIntent = Intent()
                                            uploadedIntent.action = Constants.ACTION.ACTION_STORAGE_UPLOADED_CANNOT_LOAD
                                            sendBroadcast(uploadedIntent)
                                        }*/
                            } else {

                                //itemReceiptStorage = retItemReceiptStorage

                                itemStorage = retItemStorage

                                val refreshIntent = Intent()
                                if (retItemStorage.rjStorage!!.p_cmd == "2") {
                                    refreshIntent.putExtra("STOCK", true)
                                } else {
                                    refreshIntent.putExtra("STOCK", false)
                                }
                                refreshIntent.action = Constants.ACTION.ACTION_STORAGE_FRAGMENT_REFRESH
                                mContext!!.sendBroadcast(refreshIntent)
                            }
                        }


                    } else {
                        Log.e(mTAG, "retItemReceiptStorage = null")
                    }


                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.e(mTAG, "===>Exception ")

                    runOnUiThread {

                        toast(getString(R.string.toast_server_error))
                    }

                }

                Log.e(mTAG, "===>checkIfReceiptUploadedCallback onResponse end")
                isBarcodeScanning = false
            }


        }//onResponse
    }

    fun uploadStorage() {
        Log.d(mTAG, "=== uploadStorage start ===")

        //single
        if (itemStorage != null) {
            val para = HttpParaUploadStorage.itemReceiptStorageToHttpParaUploadStorage(itemStorage as ItemStorage, user as User)

            ApiFunc().updateStorageSingle(para, uploadStorageCallback)
        } else {
            Log.e(mTAG, "itemStorage = null")
        }


        //multi
        /*val paraUploadStorageList = ArrayList<HttpParaUploadStorage>()
        Log.d(mTAG, "ReceiptList.getItemReceiptCount = " + rjStorageList!!.datalist.size)

        for (i in 0 until rjStorageList!!.datalist.size) {
            val itemReceiptStorage = rjStorageList!!.datalist[i]

            if (itemReceiptStorage != null) {
                val para = HttpParaUploadStorage.itemReceiptStorageToHttpParaUploadStorage(itemReceiptStorage, user as User)
                paraUploadStorageList.add(para)
            }


        }//for

        ApiFunc().UpdateFunList(paraUploadStorageList, uploadStorageCallback)*/

        Log.d(mTAG, "=== uploadStorage end ===")
    }

    private var uploadStorageCallback: Callback = object : Callback {
        override fun onFailure(call: Call, e: IOException) {


            Log.e(mTAG, "uploadStorageCallback err msg = $e")
            //itemReceiptFromScanDifferentHeader = null;
            //runOnUiThread(netErrRunnable)
            runOnUiThread {
                val failIntent = Intent()
                failIntent.action = Constants.ACTION.ACTION_CONNECTION_TIMEOUT
                sendBroadcast(failIntent)
            }
        }

        @Throws(IOException::class)
        override fun onResponse(call: Call, response: Response) {
            Log.e(mTAG, "upLoadStorageCallback onResponse : "+response.body.toString())
            val res = ReceiveTransform.restoreToJsonStr(response.body!!.string())
            //1.get response ,2 error or right , 3 update ui ,4. restore acState 5. update fragment detail
            runOnUiThread {
                Log.e(mTAG, "===>upLoadReceiptCallback : onResponse start")
                try {   // trans to list data
                    //val jsonStr = ReceiveTransform.addToJsonArrayStr(response.body()!!.string())
                    Log.e(mTAG, "res = $res")

                    //==== receive single record start ====
                    val rjStorageUpload: RJStorageUpload =
                        Gson().fromJson(res, RJStorageUpload::class.java) as RJStorageUpload

                    val uploadResult = rjStorageUpload.result

                    Log.e(mTAG, "uploadResult = $uploadResult")

                    if (uploadResult == "1") {
                        itemStorage!!.state = ItemStorage.ItemState.UPLOADED

                        val successIntent = Intent()
                        successIntent.action = Constants.ACTION.ACTION_STORAGE_UPLOAD_SUCCESS
                        successIntent.putExtra("RVU01", rjStorageUpload.rvu01)
                        sendBroadcast(successIntent)
                    } else {
                        itemStorage!!.state = ItemStorage.ItemState.UPLOAD_FAILED
                        val errorString: String = rjStorageUpload.rvu01
                        toast(errorString)

                        val failedIntent = Intent()
                        failedIntent.action = Constants.ACTION.ACTION_STORAGE_UPLOAD_FAILED
                        failedIntent.putExtra("REASON", rjStorageUpload.rvu01)
                        sendBroadcast(failedIntent)
                    }

                    /*
                    val poLineInt = Integer.valueOf(rjReceiptUpload.pmn02)
                    val po = rjReceiptUpload.pmn01
                    val uploadResult = rjReceiptUpload.result
                    val receiveNum = rjReceiptUpload.rva01// error mess when fail
                    val receiveLine = rjReceiptUpload.rvb02
                    //start find the raw data in list
                    Log.e(mTAG, "po = $po")
                    Log.e(mTAG, "uploadResult = $uploadResult")
                    Log.e(mTAG, "receiveNum = $receiveNum")
                    Log.e(mTAG, "receiveLine = $receiveLine")
                    if (itemReceipt != null) {

                        if (uploadResult == "1") { //success

                            if (itemReceipt!!.poNumSplit == po && poLineInt == itemReceipt!!.poLineInt) {
                                itemReceipt!!.state = ItemReceipt.ItemState.UPLOADED
                                itemReceipt!!.receiveNum = receiveNum
                                itemReceipt!!.receiveLine = receiveLine
                                //ReceiptList.replaceItem(item, j)

                                val successIntent = Intent()
                                successIntent.action =Constants.ACTION.ACTION_RECEIPT_UPLOAD_SUCCESS
                                sendBroadcast(successIntent)

                            } else { //po is different
                                val poDiffIntent = Intent()
                                poDiffIntent.action = Constants.ACTION.ACTION_RECEIPT_UPLOAD_RETURN_PO_DIFF
                                sendBroadcast(poDiffIntent)

                                itemReceipt!!.state = ItemReceipt.ItemState.UPLOADED
                                Log.e(mTAG, getString(R.string.upload_receipt_failed_po_not_match))
                                toast(getString(R.string.upload_receipt_failed_po_not_match))
                            }


                        } else { // upload failed, uploadResult == 0
                            itemReceipt!!.state = ItemReceipt.ItemState.UPLOAD_FAILED

                            val errorString: String = getString(R.string.receipt_upload_error)+" $receiveNum "+rjReceiptUpload.rva01
                            toast(errorString)

                            val failedIntent = Intent()
                            failedIntent.action = Constants.ACTION.ACTION_RECEIPT_UPLOAD_FAILED
                            failedIntent.putExtra("REASON", rjReceiptUpload.rva01)
                            sendBroadcast(failedIntent)
                        }// upload fail



                    } else {
                        Log.e(mTAG, "itemReceipt = null")
                    }

                     */


                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.e(mTAG, "===>Exception ")
                    // itemReceiptFromScanDifferentHeader = null;
                    //String temp = ex.toString();

                    itemStorage!!.state = ItemStorage.ItemState.UPLOAD_FAILED

                    val exceptionIntent = Intent()
                    exceptionIntent.action = Constants.ACTION.ACTION_STORAGE_UPLOAD_RETURN_EXCEPTION
                    sendBroadcast(exceptionIntent)

                    runOnUiThread {
                        //mLoadingView.setStatus(LoadingView.GONE)
                        //setUIToEditMode()
                        toast(getString(R.string.toast_server_error))
                    }

                }


                Log.e(mTAG, "===>upLoadStorageCallback : onResponse end")
            }


        }//onResponse
    }

    fun getMaterial(barcode: ScanBarcode?) {

        if (barcode != null) {
            Log.e(mTAG, "getMaterial poBarcodeByScan = "+barcode.poBarcodeByScan)
            // to call api
            //acState = ReceiptACState.RECEIPT_GETTING_STATE
            val para = HttpMaterialGetPara()
            para.sfp06 = "1"
            para.sfs01 = barcode.poBarcodeByScan
            para.sfs02 = "0"

            ApiFunc().getMaterial(para, getMaterialCallback)

        }


    }//getMaterial

    private var getMaterialCallback: Callback = object : Callback {

        override fun onFailure(call: Call, e: IOException) {

            Log.e(mTAG, "getMaterialCallback err msg = $e")
            runOnUiThread(netErrRunnable)
        }

        @Throws(IOException::class)
        override fun onResponse(call: Call, response: Response) {
            Log.e(mTAG, "onResponse : "+response.body.toString())

            val jsonStr = ReceiveTransform.addToJsonArrayStr(response.body!!.string())
            Log.e(mTAG, "jsonStr = $jsonStr")
            //val res = ReceiveTransform.restoreToJsonStr(response.body()!!.string())
            //1.get response ,2 error or right , 3 update ui ,4. restore acState 5. update fragment detail
            runOnUiThread {
                try {


                    materialList.clear()

                    val rjMaterialList = Gson().fromJson(jsonStr, ReceiveTransform.RJMaterialList::class.java)
                    Log.e(mTAG, "rjMaterialList.dataList.size = " + rjMaterialList.dataList.size)



                    if (rjMaterialList.dataList.size > 0) {
                        if (rjMaterialList.dataList.size == 1) {
                            if (rjMaterialList.dataList[0].result2.isNotEmpty()) {
                                val noExistIntent = Intent()
                                noExistIntent.action = Constants.ACTION.ACTION_MATERIAL_NO_NOT_EXIST
                                mContext!!.sendBroadcast(noExistIntent)

                                toastLong(rjMaterialList.dataList[0].result2)
                            }
                        } else { //size > 1
                            Log.e(mTAG, "=== materialList start ===")
                            for (rjMaterial in rjMaterialList.dataList) {
                                if (rjMaterial.result == "0") { //0 success
                                    materialList.add(rjMaterial)
                                    //Log.d(mTAG, "rjMaterial = "+rjMaterial.ima02)
                                } else { //failed
                                    Log.e(mTAG, "result = ${rjMaterial.result}")
                                }
                            }
                            Log.e(mTAG, "=== materialList end ===")

                            val refreshIntent = Intent()
                            refreshIntent.action = Constants.ACTION.ACTION_MATERIAL_FRAGMENT_REFRESH
                            mContext!!.sendBroadcast(refreshIntent)
                        }
                    } else { //size == 0
                        Log.e(mTAG, "rjMaterialList size = 0")
                    }


                } catch (e: Exception) {
                    Log.e(mTAG, "ex = $e")
                    //system error
                    toast(getString(R.string.toast_server_error))
                }
                isBarcodeScanning = false
            }


        }//onResponse
    }

    //upload
    fun updateMaterialSend(p_sfs01: String, p_sfs02: String, p_sfs05: String) {
        Log.d(mTAG, "=== updateMaterialSend start ===")

        val para = HttpParaUploadMaterial()

        para.p_sfs01 = p_sfs01
        para.p_sfs02 = p_sfs02
        para.p_sfs05 = p_sfs05

        ApiFunc().updateMaterialSend(para, updateMaterialSendCallback)





        Log.d(mTAG, "=== updateMaterialSend end ===")
    }

    private var updateMaterialSendCallback: Callback = object : Callback {
        override fun onFailure(call: Call, e: IOException) {

            Log.e(mTAG, "updateMaterialSendCallback onFailure")

            runOnUiThread {
                val failIntent = Intent()
                failIntent.action = Constants.ACTION.ACTION_CONNECTION_TIMEOUT
                sendBroadcast(failIntent)
            }
        }

        @Throws(IOException::class)
        override fun onResponse(call: Call, response: Response) {
            Log.e(mTAG, "updateMaterialSendCallback onResponse : "+response.body.toString())
            val res = response.body.toString()
            //1.get response ,2 error or right , 3 update ui ,4. restore acState 5. update fragment detail
            runOnUiThread {
                Log.e(mTAG, "===>updateMaterialSendCallback : onResponse start")
                try {   // trans to list data
                    //val jsonStr = ReceiveTransform.addToJsonArrayStr(response.body()!!.string())
                    Log.e(mTAG, "res = $res")

                    //==== receive single record start ====
                    val rjMaterialUpload: RJMaterialUpload =
                        Gson().fromJson(res, RJMaterialUpload::class.java) as RJMaterialUpload

                    if (rjMaterialUpload.result == ItemMaterial.RESULT_CORRECT) {
                        val successIntent = Intent()
                        successIntent.action = Constants.ACTION.ACTION_MATERIAL_SEND_CHANGED_SUCCESS
                        mContext!!.sendBroadcast(successIntent)
                    } else {
                        val failedIntent = Intent()
                        failedIntent.action = Constants.ACTION.ACTION_MATERIAL_SEND_CHANGED_FAILED
                        mContext!!.sendBroadcast(failedIntent)
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.e(mTAG, "===>Exception ")

                    runOnUiThread {
                        //mLoadingView.setStatus(LoadingView.GONE)
                        //setUIToEditMode()
                        toast(getString(R.string.toast_server_error))
                    }

                }

                Log.e(mTAG, "===>updateMaterialSendCallback : onResponse end")
            }


        }//onResponse
    }

    fun getProperty(barcode: ScanBarcode?) {

        if (barcode != null) {
            Log.e(mTAG, "getProperty poBarcodeByScan = "+barcode.poBarcodeByScan)
            // to call api
            //acState = ReceiptACState.RECEIPT_GETTING_STATE

            //val para = HttpPropertyGetPara()
            //para.p_faj02 = barcode.poBarcodeByScan

            ApiFunc().getProperty(barcode.poBarcodeByScan, getPropertyCallback)

        }


    }//getMaterial

    private var getPropertyCallback: Callback = object : Callback {

        override fun onFailure(call: Call, e: IOException) {

            Log.e(mTAG, "getPropertyCallback err msg = $e")
            runOnUiThread(netErrRunnable)
        }

        @Throws(IOException::class)
        override fun onResponse(call: Call, response: Response) {
            Log.e(mTAG, "onResponse : "+response.body.toString())

            val jsonStr = ReceiveTransform.addToJsonArrayStr(response.body!!.string())
            Log.e(mTAG, "jsonStr = $jsonStr")
            //val res = ReceiveTransform.restoreToJsonStr(response.body()!!.string())
            //1.get response ,2 error or right , 3 update ui ,4. restore acState 5. update fragment detail
            runOnUiThread {
                try {
                    propertyList.clear()

                    val rjPropertyList = Gson().fromJson(jsonStr, ReceiveTransform.RJPropertyList::class.java)
                    Log.e(mTAG, "rjPropertyList.dataList.size = " + rjPropertyList.dataList.size)

                    if (rjPropertyList.dataList.size > 0) {
                        if (rjPropertyList.dataList.size == 1) {
                            if (rjPropertyList.dataList[0].result == "0") {
                                Log.e(mTAG, "=== propertyList start ===")
                                for (rjProperty in rjPropertyList.dataList) {
                                    if (rjProperty.result == "0") { //0 success
                                        propertyList.add(rjProperty)
                                        //Log.d(mTAG, "rjMaterial = "+rjMaterial.ima02)
                                    } else { //failed
                                        Log.e(mTAG, "rjProperty.result = ${rjProperty.result}")
                                    }
                                }
                                Log.e(mTAG, "=== propertyList end ===")

                                val refreshIntent = Intent()
                                refreshIntent.action = Constants.ACTION.ACTION_PROPERTY_FRAGMENT_REFRESH
                                mContext!!.sendBroadcast(refreshIntent)
                            } else {
                                val noExistIntent = Intent()
                                noExistIntent.action = Constants.ACTION.ACTION_PROPERTY_NO_NOT_EXIST
                                mContext!!.sendBroadcast(noExistIntent)

                                toastLong(rjPropertyList.dataList[0].faj01)
                            }

                            //if (rjPropertyList.dataList[0].faj01.isNotEmpty()) {

                            //}
                        } else { //size > 1
                            Log.e(mTAG, "=== propertyList start ===")
                            for (rjProperty in rjPropertyList.dataList) {
                                if (rjProperty.result == "0") { //0 success
                                    propertyList.add(rjProperty)
                                    //Log.d(mTAG, "rjMaterial = "+rjMaterial.ima02)
                                } else { //failed
                                    Log.e(mTAG, "rjProperty.result = ${rjProperty.result}")
                                }
                            }
                            Log.e(mTAG, "=== propertyList end ===")

                            val refreshIntent = Intent()
                            refreshIntent.action = Constants.ACTION.ACTION_PROPERTY_FRAGMENT_REFRESH
                            mContext!!.sendBroadcast(refreshIntent)
                        }
                    } else { //size == 0
                        Log.e(mTAG, "rjMaterialList size = 0")
                    }


                } catch (e: Exception) {
                    Log.e(mTAG, "ex = $e")
                    //system error
                    toast(getString(R.string.toast_server_error))
                    val failIntent = Intent()
                    failIntent.action = Constants.ACTION.ACTION_SERVER_ERROR
                    sendBroadcast(failIntent)
                }
                isBarcodeScanning = false
            }


        }//onResponse
    }

    //guest
    fun guestInOrOutMulti(data1: String, data2: String, data3: String, data4: String, data5: String, data6: String) {

        val para = HttpGuestInOrOutMultiPara()

        para.data1 = data1 // (In: 0, Out: 2)
        para.data2 = data2 // plant
        para.data3 = data3 // pmm09 供應商編號
        para.data4 = data4 // 採購單編號
        para.data5 = data5 // 採購單項次
        para.data6 = data6 // 刷退帶刷進日期

        Log.e(mTAG, "data1 = $data1, data2 = $data2, data3 = $data3, data4 = $data4, data5 = $data5, data6 = $data6")

        ApiFunc().guestInOrOutMulti(para, guestInOrOutMultiCallback)
    }

    private var guestInOrOutMultiCallback: Callback = object : Callback {

        override fun onFailure(call: Call, e: IOException) {


            runOnUiThread(netErrRunnable)
        }

        @Throws(IOException::class)
        override fun onResponse(call: Call, response: Response) {
            Log.e(mTAG, "onResponse : "+response.body.toString())
            val res = ReceiveTransform.restoreToJsonStr(response.body!!.string())
            Log.e(mTAG, "res = $res")
            //val res = ReceiveTransform.restoreToJsonStr(response.body()!!.string())
            //1.get response ,2 error or right , 3 update ui ,4. restore acState 5. update fragment detail
            runOnUiThread {
                try {

                    val retItemGuest = ItemGuest.transRJGuestStrToItemGuest(res)

                    if (retItemGuest != null) {
                        if (!retItemGuest.rjGuest?.result.equals(ItemGuest.RESULT_CORRECT)) {
                            Log.e(mTAG, "result = " + retItemGuest.rjGuest?.result + " result2 = " + retItemGuest.rjGuest?.result2)
                            //can't receive the item
                            //val mess = retItemReceipt.poNumScanTotal + " " + retItemReceipt.rjReceipt?.result2
                            val mess = retItemGuest.rjGuest?.result2 as String
                            toastLong(mess)


                            val refreshIntent = Intent()
                            refreshIntent.action = Constants.ACTION.ACTION_GUEST_IN_OR_LEAVE_FAILED
                            mContext!!.sendBroadcast(refreshIntent)
                        }// result  = 0
                        else {
                            // success receive ,update list ,update fragment
                            Log.d(mTAG, "guest In or Out success!")

                            val msg = retItemGuest.rjGuest?.data1 as String
                            toastLong(msg)

                            val successIntent = Intent()
                            successIntent.action = Constants.ACTION.ACTION_GUEST_IN_OR_LEAVE_SUCCESS
                            mContext!!.sendBroadcast(successIntent)

                        }//result = 1
                    } else {
                        Log.e(mTAG, "retItemGuest = null")


                    }


                } catch (e: Exception) {
                    Log.e(mTAG, "ex = $e")
                    //system error
                    //toast(getString(R.string.toast_server_error))
                    //val failIntent = Intent()
                    //failIntent.action = Constants.ACTION.ACTION_SERVER_ERROR
                    //sendBroadcast(failIntent)
                }

                isBarcodeScanning = false
            }


        }//onResponse
    }

    fun getGuestMulti(plant: String) {
        Log.e(mTAG, "=== getGuestMulti start ===")
        Log.e(mTAG, "plant = $plant ===")
        val para = HttpGuestNotLeaveGetPara()
        para.data1 = plant
        ApiFunc().getGuestMulti(para, getGuestMultiCallback)
    }

    private var getGuestMultiCallback: Callback = object : Callback {

        override fun onFailure(call: Call, e: IOException) {

            runOnUiThread(netErrRunnable)
        }

        @Throws(IOException::class)
        override fun onResponse(call: Call, response: Response) {
            Log.e(mTAG, "onResponse : "+response.body.toString())
            val jsonStr = ReceiveTransform.addToJsonArrayStr(response.body!!.string())
            Log.e(mTAG, "jsonStr = $jsonStr")
            //val res = ReceiveTransform.restoreToJsonStr(response.body()!!.string())
            //1.get response ,2 error or right , 3 update ui ,4. restore acState 5. update fragment detail
            runOnUiThread {
                try {
                    //guestList.clear()
                    when(currentSearchPlant) {
                        "A" -> guestListA.clear()
                        "B" -> guestListB.clear()
                        else -> guestListT.clear()
                    }

                    val rjGuestList = Gson().fromJson(jsonStr, ReceiveTransform.RJGuestList::class.java)
                    Log.e(mTAG, "rjGuestList.dataList.size = " + rjGuestList.dataList.size)

                    if (rjGuestList.dataList.size > 0) {

                        Log.e(mTAG, "=== guestList start ===")

                        if (rjGuestList.dataList.size == 1) {
                            if (rjGuestList.dataList[0].result == "0") { //0 success
                                when(currentSearchPlant) {
                                    "A" -> {
                                        guestListA.add(rjGuestList.dataList[0])
                                        val successIntent = Intent()
                                        //successIntent.action = Constants.ACTION.ACTION_GUEST_SEARCH_GUEST_NEXT_ACTION
                                        successIntent.action = Constants.ACTION.ACTION_GUEST_SEARCH_GUEST_COMPLETE
                                        mContext!!.sendBroadcast(successIntent)
                                    }
                                    "B" -> {
                                        guestListB.add(rjGuestList.dataList[0])
                                        val successIntent = Intent()
                                        successIntent.action = Constants.ACTION.ACTION_GUEST_SEARCH_GUEST_COMPLETE
                                        mContext!!.sendBroadcast(successIntent)
                                    }
                                    else -> {
                                        guestListT.add(rjGuestList.dataList[0])
                                        val successIntent = Intent()
                                        //successIntent.action = Constants.ACTION.ACTION_GUEST_SEARCH_GUEST_NEXT_ACTION
                                        successIntent.action = Constants.ACTION.ACTION_GUEST_SEARCH_GUEST_COMPLETE
                                        mContext!!.sendBroadcast(successIntent)
                                    }
                                }
                                //guestList.add(rjGuestList.dataList[0])

                                /*val successIntent = Intent()
                                successIntent.action = Constants.ACTION.ACTION_GUEST_GET_CURRENT_PLANT_GUEST_SUCCESS
                                mContext!!.sendBroadcast(successIntent)*/
                            } else {

                                isBarcodeScanning = false
                                //val errorString = intent.getStringExtra("result2")
                                val errorString = rjGuestList.dataList[0].result2
                                toast(errorString)


                                val errorIntent = Intent()
                                errorIntent.action = Constants.ACTION.ACTION_GUEST_GET_CURRENT_PLANT_GUEST_FAILED
                                //errorIntent.putExtra("result2", rjGuestList.dataList[0].result2)
                                mContext!!.sendBroadcast(errorIntent)




                                /*if (currentSearchPlant.contentEquals(currentPlant)) {
                                    val errorIntent = Intent()
                                    errorIntent.action = Constants.ACTION.ACTION_GUEST_GET_CURRENT_PLANT_GUEST_FAILED
                                    errorIntent.putExtra("result2", rjGuestList.dataList[0].result2)
                                    mContext!!.sendBroadcast(errorIntent)
                                }*/

                                /*when(currentSearchPlant) {
                                    "A" -> {
                                        val nextIntent = Intent()
                                        nextIntent.action = Constants.ACTION.ACTION_GUEST_SEARCH_GUEST_NEXT_ACTION
                                        mContext!!.sendBroadcast(nextIntent)
                                    }
                                    "B" -> {
                                        val completeIntent = Intent()
                                        completeIntent.action = Constants.ACTION.ACTION_GUEST_SEARCH_GUEST_COMPLETE
                                        mContext!!.sendBroadcast(completeIntent)
                                    }
                                    else -> {
                                        val nextIntent = Intent()
                                        nextIntent.action = Constants.ACTION.ACTION_GUEST_SEARCH_GUEST_NEXT_ACTION
                                        mContext!!.sendBroadcast(nextIntent)
                                    }
                                }*/

                            }

                        } else { //size > 1
                            var error = 0
                            for (rjGuest in rjGuestList.dataList) {
                                if (rjGuest.result == "0") { //0 success
                                    when(currentSearchPlant) {
                                        "A" -> guestListA.add(rjGuest)
                                        "B" -> guestListB.add(rjGuest)
                                        else -> guestListT.add(rjGuest)
                                    }
                                    //guestList.add(rjGuest)
                                    //Log.d(mTAG, "rjMaterial = "+rjMaterial.ima02)


                                } else { //failed
                                    error++
                                    Log.e(mTAG, "rjGuest.result = ${rjGuest.result}")
                                }
                            }

                            Log.d(mTAG, "error = $error")
                            when(currentSearchPlant) {
                                "A" -> {
                                    val successIntent = Intent()
                                    //successIntent.action = Constants.ACTION.ACTION_GUEST_SEARCH_GUEST_NEXT_ACTION
                                    successIntent.action = Constants.ACTION.ACTION_GUEST_SEARCH_GUEST_COMPLETE
                                    mContext!!.sendBroadcast(successIntent)
                                }
                                "B" -> {
                                    val successIntent = Intent()
                                    successIntent.action = Constants.ACTION.ACTION_GUEST_SEARCH_GUEST_COMPLETE
                                    mContext!!.sendBroadcast(successIntent)
                                }
                                else -> {
                                    val successIntent = Intent()
                                    //successIntent.action = Constants.ACTION.ACTION_GUEST_SEARCH_GUEST_NEXT_ACTION
                                    successIntent.action = Constants.ACTION.ACTION_GUEST_SEARCH_GUEST_COMPLETE
                                    mContext!!.sendBroadcast(successIntent)
                                }
                            }
                            /*val successIntent = Intent()
                            successIntent.action = Constants.ACTION.ACTION_GUEST_GET_CURRENT_PLANT_GUEST_SUCCESS
                            mContext!!.sendBroadcast(successIntent)*/
                        }


                        Log.e(mTAG, "=== guestList end ===")

                        //val refreshIntent = Intent()
                        //refreshIntent.action = Constants.ACTION.ACTION_GUEST_FRAGMENT_REFRESH
                        //!!.sendBroadcast(refreshIntent)

                    } else { //size == 0
                        when(currentSearchPlant) {
                            "A" -> {
                                val noExistIntent = Intent()
                                //noExistIntent.action = Constants.ACTION.ACTION_GUEST_SEARCH_GUEST_NEXT_ACTION
                                noExistIntent.action = Constants.ACTION.ACTION_GUEST_SEARCH_GUEST_COMPLETE
                                mContext!!.sendBroadcast(noExistIntent)
                            }
                            "B" -> {
                                val noExistIntent = Intent()
                                noExistIntent.action = Constants.ACTION.ACTION_GUEST_SEARCH_GUEST_COMPLETE
                                mContext!!.sendBroadcast(noExistIntent)
                            }
                            else -> {
                                val noExistIntent = Intent()
                                //noExistIntent.action = Constants.ACTION.ACTION_GUEST_SEARCH_GUEST_NEXT_ACTION
                                noExistIntent.action = Constants.ACTION.ACTION_GUEST_SEARCH_GUEST_COMPLETE
                                mContext!!.sendBroadcast(noExistIntent)
                            }
                        }

                        /*Log.e(mTAG, "rjGuestList size = 0")
                        val noExistIntent = Intent()
                        noExistIntent.action = Constants.ACTION.ACTION_GUEST_LIST_CLEAR
                        mContext!!.sendBroadcast(noExistIntent)*/
                    }


                } catch (e: Exception) {
                    Log.e(mTAG, "ex = $e")
                    //system error
                    //toast(getString(R.string.toast_server_error))
                    //val failIntent = Intent()
                    //failIntent.action = Constants.ACTION.ACTION_SERVER_ERROR
                    //sendBroadcast(failIntent)
                }
            }


        }//onResponse
    }

    fun getOutSourcedProcessDetail(sfpp01: String) {
        Log.e(mTAG, "=== getOutSourcedProcessDetail start ===")
        Log.e(mTAG, "sfpp01 = $sfpp01 ===")
        val para = HttpOutsourcedProcessGetPara()
        para.data1 = sfpp01
        ApiFunc().getOutSourcedProcessDetail(para, getOutSourcedProcessCallback)
    }

    private var getOutSourcedProcessCallback: Callback = object : Callback {

        override fun onFailure(call: Call, e: IOException) {

            runOnUiThread(netErrRunnable)
        }

        @Throws(IOException::class)
        override fun onResponse(call: Call, response: Response) {
            Log.e(mTAG, "onResponse : "+response.body.toString())
            val jsonStr = ReceiveTransform.addToJsonArrayStr(response.body!!.string())
            Log.e(mTAG, "jsonStr = $jsonStr")
            //val res = ReceiveTransform.restoreToJsonStr(response.body()!!.string())
            //1.get response ,2 error or right , 3 update ui ,4. restore acState 5. update fragment detail
            runOnUiThread {
                try {

                    val rjOutSourceProcessedList = Gson().fromJson(jsonStr, ReceiveTransform.RJOutSourcedProcesseList::class.java)
                    Log.e(mTAG, "rjOutSourceProcessedList.dataList.size = " + rjOutSourceProcessedList.dataList.size)

                    outsourcedProcessOrderList.clear()

                    if (rjOutSourceProcessedList.dataList.size > 0) {
                        if (rjOutSourceProcessedList.dataList.size == 1) {
                            if (rjOutSourceProcessedList.dataList[0].result == "0" && rjOutSourceProcessedList.dataList[0].result2 == "") { //success


                                outsourcedProcessOrderList.add(rjOutSourceProcessedList.dataList[0])
                                fabBack!!.visibility = View.VISIBLE
                                fabSign!!.visibility = View.VISIBLE
                            } else {

                                Log.e(mTAG, "rjOutSourceProcessedList.dataList[0].result2 = ${rjOutSourceProcessedList.dataList[0].result2}")
                                toast(rjOutSourceProcessedList.dataList[0].result2)
                            }
                            val successIntent = Intent()
                            successIntent.action = Constants.ACTION.ACTION_OUTSOURCED_PROCESS_FRAGMENT_DETAIL_REFRESH
                            mContext!!.sendBroadcast(successIntent)
                        } else { //rjOutSourceProcessedList.dataList.size > 1
                            var error = 0

                            for (rjOutSourceProcessed in rjOutSourceProcessedList.dataList) {
                                if (rjOutSourceProcessed.result == "0") { //0 success

                                    outsourcedProcessOrderList.add(rjOutSourceProcessed)

                                } else { //failed
                                    error++
                                    Log.e(mTAG, "rjOutSourceProcessed.result = ${rjOutSourceProcessed.result2}")
                                }
                            }

                            fabBack!!.visibility = View.VISIBLE
                            fabSign!!.visibility = View.VISIBLE

                            val successIntent = Intent()
                            successIntent.action = Constants.ACTION.ACTION_OUTSOURCED_PROCESS_FRAGMENT_DETAIL_REFRESH
                            mContext!!.sendBroadcast(successIntent)
                        }

                    }




                } catch (e: Exception) {
                    Log.e(mTAG, "ex = $e")
                    //system error
                    //toast(getString(R.string.toast_server_error))
                    //val failIntent = Intent()
                    //failIntent.action = Constants.ACTION.ACTION_SERVER_ERROR
                    //sendBroadcast(failIntent)
                }


            }


        }//onResponse
    }

    fun getOutSourcedProcessBySupplierNo(sfpp02: String) {
        Log.e(mTAG, "=== getOutSourcedProcessBySupplierNo start ===")
        val newString = sfpp02.replace("\n", "")
        Log.e(mTAG, "sfpp02 = $newString ===")
        val para = HttpOutsourcedProcessGetPara()
        para.data1 = newString
        ApiFunc().getOutSourcedProcessBySupplierNo(para, getOutSourcedProcessBySupplierNoCallback)
    }

    private var getOutSourcedProcessBySupplierNoCallback: Callback = object : Callback {

        override fun onFailure(call: Call, e: IOException) {

            runOnUiThread(netErrRunnable)
        }

        @Throws(IOException::class)
        override fun onResponse(call: Call, response: Response) {
            isBarcodeScanning = false
            Log.e(mTAG, "onResponse : "+response.body.toString())
            val jsonStr = ReceiveTransform.addToJsonArrayStr(response.body!!.string())
            Log.e(mTAG, "jsonStr = $jsonStr")
            //val res = ReceiveTransform.restoreToJsonStr(response.body()!!.string())
            //1.get response ,2 error or right , 3 update ui ,4. restore acState 5. update fragment detail
            runOnUiThread {
                try {

                    val rjOutSourceProcessedListBySupplier = Gson().fromJson(jsonStr, ReceiveTransform.RJOutSourcedProcesseListBySupplier::class.java)
                    Log.e(mTAG, "rjOutSourceProcessedListBySupplier.dataList.size = " + rjOutSourceProcessedListBySupplier.dataList.size)

                    outsourcedProcessOrderListBySupplier.clear()

                    if (rjOutSourceProcessedListBySupplier.dataList.size > 0) {
                        if (rjOutSourceProcessedListBySupplier.dataList.size == 1) {
                            if (rjOutSourceProcessedListBySupplier.dataList[0].result == "0" && rjOutSourceProcessedListBySupplier.dataList[0].result2 == "") { //success


                                outsourcedProcessOrderListBySupplier.add(rjOutSourceProcessedListBySupplier.dataList[0])

                            } else {

                                Log.e(mTAG, "rjOutSourceProcessedList.dataList[0].result2 = ${rjOutSourceProcessedListBySupplier.dataList[0].result2}")
                                toast(rjOutSourceProcessedListBySupplier.dataList[0].result2)
                            }
                            val successIntent = Intent()
                            successIntent.action = Constants.ACTION.ACTION_OUTSOURCED_PROCESS_FRAGMENT_REFRESH
                            mContext!!.sendBroadcast(successIntent)
                        } else { //rjOutSourceProcessedList.dataList.size > 1
                            var error = 0

                            for (rjOutSourceProcessedBySupplier in rjOutSourceProcessedListBySupplier.dataList) {
                                if (rjOutSourceProcessedBySupplier.result == "0") { //0 success

                                    outsourcedProcessOrderListBySupplier.add(rjOutSourceProcessedBySupplier)

                                } else { //failed
                                    error++
                                    Log.e(mTAG, "rjOutSourceProcessedBySupplier.result = ${rjOutSourceProcessedBySupplier.result2}")
                                }
                            }

                            val successIntent = Intent()
                            successIntent.action = Constants.ACTION.ACTION_OUTSOURCED_PROCESS_FRAGMENT_REFRESH
                            mContext!!.sendBroadcast(successIntent)
                        }

                    }


                } catch (e: Exception) {
                    Log.e(mTAG, "ex = $e")
                    //system error
                    //toast(getString(R.string.toast_server_error))
                    //val failIntent = Intent()
                    //failIntent.action = Constants.ACTION.ACTION_SERVER_ERROR
                    //sendBroadcast(failIntent)
                }
            }


        }//onResponse
    }

    fun confirmOutSourcedProcessSign(outSourcedSendOrder: String, signFileName: String, userName: String) {
        Log.e(mTAG, "=== confirmOutSourcedProcessSign start ===")

        Log.e(mTAG, "outSourcedSendOrder = $outSourcedSendOrder, signFileName = $signFileName, userName = $userName")

        val para = HttpOutsourcedProcessSignConfirmGetPara()
        para.data1 = outSourcedSendOrder
        para.data2 = signFileName
        para.data3 = userName
        ApiFunc().confirmOutSourcedProcessSign(para, confirmOutSourcedProcessSignCallback)
    }

    private var confirmOutSourcedProcessSignCallback: Callback = object : Callback {

        override fun onFailure(call: Call, e: IOException) {

            runOnUiThread(netErrRunnable)
        }

        @Throws(IOException::class)
        override fun onResponse(call: Call, response: Response) {
            Log.e(mTAG, "onResponse : "+response.body.toString())
            //val jsonStr = ReceiveTransform.addToJsonArrayStr(response.body!!.string())
            val res = ReceiveTransform.restoreToJsonStr(response.body!!.string())
            Log.e(mTAG, "res = $res")
            //val res = ReceiveTransform.restoreToJsonStr(response.body()!!.string())
            //1.get response ,2 error or right , 3 update ui ,4. restore acState 5. update fragment detail
            runOnUiThread {
                try {

                    //==== receive single record start ====
                    val rjOutSourcedConfirm: RJOutSourcedConfirm = Gson().fromJson(res, RJOutSourcedConfirm::class.java) as RJOutSourcedConfirm

                    if (rjOutSourcedConfirm.result == "0") {
                        val successIntent = Intent()
                        successIntent.action = Constants.ACTION.ACTION_OUTSOURCED_PROCESS_SIGN_UPLOAD_SUCCESS
                        successIntent.putExtra("SEND_ORDER", currentOutSourcedSendOrder)
                        mContext!!.sendBroadcast(successIntent)
                    } else {
                        val failedIntent = Intent()
                        failedIntent.action = Constants.ACTION.ACTION_OUTSOURCED_PROCESS_SIGN_UPLOAD_FAILED
                        mContext!!.sendBroadcast(failedIntent)

                        toast(rjOutSourcedConfirm.result2)
                    }


                } catch (e: Exception) {
                    Log.e(mTAG, "ex = $e")
                    //system error
                    //toast(getString(R.string.toast_server_error))
                    //val failIntent = Intent()
                    //failIntent.action = Constants.ACTION.ACTION_SERVER_ERROR
                    //sendBroadcast(failIntent)
                }
            }


        }//onResponse
    }

    internal var netErrRunnable: Runnable = Runnable {

        isBarcodeScanning = false

        //mLoadingView.setStatus(LoadingView.GONE)
        // Toast.makeText(mContext,getString(R.string.toast_network_error),Toast.LENGTH_LONG).show();
        //showMyToast(getString(R.string.toast_network_error), mContext)
        toast(getString(R.string.toast_network_error))
        val failIntent = Intent()
        failIntent.action = Constants.ACTION.ACTION_NETWORK_FAILED
        sendBroadcast(failIntent)


    }

    fun printLabel(
        text1: String,
        text2: String,
        pmn04: String,
        pmn20: String,
        checkQC: String,
        ima36: String,
        rvb38: String
    ): Int {
        Log.e(mTAG, "[printLabel start]")

        var ret = 0

        if (bluetoothPrintFunc != null) {
            Log.e(mTAG, "text1 = $text1")
            Log.e(mTAG, "text2 = $text2")
            Log.e(mTAG, "checkQC = $checkQC")
            try {
                bluetoothPrintFunc!!.printText(text1)
                bluetoothPrintFunc!!.printBarCode(text2)
                bluetoothPrintFunc!!.printText(text2)
                //val text3 = pmn04
                val text5 = "$pmn04/$pmn20"
                bluetoothPrintFunc!!.printText(text5)
                var text4 = "$ima36/$rvb38"
                if (checkQC != "Y") {
                    text4 = "$text4(免檢)"
                    //bluetoothPrintFunc.printText("免檢");
                }
                bluetoothPrintFunc!!.printText(text4)
                val printform = byteArrayOf(12)
                mChatService!!.write(printform)
            } catch (e: UnsupportedEncodingException) {
                ret = 1
                e.printStackTrace()
            }


        } else {
            Log.e(mTAG, "bluetoothPrintFunc = null")
            ret = 1
        }





        Log.e(mTAG, "[printLabel end]  ret = $ret")
        return ret
    }

    /*private val mSeekHandler = object : Handler() {
        override fun handleMessage(msg: Message) {

            Log.e(mTAG, "receive close")
            dialog!!.dismiss()

            val pageSelectIntent = Intent()
            pageSelectIntent.action = Constants.ACTION.ACTION_MATERIAL_SEEK_BAR_SELECT_PAGE_ACTION
            pageSelectIntent.putExtra("PAGE", seekBarCurrentPage)
            mContext!!.sendBroadcast(pageSelectIntent)

            true
        }
    }*/

    //private class SeekHandle(activity: MainActivity) : Handler() {
    private class SeekHandle(activity: MainActivity) : Executor {
        private val mActivity: WeakReference<MainActivity> = WeakReference(activity)
        private val mTAG = MainActivity::class.java.name

        /*override fun handleMessage(msg: Message) {

            if (mActivity.get() == null) {
                return
            }
            val activity = mActivity.get()


            Log.e(mTAG, "receive close")
            activity!!.dialog!!.dismiss()

            val pageSelectIntent = Intent()

            pageSelectIntent.action = Constants.ACTION.ACTION_SEEK_BAR_SELECT_PAGE_ACTION

            pageSelectIntent.putExtra("PAGE", seekBarCurrentPage)
            activity.sendBroadcast(pageSelectIntent)
        }*/

        override fun execute(p0: Runnable?) {

            //val mActivity: WeakReference<MainActivity> = WeakReference(this)

            val activity = mActivity.get()


            Log.e(mTAG, "receive close")
            activity!!.dialog!!.dismiss()

            val pageSelectIntent = Intent()

            pageSelectIntent.action = Constants.ACTION.ACTION_SEEK_BAR_SELECT_PAGE_ACTION

            pageSelectIntent.putExtra("PAGE", seekBarCurrentPage)
            activity.sendBroadcast(pageSelectIntent)
        }
    }

    internal var seekRunnable: Runnable = Runnable {

        /*val mActivity: WeakReference<MainActivity> = WeakReference(this)

        val activity = mActivity.get()


        Log.e(mTAG, "receive close")
        activity!!.dialog!!.dismiss()

        val pageSelectIntent = Intent()

        pageSelectIntent.action = Constants.ACTION.ACTION_SEEK_BAR_SELECT_PAGE_ACTION

        pageSelectIntent.putExtra("PAGE", seekBarCurrentPage)
        activity.sendBroadcast(pageSelectIntent)*/



    }

    private fun showMaterialSeekDialog() {

        val promptView = View.inflate(this@MainActivity, R.layout.page_dialog, null)

        val alertDialogBuilder = AlertDialog.Builder(this@MainActivity)
        //AlertDialog dialog = null;

        alertDialogBuilder.setView(promptView)

        val textPage = promptView.findViewById<TextView>(R.id.textPage)
        val textPartNo = promptView.findViewById<TextView>(R.id.textPartNo)
        val textPartName = promptView.findViewById<TextView>(R.id.textPartName)
        val seekBarPage = promptView.findViewById<SeekBar>(R.id.seekBar)

        if (materialList.size > 0) {
            seekBarPage.max = materialList.size - 1
            seekBarPage.progress = currentMaterialPage
        } else {
            seekBarPage.max = 0
            seekBarPage.progress = 0
        }

        var temp = 0

        if (materialList.size > 0) {
            textPartNo.text = materialList[currentMaterialPage].sfs04
            textPartName.text = materialList[currentMaterialPage].ima02
            temp = currentMaterialPage+1
        }

        val pageString = getString(R.string.material_viewpage_page, temp, materialList.size)
        textPage.text = pageString

        seekBarPage.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {

                val page = progress + 1

                textPage.text = getString(R.string.material_viewpage_page, page, materialList.size)

                if (materialList.size > 0) {
                    textPartNo.text = materialList[progress].sfs04
                    textPartName.text = materialList[progress].ima02
                }

                seekBarCurrentPage = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {



                //val msg = Message()
                //mSeekHandler.sendMessage(msg)
                val msg = SeekHandle(this@MainActivity)
                //msg.sendEmptyMessage(0)
                msg.execute(seekRunnable)

            }
        })

        alertDialogBuilder.setCancelable(false)
        dialog = alertDialogBuilder.show()
        //dialog!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)

    }

    private fun showPropertySeekDialog() {

        val promptView = View.inflate(this@MainActivity, R.layout.page_dialog, null)

        val alertDialogBuilder = AlertDialog.Builder(this@MainActivity)
        //AlertDialog dialog = null;

        alertDialogBuilder.setView(promptView)

        val textPage = promptView.findViewById<TextView>(R.id.textPage)
        val textPartNo = promptView.findViewById<TextView>(R.id.textPartNo)
        val textPartName = promptView.findViewById<TextView>(R.id.textPartName)
        val seekBarPage = promptView.findViewById<SeekBar>(R.id.seekBar)

        if (propertyList.size > 0) {
            seekBarPage.max = propertyList.size - 1
            seekBarPage.progress = currentPropertyPage
        }

        if (propertyList.size > 0) {
            textPartNo.text = propertyList[currentPropertyPage].faj02
            textPartName.text = propertyList[currentPropertyPage].faj06
        }

        val pageString = getString(R.string.material_viewpage_page, currentPropertyPage+1, propertyList.size)
        textPage.text = pageString

        seekBarPage.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {

                val page = progress + 1

                textPage.text = getString(R.string.material_viewpage_page, page, propertyList.size)

                if (propertyList.size > 0) {
                    textPartNo.text = propertyList[progress].faj02
                    textPartName.text = propertyList[progress].faj06
                }

                seekBarCurrentPage = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {



                //val msg = Message()
                //mSeekHandler.sendMessage(msg)
                val msg = SeekHandle(this@MainActivity)
                //msg.sendEmptyMessage(0)
                msg.execute(seekRunnable)
            }
        })

        alertDialogBuilder.setCancelable(false)
        dialog = alertDialogBuilder.show()
        //dialog!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)

    }

    private fun showExitConfirmDialog() {

        // get prompts.xml view
        /*LayoutInflater layoutInflater = LayoutInflater.from(Nfc_read_app.this);
        View promptView = layoutInflater.inflate(R.layout.input_dialog, null);*/
        val promptView = View.inflate(this@MainActivity, R.layout.confirm_dialog, null)

        val alertDialogBuilder = AlertDialog.Builder(this@MainActivity).create()
        alertDialogBuilder.setView(promptView)

        //final EditText editFileName = (EditText) promptView.findViewById(R.id.editFileName);
        val textViewMsg = promptView.findViewById<TextView>(R.id.textViewDialog)
        val btnCancel = promptView.findViewById<Button>(R.id.btnDialogCancel)
        val btnConfirm = promptView.findViewById<Button>(R.id.btnDialogConfirm)

        textViewMsg.text = getString(R.string.exit_app_msg)
        btnCancel.text = getString(R.string.cancel)
        btnConfirm.text = getString(R.string.confirm)

        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
        btnCancel!!.setOnClickListener {
            alertDialogBuilder.dismiss()
        }
        btnConfirm!!.setOnClickListener {
            val drawer : DrawerLayout = findViewById(R.id.drawer_layout)
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START)
            }
            alertDialogBuilder.dismiss()
            //isLogin = false

            finish()


        }
        alertDialogBuilder.show()
    }

    private fun showLogoutConfirmDialog() {

        // get prompts.xml view
        /*LayoutInflater layoutInflater = LayoutInflater.from(Nfc_read_app.this);
        View promptView = layoutInflater.inflate(R.layout.input_dialog, null);*/
        val promptView = View.inflate(this@MainActivity, R.layout.confirm_dialog, null)

        val alertDialogBuilder = AlertDialog.Builder(this@MainActivity).create()
        alertDialogBuilder.setView(promptView)

        //final EditText editFileName = (EditText) promptView.findViewById(R.id.editFileName);
        val textViewMsg = promptView.findViewById<TextView>(R.id.textViewDialog)
        val btnCancel = promptView.findViewById<Button>(R.id.btnDialogCancel)
        val btnConfirm = promptView.findViewById<Button>(R.id.btnDialogConfirm)

        textViewMsg.text = getString(R.string.logout_title_msg)
        btnCancel.text = getString(R.string.cancel)
        btnConfirm.text = getString(R.string.confirm)

        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
        btnCancel!!.setOnClickListener {
            if (currentSelectMenuItem != null) {
                currentSelectMenuItem!!.isChecked = false
            }

            alertDialogBuilder.dismiss()
        }
        btnConfirm!!.setOnClickListener {
            val drawer: DrawerLayout = findViewById(R.id.drawer_layout)
            drawer.closeDrawer(GravityCompat.START)

            val logoutIntent = Intent(Constants.ACTION.ACTION_LOGOUT_ACTION)
            mContext?.sendBroadcast(logoutIntent)
            alertDialogBuilder.dismiss()
        }
        alertDialogBuilder.show()


    }

    private fun showPrintAgainConfirmDialog() {

        // get prompts.xml view
        /*LayoutInflater layoutInflater = LayoutInflater.from(Nfc_read_app.this);
        View promptView = layoutInflater.inflate(R.layout.input_dialog, null);*/
        val promptView = View.inflate(this@MainActivity, R.layout.confirm_dialog, null)

        val alertDialogBuilder = AlertDialog.Builder(this@MainActivity).create()
        alertDialogBuilder.setView(promptView)

        //final EditText editFileName = (EditText) promptView.findViewById(R.id.editFileName);
        val textViewMsg = promptView.findViewById<TextView>(R.id.textViewDialog)
        val btnCancel = promptView.findViewById<Button>(R.id.btnDialogCancel)
        val btnConfirm = promptView.findViewById<Button>(R.id.btnDialogConfirm)

        textViewMsg.text = getString(R.string.fab_print_confirm)
        btnCancel.text = getString(R.string.cancel)
        btnConfirm.text = getString(R.string.confirm)

        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
        btnCancel!!.setOnClickListener {
            alertDialogBuilder.dismiss()
        }
        btnConfirm!!.setOnClickListener {
            if (itemReceipt != null) {

                when(printerStatus) {
                    BluetoothChatService.STATE_NONE, BluetoothChatService.STATE_LISTEN, BluetoothChatService.STATE_CONNECTING-> {
                        toast(getString(R.string.tag_printer_status_not_connected))
                    }

                    BluetoothChatService.STATE_CONNECTED-> {
                        when(printStatus) {
                            PrintStatus.PRINT_SUCCESS -> {
                                val addString = when (itemReceipt!!.receiveLine.length) {
                                    0 -> "00"
                                    1 -> "0"
                                    else -> ""
                                }


                                val printContent: String = itemReceipt!!.receiveNum + addString + itemReceipt!!.receiveLine

                                //print 1
                                val ret: Int
                                ret = printLabel(
                                    itemReceipt!!.poNumSplit + "-" + itemReceipt!!.poLineInt,
                                    printContent,
                                    itemReceipt!!.rjReceipt!!.pmn04,
                                    itemReceipt!!.rjReceipt!!.pmn20,
                                    itemReceipt!!.rjReceipt!!.pmnud02,
                                    itemReceipt!!.rjReceipt!!.ima36,
                                    itemReceipt!!.rjReceipt!!.rvb38
                                )

                                when(ret) {
                                    0 -> {
                                        //itemReceipt!!.state = ItemReceipt.ItemState.PRINTED
                                        Log.d(mTAG, "(fabPrintAgain) Print 1 tag success")
                                        toast(getString(R.string.receipt_tag_printed))
                                        printStatus = PrintStatus.PRINT_SUCCESS
                                    }
                                    else -> {
                                        Log.d(mTAG, "(fabPrintAgain) tag was printed failed")
                                        toast(getString(R.string.print_error))
                                        //fabPrint!!.visibility = View.VISIBLE
                                        fabPrint!!.show()
                                        printStatus = PrintStatus.PRINT_ERROR
                                        //itemReceipt!!.state = ItemReceipt.ItemState.PRINTED_FAILED
                                    }
                                }
                            }

                            PrintStatus.PRINT_ERROR -> {
                                toast(getString(R.string.print_error))
                                //fabPrint!!.visibility = View.VISIBLE
                                fabPrint!!.show()
                                //printStatus = PrintStatus.PRINT_WAITING

                                //val connectIntent = Intent()
                                //connectIntent.action = Constants.ACTION.ACTION_PRINT_ERROR
                                //sendBroadcast(connectIntent)
                            }
                        }
                    }
                }

            } else {
                Log.e(mTAG, "itemReceipt = null")
                toastLong(getString(R.string.receipt_print_again_data_null))
            }

            alertDialogBuilder.dismiss()
        }
        alertDialogBuilder.show()
    }

    private fun showCurrentVersionDialog() {

        // get prompts.xml view
        /*LayoutInflater layoutInflater = LayoutInflater.from(Nfc_read_app.this);
        View promptView = layoutInflater.inflate(R.layout.input_dialog, null);*/
        val promptView = View.inflate(this@MainActivity, R.layout.about_dialog, null)

        val alertDialogBuilder = AlertDialog.Builder(this@MainActivity).create()
        alertDialogBuilder.setView(promptView)

        //final EditText editFileName = (EditText) promptView.findViewById(R.id.editFileName);
        val textViewMsg = promptView.findViewById<TextView>(R.id.textViewDialog)
        val textViewFixMsg = promptView.findViewById<TextView>(R.id.textViewFixHistory)
        val btnCancel = promptView.findViewById<Button>(R.id.btnDialogCancel)
        val btnConfirm = promptView.findViewById<Button>(R.id.btnDialogConfirm)



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val pInfo = mContext!!.packageManager.getPackageInfo(packageName, 0)
            textViewMsg.text = getString(R.string.version_string, pInfo.longVersionCode, pInfo.versionName)
        } else {
            textViewMsg.text = getString(R.string.version_string, BuildConfig.VERSION_CODE, BuildConfig.VERSION_NAME)
        }


        var msg = "1. 修改數量時，小數點無法使用的問題\n"
        msg += "2. 新增上傳確認失敗的提示對話方塊\n"
        msg += "3. 將輸入欄位預設都為大寫輸入\n"
        msg += "4. 收料畫面加入\"重新連線標籤機\"於右上功能列表。\n用於標籤機無紙時更換新紙時，再按此選項重新連接，便可再列印標籤。"

        textViewFixMsg.text = msg

        btnCancel.text = getString(R.string.cancel)
        btnCancel.visibility = View.GONE
        btnConfirm.text = getString(R.string.confirm)

        // setup a dialog window
        alertDialogBuilder.setCancelable(false)

        btnConfirm!!.setOnClickListener {
            if (currentSelectMenuItem != null) {
                currentSelectMenuItem!!.isChecked = false
            }
            alertDialogBuilder.dismiss()
        }
        alertDialogBuilder.show()

    }
    @SuppressLint("CommitPrefEdits")
    private fun showSettingReceiptDialog() {
        val promptView = View.inflate(this@MainActivity, R.layout.receipt_setting_dialog, null)

        val alertDialogBuilder = AlertDialog.Builder(this@MainActivity).create()
        alertDialogBuilder.setView(promptView)

        //final EditText editFileName = (EditText) promptView.findViewById(R.id.editFileName);
        val textViewMsg = promptView.findViewById<TextView>(R.id.textViewReceiptDialog)
        val checkBox = promptView.findViewById<CheckBox>(R.id.checkBoxReceiptAutoConfirmUploadedDialog)
        //val btnCancel = promptView.findViewById<Button>(R.id.btnReceiptDialogCancel)
        val btnConfirm = promptView.findViewById<Button>(R.id.btnReceiptDialogConfirm)

        textViewMsg.text = getString(R.string.receipt_setting_title)
        checkBox.text = getString(R.string.setting_receipt_upload_auto_confirm)
        //btnCancel.text = getString(R.string.cancel)
        btnConfirm.text = getString(R.string.confirm)

        checkBox.isChecked = isReceiptUploadAutoConfirm

        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
        /*btnCancel!!.setOnClickListener {
            alertDialogBuilder.dismiss()
        }*/
        btnConfirm!!.setOnClickListener {

            isReceiptUploadAutoConfirm = checkBox.isChecked

            editor = pref!!.edit()
            editor!!.putBoolean("IS_RECEIPT_UPLOAD_AUTO_CONFIRM", isReceiptUploadAutoConfirm)
            editor!!.apply()

            /*progressBar!!.indeterminateTintList = ColorStateList.valueOf(colorCodePink)
            progressBar!!.visibility = View.VISIBLE

            btnUpload!!.isEnabled = false

            val uploadIntent = Intent()
            uploadIntent.action = Constants.ACTION.ACTION_RECEIPT_UPLOAD_ACTION
            receiptContext!!.sendBroadcast(uploadIntent)*/

            alertDialogBuilder.dismiss()
        }
        alertDialogBuilder.show()
    }

    fun loadAndParseXML(xmlString: InputStream): RJReceiptUploadConfirm {


        val pullParser = Xml.newPullParser()
        //int i=0;
        //String value="";
        var tagStart: String
        var tagValue = ""
        //boolean start_get_item_from_tag = false;
        val item = RJReceiptUploadConfirm()
        try {
            pullParser.setInput(xmlString, "utf-8")

            //利用eventType來判斷目前分析到XML是哪一個部份
            var eventType = pullParser.eventType
            //XmlPullParser.END_DOCUMENT表示已經完成分析XML
            //var item: MeetingListItem? = null
            //ArrayList<String> myArrayList = new ArrayList<>();


            while (eventType != XmlPullParser.END_DOCUMENT) {
                //i++;
                //XmlPullParser.START_TAG表示目前分析到的是XML的Tag，如<title>

                if (eventType == XmlPullParser.START_TAG) {
                    tagStart = pullParser.name
                    Log.e(mTAG, "<$tagStart>")
                    if (tagStart == "Execution") {
                        Log.i(mTAG, "=== Start of Execution ===")
                        //myArrayList.clear();
                        //item = MeetingListItem()
                    }
                }
                //XmlPullParser.TEXT表示目前分析到的是XML Tag的值，如：台南美食吃不完
                if (eventType == XmlPullParser.TEXT) {
                    tagValue = pullParser.text



                    //tv02.setText(tv02.getText() + ", " + value);
                }



                if (eventType == XmlPullParser.END_TAG) {
                    val name = pullParser.name
                    val attributeCount = pullParser.attributeCount
                    Log.e(mTAG, "value = $tagValue, attributeCount = $attributeCount" )
                    if (attributeCount == 3) {
                        item.code = pullParser.getAttributeValue(0)
                        item.sqlcode = pullParser.getAttributeValue(1)
                        item.description = pullParser.getAttributeValue(2)
                        val code = pullParser.getAttributeValue(0)
                        val sqlcode = pullParser.getAttributeValue(1)
                        val description = pullParser.getAttributeValue(2)
                        Log.e(mTAG, "code = $code, sqlcode = $sqlcode, description = $description")
                    } else if (attributeCount == 2) {
                        item.name = pullParser.getAttributeValue(0)
                        item.value = pullParser.getAttributeValue(1)
                    }
                    //myArrayList.add(tagValue);
                    Log.e(mTAG, "</$name>")

                    /*if (name != null && item != null) {

                        when (name) {
                            "room_no" -> item!!.setRoom_no(tagValue)
                            "master" -> item!!.setMaster(tagValue)
                            "emp_name" -> item!!.setEmp_name(tagValue)
                            "dept_name" -> item!!.setDept_name(tagValue)
                            "room_name" -> item!!.setRoom_name(tagValue)
                            "meeting_no" -> item!!.setMeeting_no(tagValue)
                            "start_date" -> {
                                val remove_string_start =
                                    tagValue.substring(0, tagValue.length - 6)
                                val splitter_start = remove_string_start.split("T".toRegex())
                                    .dropLastWhile { it.isEmpty() }.toTypedArray()
                                item!!.setStart_date(splitter_start[0] + " " + splitter_start[1])
                            }
                            "end_date" -> {
                                val remove_string_end = tagValue.substring(0, tagValue.length - 6)
                                val splitter_end = remove_string_end.split("T".toRegex())
                                    .dropLastWhile { it.isEmpty() }.toTypedArray()
                                item!!.setEnd_date(splitter_end[0] + " " + splitter_end[1])
                            }
                            "approver" -> item!!.setApprove(tagValue)
                            "approve_date" -> {
                                val remove_string_approve =
                                    tagValue.substring(0, tagValue.length - 6)
                                val splitter_approve = remove_string_approve.split("T".toRegex())
                                    .dropLastWhile { it.isEmpty() }.toTypedArray()
                                item!!.setApprove_date(splitter_approve[0] + " " + splitter_approve[1])
                            }
                            "subject" -> item!!.setSubject(tagValue)
                            "bad_sp" -> item!!.setBad_sp(tagValue)
                            "memo" -> item!!.setMemo(tagValue)
                            "meeting_type" -> item!!.setMeeting_type(tagValue)
                            "recorder" -> item!!.setRecorder(tagValue)
                            else -> {
                            }
                        }

                        if (name == "MEETING_LIST") {
                            Log.i(TAG, "=== End of MEETING_LIST ===")
                            AllFragment.meetingList.add(item)
                            meeting_count++
                        }
                    }*/

                }
                //analize next
                try {
                    eventType = pullParser.next()
                } catch (ep: XmlPullParserException) {
                    ep.printStackTrace()
                }

            }

        } catch (e: XmlPullParserException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return item
    }


}
