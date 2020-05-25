package com.magtonic.magtonicwarehouse

import android.app.AlertDialog
import android.content.*
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.magtonic.magtonicwarehouse.MainActivity.Companion.isEraser
import com.magtonic.magtonicwarehouse.MainActivity.Companion.penColor
import com.magtonic.magtonicwarehouse.MainActivity.Companion.penWidth
import com.magtonic.magtonicwarehouse.data.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*


class SignActivity : AppCompatActivity() {
    private val mTAG = SignActivity::class.java.name

    var progressBar: ProgressBar? = null
    private var relativeLayout: RelativeLayout? = null
    private var linearLayoutSign: LinearLayout? = null
    private var linearLayoutUpload: LinearLayout? = null



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

    private val fileUtils: FileUtils?= FileUtils()
    private var uploadSuccess: Boolean = false

    private var linearLayoutSignDetailList: LinearLayout?= null
    private var imageViewShowSignature: ImageView?=null
    private var uploadSignName: String = ""
    private var sendOrder: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign)

        val intent = this.intent
        sendOrder = intent.getStringExtra("SEND_ORDER")
        Log.e(mTAG, "sendOrder = $sendOrder")

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
            showLogoutConfirmDialog()
        }

        btnPrev!!.setOnClickListener {
            paintBoard!!.undo()
        }

        btnSignConfirm!!.setOnClickListener {
            val confirmIntent = Intent()
            confirmIntent.action = Constants.ACTION.ACTION_OUTSOURCED_PROCESS_SIGN_UPLOAD_ACTION
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
            actionBar.title = getString(R.string.nav_outsourced)

        }


        val filter: IntentFilter

        mReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action != null) {
                    if (intent.action!!.equals(Constants.ACTION.ACTION_OUTSOURCED_PROCESS_SIGN_FTP_CONNECT_TIMEOUT, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_OUTSOURCED_PROCESS_SIGN_FTP_CONNECT_TIMEOUT")
                        progressBar!!.visibility = View.GONE
                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_OUTSOURCED_PROCESS_SIGN_FTP_CONNECT_UNKNOWN_HOST, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_OUTSOURCED_PROCESS_SIGN_FTP_CONNECT_UNKNOWN_HOST")
                        progressBar!!.visibility = View.GONE
                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_OUTSOURCED_PROCESS_SIGN_FTP_CONNECT_FAILED, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_OUTSOURCED_PROCESS_SIGN_FTP_CONNECT_FAILED")
                        progressBar!!.visibility = View.GONE
                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_OUTSOURCED_PROCESS_SIGN_FTP_UPLOAD_FAILED, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_OUTSOURCED_PROCESS_SIGN_FTP_UPLOAD_FAILED")
                        progressBar!!.visibility = View.GONE
                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_OUTSOURCED_PROCESS_SIGN_FTP_UPLOAD_SUCCESS, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_OUTSOURCED_PROCESS_SIGN_FTP_UPLOAD_SUCCESS")

                        uploadSuccess = true
                        progressBar!!.visibility = View.GONE
                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_OUTSOURCED_PROCESS_SIGN_FTP_UPLOAD_COMPLETE, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_OUTSOURCED_PROCESS_SIGN_FTP_UPLOAD_COMPLETE")

                        linearLayoutSign!!.visibility = View.GONE
                        linearLayoutUpload!!.visibility = View.VISIBLE

                        val promptView = View.inflate(this@SignActivity, R.layout.fragment_receipt_item, null)

                        val sendOrderHeader = promptView.findViewById<TextView>(R.id.receiptItemDetailHeader)
                        val sendOrderContent = promptView.findViewById<TextView>(R.id.receiptItemDetailContent)
                        sendOrderHeader.text = getString(R.string.outsource_send_no)
                        sendOrderContent.text = sendOrder
                        linearLayoutSignDetailList!!.addView(promptView)

                        val headerPromptView = View.inflate(this@SignActivity, R.layout.fragment_receipt_item_header, null)
                        val barHeader = headerPromptView.findViewById<TextView>(R.id.outSourcedProcessLowerItemDetailHeader)
                        val barContent = headerPromptView.findViewById<TextView>(R.id.outSourcedProcessLowerItemDetailContentStatic)
                        val barQuantity = headerPromptView.findViewById<TextView>(R.id.outSourcedProcessLowerItemDetailContentDynamic)

                        barHeader.text = getString(R.string.outsource_part_no)
                        barContent.text = getString(R.string.outsource_part_name)
                        barQuantity.text = getString(R.string.outsource_quantity)
                        linearLayoutSignDetailList!!.addView(headerPromptView)

                        for (rjOutSourceProcessed in MainActivity.outsourcedProcessOrderList) {

                            //val outsourcedProcessDetailItem = OutsourcedProcessDetailItem(rjOutSourceProcessed.data1, rjOutSourceProcessed.data2, rjOutSourceProcessed.data3, rjOutSourceProcessed.data4,
                            //    rjOutSourceProcessed.data5, rjOutSourceProcessed.data6, rjOutSourceProcessed.data7, rjOutSourceProcessed.data8)
                            //outsourcedProcessDetailList.add(outsourcedProcessDetailItem)
                            val insidePromptView = View.inflate(this@SignActivity, R.layout.fragment_outsourced_process_sign_show_detail_item, null)

                            val itemHeader = insidePromptView.findViewById<TextView>(R.id.outSourcedProcessSignShowDetailItemHeader)
                            val itemContent = insidePromptView.findViewById<TextView>(R.id.outSourcedProcessSignShowDetailItemContent)
                            val itemQuantity = insidePromptView.findViewById<TextView>(R.id.outSourcedProcessSignShowDetailItemQuantity)

                            itemHeader.text = rjOutSourceProcessed.data3
                            itemContent.text = rjOutSourceProcessed.data6
                            itemQuantity.text = rjOutSourceProcessed.data4

                            linearLayoutSignDetailList!!.addView(insidePromptView)
                        }

                        imageViewShowSignature!!.setImageBitmap(paintBoard!!.bitmap)
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

            //filter.addAction(Constants.ACTION.ACTION_RECEIPT_ALREADY_UPLOADED_SEND_TO_FRAGMENT)
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
        format: Bitmap.CompressFormat, mimeType: String,
        displayName: String
    ): String {
        var path = ""

        val relativeLocation = Environment.DIRECTORY_PICTURES
        val contentValues = ContentValues()
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, relativeLocation)
        }
        val resolver = context.contentResolver
        var stream: OutputStream? = null
        val byteArrayOutputStream = ByteArrayOutputStream()

        var uri: Uri? = null
        try {
            val contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            uri = resolver.insert(contentUri, contentValues)

            Log.e(mTAG, "Uri = $uri")
            path = getRealPathFromURI(context, uri) as String
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


        /*val scaledHeight = 512.0 //stick height to 512
        val aspectRatio =  paintBoard!!.height / scaledHeight

        val scaledWidth = paintBoard!!.width / aspectRatio

        Log.d(mTAG, "scaledHeight = $scaledHeight, aspectRatio = $aspectRatio, scaledWidth = $scaledWidth")

        val scaledImage = Bitmap.createScaledBitmap(bitmap, scaledWidth.toInt(), scaledHeight.toInt(), false)
        */

        bitmap.compress(format, 100, byteArrayOutputStream)
        //scaledImage.compress(format, 100, byteArrayOutputStream)
        val imageByteArray = byteArrayOutputStream.toByteArray()

        Log.e(mTAG, "imageByteArray size = ${imageByteArray.size}")




        //val dataString = String(imageByteArray, Charset.forName("ISO-8859-1"))
        //Log.e(mTAG, "dataString = $dataString")
        MainActivity.base64 = Base64.encodeToString(imageByteArray, Base64.DEFAULT)
        val decodeByeArray: ByteArray = Base64.decode(MainActivity.base64, Base64.DEFAULT)
        Log.e(mTAG, "base64 size = ${MainActivity.base64.length}")

        //for (i in base64.length-10 until base64.length) {
        /*for (i in 0 until MainActivity.base64.length) {
            System.out.print(MainActivity.base64[i].toChar())
        }*/

        //Log.d(mTAG, "base64 String = ${MainActivity.base64}")

        //Log.e(mTAG, "decodeByeArray size = ${decodeByeArray.size}")

        //Log.e(mTAG, "dataString = $dataString")
        //test for save sign

        /*val testIntent = Intent()
        testIntent.action = Constants.ACTION.ACTION_OUTSOURCED_PROCESS_SIGN_UPLOAD_ACTION
        testIntent.putExtra("ID", "testID")
        testIntent.putExtra("TOPIC", "testTopic")
        testIntent.putExtra("DESCRIPTION", "testDescription")
        //testIntent.putExtra("SIGN", base64)
        signContext!!.sendBroadcast(testIntent)*/

        return path
    }

    private fun getRealPathFromURI(context: Context, contentUri: Uri?
    ): String? {

        var ret = ""

        if (contentUri != null) {
            ret = fileUtils!!.getPath(context, contentUri).toString()
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

    private fun getRealPathFromURI2(context: Context, contentUri: Uri?): String? {
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
            /*while (cursor.moveToNext()) {
                imageId = cursor.getLong(columnIndexID)
                val uriImage = Uri.withAppendedPath(contentUri , "" + imageId)
                listOfAllImages.add(uriImage)
            }*/
            cursor.close()
        }

        return ret
    }

    private fun showLogoutConfirmDialog() {

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

            val sdf = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())
            val currentDateAndTime: String = sdf.format(Date())

            //val fileName = (System.currentTimeMillis() / 1000).toString() + ".jpg"
            uploadSignName = "$currentDateAndTime.jpg"

            val scaledWidth = 320.0 //stick height to 512
            //val scaledHeight = 512.0 //stick height to 512
            val aspectRatio =  scaledWidth / paintBoard!!.width

            val scaledHeight = paintBoard!!.height * aspectRatio

            Log.d(mTAG, "scaledWidth = $scaledWidth, aspectRatio = $aspectRatio, scaledHeight = $scaledHeight")

            val scaledImage = Bitmap.createScaledBitmap(paintBoard!!.bitmap, scaledWidth.toInt(), scaledHeight.toInt(), false)

            //saveBitmap(drawContext as Context, paintBoard!!.bitmap, CompressFormat.JPEG,"image/jpeg", fileName)
            val path = saveBitmap(this@SignActivity as Context, scaledImage, Bitmap.CompressFormat.JPEG,"image/jpeg", uploadSignName)

            if (path != "")
            {
                progressBar!!.visibility = View.VISIBLE
                val ftpUtils = FTPUtils(signContext as Context,"192.1.1.121", 21, "iepftp", "T69924056Ftp", uploadSignName, path)
                val ftpTask = FtpTask()
                ftpTask.execute(ftpUtils)
            } else {
                Log.e(mTAG, "Path = null")
            }





            alertDialogBuilder.dismiss()
        }
        alertDialogBuilder.show()


    }

    //private class FtpTask : AsyncTask<FTPUtils, Void?, FTPClient>() {
    private class FtpTask : AsyncTask<FTPUtils, Void?, Context>() {

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
    }
}