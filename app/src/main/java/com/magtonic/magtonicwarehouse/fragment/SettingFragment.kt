package com.magtonic.magtonicwarehouse.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
//import android.support.v4.app.Fragment
import androidx.fragment.app.Fragment
import android.util.Log

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView


import com.magtonic.magtonicwarehouse.MainActivity.Companion.mConnectedDeviceName
import com.magtonic.magtonicwarehouse.MainActivity.Companion.printerAddress
import com.magtonic.magtonicwarehouse.MainActivity.Companion.printerStatus
import com.magtonic.magtonicwarehouse.R
import com.magtonic.magtonicwarehouse.bluetoothchat.BluetoothChatService
import com.magtonic.magtonicwarehouse.data.Constants


class SettingFragment: Fragment() {
    private val mTAG = SettingFragment::class.java.name

    private var settingContext: Context? = null

    companion object {
        //private val mTAG = LoginFragment::class.java.name

        private var mReceiver: BroadcastReceiver? = null
        private var isRegister = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        settingContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Log.d(mTAG, "onCreateView")



        val view = inflater.inflate(R.layout.fragment_setting, container, false)
        val settingBluetoothMac: TextView = view.findViewById(R.id.setting_bluetooth_mac)
        val settingBluetoothName: TextView = view.findViewById(R.id.setting_bluetooth_name)
        val settingBluetoothStatus: TextView = view.findViewById(R.id.setting_bluetooth_status)
        val btnConnect: Button = view.findViewById(R.id.btnConnect)
        val btnPrintTest: Button = view.findViewById(R.id.btnPrintTest)

        when (printerStatus) {
            BluetoothChatService.STATE_NONE, BluetoothChatService.STATE_LISTEN-> {
                settingBluetoothName.text = ""
                settingBluetoothMac.text = ""
                //settingBluetoothStatus.setText(getString(R.string.setting_bluetooth_status_none))
                settingBluetoothStatus.text = getString(R.string.setting_bluetooth_status_none)
                btnPrintTest.isEnabled = false
            }

            BluetoothChatService.STATE_CONNECTING-> {
                settingBluetoothName.text = ""
                settingBluetoothMac.text = ""
                //settingBluetoothStatus.setText(getString(R.string.setting_bluetooth_status_connecting))
                settingBluetoothStatus.text = getString(R.string.setting_bluetooth_status_connecting)
                btnPrintTest.isEnabled = false
            }

            BluetoothChatService.STATE_CONNECTED-> {
                settingBluetoothName.text = mConnectedDeviceName
                settingBluetoothMac.text = printerAddress
                //settingBluetoothStatus.setText(getString(R.string.setting_bluetooth_status_connected))
                settingBluetoothStatus.text = getString(R.string.setting_bluetooth_status_connected)
                btnPrintTest.isEnabled = true
            }
        }




        settingContext = context



        btnConnect.setOnClickListener {
            settingBluetoothName.text = ""
            settingBluetoothMac.text = ""
            settingBluetoothStatus.text = getString(R.string.setting_bluetooth_status_none)

            val connectIntent = Intent()
            connectIntent.action = Constants.ACTION.ACTION_SETTING_BLUETOOTH_CONNECT_ACTION
            settingContext!!.sendBroadcast(connectIntent)
        }



        btnPrintTest.setOnClickListener {
            val testIntent = Intent()
            testIntent.action = Constants.ACTION.ACTION_SETTING_PRINTTEST_ACTION
            settingContext!!.sendBroadcast(testIntent)
        }

        val filter: IntentFilter

        mReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action != null) {
                    if (intent.action!!.equals(Constants.ACTION.ACTION_SETTING_BLUETOOTH_STATE_CHANGE, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_SETTING_BLUETOOTH_STATE_CHANGE")

                        val status: Int = intent.getIntExtra("STATUS", BluetoothChatService.STATE_NONE)
                        val deviceName: String = intent.getStringExtra("DEVICENAME") as String

                        when(status) {
                            BluetoothChatService.STATE_NONE, BluetoothChatService.STATE_LISTEN-> {
                                settingBluetoothName.text = ""
                                settingBluetoothMac.text = ""
                                settingBluetoothStatus.text = getString(R.string.setting_bluetooth_status_none)
                                btnPrintTest.isEnabled = false
                            }

                            BluetoothChatService.STATE_CONNECTING-> {
                                settingBluetoothName.text = ""
                                settingBluetoothMac.text = printerAddress
                                settingBluetoothStatus.text = getString(R.string.setting_bluetooth_status_connecting)
                                btnPrintTest.isEnabled = false
                            }

                            BluetoothChatService.STATE_CONNECTED-> {
                                settingBluetoothName.text = deviceName
                                settingBluetoothMac.text = printerAddress
                                settingBluetoothStatus.text = getString(R.string.setting_bluetooth_status_connected)
                                btnPrintTest.isEnabled = true
                            }
                        }
                    }
                }
            }
        }


        if (!isRegister) {
            filter = IntentFilter()
            filter.addAction(Constants.ACTION.ACTION_SETTING_BLUETOOTH_STATE_CHANGE)
            settingContext?.registerReceiver(mReceiver, filter)
            isRegister = true
            Log.d(mTAG, "registerReceiver mReceiver")
        }

        return view
    }

    override fun onDestroyView() {
        Log.i(mTAG, "onDestroy")

        if (isRegister && mReceiver != null) {
            try {
                settingContext!!.unregisterReceiver(mReceiver)
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            }

            isRegister = false
            mReceiver = null
            Log.d(mTAG, "unregisterReceiver mReceiver")
        }

        super.onDestroyView()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        Log.i(mTAG, "onActivityCreated")
        super.onActivityCreated(savedInstanceState)

    }
}