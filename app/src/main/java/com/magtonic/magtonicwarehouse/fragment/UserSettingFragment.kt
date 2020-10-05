package com.magtonic.magtonicwarehouse.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.magtonic.magtonicwarehouse.MainActivity.Companion.isLogEnable
import com.magtonic.magtonicwarehouse.MainActivity.Companion.isReceiptUploadAutoConfirm
import com.magtonic.magtonicwarehouse.MainActivity.Companion.timeOutSeconds
import com.magtonic.magtonicwarehouse.R
import com.magtonic.magtonicwarehouse.data.Constants

class UserSettingFragment : Fragment() {
    private val mTAG = UserSettingFragment::class.java.name
    private var userSettingContext: Context? = null

    //private val timeoutSecondsHashMap = HashMap<String, String>()
    private val timeoutSecondsNameList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userSettingContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Log.d(mTAG, "onCreateView")

        /*timeoutSecondsHashMap.clear()
        timeoutSecondsHashMap["5"] = "5"
        timeoutSecondsHashMap["10"] = "10"
        timeoutSecondsHashMap["15"] = "15"
        timeoutSecondsHashMap["20"] = "20"
        timeoutSecondsHashMap["25"] = "25"
        timeoutSecondsHashMap["30"] = "30"
        timeoutSecondsHashMap["35"] = "35"
        timeoutSecondsHashMap["40"] = "40"
        timeoutSecondsHashMap["45"] = "45"
        timeoutSecondsHashMap["50"] = "50"
        timeoutSecondsHashMap["55"] = "55"
        timeoutSecondsHashMap["60"] = "60"*/

        timeoutSecondsNameList.clear()
        timeoutSecondsNameList.add("5")
        timeoutSecondsNameList.add("10")
        timeoutSecondsNameList.add("15")
        timeoutSecondsNameList.add("20")
        timeoutSecondsNameList.add("25")
        timeoutSecondsNameList.add("30")
        timeoutSecondsNameList.add("35")
        timeoutSecondsNameList.add("40")
        timeoutSecondsNameList.add("45")
        timeoutSecondsNameList.add("50")
        timeoutSecondsNameList.add("55")
        timeoutSecondsNameList.add("60")

        val view = inflater.inflate(R.layout.fragment_user_setting, container, false)
        val receiptAutoConfirmUploaded = view.findViewById<CheckBox>(R.id.checkBoxReceiptAutoConfirmUploaded)
        val logEnable = view.findViewById<CheckBox>(R.id.checkBoxLogEnable)
        val timeoutSpinner = view.findViewById<Spinner>(R.id.timeoutSpinner)

        val adapter: ArrayAdapter<String> = ArrayAdapter(userSettingContext as Context, R.layout.myspinner, timeoutSecondsNameList)
        timeoutSpinner.adapter = adapter

        var select = 0
        for (i in 0 until timeoutSecondsNameList.size) {
            if (timeOutSeconds.toString() == (timeoutSecondsNameList[i]))
            {
                select = i;
            }
        }
        timeoutSpinner.setSelection(select)

        timeoutSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                Log.e(mTAG, "position = $position")

                if (timeOutSeconds != timeoutSecondsNameList[position].toLong()) {
                    val successIntent = Intent()
                    successIntent.putExtra("TIMEOUT_SECOND", timeoutSecondsNameList[position])
                    successIntent.action = Constants.ACTION.ACTION_SETTING_TIMEOUT_CHANGE
                    userSettingContext!!.sendBroadcast(successIntent)
                }


            }

        }

        receiptAutoConfirmUploaded.isChecked = isReceiptUploadAutoConfirm
        logEnable.isChecked = isLogEnable

        receiptAutoConfirmUploaded.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                val successIntent = Intent()
                successIntent.action = Constants.ACTION.ACTION_SETTING_RECEIPT_AUTO_CONFIRM_UPLOADED_ON
                userSettingContext!!.sendBroadcast(successIntent)
            } else {
                val successIntent = Intent()
                successIntent.action = Constants.ACTION.ACTION_SETTING_RECEIPT_AUTO_CONFIRM_UPLOADED_OFF
                userSettingContext!!.sendBroadcast(successIntent)
            }

        }

        logEnable.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                val successIntent = Intent()
                successIntent.action = Constants.ACTION.ACTION_SETTING_LOG_ENABLE_ON
                userSettingContext!!.sendBroadcast(successIntent)
            } else {
                val successIntent = Intent()
                successIntent.action = Constants.ACTION.ACTION_SETTING_LOG_ENABLE_OFF
                userSettingContext!!.sendBroadcast(successIntent)
            }

        }


        return view
    }

    override fun onDestroyView() {
        Log.i(mTAG, "onDestroy")



        super.onDestroyView()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        Log.i(mTAG, "onActivityCreated")
        super.onActivityCreated(savedInstanceState)

    }
}