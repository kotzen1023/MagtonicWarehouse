package com.magtonic.magtonicwarehouse.api

import android.util.Log
import com.google.gson.Gson
import com.magtonic.magtonicwarehouse.MainActivity.Companion.base_ip_address_webservice
import com.magtonic.magtonicwarehouse.MainActivity.Companion.iep_ip_address_webservice
import com.magtonic.magtonicwarehouse.MainActivity.Companion.real_ip_address_webservice
import com.magtonic.magtonicwarehouse.MainActivity.Companion.timeOutSeconds
import com.magtonic.magtonicwarehouse.data.Constants

import com.magtonic.magtonicwarehouse.model.send.*
import okhttp3.*
import java.io.IOException

import java.util.concurrent.TimeUnit

import java.util.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody



class ApiFunc {
    private val mTAG = ApiFunc::class.java.name
    //val baseIP = "http://192.1.1.42:81/asmx/WebService.asmx/"
    //private val baseIP = "http://192.1.1.50/asmx/webservice.asmx/"
    //private val baseIP = Constants.WebServiceIpAddress.BASE_IP
    private val baseIP = base_ip_address_webservice
    //private val testIP = "http://192.1.1.38/web/ws/r/aws_ttsrv2_toptest"
    //private val realIP = "http://192.1.1.38/web/ws/r/aws_ttsrv2"
    //private val realIP = Constants.WebServiceIpAddress.REAL_IP
    private val realIP = real_ip_address_webservice
    //private val iepIP = "http://192.1.1.121/webs.asmx/"
    //private val iepIP = Constants.WebServiceIpAddress.IEP_IP
    private val iepIP = iep_ip_address_webservice
    //private val apiTestSign = "http://192.1.16.152:8080/JerseyExample/rest/data/getdata"
    //private val outsideIP = "http://61.216.114.217/asmx/WebService.asmx/"
    private val outsideIP = Constants.WebServiceIpAddress.OUTSIDE_IP
    //api http address string define

    //1.Login
    private val apiStrLogin = baseIP + "Chk_zx01"

    //receipt
    //2.Get Receipt Content
    private val apiStrGetReceipt = baseIP + "Sel_pmn01"

    private val apiStrGetReceiptPoint = baseIP + "Sel_pmn03"

    //3.Upload Receipt Array
    private val apiStrUploadReceiptArr = baseIP + "Ins_rva01"

    //3.4 Upload Receipt Point Array
    private val apiStrUploadReceiptPointArr = baseIP + "Ins_rva02"

    //3.5 Confirm Upload Receipt
    private val apiStrConfirmUploadReceiptArr = realIP

    //4.Get Receipt History
    //private val apiStrGetReceiptHis = baseIP + "Sel_rvb01"

    //storage
    //5.
    private val apiStrGetRecDataFun = baseIP + "Sel_rvb02"

    //6.
    private val apiStrGetUpdateFun = baseIP + "Ins_rvu01"

    //7.
    private val apiStrGetMaterialFun = baseIP + "Sel_sfs"

    //8.
    private val apiStrUpdateMaterialSend = baseIP + "Upd_sfs05"

    //property
    private val apiStrGetPropertyFun = outsideIP + "Sel_faj"

    //private val apiStrGuestInOrOut = iepIP + "webs_app_car001"

    //private val apiStrGetGuestNotLeaveYet = iepIP + "webs_app_car002"

    private val apiStrGuestInOrOutMulti = iepIP + "webs_app_car003"

    private val apiStrGetGuestNotLeaveYetMulti = iepIP + "webs_app_car004"

    //private val apiStrOutsourcedProcessDetail = iepIP + "webs_app_sfpp01"

    private val apiStrOutsourcedProcessDetail = iepIP + "webs_app_sfpp05"

    //private val apiStrOutsourcedProcessBySupplierNo = iepIP + "webs_app_sfpp02"

    private val apiStrOutsourcedProcessBySupplierNo = iepIP + "webs_app_sfpp06"

    private val apiStrOutsourcedProcessSignConfirm = iepIP + "webs_app_sfpp03"

    private val apiStrIssuanceLookup = iepIP + "webs_app_sfpp04"

    private val apiStrReturnOfGoods = iepIP + "webs_app_rvu01"

    private val apiStrReturnOfGoodsDetail = iepIP + "webs_app_rvu02"

    private val apiStrReturnOfGoodsConfirm = iepIP + "webs_app_rvup01"

    private object ContentType {

        const val title = "Content-Type"
        const val xxxForm = "application/x-www-form-urlencoded"

    }//ContentType

    fun login(para: HttpUserAuthPara, callback: Callback) {
        Log.e("ApiFunc", "login")
        val paraMap = HashMap<String, String>()
        paraMap["p_json"] = Gson().toJson(para)
        postWithMultiKey(paraMap, callback)

    }//login

    fun getReceipt(para: HttpReceiptGetPara, callback: Callback) {
        Log.e(mTAG, "ApiFunc->getReceipt")
        postWithParaPJsonStrandTimeOut(apiStrGetReceipt, Gson().toJson(para), callback)

    }

    fun getReceiptPoint(para: HttpReceiptPointGetPara, callback: Callback) {
        Log.e(mTAG, "ApiFunc->getReceiptPoint")
        postWithParaPJsonStrandTimeOut(apiStrGetReceiptPoint, Gson().toJson(para), callback)

    }

    /*fun getHistory(para: HttpReceiptGetPara, callback: Callback) {
        postWithParaPJsonStr(apiStrGetReceiptHis, Gson().toJson(para), callback)

    }*/

    fun getStorage(para: HttpStorageGetPara, callback: Callback) {
        postWithParaPJsonStr(apiStrGetRecDataFun, Gson().toJson(para), callback)
    }
    //material
    fun getMaterial(para: HttpMaterialGetPara, callback: Callback) {
        postWithParaPJsonStr(apiStrGetMaterialFun, Gson().toJson(para), callback)
    }

    //property
    //fun getProperty(para: HttpPropertyGetPara, callback: Callback) {
    fun getProperty(para: String, callback: Callback) {
        //postWithParaPFAJ02Str(apiStrGetPropertyFun, Gson().toJson(para), callback)
        postWithParaPFAJ02Str(para, callback)
    }

    //guest
    /*fun guestInOrOut(para: HttpGuestInOrOutPara, callback: Callback) {
        postWithParaPJsonStr(apiStrGuestInOrOut, Gson().toJson(para), callback)
    }

    fun getGuest(para: HttpGuestNotLeaveGetPara, callback: Callback) {
        postWithParaPJsonStr(apiStrGetGuestNotLeaveYet, Gson().toJson(para), callback)
    }*/

    fun guestInOrOutMulti(para: HttpGuestInOrOutMultiPara, callback: Callback) {
        postWithParaPJsonStr(apiStrGuestInOrOutMulti, Gson().toJson(para), callback)
    }

    fun getGuestMulti(para: HttpGuestNotLeaveGetPara, callback: Callback) {
        postWithParaPJsonStr(apiStrGetGuestNotLeaveYetMulti, Gson().toJson(para), callback)
    }

    fun getOutSourcedProcessDetail(getPara: HttpOutsourcedProcessGetPara, callback: Callback) {
        postWithParaPJsonStrandTimeOutOutSource(apiStrOutsourcedProcessDetail, Gson().toJson(getPara), callback)
    }

    fun getOutSourcedProcessBySupplierNo(getPara: HttpOutsourcedProcessGetPara, callback: Callback) {
        postWithParaPJsonStrandTimeOutOutSource(apiStrOutsourcedProcessBySupplierNo, Gson().toJson(getPara), callback)
    }

    fun confirmOutSourcedProcessSign(getPara: HttpOutsourcedProcessSignConfirmGetPara, callback: Callback) {
        postWithParaPJsonStr(apiStrOutsourcedProcessSignConfirm, Gson().toJson(getPara), callback)
    }

    fun getIssuanceLookup(getPara: HttpIssuanceLookupGetPara, callback: Callback) {
        postWithParaPJsonStr(apiStrIssuanceLookup, Gson().toJson(getPara), callback)
    }

    fun getReturnOfGoodsOrder(getPara: HttpReturnOfGoodsGetPara, callback: Callback) {
        postWithParaPJsonStr(apiStrReturnOfGoods, Gson().toJson(getPara), callback)
    }

    fun getReturnOfGoodsDetail(getPara: HttpReturnOfGoodsGetPara, callback: Callback) {
        postWithParaPJsonStr(apiStrReturnOfGoodsDetail, Gson().toJson(getPara), callback)
    }

    fun confirmReturnOfGoodsSign(getPara: HttpReturnOfGoodsConfirmGetPara, callback: Callback) {
        postWithParaPJsonStr(apiStrReturnOfGoodsConfirm, Gson().toJson(getPara), callback)
    }

    // post with only one para  "p_json"
    private fun postWithParaPJsonStr(url: String, jsonStr: String, callback: Callback) {
        Log.e(mTAG, "->postWithParaPJsonStr")
        Log.e(mTAG, "send jsonStr = $jsonStr")
        val body = FormBody.Builder()
            .add("p_json", jsonStr)
            .build()

        val request = Request.Builder()
            .url(url)
            .post(body)
            .addHeader(ContentType.title, ContentType.xxxForm)
            .build()

        val client = OkHttpClient().newBuilder()
            .retryOnConnectionFailure(false)
            .build()

        try {
            val response = client.newCall(request).enqueue(callback)
            Log.d("postWithParaPJsonStr", "response = $response")

            client.dispatcher.executorService.shutdown()
            client.connectionPool.evictAll()
            client.cache?.close()

        } catch (e: IOException) {



            e.printStackTrace()
        }

    }

    private fun postWithParaPFAJ02Str(jsonStr: String, callback: Callback) {
        Log.e(mTAG, "->postWithParaPJsonStr")
        //Log.e(mTAG, "=======> $jsonStr" )

        val url = apiStrGetPropertyFun

        val body = FormBody.Builder()
            .add("p_faj02", jsonStr)
            .build()

        val request = Request.Builder()
            .url(url)
            .post(body)
            .addHeader(ContentType.title, ContentType.xxxForm)
            .build()

        val client = OkHttpClient().newBuilder()
            .retryOnConnectionFailure(false)
            .build()

        try {
            val response = client.newCall(request).enqueue(callback)
            Log.d("postWithParaPJsonStr", "response = $response")

            client.dispatcher.executorService.shutdown()
            client.connectionPool.evictAll()
            client.cache?.close()

        } catch (e: IOException) {



            e.printStackTrace()
        }

    }

    private fun postWithParaPJsonStrandTimeOutOutSource(url: String, jsonStr: String, callback: Callback) {
        Log.e(mTAG, "->postWithParaPJsonStrandTimeOutOutSource")


        val body = FormBody.Builder()
            .add("p_json", jsonStr)
            .build()

        val request = Request.Builder()
            .url(url)
            .post(body)
            .addHeader(ContentType.title, ContentType.xxxForm)
            .build()



        val client = OkHttpClient().newBuilder()
            //.connectTimeout(5000, TimeUnit.MILLISECONDS) //5 secs
            //.readTimeout(5000, TimeUnit.MILLISECONDS) //5 secs
            //.writeTimeout(5000, TimeUnit.MILLISECONDS) //5 secs
            .connectTimeout(10, TimeUnit.SECONDS) //5 secs
            .readTimeout(10, TimeUnit.SECONDS) //5 secs
            .writeTimeout(10, TimeUnit.SECONDS) //5 secs
            .retryOnConnectionFailure(false)
            .build()


        try {
            val response = client.newCall(request).enqueue(callback)



            Log.d("pPara_pjson_timeout", "response = $response")

            client.dispatcher.executorService.shutdown()
            client.connectionPool.evictAll()
            client.cache?.close()

        } catch (e: IOException) {
            e.printStackTrace()
        }


    }

    private fun postWithParaPJsonStrandTimeOut(url: String, jsonStr: String, callback: Callback) {
        Log.e(mTAG, "->postWithParaPJsonStrandTimeOut")


        val body = FormBody.Builder()
            .add("p_json", jsonStr)
            .build()

        val request = Request.Builder()
            .url(url)
            .post(body)
            .addHeader(ContentType.title, ContentType.xxxForm)
            .build()



        val client = OkHttpClient().newBuilder()
            //.connectTimeout(5000, TimeUnit.MILLISECONDS) //5 secs
            //.readTimeout(5000, TimeUnit.MILLISECONDS) //5 secs
            //.writeTimeout(5000, TimeUnit.MILLISECONDS) //5 secs
            .connectTimeout(timeOutSeconds, TimeUnit.SECONDS) //5 secs
            .readTimeout(timeOutSeconds, TimeUnit.SECONDS) //5 secs
            .writeTimeout(timeOutSeconds, TimeUnit.SECONDS) //5 secs
            .retryOnConnectionFailure(false)
            .build()


        try {
            val response = client.newCall(request).enqueue(callback)



            Log.d("pPara_pjson_timeout", "response = $response")

            client.dispatcher.executorService.shutdown()
            client.connectionPool.evictAll()
            client.cache?.close()

        } catch (e: IOException) {
            e.printStackTrace()
        }


    }

    private fun postWithMultiKey(paraMap: HashMap<String, String>?, callback: Callback) {
        Log.e(mTAG, "postWithMultiKey")
        val builder = FormBody.Builder()

        val url = apiStrLogin

        for (key in paraMap!!.keys) {

            val value = paraMap[key]
            if (value != null) {
                builder.add(key, value)
            }

        }

        val body = builder.build()
        val rBuilder = Request.Builder()
        Log.e(mTAG, "url = $url")
        rBuilder.url(url)
        rBuilder.post(body)

        rBuilder.addHeader(ContentType.title, ContentType.xxxForm)
        val req = rBuilder.build()

        val client = OkHttpClient().newBuilder()
            .retryOnConnectionFailure(false)
            .build()

        try {
            val response = client.newCall(req).enqueue(callback)
            Log.d("postWithMultiKey", "response = $response")

            client.dispatcher.executorService.shutdown()
            client.connectionPool.evictAll()
            client.cache?.close()

        } catch (e: IOException) {
            e.printStackTrace()
        }


    }


    /*fun uploadReceiptList(uploadReceiptList: List<HttpParaUploadReceipt>, callback: Callback) {
        val gson = Gson()
        val builder = StringBuilder()
        builder.append("[")
        for (httpParaUploadReceipt in uploadReceiptList) {
            builder.append(gson.toJson(httpParaUploadReceipt))
            builder.append(",")
        }//for
        builder.deleteCharAt(builder.length - 1)
        builder.append("]")
        //String temp = builder.toString();
        postWithParaPJsonStr(apiStrUploadReceiptArr, builder.toString(), callback)

    }*/

    fun uploadReceiptSingle(httpParaUploadReceipt: HttpParaUploadReceipt, callback: Callback) {
        val gson = Gson()
        val builder = StringBuilder()
        builder.append("[")
        builder.append(gson.toJson(httpParaUploadReceipt))
        //Log.e(mTAG, "uploadReceiptSingle json = ${gson.toJson(httpParaUploadReceipt)}")
        builder.append("]")
        //postWithParaPJsonStr(apiStrUploadReceiptArr, builder.toString(), callback)

        postWithParaPJsonStrandTimeOut(apiStrUploadReceiptArr, builder.toString(), callback)
    }

    fun uploadReceiptPointSingle(httpParaUploadReceiptPoint: HttpParaUploadReceiptPoint, callback: Callback) {
        val gson = Gson()
        val builder = StringBuilder()
        builder.append("[")
        builder.append(gson.toJson(httpParaUploadReceiptPoint))
        Log.e(mTAG, "uploadReceiptSingle json = ${gson.toJson(httpParaUploadReceiptPoint)}")
        builder.append("]")
        //postWithParaPJsonStr(apiStrUploadReceiptArr, builder.toString(), callback)

        postWithParaPJsonStrandTimeOut(apiStrUploadReceiptPointArr, builder.toString(), callback)
    }

    //storage
    /*@Throws(InterruptedException::class)
    fun UpdateFun(jsonString: String): String {//入庫單資料上傳ERP post方法
        val formBody = FormBody.Builder()
            .add("p_json", jsonString)
            .build()
        val request = Request.Builder()
            .url(HttpStr.getUpdateFun())
            .post(formBody)
            .build()
        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                responseString = "fail"
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                responseString = response.body()!!.string()
            }
        })
        Thread.sleep(500)
        return responseString
    }*/

    /*fun UpdateFunList(uploadStorageList: List<HttpParaUploadStorage>, callback: Callback) {//入庫單資料上傳ERP post方法

        val gson = Gson()
        val builder = StringBuilder()
        builder.append("[")
        for (httpParaUploadReceipt in uploadStorageList) {
            builder.append(gson.toJson(HttpParaUploadStorage))
            builder.append(",")
        }//for
        builder.deleteCharAt(builder.length - 1)
        builder.append("]")
        //String temp = builder.toString();
        postWithParaPJsonStr(apiStrGetUpdateFun, builder.toString(), callback)

        //postWithParaPJsonStr(apiStrGetUpdateFun, jsonString, callback)

    }*/

    fun updateStorageSingle(httpParaUploadStorage: HttpParaUploadStorage, callback: Callback) {
        val gson = Gson()
        val builder = StringBuilder()
        builder.append("[")
        builder.append(gson.toJson(httpParaUploadStorage))
        builder.append("]")

        postWithParaPJsonStrandTimeOut(apiStrGetUpdateFun, builder.toString(), callback)
    }

    fun updateMaterialSend(httpParaUploadMaterial: HttpParaUploadMaterial, callback: Callback) {
        Log.e(mTAG, "->updateMaterialSend")
        val body = FormBody.Builder()
            .add("p_sfs01", httpParaUploadMaterial.p_sfs01)
            .add("p_sfs02", httpParaUploadMaterial.p_sfs02)
            .add("p_sfs05", httpParaUploadMaterial.p_sfs05)
            .build()

        val request = Request.Builder()
            .url(apiStrUpdateMaterialSend)
            .post(body)
            .addHeader(ContentType.title, ContentType.xxxForm)
            .build()

        val client = OkHttpClient().newBuilder()
            //.connectTimeout(5000, TimeUnit.MILLISECONDS) //5 secs
            //.readTimeout(5000, TimeUnit.MILLISECONDS) //5 secs
            //.writeTimeout(5000, TimeUnit.MILLISECONDS) //5 secs
            .connectTimeout(timeOutSeconds, TimeUnit.SECONDS) //5 secs
            .readTimeout(timeOutSeconds, TimeUnit.SECONDS) //5 secs
            .writeTimeout(timeOutSeconds, TimeUnit.SECONDS) //5 secs
            .retryOnConnectionFailure(false)
            .build()

        try {
            val response = client.newCall(request).enqueue(callback)
            Log.d("updateMaterialSend", "response = $response")

            client.dispatcher.executorService.shutdown()
            client.connectionPool.evictAll()
            client.cache?.close()

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    //for test
    /*fun uploadOutSourcedWithSign(httpOutsourcedProcessGetPara: HttpOutsourcedProcessGetPara, callback: Callback) {
        val gson = Gson()
        val builder = StringBuilder()


        //builder.append("[")
        builder.append(gson.toJson(httpOutsourcedProcessGetPara))
        //builder.append("]")
        //postWithParaPJsonStr(apiStrUploadReceiptArr, builder.toString(), callback)

        Log.e(mTAG, "builder.length = ${builder.length}")
        Log.e(mTAG, "builder.toString() = $builder")

        //postWithParaPJsonStrandTimeOut(apiTestSign, builder.toString(), callback)
    }*/

    fun confirmUploadReceiptSend(httpParaConfirmReceiptUpload: HttpParaConfirmReceiptUpload, callback: Callback) {
        Log.e(mTAG, "->confirmUploadReceiptSend")


        val mediaType = ("application/xml; charset=utf-8").toMediaType()

        val soapString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tip=\"http://www.dsc.com.tw/tiptop/TIPTOPServiceGateWay\">\n" +
                "<soapenv:Header/>\n" +
                "<soapenv:Body>\n" +
                "<tip:CreatePOReceivingDataConfirmRequest>\n" +
                "<tip:request>\n" +
                "&lt;Request>\n" +
                "&lt;Access>\n" +
                "&lt;Authentication user=\"tiptop\" password=\"tiptop\" />\n" +
                "&lt;Connection application=\"APP\" source=\"192.1.1.38\" />\n" +
                "&lt;Organization name=\"TONIC\" />\n" +
                "&lt;Locale language=\"zh_tw\" />\n" +
                "&lt;/Access>\n" +
                "&lt;RequestContent>\n" +
                "&lt;Parameter>\n" +
                "&lt;Record>\n" +
                "&lt;/Record>\n" +
                "&lt;/Parameter>\n" +
                "&lt;Document>\n" +
                "&lt;RecordSet id=\"1\" >\n" +
                "&lt;Master name=\"rva_file\">\n" +
                "&lt;Record>\n" +
                "&lt;Field name=\"rva01\" value=\""+httpParaConfirmReceiptUpload.rva01+"\" />\n" +
                "&lt;/Record>\n" +
                "&lt;/Master>\n" +
                "&lt;/RecordSet>\n" +
                "&lt;/Document>\n" +
                "&lt;/RequestContent>\n" +
                "&lt;/Request>\n" +
                "</tip:request>\n" +
                "</tip:CreatePOReceivingDataConfirmRequest>\n" +
                "</soapenv:Body>\n" +
                "</soapenv:Envelope>"


        val body: RequestBody = soapString.toRequestBody(mediaType)

        val request = Request.Builder()
            .url(apiStrConfirmUploadReceiptArr)
            .post(body)
            .addHeader("SOAPAction", "\"\"")
            .addHeader("accept-encoding", "identity")
            .build()

        /*val body = FormBody.Builder()
            .add("rva01", httpParaConfirmReceiptUpload.rva01)
            .build()

        val request = Request.Builder()
            .url(apiStrConfirmUploadReceiptArr)
            .post(body)
            .addHeader(ContentType.title, ContentType.xxxForm)
            .build()*/

        val client = OkHttpClient().newBuilder()
            //.connectTimeout(5000, TimeUnit.MILLISECONDS) //5 secs
            //.readTimeout(5000, TimeUnit.MILLISECONDS) //5 secs
            //.writeTimeout(5000, TimeUnit.MILLISECONDS) //5 secs
            .connectTimeout(timeOutSeconds, TimeUnit.SECONDS) //5 secs
            .readTimeout(timeOutSeconds, TimeUnit.SECONDS) //5 secs
            .writeTimeout(timeOutSeconds, TimeUnit.SECONDS) //5 secs
            .retryOnConnectionFailure(false)
            .build()

        try {
            val response = client.newCall(request).enqueue(callback)
            Log.d("confirmUpload", "response = $response")


            client.dispatcher.executorService.shutdown()
            client.connectionPool.evictAll()
            client.cache?.close()

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


}