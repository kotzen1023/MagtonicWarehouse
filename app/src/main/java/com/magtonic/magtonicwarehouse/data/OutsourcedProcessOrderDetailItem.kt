package com.magtonic.magtonicwarehouse.data

class OutsourcedProcessOrderDetailItem(data1: String, data2: String, data3: String, data4: String,
                                       data5: String, data6: String, data7: String, data8: String) {
    private var data1: String = ""
    private var data2: String = ""
    private var data3: String = ""
    private var data4: String = ""
    private var data5: String = ""
    private var data6: String = ""
    private var data7: String = ""
    private var data8: String = ""

    init {
        this.data1 = data1
        this.data2 = data2
        this.data3 = data3
        this.data4 = data4
        this.data5 = data5
        this.data6 = data6
        this.data7 = data7
        this.data8 = data8
    }

    fun getData1(): String {
        return data1
    }

    fun setData1(data1: String) {
        this.data1 = data1
    }

    fun getData2(): String {
        return data2
    }

    fun setData2(data2: String) {
        this.data2 = data2
    }

    fun getData3(): String {
        return data3
    }

    fun setData3(data3: String) {
        this.data3 = data3
    }

    fun getData4(): String {
        return data4
    }

    fun setData4(data4: String) {
        this.data4 = data4
    }

    fun getData5(): String {
        return data5
    }

    fun setData5(data5: String) {
        this.data5 = data5
    }

    fun getData6(): String {
        return data6
    }

    fun setData6(data6: String) {
        this.data6 = data6
    }

    fun getData7(): String {
        return data7
    }

    fun setData7(data7: String) {
        this.data7 = data7
    }

    fun getData8(): String {
        return data8
    }

    fun setData8(data8: String) {
        this.data8 = data8
    }
}