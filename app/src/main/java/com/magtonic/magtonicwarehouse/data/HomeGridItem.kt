package com.magtonic.magtonicwarehouse.data

class HomeGridItem(appId: String, imgId: Int, stringId: Int) {
    private var appId: String = ""
    private var imgId: Int = 0
    private var stringId: Int = 0

    init {
        this.appId = appId
        this.imgId = imgId
        this.stringId = stringId
    }

    fun getAppId(): String {
        return appId
    }

    fun setAppId(appId: String) {
        this.appId = appId
    }

    fun getImgId(): Int {
        return imgId
    }

    fun setImgId(imgId: Int) {
        this.imgId = imgId
    }

    fun getStringId(): Int {
        return stringId
    }

    fun setStringId(stringId: Int) {
        this.stringId = stringId
    }
}