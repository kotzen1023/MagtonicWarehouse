package com.magtonic.magtonicwarehouse.model.send

import com.magtonic.magtonicwarehouse.model.sys.User
import com.magtonic.magtonicwarehouse.model.ui.ItemStorage

class HttpParaUploadStorage: HttpParaBase() {
    var rvb01 : String = "" //收貨單號
    var rvb02 : String = "" //收貨單號項次
    var rvb36 : String = "" //倉庫
    var rvb37 : String = "" //儲位
    var rvb38 : String = "" //批號
    var rvb33 : String = "" //入庫數量
    var rvv01 : String = "" //入庫單號
    var rvv02 : String = ""//入庫單號項次
    var user : String = "" //登入人員

    companion object {
        fun itemReceiptStorageToHttpParaUploadStorage(itemStorage: ItemStorage, user: User): HttpParaUploadStorage {

            val httpParaUploadStorage = HttpParaUploadStorage()

            httpParaUploadStorage.rvb01 = itemStorage.rjStorage!!.rvb01
            httpParaUploadStorage.rvb02 = itemStorage.rjStorage!!.rvb02
            httpParaUploadStorage.rvb36 = itemStorage.rjStorage!!.rvb36
            httpParaUploadStorage.rvb37 = itemStorage.rjStorage!!.rvb37
            httpParaUploadStorage.rvb38 = itemStorage.rjStorage!!.rvb38
            httpParaUploadStorage.rvb33 = itemStorage.rjStorage!!.rvb33
            httpParaUploadStorage.rvv01 = itemStorage.rjStorage!!.rvv01
            httpParaUploadStorage.rvv02 = itemStorage.rjStorage!!.rvv02
            httpParaUploadStorage.user = user.userAccount

            return httpParaUploadStorage
        }
    }
}