package com.magtonic.magtonicwarehouse.bluetoothchat.printer

import com.magtonic.magtonicwarehouse.bluetoothchat.BluetoothChatService
import java.io.UnsupportedEncodingException

class BluetoothPrinterFuncs(mCS: BluetoothChatService) {
    //internal var LK_CPCL_BCS_39 = "39"
    //internal var LK_CPCL_BCS_93 = "93"
    //private var LK_CPCL_BCS_128 = "128"

    //internal var LK_CPCL_BCS_0RATIO = 0
    //private var LK_CPCL_BCS_1RATIO = 1
    //internal var LK_CPCL_BCS_2RATIO = 2
    //internal var LK_CPCL_BCS_3RATIO = 3
    //internal var LK_CPCL_BCS_4RATIO = 4
    //internal var LK_CPCL_BCS_20RATIO = 5
    //internal var LK_CPCL_BCS_21RATIO = 6
    //internal var LK_CPCL_BCS_22RATIO = 7
    //internal var LK_CPCL_BCS_23RATIO = 8
    //internal var LK_CPCL_BCS_24RATIO = 9
    //internal var LK_CPCL_BCS_25RATIO = 10
    //internal var LK_CPCL_BCS_26RATIO = 11
    //internal var LK_CPCL_BCS_27RATIO = 12
    //internal var LK_CPCL_BCS_28RATIO = 13
    //internal var LK_CPCL_BCS_29RATIO = 14
    //internal var LK_CPCL_BCS_30RATIO = 15
    //private var stringBuffer: StringBuffer? = null
    private var mChatService: BluetoothChatService? = null

    init {
        mChatService = mCS
    }

    /*fun BluetoothPrinterFuncs(mCS: BluetoothChatService) {
        mChatService = mCS
    }*/

    /////*******ESC/POS列印語言
    private fun initPrinter() {
        val initPrinter = byteArrayOf(0x1B, 0x40)// 初始化
        mChatService!!.write(initPrinter)
    }

    /*@Throws(UnsupportedEncodingException::class)
    fun print_qr_code(qrdata: String) {
        val storeLen = qrdata.toByteArray(charset("Big5")).size + 3
        val storePl = (storeLen % 256).toByte()
        val storePh = (storeLen / 256).toByte()

        val modelQR = byteArrayOf(0x1d, 0x28, 0x6b, 0x04, 0x00, 0x31, 0x41, 0x30, 0x00)
        val sizeQR = byteArrayOf(0x1d, 0x28, 0x6b, 0x03, 0x00, 0x31, 0x43, 0x05)
        val errorQR = byteArrayOf(0x1d, 0x28, 0x6b, 0x03, 0x00, 0x31, 0x45, 0x31)
        val storeQR = byteArrayOf(0x1d, 0x28, 0x6b, storePl, storePh, 0x31, 0x50, 0x30)
        val printQR = byteArrayOf(0x1d, 0x28, 0x6b, 0x03, 0x00, 0x31, 0x51, 0x30)

        val alignByte = byteArrayOf(0x1B, 0x61, 1)// 0：靠左；1：居中；2：靠右
        mChatService!!.write(alignByte)

        mChatService!!.write(modelQR)
        mChatService!!.write(sizeQR)
        mChatService!!.write(errorQR)
        mChatService!!.write(storeQR)
        mChatService!!.write(qrdata.toByteArray(charset("Big5")))
        mChatService!!.write(printQR)
        feedAndCut("10")
    }*/

    fun printBarCode(content: String) {
        val bytes = content.toByteArray()
        val cmd = ByteArray(bytes.size + 4)
        // 印barcode指令
        cmd[0] = 0x1D// 29
        cmd[1] = 0x6B// 107
        cmd[2] = 73// barcode code39:69  code93:72 code128:73
        cmd[3] = bytes.size.toByte()// barcode長度
        for (i in bytes.indices) {
            cmd[4 + i] = bytes[i]
        }
        initPrinter()// 一定要初始化，不然barcode打不出来

        // 對齊設定
        val alignByte = byteArrayOf(0x1B, 0x61, 1)// 0：靠左；1：居中；2：靠右
        mChatService!!.write(alignByte)

        // 設定barcode的高度(PS:寬高要不全設，或是全不設)
        val setCodeHeigthByte = byteArrayOf(0x1D, 0x68, 100.toByte())
        mChatService!!.write(setCodeHeigthByte)
        // barcode的宽度
        val setCodeWidthByte = byteArrayOf(0x1D, 0x77, 1.toByte())
        mChatService!!.write(setCodeWidthByte)

        // 設定文字印在barcode下方
        //val codeStrByte = byteArrayOf(0x1D, 0x48, 2)
        // mChatService.write(codeStrByte);

        // 開始印barcode
        mChatService!!.write(cmd)
        //byte[] test123 = {12};
        //mChatService.write(test123);
        // 空白行
        //byte[] lF = { 0x0A, 0x0A};// 两行
        //mChatService.write();
    }

    @Throws(UnsupportedEncodingException::class)
    fun printText(text: String) {
        val context = text.toByteArray(charset("Big5"))
        val alignByte = byteArrayOf(0x1B, 0x61, 1)
        val size = byteArrayOf(0x1d, 0x21, 16)
        //val textsize1 = byteArrayOf(0x1b, 0x21, 0x0)
        //val textsize3 = byteArrayOf(0x1b, 0x21, 0x08)
        val deviceFontB = byteArrayOf(8, 77, 0, 67)
        val lF = byteArrayOf(0x0A)// 两行

        initPrinter()
        mChatService!!.write(size)
        mChatService!!.write(deviceFontB)
        //mChatService.write(textsize1);
        mChatService!!.write(alignByte)
        mChatService!!.write(context)
        mChatService!!.write(lF)
        feedAndCut("0")
    }

    private fun feedAndCut(fcw: String) {
        val fi = Integer.valueOf(fcw)
        //byte[] FcB = { 0x1D,86,65,(byte)fi};
        val fcB = byteArrayOf(0x1b, 0x4a, fi.toByte())
        mChatService!!.write(fcB)

        //另外一种切纸的方式
        //        byte[] bytes = {29, 86, 0};
        //        socketOut.write(bytes);
    }

    /////*******ESC/POS列印語言
    /////*******CLPL列印語言
   /*@Throws(UnsupportedEncodingException::class)
    fun CLPLbarcodePrinter(content1: String, content2: String) {
        this.stringBuffer = StringBuffer()
        this.setForm(0, 200, 200, 406, 1)
        this.addTokenLast("GAP-SENSE")

        this.setCPCLBarcode(0, 3, 0)
        this.printCPCLText(7, 3, 75, 50, content1, 0)
        this.printCPCLBarcode(LK_CPCL_BCS_128, 1, LK_CPCL_BCS_1RATIO, 50, 35, 100, content2, 0)
        this.addTokenLast("FORM")
        this.addTokenLast("PRINT")
        mChatService!!.write(this.getBuffer()!!.toString().toByteArray(charset("BIG5")))

    }

    private fun setForm(horizonOffset: Int, resolX: Int, resolY: Int, labelHeight: Int, quantity: Int) {
        this.addTokenSpace("!")
        this.addTokenSpace(horizonOffset)
        this.addTokenSpace(resolX)
        this.addTokenSpace(resolY)
        this.addTokenSpace(labelHeight)
        this.addTokenLast(quantity)
    }

    private fun setCPCLBarcode(fontNum: Int, fontSize: Int, offset: Int) {
        this.addTokenSpace("BARCODE-TEXT")
        this.addTokenSpace(fontNum)
        this.addTokenSpace(fontSize)
        this.addTokenLast(offset)
    }

    private fun printCPCLBarcode(
        barcodeType: String,
        NB: Int,
        ratio: Int,
        barHeight: Int,
        printX: Int,
        printY: Int,
        data: String,
        count: Int
    ) {
        this.addTokenSpace("B")
        this.addTokenSpace(barcodeType)
        this.addTokenSpace(NB)
        this.addTokenSpace(ratio)
        this.addTokenSpace(barHeight)
        this.addTokenSpace(printX)
        this.addTokenSpace(printY)
        this.addTokenLast(data)
        if (count > 1) {
            this.addTokenSpace("COUNT")
            this.addTokenLast(count)
        }

        this.addTokenLast("BARCODE-TEXT OFF")
    }

    private fun printCPCLText(fontType: Int, fontSize: Int, printX: Int, printY: Int, data: String, count: Int) {
        this.addTokenSpace("T")
        this.addTokenSpace(fontType)
        this.addTokenSpace(fontSize)
        this.addTokenSpace(printX)
        this.addTokenSpace(printY)
        this.addTokenLast(data)
        if (count > 1) {
            this.addTokenSpace("COUNT")
            this.addTokenLast(count)
        }

    }

    private fun addTokenLast(token: String) {
        this.stringBuffer!!.append(token)
        this.stringBuffer!!.append("\r\n")
    }

    private fun addTokenLast(token: Int) {
        this.stringBuffer!!.append(token)
        this.stringBuffer!!.append("\r\n")
    }

    private fun addTokenSpace(token: String) {
        this.stringBuffer!!.append(token)
        this.stringBuffer!!.append(" ")
    }

    private fun addTokenSpace(token: Int) {
        this.stringBuffer!!.append(token)
        this.stringBuffer!!.append(" ")
    }

    private fun getBuffer(): StringBuffer? {
        return this.stringBuffer
    }*/
}