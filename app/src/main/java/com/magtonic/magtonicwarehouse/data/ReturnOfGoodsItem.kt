package com.magtonic.magtonicwarehouse.data

class ReturnOfGoodsItem (data1: String, data2: String, data3: String, data4: String, data5: String) {
    private var data1: String = ""
    private var data2: String = ""
    private var data3: String = ""
    private var data4: String = ""
    private var data5: String = ""
    private var isSigned: Boolean = false
    private var signedNumber: Int = 0

    init {
        this.data1 = data1
        this.data2 = data2
        this.data3 = data3
        this.data4 = data4
        this.data5 = data5
        isSigned = false
        signedNumber = 0
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

    fun getData4(): String {
        return data4
    }

    fun getData5(): String {
        return data5
    }

    fun getIsSigned(): Boolean {
        return isSigned
    }

    fun setIsSigned(isSigned: Boolean) {
        this.isSigned = isSigned
    }

    fun getSignedNum(): Int {
        return signedNumber
    }

    fun setSignedNum(signedNumber: Int) {
        this.signedNumber = signedNumber
    }
}