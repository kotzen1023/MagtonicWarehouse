package com.magtonic.magtonicwarehouse.fragment


import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.magtonic.magtonicwarehouse.MainActivity.Companion.base64
import com.magtonic.magtonicwarehouse.R
import com.magtonic.magtonicwarehouse.data.Constants
import com.magtonic.magtonicwarehouse.data.PaintBoard
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.OutputStream


class DrawFragment : Fragment() {
    private val mTAG = DrawFragment::class.java.name
    private var drawContext: Context? = null


    private var paintBoard: PaintBoard?= null
    private var btnClear: Button?= null
    private var btnSave: Button?=null
    private var btnPrev: Button?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        drawContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Log.d(mTAG, "onCreateView")



        val view = inflater.inflate(R.layout.fragment_draw, container, false)
        paintBoard = view.findViewById(R.id.viewPaint)
        btnClear = view.findViewById(R.id.btnClear)
        btnSave = view.findViewById(R.id.btnSave)
        btnPrev = view.findViewById(R.id.btnPrev)

        btnClear!!.setOnClickListener {
            paintBoard!!.clear()
        }

        btnSave!!.setOnClickListener {
            showLogoutConfirmDialog()
        }

        btnPrev!!.setOnClickListener {
            paintBoard!!.undo()
        }


        return view
    }

    override fun onDestroyView() {
        Log.i(mTAG, "onDestroy")



        super.onDestroyView()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        Log.i(mTAG, "onActivityCreated")
        super.onActivityCreated(savedInstanceState)

    }

    /*fun saveBitmap(stream: OutputStream) {
        paintBoard!!.bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
    }*/

    /*fun save() {
        try {
            val fileName = (System.currentTimeMillis() / 1000).toString() + ".jpg"
            val file = File(getExternalFilesDir)
            //val file = File(Environment.getExternalStorageDirectory(), fileName)
            val stream = FileOutputStream(file)
            saveBitmap(stream)
            stream.close()

            val intent = Intent()
            intent.setAction(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            intent.setData(Uri.fromFile(Environment.getExternalStorageDirectory()))
            sendBroadcast(intent)

            Log.e(mTAG, "Save Success")

        } catch(e:Exception) {
            println(e)
            Log.e(mTAG, "Save Failed")

        }
    }*/

    @Throws(IOException::class)
    private fun saveBitmap(
        context: Context, bitmap: Bitmap,
        format: CompressFormat, mimeType: String,
        displayName: String
    ) {
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
            val path = getRealPathFromURI(context, uri)
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
        base64 = Base64.encodeToString(imageByteArray, Base64.DEFAULT)
        val decodeByeArray: ByteArray = Base64.decode(base64, Base64.DEFAULT)
        Log.e(mTAG, "base64 size = ${base64.length}")

        //for (i in base64.length-10 until base64.length) {
        for (i in 0 until base64.length) {
            System.out.print(base64[i].toChar())
        }

        Log.d(mTAG, "base64 String = ${base64}")

        Log.e(mTAG, "decodeByeArray size = ${decodeByeArray.size}")

        //Log.e(mTAG, "dataString = $dataString")
        //test for save sign
        val testIntent = Intent()
        testIntent.action = Constants.ACTION.ACTION_OUTSOURCED_PROCESS_SIGN_UPLOAD_ACTION
        testIntent.putExtra("ID", "testID")
        testIntent.putExtra("TOPIC", "testTopic")
        testIntent.putExtra("DESCRIPTION", "testDescription")
        //testIntent.putExtra("SIGN", base64)
        drawContext!!.sendBroadcast(testIntent)
    }

    private fun getRealPathFromURI(context: Context, contentUri: Uri?
    ): String? {
        var cursor: Cursor? = null
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
        }
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
        val promptView = View.inflate(drawContext, R.layout.confirm_dialog, null)

        val alertDialogBuilder = AlertDialog.Builder(drawContext).create()
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
            val fileName = (System.currentTimeMillis() / 1000).toString() + ".jpg"

            val scaledWidth = 320.0 //stick height to 512
            //val scaledHeight = 512.0 //stick height to 512
            val aspectRatio =  scaledWidth / paintBoard!!.width

            val scaledHeight = paintBoard!!.height * aspectRatio

            Log.d(mTAG, "scaledWidth = $scaledWidth, aspectRatio = $aspectRatio, scaledHeight = $scaledHeight")

            val scaledImage = Bitmap.createScaledBitmap(paintBoard!!.bitmap, scaledWidth.toInt(), scaledHeight.toInt(), false)

            //saveBitmap(drawContext as Context, paintBoard!!.bitmap, CompressFormat.JPEG,"image/jpeg", fileName)
            saveBitmap(drawContext as Context, scaledImage, CompressFormat.JPEG,"image/jpeg", fileName)
            alertDialogBuilder.dismiss()
        }
        alertDialogBuilder.show()


    }


}




