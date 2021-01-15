package com.magtonic.magtonicwarehouse.model.send

import com.magtonic.magtonicwarehouse.model.sys.User
import com.magtonic.magtonicwarehouse.model.ui.ItemReceipt

class HttpParaUploadReceiptPoint : HttpParaBase() {
    var pmn01: String = ""//採購單號 => ItemReceipt PONum_spilit
    var pmn02: String = ""//採購單項次
    var ima35: String = ""//倉庫
    var ima36: String = ""//儲位
    var pmn20: String = ""//交貨數量 map to  receive parameter  未交數量
    var pmm02: String = ""//採購單性質
    var pmm09: String = ""//供應商編號
    var user: String = ""// user

    /*var pmn43: String = ""// 製程序
    var pmm43: String = "" // 稅率
    var pmn41: String = "" // 工單號碼
    var pmnud02: String = "" // 檢驗否(外檢用)
    var pmn04: String = "" // 料件編號
    var pmn31: String = "" // 採購單價
    var pmn31t: String = "" // 含稅單價
    var pmn88: String = "" // 未稅金額
    var pmn88t: String = "" // 含稅金額
    var pmn86: String = "" // 計價單位
    var rvb08: String = "" // 可收貨數量
    var rvb051: String = "" // 品名
    var rvb90_fac: String = "" // 採購對庫存單位轉換率
    var ima25: String = "" // 庫存單位
    var ima44: String = "" // 採購單位
    var ima44_fac: String = "" // 採購/庫存單位轉換率
    var rvb38: String = "" // 批號
    */

    var ta_rvb01: String = "" // 交貨指示單(看板卡)

    companion object {



        fun itemReceiptToHttpParaUploadReceiptPoint(itemReceipt: ItemReceipt, user: User, ta_rvb01: String): HttpParaUploadReceiptPoint {

            val httpParaUploadReceiptPoint = HttpParaUploadReceiptPoint()

            //httpParaUploadReceipt.p_cmd locked
            httpParaUploadReceiptPoint.ima35 = itemReceipt.rjReceipt!!.ima35
            httpParaUploadReceiptPoint.ima36 = itemReceipt.rjReceipt!!.ima36
            httpParaUploadReceiptPoint.pmm02 = itemReceipt.rjReceipt!!.pmm02
            httpParaUploadReceiptPoint.pmm09 = itemReceipt.rjReceipt!!.pmm09
            httpParaUploadReceiptPoint.pmn01 = itemReceipt.rjReceipt!!.pmn01
            httpParaUploadReceiptPoint.pmn02 = itemReceipt.rjReceipt!!.pmn02
            httpParaUploadReceiptPoint.pmn20 = itemReceipt.rjReceipt!!.pmn20
            httpParaUploadReceiptPoint.user = user.userAccount

            /*httpParaUploadReceiptPoint.pmn43 = ""
            httpParaUploadReceiptPoint.pmm43 = ""
            httpParaUploadReceiptPoint.pmn41 = ""
            httpParaUploadReceiptPoint.pmnud02 = ""
            httpParaUploadReceiptPoint.pmn04 = ""
            httpParaUploadReceiptPoint.pmn31 = ""
            httpParaUploadReceiptPoint.pmn31t = ""
            httpParaUploadReceiptPoint.pmn88 = ""
            httpParaUploadReceiptPoint.pmn88t = ""
            httpParaUploadReceiptPoint.pmn86 = ""
            httpParaUploadReceiptPoint.rvb08 = ""
            httpParaUploadReceiptPoint.rvb051 = ""
            httpParaUploadReceiptPoint.rvb90_fac = ""
            httpParaUploadReceiptPoint.ima25 = ""
            httpParaUploadReceiptPoint.ima44 = ""
            httpParaUploadReceiptPoint.ima44_fac =""
            httpParaUploadReceiptPoint.rvb38 = ""
            */

            httpParaUploadReceiptPoint.ta_rvb01 = ta_rvb01

            return httpParaUploadReceiptPoint
        }
    }
}