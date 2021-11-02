package com.magtonic.magtonicwarehouse.data

class OutsourcedProcessSupplierItem(data1: String, data2: String, data3: String) {
    private var data1: String = ""
    private var data2: String = ""
    private var data3: String = ""
    private var isSigned: Boolean = false
    private var signedNumber: Int = 0

    init {
        this.data1 = data1
        this.data2 = data2
        this.data3 = data3
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

    fun setData3(data3: String) {
        this.data3 = data3
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

    fun serSignedNum(signedNumber: Int) {
        this.signedNumber = signedNumber
    }
}