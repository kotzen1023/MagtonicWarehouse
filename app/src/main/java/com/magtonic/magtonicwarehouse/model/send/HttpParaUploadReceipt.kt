package com.magtonic.magtonicwarehouse.model.send

import com.magtonic.magtonicwarehouse.model.ui.ItemReceipt
import com.magtonic.magtonicwarehouse.model.sys.User


class HttpParaUploadReceipt : HttpParaBase() {
    var pmn01: String = ""//採購單號 => ItemReceipt PONum_spilit
    var pmn02: String = ""//採購單項次
    var ima35: String = ""//倉庫
    var ima36: String = ""//儲位
    var pmn20: String = ""//交貨數量 map to  receive parameter  未交數量
    var pmm02: String = ""//採購單性質
    var pmm09: String = ""//供應商編號
    var user: String = ""// user

    companion object {



        fun itemReceiptToHttpParaUploadReceipt(itemReceipt: ItemReceipt, user: User): HttpParaUploadReceipt {

            val httpParaUploadReceipt = HttpParaUploadReceipt()

            //httpParaUploadReceipt.p_cmd locked
            httpParaUploadReceipt.ima35 = itemReceipt.rjReceipt!!.ima35
            httpParaUploadReceipt.ima36 = itemReceipt.rjReceipt!!.ima36
            httpParaUploadReceipt.pmm02 = itemReceipt.rjReceipt!!.pmm02
            httpParaUploadReceipt.pmm09 = itemReceipt.rjReceipt!!.pmm09
            httpParaUploadReceipt.pmn01 = itemReceipt.poNumSplit
            httpParaUploadReceipt.pmn02 = itemReceipt.rjReceipt!!.pmn02
            httpParaUploadReceipt.pmn20 = itemReceipt.rjReceipt!!.pmn20
            httpParaUploadReceipt.user = user.userAccount

            return httpParaUploadReceipt
        }//itemReceiptToHttpParaUploadReceipt

        /*fun list_ItemReceipt_To_List_HttpParaUploadReceipt(
            itemReceiptList: List<ItemReceipt>, user: User
        ): List<HttpParaUploadReceipt> {
            val uploadReceiptList = ArrayList<HttpParaUploadReceipt>()

            for (item in itemReceiptList) {
                val httpParaUploadReceipt = itemReceiptToHttpParaUploadReceipt(item, user)
                uploadReceiptList.add(httpParaUploadReceipt)
            }//for

            return uploadReceiptList
        }//list_ItemReceipt_To_List_HttpParaUploadReceipt
        */
    }


}