package com.magtonic.magtonicwarehouse.model.receive



class RJReceiptUpload : RJBase() {
    var rva01 = ""//result = 0 => fail reason ,result = 1 => 收貨單單號 NOT USE  now
    var rvb02 = ""//收貨單項次  if fail , it is empty now.

    var pmn01 = ""//採購單 number
    var pmn02 = ""//採購單項次

    /*fun trans_RJReceiptStr_To_RJReceiptUpload(RJUploadStr: String, PONum_spilit: String): RJReceiptUpload? {
        val gson = Gson()

        //val itemReceipt = ItemReceipt()
        //val rjReceipt: RJReceipt
        val rjReceiptUpload: RJReceiptUpload
        try {
            rjReceiptUpload = gson.fromJson<Any>(RJUploadStr, RJReceiptUpload::class.java) as RJReceiptUpload


            return rjReceiptUpload

        } catch (ex: Exception) {
            return null
        }


    }//trans_RJReceiptStr_To_ItemReceipt
    */
}