package com.magtonic.magtonicwarehouse.data


import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.ProgressBar
import org.apache.commons.net.ftp.FTP
import org.apache.commons.net.ftp.FTPClient
import org.apache.commons.net.ftp.FTPConnectionClosedException
import org.apache.commons.net.io.CopyStreamException
import java.io.*
import java.lang.Exception
import java.net.SocketException
import java.net.UnknownHostException


class FTPUtils(context: Context, ftpUrl: String, ftpPort: Int, userName: String, userPassword: String, filename: String, path: String) {
    private val mTAG = FTPUtils::class.java.name
    var ftpClient: FTPClient? = null
    private var ftpUrl: String? = null
    private var ftpPort: Int = 0
    private var userName: String? = null
    private var userPassword: String? = null
    private var filename: String? =null
    private var path: String? = null
    var mContext: Context? = null


    init {
        this.mContext = context
        this.ftpUrl = ftpUrl
        this.ftpPort = ftpPort
        this.userName = userName
        this.userPassword = userPassword
        this.filename = filename
        this.path = path

        ftpClient = FTPClient()
        ftpClient!!.connectTimeout = 5000
        ftpClient!!.controlEncoding = "UTF-8"


    }



    fun uploadFile(): Long {
        var size: Long = 0
        /*if (!initFtp()) {
            return 0
        }*/



        if (!ftpClient!!.isConnected) {
            Log.e(mTAG, "ftpClient is not connected.")

            try {

                ftpClient!!.connect(ftpUrl, ftpPort)
                ftpClient!!.login(userName, userPassword)

                ftpClient!!.bufferSize = 1024
                ftpClient!!.enterLocalPassiveMode()
                ftpClient!!.setFileType(FTP.BINARY_FILE_TYPE)
                //val paths: List<String> = File(filename).getParent().split("/")
                //for (s in paths) {
                //    ftpClient!!.makeDirectory(s)
                //    ftpClient!!.changeWorkingDirectory(s)
                //}
                val bis = BufferedInputStream(FileInputStream(File(path as String)))

                //檢查遠端伺服器是否有相同的文件, 有的話表示上次沒傳成功, 需先砍掉
                //val files = ftpClient!!.listFiles(File(filename).getPath())
                //if (files.size == 1) ftpClient!!.deleteFile(File(filename).getPath())
                ftpClient!!.storeFile(File(filename as String).path, bis)
                bis.close()
                size = ftpClient!!.listFiles(File(filename as String).path)[0].size

                Log.d(mTAG, "Upload file size = $size")

                ftpClient!!.logout()
                Log.d(mTAG, "ftp logout")
                ftpClient!!.disconnect()
                Log.d(mTAG, "ftp disconnect")

                val successIntent = Intent()
                successIntent.action = Constants.ACTION.ACTION_OUTSOURCED_PROCESS_SIGN_FTP_UPLOAD_SUCCESS
                mContext!!.sendBroadcast(successIntent)

            } catch (ex1: SocketException) {
                ex1.printStackTrace()
                val timeoutIntent = Intent()
                timeoutIntent.action = Constants.ACTION.ACTION_OUTSOURCED_PROCESS_SIGN_FTP_CONNECT_TIMEOUT
                mContext!!.sendBroadcast(timeoutIntent)
            } catch (ex2: UnknownHostException) {
                ex2.printStackTrace()
                val unknownHostIntent = Intent()
                unknownHostIntent.action = Constants.ACTION.ACTION_OUTSOURCED_PROCESS_SIGN_FTP_CONNECT_TIMEOUT
                mContext!!.sendBroadcast(unknownHostIntent)
            } catch (ex3: FTPConnectionClosedException) {
                val closeIntent = Intent()
                closeIntent.action = Constants.ACTION.ACTION_OUTSOURCED_PROCESS_SIGN_FTP_UPLOAD_FAILED
                mContext!!.sendBroadcast(closeIntent)
            } catch (ex4: CopyStreamException) {
                val copyIntent = Intent()
                copyIntent.action = Constants.ACTION.ACTION_OUTSOURCED_PROCESS_SIGN_FTP_UPLOAD_FAILED
                mContext!!.sendBroadcast(copyIntent)
            } catch (ex: Exception) {
                val failedIntent = Intent()
                failedIntent.action = Constants.ACTION.ACTION_OUTSOURCED_PROCESS_SIGN_FTP_CONNECT_FAILED
                mContext!!.sendBroadcast(failedIntent)
                ex.printStackTrace()
            }

        }

        /*try {
            ftpClient!!.bufferSize = 1024
            ftpClient!!.enterLocalPassiveMode()
            ftpClient!!.setFileType(FTP.BINARY_FILE_TYPE)
            //val paths: List<String> = File(filename).getParent().split("/")
            //for (s in paths) {
            //    ftpClient!!.makeDirectory(s)
            //    ftpClient!!.changeWorkingDirectory(s)
            //}
            val bis = BufferedInputStream(FileInputStream(File(path as String)))

            //檢查遠端伺服器是否有相同的文件, 有的話表示上次沒傳成功, 需先砍掉
            //val files = ftpClient!!.listFiles(File(filename).getPath())
            //if (files.size == 1) ftpClient!!.deleteFile(File(filename).getPath())
            ftpClient!!.storeFile(File(filename as String).path, bis)
            bis.close()
            size = ftpClient!!.listFiles(File(filename as String).path)[0].size

            ftpClient!!.logout()
            ftpClient!!.disconnect()
        } catch (ex: IOException) {
            ex.printStackTrace()

        }*/

        return size
    }

    fun downLoadFile(remoteFile: File, localFile: File?): Boolean {
        if (!ftpClient!!.isConnected) {
            return false
        } else {
            try {
                val outputStream: OutputStream = BufferedOutputStream(FileOutputStream(localFile))
                ftpClient!!.retrieveFile(remoteFile.getPath(), outputStream)
                outputStream.close()
                ftpClient!!.logout()
                ftpClient!!.disconnect()
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
        }

        return true
    }


}