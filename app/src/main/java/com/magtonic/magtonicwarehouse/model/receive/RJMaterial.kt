package com.magtonic.magtonicwarehouse.model.receive



class RJMaterial : RJBase() {
    var result2 = ""

    var sfs01 = "發料單號"
    var sfs02 = "項次"
    var sfs03 = "工單單號"
    var sfs04 = "料號"
    var sfs05 = "實際發料數量"
    //var sfs05_old = "預計發料數量"
    var sfs06 = "單位"
    var sfs07 = "倉庫"
    var sfs08 = "儲位"
    var sfs09 = "批號"
    var sfa05 = "應發數量"
    var sfa06 = ""
    var ima02 = "品名"
    var ima021 = "規格"
    var img10 = "庫存數量"
    var update = 0 //0: not update, 1: update failed, 2: update success
}