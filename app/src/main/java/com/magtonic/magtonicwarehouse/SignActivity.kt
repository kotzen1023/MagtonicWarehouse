package com.magtonic.magtonicwarehouse

import android.app.AlertDialog
import android.content.*

import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore

import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import com.magtonic.magtonicwarehouse.MainActivity.Companion.ftp_ip_address
import com.magtonic.magtonicwarehouse.MainActivity.Companion.isEraser
import com.magtonic.magtonicwarehouse.MainActivity.Companion.penColor
import com.magtonic.magtonicwarehouse.MainActivity.Companion.penWidth
import com.magtonic.magtonicwarehouse.data.Constants
import com.magtonic.magtonicwarehouse.data.FTPUtils
import com.magtonic.magtonicwarehouse.data.FileUtils
import com.magtonic.magtonicwarehouse.data.PaintBoard
import kotlinx.coroutines.*
import org.xmlpull.v1.XmlPullParserException


import java.io.IOException
import java.io.OutputStream



import kotlin.coroutines.CoroutineContext


class SignActivity : AppCompatActivity() {
    private val mTAG = SignActivity::class.java.name

    var progressBar: ProgressBar? = null
    private var relativeLayout: RelativeLayout? = null
    private var linearLayoutSign: LinearLayout? = null
    private var linearLayoutUpload: LinearLayout? = null
    private var toastHandle: Toast? = null


    private var signContext: Context? = null


    private var mReceiver: BroadcastReceiver? = null
    private var isRegister = false

    private var menuItemEraser: MenuItem? = null
    //private var isEraser: Boolean = false
    //private var penColor: Int = Color.BLACK
    //private var penWidth: Float = 10f

    private var paintBoard: PaintBoard?= null
    private var btnClear: Button?= null
    private var btnSave: Button?=null
    private var btnPrev: Button?=null
    private var btnSignConfirm: Button?=null

    private val fileUtils: FileUtils = FileUtils()
    private var uploadSuccess: Boolean = false

    private var linearLayoutSignDetailList: LinearLayout?= null
    private var imageViewShowSignature: ImageView?=null
    private var uploadSignName: String = ""
    private var sendOrder: String = ""
    private var title: String = ""
    private var sendFragment: String = ""
    private var warehouse: String = ""
    private var type: String = ""
    private var date: String = ""

    private var signImageUriPath: Uri ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign)

        val intent = this.intent
        sendOrder = intent.getStringExtra("SEND_ORDER") as String
        title = intent.getStringExtra("TITLE") as String
        sendFragment = intent.getStringExtra("SEND_FRAGMENT") as String
        try {
            warehouse = intent.getStringExtra("WAREHOUSE") as String
        } catch (ep: NullPointerException) {
            ep.printStackTrace()
        }

        if (intent.getStringExtra("TYPE") != null) {
            type = intent.getStringExtra("TYPE") as String
        }

        if (intent.getStringExtra("DATE") != null) {
            date = intent.getStringExtra("DATE") as String
        }



        Log.e(mTAG, "sendOrder = $sendOrder")
        Log.e(mTAG, "title = $title")
        Log.e(mTAG, "sendFragment = $sendFragment")
        Log.e(mTAG, "warehouse = $warehouse")

        signContext = applicationContext

        relativeLayout = findViewById(R.id.sign_container)
        linearLayoutSign = findViewById(R.id.linearLayoutSign)
        linearLayoutUpload = findViewById(R.id.linearLayoutUpload)

        linearLayoutSignDetailList = findViewById(R.id.linearLayoutSignDetailList)
        imageViewShowSignature = findViewById(R.id.imageViewShowSignature)

        progressBar = ProgressBar(signContext, null, android.R.attr.progressBarStyleLarge)
        val params = RelativeLayout.LayoutParams(MainActivity.screenHeight / 4, MainActivity.screenWidth / 4)
        params.addRule(RelativeLayout.CENTER_IN_PARENT)

        val localRelativeLayout: RelativeLayout? = relativeLayout
        if (localRelativeLayout != null) {
            localRelativeLayout.addView(progressBar, params)
        } else {
            Log.e(mTAG, "localRelativeLayout = null")
        }
        progressBar!!.visibility = View.GONE

        paintBoard = findViewById(R.id.signViewPaint)
        btnClear = findViewById(R.id.signBtnClear)
        btnSave = findViewById(R.id.signBtnSave)
        btnPrev = findViewById(R.id.signBtnPrev)
        btnSignConfirm = findViewById(R.id.signConfirm)

        btnClear!!.setOnClickListener {
            paintBoard!!.clear()
        }

        btnSave!!.setOnClickListener {
            showSignUploadDialog()
        }

        btnPrev!!.setOnClickListener {
            paintBoard!!.undo()
        }

        btnSignConfirm!!.setOnClickListener {
            progressBar!!.visibility = View.VISIBLE

            val confirmIntent = Intent()

            when (sendFragment) {
                "OUTSOURCED_PROCESS" -> {
                    confirmIntent.action = Constants.ACTION.ACTION_OUTSOURCED_PROCESS_SIGN_UPLOAD_ACTION
                }
                "RETURN_OF_GOODS" -> {
                    confirmIntent.action = Constants.ACTION.ACTION_RETURN_OF_GOODS_SIGN_UPLOAD_ACTION
                }
            }


            confirmIntent.putExtra("SEND_ORDER", sendOrder)
            confirmIntent.putExtra("SIGN_FILE_NAME", uploadSignName)
            signContext!!.sendBroadcast(confirmIntent)
        }

        //for action bar
        val actionBar: androidx.appcompat.app.ActionBar? = supportActionBar

        if (actionBar != null) {

            actionBar.setDisplayUseLogoEnabled(true)
            actionBar.setDisplayShowHomeEnabled(true)
            actionBar.setDisplayHomeAsUpEnabled(true)
            //actionBar.title = getString(R.string.nav_outsourced)
            actionBar.title = title
        }


        val filter: IntentFilter

        mReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action != null) {
                    when {
                        intent.action!!.equals(Constants.ACTION.ACTION_OUTSOURCED_PROCESS_SIGN_FTP_CONNECT_TIMEOUT, ignoreCase = true) -> {
                            Log.d(mTAG, "ACTION_OUTSOURCED_PROCESS_SIGN_FTP_CONNECT_TIMEOUT")
                            progressBar!!.visibility = View.GONE
                            toast(getString(R.string.connect_timeout))
                        }
                        intent.action!!.equals(Constants.ACTION.ACTION_OUTSOURCED_PROCESS_SIGN_FTP_CONNECT_UNKNOWN_HOST, ignoreCase = true) -> {
                            Log.d(mTAG, "ACTION_OUTSOURCED_PROCESS_SIGN_FTP_CONNECT_UNKNOWN_HOST")
                            progressBar!!.visibility = View.GONE
                            toast(getString(R.string.toast_server_error))
                        }
                        intent.action!!.equals(Constants.ACTION.ACTION_OUTSOURCED_PROCESS_SIGN_FTP_CONNECT_FAILED, ignoreCase = true) -> {
                            Log.d(mTAG, "ACTION_OUTSOURCED_PROCESS_SIGN_FTP_CONNECT_FAILED")
                            progressBar!!.visibility = View.GONE
                            toast(getString(R.string.ftp_connect_error))
                        }
                        intent.action!!.equals(Constants.ACTION.ACTION_OUTSOURCED_PROCESS_SIGN_FTP_UPLOAD_FAILED, ignoreCase = true) -> {
                            Log.d(mTAG, "ACTION_OUTSOURCED_PROCESS_SIGN_FTP_UPLOAD_FAILED")
                            progressBar!!.visibility = View.GONE

                            toast(getString(R.string.outsourced_process_sign_upload_failed))
                        }
                        intent.action!!.equals(Constants.ACTION.ACTION_OUTSOURCED_PROCESS_SIGN_FTP_UPLOAD_SUCCESS, ignoreCase = true) -> {
                            Log.d(mTAG, "ACTION_OUTSOURCED_PROCESS_SIGN_FTP_UPLOAD_SUCCESS")

                            uploadSuccess = true
                            //progressBar!!.visibility = View.GONE

                            when (sendFragment) {
                                "OUTSOURCED_PROCESS" -> {
                                    toast(getString(R.string.outsourced_process_sign_upload_success))
                                    btnSignConfirm!!.text = getString(R.string.outsourced_process_send_order_sign_confirm)
                                }
                                "RETURN_OF_GOODS" -> {
                                    toast(getString(R.string.return_of_goods_sign_upload_success))
                                    btnSignConfirm!!.text = getString(R.string.return_of_goods_order_sign_confirm)
                                }
                            }


                        }
                        intent.action!!.equals(Constants.ACTION.ACTION_OUTSOURCED_PROCESS_SIGN_FTP_UPLOAD_COMPLETE, ignoreCase = true) -> {
                            Log.d(mTAG, "ACTION_OUTSOURCED_PROCESS_SIGN_FTP_UPLOAD_COMPLETE")

                            //delete image

                            deleteImage()
                            /*if (!ret) {
                                            Log.e(mTAG, "Delete $signImageAbsolutePath success!")
                                        } else {
                                            Log.e(mTAG, "delete $signImageAbsolutePath failed")
                                        }*/

                            progressBar!!.visibility = View.GONE

                            linearLayoutSign!!.visibility = View.GONE
                            linearLayoutUpload!!.visibility = View.VISIBLE

                            val promptView = View.inflate(this@SignActivity, R.layout.fragment_receipt_item, null)

                            val sendOrderHeader = promptView.findViewById<TextView>(R.id.receiptItemDetailHeader)
                            val sendOrderContent = promptView.findViewById<TextView>(R.id.receiptItemDetailContent)

                            when (sendFragment) {
                                "OUTSOURCED_PROCESS" -> {
                                    sendOrderHeader.text = getString(R.string.outsource_send_no)
                                }
                                "RETURN_OF_GOODS" -> {
                                    sendOrderHeader.text = getString(R.string.return_of_goods_dialog_order_header)
                                }
                            }


                            sendOrderContent.text = sendOrder
                            linearLayoutSignDetailList!!.addView(promptView)

                            when (sendFragment) {
                                "OUTSOURCED_PROCESS" -> {
                                    val headerPromptView = View.inflate(this@SignActivity, R.layout.fragment_receipt_item_header, null)
                                    val barHeader = headerPromptView.findViewById<TextView>(R.id.outSourcedProcessLowerItemDetailHeader)
                                    val barContent = headerPromptView.findViewById<TextView>(R.id.outSourcedProcessLowerItemDetailContentStatic)
                                    val barQuantity = headerPromptView.findViewById<TextView>(R.id.outSourcedProcessLowerItemDetailContentDynamic)

                                    barHeader.text = getString(R.string.outsource_part_no)
                                    barContent.text = getString(R.string.outsource_part_name)
                                    barQuantity.text = getString(R.string.outsource_quantity)
                                    linearLayoutSignDetailList!!.addView(headerPromptView)
                                }
                                "RETURN_OF_GOODS" -> {

                                    val typePromptView = View.inflate(this@SignActivity, R.layout.fragment_receipt_item, null)
                                    val datePromptView = View.inflate(this@SignActivity, R.layout.fragment_receipt_item, null)

                                    val typeHeader = typePromptView.findViewById<TextView>(R.id.receiptItemDetailHeader)
                                    val typeContent = typePromptView.findViewById<TextView>(R.id.receiptItemDetailContent)

                                    val dateHeader = datePromptView.findViewById<TextView>(R.id.receiptItemDetailHeader)
                                    val dateContent = datePromptView.findViewById<TextView>(R.id.receiptItemDetailContent)

                                    typeHeader.text = getString(R.string.return_of_goods_dialog_type_header)
                                    typeContent.text = type

                                    dateHeader.text = getString(R.string.return_of_goods_dialog_date_header)
                                    dateContent.text = date

                                    linearLayoutSignDetailList!!.addView(typePromptView)
                                    linearLayoutSignDetailList!!.addView(datePromptView)
                                }
                            }

                            when (sendFragment) {
                                "OUTSOURCED_PROCESS" -> {
                                    for (rjOutSourceProcessed in MainActivity.outsourcedProcessOrderList) {

                                        if (rjOutSourceProcessed.data9 == warehouse) {
                                            val insidePromptView = View.inflate(
                                                this@SignActivity,
                                                R.layout.fragment_outsourced_process_sign_show_detail_item,
                                                null
                                            )

                                            val itemHeader =
                                                insidePromptView.findViewById<TextView>(R.id.outSourcedProcessSignShowDetailItemHeader)
                                            val itemContent =
                                                insidePromptView.findViewById<TextView>(R.id.outSourcedProcessSignShowDetailItemContent)
                                            val itemQuantity =
                                                insidePromptView.findViewById<TextView>(R.id.outSourcedProcessSignShowDetailItemQuantity)

                                            itemHeader.text = rjOutSourceProcessed.data3
                                            itemContent.text = rjOutSourceProcessed.data6
                                            itemQuantity.text = rjOutSourceProcessed.data4

                                            linearLayoutSignDetailList!!.addView(insidePromptView)
                                        }
                                    }
                                }
                                "RETURN_OF_GOODS" -> {
                                    val numPromptView = View.inflate(this@SignActivity, R.layout.fragment_receipt_item, null)
                                    val buyOrderPromptView = View.inflate(this@SignActivity, R.layout.fragment_receipt_item, null)
                                    val buyNumPromptView = View.inflate(this@SignActivity, R.layout.fragment_receipt_item, null)
                                    val partNoPromptView = View.inflate(this@SignActivity, R.layout.fragment_receipt_item, null)
                                    val quantityPromptView = View.inflate(this@SignActivity, R.layout.fragment_receipt_item, null)
                                    val unitPromptView = View.inflate(this@SignActivity, R.layout.fragment_receipt_item, null)
                                    val namePromptView = View.inflate(this@SignActivity, R.layout.fragment_receipt_item, null)
                                    val specPromptView = View.inflate(this@SignActivity, R.layout.fragment_receipt_item, null)

                                    val numHeader = numPromptView.findViewById<TextView>(R.id.receiptItemDetailHeader)
                                    val numContent = numPromptView.findViewById<TextView>(R.id.receiptItemDetailContent)

                                    val buyOrderHeader = buyOrderPromptView.findViewById<TextView>(R.id.receiptItemDetailHeader)
                                    val buyOrderContent = buyOrderPromptView.findViewById<TextView>(R.id.receiptItemDetailContent)

                                    val buyNumHeader = buyNumPromptView.findViewById<TextView>(R.id.receiptItemDetailHeader)
                                    val buyNumContent = buyNumPromptView.findViewById<TextView>(R.id.receiptItemDetailContent)

                                    val partNoHeader = partNoPromptView.findViewById<TextView>(R.id.receiptItemDetailHeader)
                                    val partNoContent = partNoPromptView.findViewById<TextView>(R.id.receiptItemDetailContent)

                                    val quantityHeader = quantityPromptView.findViewById<TextView>(R.id.receiptItemDetailHeader)
                                    val quantityContent = quantityPromptView.findViewById<TextView>(R.id.receiptItemDetailContent)

                                    val unitHeader = unitPromptView.findViewById<TextView>(R.id.receiptItemDetailHeader)
                                    val unitContent = unitPromptView.findViewById<TextView>(R.id.receiptItemDetailContent)

                                    val nameHeader = namePromptView.findViewById<TextView>(R.id.receiptItemDetailHeader)
                                    val nameContent = namePromptView.findViewById<TextView>(R.id.receiptItemDetailContent)

                                    val specHeader = specPromptView.findViewById<TextView>(R.id.receiptItemDetailHeader)
                                    val specContent = specPromptView.findViewById<TextView>(R.id.receiptItemDetailContent)

                                    numHeader.text = "退貨項次"
                                    numContent.text = MainActivity.returnOfGoodsDetailList[0].data1

                                    buyOrderHeader.text = "採購單號"
                                    buyOrderContent.text = MainActivity.returnOfGoodsDetailList[0].data2

                                    buyNumHeader.text = "採購單項次"
                                    buyNumContent.text = MainActivity.returnOfGoodsDetailList[0].data3

                                    partNoHeader.text = "料件編號"
                                    partNoContent.text = MainActivity.returnOfGoodsDetailList[0].data4

                                    quantityHeader.text = "數量"
                                    quantityContent.text = MainActivity.returnOfGoodsDetailList[0].data5

                                    unitHeader.text = "單位"
                                    unitContent.text = MainActivity.returnOfGoodsDetailList[0].data6

                                    nameHeader.text = "品名"
                                    nameContent.text = MainActivity.returnOfGoodsDetailList[0].data7

                                    specHeader.text = "規格"
                                    specContent.text = MainActivity.returnOfGoodsDetailList[0].data8

                                    linearLayoutSignDetailList!!.addView(numPromptView)
                                    linearLayoutSignDetailList!!.addView(buyOrderPromptView)
                                    linearLayoutSignDetailList!!.addView(buyNumPromptView)
                                    linearLayoutSignDetailList!!.addView(partNoPromptView)
                                    linearLayoutSignDetailList!!.addView(quantityPromptView)
                                    linearLayoutSignDetailList!!.addView(unitPromptView)
                                    linearLayoutSignDetailList!!.addView(namePromptView)
                                    linearLayoutSignDetailList!!.addView(specPromptView)
                                }
                            }



                            imageViewShowSignature!!.setImageBitmap(paintBoard!!.bitmap)
                        }
                        intent.action!!.equals(Constants.ACTION.ACTION_OUTSOURCED_PROCESS_SIGN_UPLOAD_FAILED, ignoreCase = true) -> {
                            Log.d(mTAG, "ACTION_OUTSOURCED_PROCESS_SIGN_UPLOAD_FAILED")

                            progressBar!!.visibility = View.GONE
                            toast(getString(R.string.outsourced_process_sign_upload_confirm_failed))
                        }
                        intent.action!!.equals(Constants.ACTION.ACTION_OUTSOURCED_PROCESS_SIGN_UPLOAD_SUCCESS, ignoreCase = true) -> {
                            Log.d(mTAG, "ACTION_OUTSOURCED_PROCESS_SIGN_UPLOAD_SUCCESS")



                            progressBar!!.visibility = View.GONE
                            finish()
                        }
                        intent.action!!.equals(Constants.ACTION.ACTION_RETURN_OF_GOODS_SIGN_UPLOAD_FAILED, ignoreCase = true) -> {
                            Log.d(mTAG, "ACTION_RETURN_OF_GOODS_SIGN_UPLOAD_FAILED")

                            progressBar!!.visibility = View.GONE
                            toast(getString(R.string.outsourced_process_sign_upload_confirm_failed))
                        }
                        intent.action!!.equals(Constants.ACTION.ACTION_RETURN_OF_GOODS_SIGN_UPLOAD_SUCCESS, ignoreCase = true) -> {
                            Log.d(mTAG, "ACTION_RETURN_OF_GOODS_SIGN_UPLOAD_SUCCESS")



                            progressBar!!.visibility = View.GONE
                            finish()
                        }
                    }

                }
            }
        }

        if (!isRegister) {
            filter = IntentFilter()
            filter.addAction(Constants.ACTION.ACTION_OUTSOURCED_PROCESS_SIGN_FTP_CONNECT_TIMEOUT)
            filter.addAction(Constants.ACTION.ACTION_OUTSOURCED_PROCESS_SIGN_FTP_CONNECT_UNKNOWN_HOST)
            filter.addAction(Constants.ACTION.ACTION_OUTSOURCED_PROCESS_SIGN_FTP_CONNECT_FAILED)
            filter.addAction(Constants.ACTION.ACTION_OUTSOURCED_PROCESS_SIGN_FTP_UPLOAD_FAILED)
            filter.addAction(Constants.ACTION.ACTION_OUTSOURCED_PROCESS_SIGN_FTP_UPLOAD_SUCCESS)
            filter.addAction(Constants.ACTION.ACTION_OUTSOURCED_PROCESS_SIGN_FTP_UPLOAD_COMPLETE)

            filter.addAction(Constants.ACTION.ACTION_OUTSOURCED_PROCESS_SIGN_UPLOAD_FAILED)
            filter.addAction(Constants.ACTION.ACTION_OUTSOURCED_PROCESS_SIGN_UPLOAD_SUCCESS)

            filter.addAction(Constants.ACTION.ACTION_RETURN_OF_GOODS_SIGN_UPLOAD_FAILED)
            filter.addAction(Constants.ACTION.ACTION_RETURN_OF_GOODS_SIGN_UPLOAD_SUCCESS)
            signContext?.registerReceiver(mReceiver, filter)
            isRegister = true
            Log.d(mTAG, "registerReceiver mReceiver")
        }
    }

    override fun onDestroy() {
        Log.i(mTAG, "onDestroy")

        if (isRegister && mReceiver != null) {
            try {
                signContext!!.unregisterReceiver(mReceiver)
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

        //showExitConfirmDialog()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.sign, menu)

        menuItemEraser = menu.findItem(R.id.sign_draw_pen_or_eraser)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            android.R.id.home-> {
                finish()
            }

            R.id.sign_draw_pen_or_eraser-> {
                Log.e(mTAG, "sign_draw_pen_or_eraser: $isEraser")

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
        }


        return true
    }

    @Throws(IOException::class)
    private fun saveBitmap(
        context: Context, bitmap: Bitmap,
        //format: Bitmap.CompressFormat, mimeType: String,
        displayName: String
    ): String {
        val path: String
        val format = Bitmap.CompressFormat.JPEG
        val mimeType = "image/jpeg"

        val relativeLocation = Environment.DIRECTORY_PICTURES
        val contentValues = ContentValues()
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, relativeLocation)
        }
        val resolver = context.contentResolver
        var stream: OutputStream? = null
        //val byteArrayOutputStream = ByteArrayOutputStream()

        var uri: Uri? = null
        try {
            val contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            uri = resolver.insert(contentUri, contentValues)

            signImageUriPath = uri

            Log.e(mTAG, "Uri = $uri")
            path = getRealPathFromURI(context, uri)


            //val path2 = getRealPathFromURI2(context, uri)
            Log.e(mTAG, "path = $path")


            //Log.e(mTAG, "path2 = $path2")
            if (uri == null) {
                throw IOException("Failed to create new MediaStore record.")
            }
            stream = resolver.openOutputStream(uri)

            if (stream == null) {
                throw IOException("Failed to get output stream.")
            }
            val ret =bitmap.compress(format, 100, stream)
            if (!ret) {
                throw IOException("Failed to save bitmap.")
            }
        } catch (e: IOException) {
            if (uri != null) {
                resolver.delete(uri, null, null)
            }
            throw e
        } finally {
            stream?.close()
        }


        /*
        bitmap.compress(format, 100, byteArrayOutputStream)
        //scaledImage.compress(format, 100, byteArrayOutputStream)
        val imageByteArray = byteArrayOutputStream.toByteArray()

        Log.e(mTAG, "imageByteArray size = ${imageByteArray.size}")

        MainActivity.base64 = Base64.encodeToString(imageByteArray, Base64.DEFAULT)
        val decodeByeArray: ByteArray = Base64.decode(MainActivity.base64, Base64.DEFAULT)
        Log.e(mTAG, "base64 size = ${MainActivity.base64.length}")
        */

        return path
    }

    private fun deleteImage() {

        val resolver = signContext!!.contentResolver
        val ret = resolver.delete (signImageUriPath as Uri,null ,null )

        Log.e(mTAG, "ret = $ret")
    }

    private fun getRealPathFromURI(context: Context, contentUri: Uri?
    ): String {

        var ret = ""

        if (contentUri != null) {
            ret = fileUtils.getPath(context, contentUri).toString()
        }
        /*var cursor: Cursor? = null
        return try {
            //val proj = arrayOf(MediaStore.Images.Media.DATA)
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            cursor = context.contentResolver.query(contentUri!!, proj, null, null, null)
            val column_index: Int = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            cursor.getString(column_index)
        } finally {
            if (cursor != null) {
                cursor.close()
            }
        }*/
        return ret
    }

    /*private fun getRealPathFromURI2(context: Context, contentUri: Uri?): String? {
        val cursor: Cursor?
        val columnIndexID: Int
        val listOfAllImages: MutableList<Uri> = mutableListOf()
        val projection = arrayOf(MediaStore.Images.Media._ID)
        var imageId: Long
        var ret = ""
        cursor = context.contentResolver.query(contentUri as Uri, projection, null, null, null)
        if (cursor != null) {
            columnIndexID = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            ret = cursor.getString(columnIndexID)
            Log.e(mTAG, "getRealPathFromURI2 = $ret")

            cursor.close()
        }

        return ret
    }*/

    private fun showSignUploadDialog() {

        // get prompts.xml view
        /*LayoutInflater layoutInflater = LayoutInflater.from(Nfc_read_app.this);
        View promptView = layoutInflater.inflate(R.layout.input_dialog, null);*/
        val promptView = View.inflate(this@SignActivity, R.layout.confirm_dialog, null)

        val alertDialogBuilder = AlertDialog.Builder(this@SignActivity).create()
        alertDialogBuilder.setView(promptView)

        //final EditText editFileName = (EditText) promptView.findViewById(R.id.editFileName);
        val textViewMsg = promptView.findViewById<TextView>(R.id.textViewDialog)
        val btnCancel = promptView.findViewById<Button>(R.id.btnDialogCancel)
        val btnConfirm = promptView.findViewById<Button>(R.id.btnDialogConfirm)


        textViewMsg.text = getString(R.string.draw_save_title)
        btnCancel.text = getString(R.string.cancel)
        btnConfirm.text = getString(R.string.confirm)


        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
        btnCancel!!.setOnClickListener {

            alertDialogBuilder.dismiss()
        }
        btnConfirm!!.setOnClickListener {

            /*val sdf = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())
            val currentDateAndTime: String = sdf.format(Date())
            uploadSignName = "$currentDateAndTime.jpg"*/
            //uploadSignName = "$sendOrder.jpg"

            val scaledWidth = 320.0 //stick height to 512
            //val scaledHeight = 512.0 //stick height to 512
            val aspectRatio =  scaledWidth / paintBoard!!.width

            val scaledHeight = paintBoard!!.height * aspectRatio

            Log.d(mTAG, "scaledWidth = $scaledWidth, aspectRatio = $aspectRatio, scaledHeight = $scaledHeight")

            val scaledImage = Bitmap.createScaledBitmap(paintBoard!!.bitmap, scaledWidth.toInt(), scaledHeight.toInt(), false)

            //saveBitmap(drawContext as Context, paintBoard!!.bitmap, CompressFormat.JPEG,"image/jpeg", fileName)


            when (sendFragment) {
                "OUTSOURCED_PROCESS" -> {
                    uploadSignName = "$sendOrder$warehouse.jpg"
                }
                "RETURN_OF_GOODS" -> {
                    uploadSignName = "$sendOrder.jpg"
                }
            }
            val path = saveBitmap(this@SignActivity as Context, scaledImage,  uploadSignName)

            Log.e(mTAG, "===>$path")

            if (path != "")
            {
                progressBar!!.visibility = View.VISIBLE

                when (sendFragment) {
                    "OUTSOURCED_PROCESS" -> {
                        Log.e(mTAG, "->OUTSOURCED_PROCESS")
                        val ftpUtils = FTPUtils(signContext as Context,
                            ftp_ip_address, Constants.FtpInfo.PORT, Constants.FtpInfo.OUTSOURCED_USER, Constants.FtpInfo.OUTSOURCED_PASSWORD, uploadSignName, path)
                        val coroutineFtp = Presenter(ftpUtils)
                        coroutineFtp.execute()
                    }
                    "RETURN_OF_GOODS" -> {
                        Log.e(mTAG, "->RETURN_OF_GOODS")
                        val ftpUtils = FTPUtils(signContext as Context,
                            ftp_ip_address, Constants.FtpInfo.PORT, Constants.FtpInfo.RETURN_OF_GOODS_USER, Constants.FtpInfo.RETURN_OF_GOODS_PASSWORD, uploadSignName, path)
                        val coroutineFtp = Presenter(ftpUtils)
                        coroutineFtp.execute()
                    }
                }

                /*val ftpUtils = FTPUtils(signContext as Context,
                    Constants.FtpInfo.IP_ADDRESS, Constants.FtpInfo.PORT, Constants.FtpInfo.OUTSOURCED_USER, Constants.FtpInfo.OUTSOURCED_PASSWORD, uploadSignName, path)
                val coroutineFtp = Presenter(ftpUtils)
                coroutineFtp.execute()
                */
                //val ftpTask = FtpTask()
                //ftpTask.execute(ftpUtils)
            } else {
                Log.e(mTAG, "Path = null")
            }





            alertDialogBuilder.dismiss()
        }
        alertDialogBuilder.show()


    }

    private fun toast(message: String) {

        if (toastHandle != null)
            toastHandle!!.cancel()

        val toast = Toast.makeText(signContext, HtmlCompat.fromHtml("<h1>$message</h1>", HtmlCompat.FROM_HTML_MODE_COMPACT), Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER_HORIZONTAL or Gravity.CENTER_VERTICAL, 0, 0)

        /*val toast = Toast.makeText(signContext, message, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER_HORIZONTAL or Gravity.CENTER_VERTICAL, 0, 0)
        val group = toast.view as ViewGroup
        val textView = group.getChildAt(0) as TextView
        textView.textSize = 30.0f*/
        toast.show()

        toastHandle = toast
    }

    //private class FtpTask : AsyncTask<FTPUtils, Void?, FTPClient>() {
    /*private class FtpTask : AsyncTask<FTPUtils, Void?, Context>() {

        override fun onPreExecute() {


            super.onPreExecute()
        }

        override fun onPostExecute(context: Context) {
            Log.v("FTPTask", "task complete")

            val completeIntent = Intent()
            completeIntent.action = Constants.ACTION.ACTION_OUTSOURCED_PROCESS_SIGN_FTP_UPLOAD_COMPLETE
            context.sendBroadcast(completeIntent)

            //Where ftpClient is a instance variable in the main activity

        }

        override fun doInBackground(vararg params: FTPUtils?): Context {
            if (params[0] != null) {
                params[0]!!.uploadFile()
            }

            return params[0]!!.mContext as Context
        }
    }*/

    private class Presenter(ftpUtils: FTPUtils) : CoroutineScope {
        private var job: Job = Job()

        override val coroutineContext: CoroutineContext
            get() = Dispatchers.Main + job // to run code in Main(UI) Thread

        private var ftpUtils: FTPUtils ?= null
        private var isUploadSuccess = false
        init {
            this.ftpUtils =  ftpUtils
        }


        // call this method to cancel a coroutine when you don't need it anymore,
        // e.g. when user closes the screen

        /*fun cancel() {
            job.cancel()
        }*/

        fun execute() = launch {
            onPreExecute()
            //val result = doInBackground() // runs in background thread without blocking the Main Thread
            doInBackground() // runs in background thread without blocking the Main Thread
            onPostExecute()
        }

        private suspend fun doInBackground(): String = withContext(Dispatchers.IO) { // to run code in Background Thread
            // do async work

            isUploadSuccess = ftpUtils!!.uploadFile()
            delay(1000) // simulate async work
            return@withContext "SomeResult"
        }

        // Runs on the Main(UI) Thread
        private fun onPreExecute() {
            Log.e("FTPTask", "task start")
            // show progress
        }

        // Runs on the Main(UI) Thread
        //private fun onPostExecute(result: String) {
        private fun onPostExecute() {
            // hide progress
            Log.e("FTPTask", "task complete, isUploadSuccess = $isUploadSuccess")

            if (isUploadSuccess) {

                val outsourcedCompleteIntent = Intent()
                outsourcedCompleteIntent.action = Constants.ACTION.ACTION_OUTSOURCED_PROCESS_SIGN_FTP_UPLOAD_COMPLETE
                ftpUtils!!.mContext!!.sendBroadcast(outsourcedCompleteIntent)
            }

        }
    }
}