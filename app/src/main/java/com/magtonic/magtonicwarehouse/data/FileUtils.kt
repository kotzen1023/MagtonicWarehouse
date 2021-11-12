package com.magtonic.magtonicwarehouse.data

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build

import android.provider.DocumentsContract
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import androidx.annotation.RequiresApi
import com.magtonic.magtonicwarehouse.persistence.SupplierData
import org.apache.commons.io.IOUtils
import org.w3c.dom.Document
import org.w3c.dom.Element
import java.io.*

import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException
import javax.xml.transform.OutputKeys
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerException
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import android.util.Xml

import org.xmlpull.v1.XmlPullParser








class FileUtils {
    private val mTAG = FileUtils::class.java.name

    /* Get uri related content real local file path. */
    fun getPath(ctx: Context, uri: Uri): String? {
        return try {
            getUriRealPathAboveKitkat(ctx, uri)
            /*if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                // Android OS below sdk version 19
                getRealPath(ctx.getContentResolver(), uri, null)

            } else {
                // Android OS above sdk version 19.
                getUriRealPathAboveKitkat(ctx, uri)
            }*/
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d(mTAG, "FilePath Catch: $e")
            getFilePathFromURI(ctx, uri)
        }
    }

    private fun getFilePathFromURI(context: Context, contentUri: Uri): String? {
        //copy file and send new file path
        val fileName = getFileName(contentUri)
        if (!TextUtils.isEmpty(fileName)) {
            //val TEMP_DIR_PATH: String = Environment.getExternalStorageDirectory().getPath()
            val tempDirPath: String = context.getExternalFilesDir(null)!!.path
            val copyFile = File(tempDirPath + File.separator.toString() + fileName)
            Log.d(mTAG, "FilePath copyFile: $copyFile")
            copy(context, contentUri, copyFile)
            return copyFile.absolutePath
        }
        return null
    }

    private fun getFileName(uri: Uri?): String? {
        if (uri == null) return null
        var fileName: String? = null
        val path: String = uri.path as String
        val cut = path.lastIndexOf('/')
        if (cut != -1) {
            fileName = path.substring(cut + 1)
        }
        return fileName
    }

    private fun copy(context: Context, srcUri: Uri?, dstFile: File?) {
        try {
            val inputStream: InputStream = srcUri?.let { context.contentResolver.openInputStream(it) }
                ?: return
            val outputStream: OutputStream = FileOutputStream(dstFile as File)
            IOUtils.copy(inputStream, outputStream) // org.apache.commons.io

            inputStream.close()
            outputStream.close()
        } catch (e: Exception) { // IOException
            e.printStackTrace()
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private fun getUriRealPathAboveKitkat(ctx: Context?, uri: Uri?): String? {
        var ret: String? = ""
        if (ctx != null && uri != null) {
            if (isContentUri(uri)) {
                ret = if (isGooglePhotoDoc(uri.authority as String)) {
                    uri.lastPathSegment
                } else {
                    getRealPath(ctx.contentResolver, uri, null)
                }
            } else if (isFileUri(uri)) {
                ret = uri.path
            } else if (isDocumentUri(ctx, uri)) {

                // Get uri related document id.
                val documentId = DocumentsContract.getDocumentId(uri)

                // Get uri authority.
                val uriAuthority: String = uri.authority as String
                if (isMediaDoc(uriAuthority)) {
                    val idArr = documentId.split(":").toTypedArray()
                    if (idArr.size == 2) {
                        // First item is document type.
                        val docType = idArr[0]

                        // Second item is document real id.
                        val realDocId = idArr[1]

                        // Get content uri by document type.
                        var mediaContentUri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        when (docType) {
                            "image" -> {
                                mediaContentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                            }
                            "video" -> {
                                mediaContentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                            }
                            "audio" -> {
                                mediaContentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                            }

                            // Get where clause with real document id.
                        }

                        // Get where clause with real document id.
                        val whereClause =
                            MediaStore.Images.Media._ID + " = " + realDocId
                        ret = getRealPath(ctx.contentResolver, mediaContentUri, whereClause)
                    }
                } else if (isDownloadDoc(uriAuthority)) {
                    // Build download uri.
                    val downloadUri: Uri = Uri.parse("content://downloads/public_downloads")

                    // Append download document id at uri end.
                    val downloadUriAppendId: Uri =
                        ContentUris.withAppendedId(downloadUri, java.lang.Long.valueOf(documentId))
                    ret = getRealPath(ctx.contentResolver, downloadUriAppendId, null)
                } else if (isExternalStoreDoc(uriAuthority)) {
                    val idArr = documentId.split(":").toTypedArray()
                    if (idArr.size == 2) {
                        val type = idArr[0]
                        val realDocId = idArr[1]
                        if ("primary".equals(type, ignoreCase = true)) {
                            //ret = Environment.getExternalStorageDirectory()
                            ret = ctx.getExternalFilesDir(null)!!.path + "/" + realDocId
                        }
                    }
                }
            }
        }
        return ret
    }

    /* Check whether this uri represent a document or not. */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private fun isDocumentUri(ctx: Context?, uri: Uri?): Boolean {
        var ret = false
        if (ctx != null && uri != null) {
            ret = DocumentsContract.isDocumentUri(ctx, uri)
        }
        return ret
    }

    /* Check whether this uri is a content uri or not.
     *  content uri like content://media/external/images/media/1302716
     *  */
    private fun isContentUri(uri: Uri?): Boolean {
        var ret = false
        if (uri != null) {
            val uriSchema: String = uri.scheme as String
            if ("content".equals(uriSchema, ignoreCase = true)) {
                ret = true
            }
        }
        return ret
    }

    /* Check whether this uri is a file uri or not.
     *  file uri like file:///storage/41B7-12F1/DCIM/Camera/IMG_20180211_095139.jpg
     * */
    private fun isFileUri(uri: Uri?): Boolean {
        var ret = false
        if (uri != null) {
            val uriSchema: String = uri.scheme as String
            if ("file".equals(uriSchema, ignoreCase = true)) {
                ret = true
            }
        }
        return ret
    }

    /* Check whether this document is provided by ExternalStorageProvider. */
    private fun isExternalStoreDoc(uriAuthority: String): Boolean {
        var ret = false
        if ("com.android.externalstorage.documents" == uriAuthority) {
            ret = true
        }
        return ret
    }

    /* Check whether this document is provided by DownloadsProvider. */
    private fun isDownloadDoc(uriAuthority: String): Boolean {
        var ret = false
        if ("com.android.providers.downloads.documents" == uriAuthority) {
            ret = true
        }
        return ret
    }

    /* Check whether this document is provided by MediaProvider. */
    private fun isMediaDoc(uriAuthority: String): Boolean {
        var ret = false
        if ("com.android.providers.media.documents" == uriAuthority) {
            ret = true
        }
        return ret
    }

    /* Check whether this document is provided by google photos. */
    private fun isGooglePhotoDoc(uriAuthority: String): Boolean {
        var ret = false
        if ("com.google.android.apps.photos.content" == uriAuthority) {
            ret = true
        }
        return ret
    }

    /* Return uri represented document file real local path.*/
    @SuppressLint("Recycle")
    private fun getRealPath(
        contentResolver: ContentResolver,
        uri: Uri,
        whereClause: String?
    ): String {
        var ret = ""

        // Query the uri with condition.
        val cursor: Cursor? = contentResolver.query(uri, null, whereClause, null, null)
        if (cursor != null) {
            val moveToFirst: Boolean = cursor.moveToFirst()
            if (moveToFirst) {

                // Get columns name by uri type.
                var columnName = MediaStore.Images.Media.DATA
                when {
                    uri === MediaStore.Images.Media.EXTERNAL_CONTENT_URI -> {
                        columnName = MediaStore.Images.Media.DATA
                    }
                    uri === MediaStore.Audio.Media.EXTERNAL_CONTENT_URI -> {
                        columnName = MediaStore.Audio.Media.DATA
                    }
                    uri === MediaStore.Video.Media.EXTERNAL_CONTENT_URI -> {
                        columnName = MediaStore.Video.Media.DATA
                    }

                    // Get column index.

                    // Get column value which is the uri related file local path.
                }

                // Get column index.
                val columnIndex: Int = cursor.getColumnIndex(columnName)

                // Get column value which is the uri related file local path.
                ret = cursor.getString(columnIndex)
            }
        }
        return ret
    }

    fun writeXmlFile(list: ArrayList<SupplierData>) {
        Log.e(mTAG, "=== writeXmlFile start ===")
        //val downloadFolder = ctx.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        val downloadFolder = "/storage/emulated/0/Download"
        Log.e(mTAG, "downloadFolder= $downloadFolder")
        try {
            val dFact: DocumentBuilderFactory = DocumentBuilderFactory.newInstance()
            val build: DocumentBuilder = dFact.newDocumentBuilder()
            val doc: Document = build.newDocument()
            val root: Element = doc.createElement("suppliers")
            doc.appendChild(root)
            //val Details: Element = doc.createElement("Details")
            //root.appendChild(Details)
            for (dtl in list) {
                val data: Element = doc.createElement("data")
                root.appendChild(data)

                val key: Element = doc.createElement("key")
                key.appendChild(doc.createTextNode(java.lang.String.valueOf(dtl.getKey())))
                data.appendChild(key)

                val name: Element = doc.createElement("name")
                name.appendChild(doc.createTextNode(java.lang.String.valueOf(dtl.getName())))
                data.appendChild(name)

                val uniNumber: Element = doc.createElement("uniNumber")
                uniNumber.appendChild(doc.createTextNode(java.lang.String.valueOf(dtl.getNumber())))
                data.appendChild(uniNumber)
            }

            // Save the document to the disk file
            val tranFactory: TransformerFactory = TransformerFactory.newInstance()
            val aTransformer: Transformer = tranFactory.newTransformer()

            // format the XML nicely
            aTransformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1")
            aTransformer.setOutputProperty(
                "{http://xml.apache.org/xslt}indent-amount", "4"
            )
            aTransformer.setOutputProperty(OutputKeys.INDENT, "yes")
            val source = DOMSource(doc)
            try {
                // location and name of XML file you can change as per need
                val fos = FileWriter("$downloadFolder/suppliers.xml")
                val result = StreamResult(fos)
                aTransformer.transform(source, result)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } catch (ex: TransformerException) {
            println("Error outputting document")
        } catch (ex: ParserConfigurationException) {
            println("Error building document")
        }
        Log.e(mTAG, "=== writeXmlFile end ===")
    }

    fun readXmlFromFile(): ArrayList<SupplierData> {
        Log.e(mTAG, "=== readXmlFromFile start ===")
        val downloadFolder = "/storage/emulated/0/Download"
        val xmlFile = "$downloadFolder/suppliers.xml"
        val dataList= ArrayList<SupplierData>()
        dataList.clear()
        try {
            //val fileInputStream = ctx!!.openFileInput(xmlFile)
            val fileInputStream = FileInputStream(File(xmlFile))
            //val len = fileInputStream.available()
            //val buffer = ByteArray(len)
            //fileInputStream.read(buffer)

            val parser = Xml.newPullParser()
            parser.setInput(fileInputStream, "UTF-8")

            var eventType = parser.eventType

            var key = ""
            var name = ""
            var uniNumber = ""
            while (eventType != XmlPullParser.END_DOCUMENT) {
                //Log.e(mTAG, "parser.name = ${parser.name}")
                when (eventType) {

                    XmlPullParser.START_DOCUMENT-> {

                        Log.e(mTAG, "START_DOCUMENT")
                    }

                    XmlPullParser.START_TAG ->{

                        when(parser.name) {
                            "data" -> {

                            }
                            "key" -> {
                                key = parser.nextText()
                            }
                            "name" -> {
                                name = parser.nextText()
                            }
                            "uniNumber" -> {
                                uniNumber = parser.nextText()
                            }
                        }

                    }

                    XmlPullParser.END_TAG->{
                        if (parser.name.equals("data")) {
                            val supplierData = SupplierData(key, name, uniNumber)
                            dataList.add(supplierData)
                        }
                    }
                    //else-> {
                    //    Log.e(mTAG, "Unknown tag")
                    //}
                }
                eventType = parser.next()
            }

            Log.e(mTAG, "dataList size = ${dataList.size}")

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }



        Log.e(mTAG, "=== readXmlFromFile end ===")
        return dataList
    }
}