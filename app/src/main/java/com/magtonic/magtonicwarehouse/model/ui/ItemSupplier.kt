package com.magtonic.magtonicwarehouse.model.ui

import com.google.gson.Gson
import com.magtonic.magtonicwarehouse.model.receive.RJSupplier

class ItemSupplier {
    var rjSupplier: RJSupplier? = RJSupplier()

    companion object {
        const val RESULT_CORRECT = "0"

        fun tranRJSupplierStrToItemSupplier(RJItemSupplierStr: String): ItemSupplier? {
            val gson = Gson()
            val itemSupplier = ItemSupplier()
            val rjSupplier: RJSupplier // = new RJReceipt();
            try {
                rjSupplier = gson.fromJson<Any>(RJItemSupplierStr, RJSupplier::class.java) as RJSupplier
                itemSupplier.rjSupplier = rjSupplier
            } catch (ex: Exception) {
                return null
            }

            return itemSupplier
        }//trans_RJReceiptStr_To_ItemReceipt

    }
}