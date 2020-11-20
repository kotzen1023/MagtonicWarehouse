package com.magtonic.magtonicwarehouse.data

class ReturnOfGoodsItem (data1: String, data2: String, data3: String) {
    private var data1: String = ""
    private var data2: String = ""
    private var data3: String = ""
    private var isSigned: Boolean = false

    init {
        this.data1 = data1
        this.data2 = data2
        this.data3 = data3
        isSigned = false
    }

    fun getData1(): String {
        return data1
    }

    fun getData2(): String {
        return data2
    }

    fun getData3(): String {
        return data3
    }

    fun getIsSigned(): Boolean {
        return isSigned
    }

    fun setIsSigned(isSigned: Boolean) {
        this.isSigned = isSigned
    }
}