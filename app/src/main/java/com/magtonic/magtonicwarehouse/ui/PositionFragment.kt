package com.magtonic.magtonicwarehouse.ui

import android.R
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.magtonic.magtonicwarehouse.data.Constants
import com.magtonic.magtonicwarehouse.databinding.FragmentPositionBinding
import java.util.*

class PositionFragment : Fragment() {
    private val mTAG = PositionFragment::class.java.name

    //private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentPositionBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var progressBar: ProgressBar? = null
    private var relativeLayout: RelativeLayout? = null
    private var barcodeInput: EditText? = null
    private var linearLayout: LinearLayout? = null

    private var mReceiver: BroadcastReceiver? = null
    private var isRegister = false

    private var toastHandle: Toast? = null

    private var positionContext: Context? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        positionContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //homeViewModel =
        //    ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentPositionBinding.inflate(inflater, container, false)
        val root: View = binding.root

        relativeLayout = binding.positionContainer
        linearLayout = binding.linearLayoutPosition
        progressBar = ProgressBar(positionContext, null, R.attr.progressBarStyleLarge)

        barcodeInput = binding.editTextPoint
        //val textView: TextView = binding.textHome
        //homeViewModel.text.observe(viewLifecycleOwner, Observer {
        //    textView.text = it
        //})

        barcodeInput!!.setOnEditorActionListener { _, actionId, _ ->

            when(actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    Log.e(mTAG, "IME_ACTION_DONE")

                    //val hideIntent = Intent()
                    //hideIntent.action = Constants.ACTION.ACTION_HIDE_KEYBOARD
                    //positionContext?.sendBroadcast(hideIntent)

                    val searchIntent = Intent()
                    searchIntent.action = Constants.ACTION.ACTION_USER_INPUT_SEARCH
                    searchIntent.putExtra("INPUT_NO",
                        barcodeInput!!.text.toString().uppercase(Locale.getDefault())
                    )
                    positionContext?.sendBroadcast(searchIntent)
                    true
                }

                EditorInfo.IME_ACTION_GO -> {
                    Log.e(mTAG, "IME_ACTION_GO")
                    true
                }

                EditorInfo.IME_ACTION_NEXT -> {
                    Log.e(mTAG, "IME_ACTION_NEXT")

                    //val hideIntent = Intent()
                    //hideIntent.action = Constants.ACTION.ACTION_HIDE_KEYBOARD
                    //positionContext?.sendBroadcast(hideIntent)

                    val searchIntent = Intent()
                    searchIntent.action = Constants.ACTION.ACTION_USER_INPUT_SEARCH
                    searchIntent.putExtra("INPUT_NO",
                        barcodeInput!!.text.toString().uppercase(Locale.getDefault())
                    )
                    positionContext?.sendBroadcast(searchIntent)
                    true
                }

                EditorInfo.IME_ACTION_SEND -> {
                    Log.e(mTAG, "IME_ACTION_SEND")
                    true
                }

                else -> {
                    false
                }
            }




        }

        val filter: IntentFilter

        mReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action != null) {
                    if (intent.action!!.equals(Constants.ACTION.ACTION_BARCODE_NULL, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_BARCODE_NULL")



                        progressBar!!.visibility = View.GONE


                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_NETWORK_FAILED, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_NETWORK_FAILED")

                        progressBar!!.visibility = View.GONE



                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_CONNECTION_TIMEOUT, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_CONNECTION_TIMEOUT")

                        progressBar!!.visibility = View.GONE



                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_CONNECTION_NO_ROUTE_TO_HOST, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_CONNECTION_NO_ROUTE_TO_HOST")

                        progressBar!!.visibility = View.GONE


                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_SERVER_ERROR, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_SERVER_ERROR")

                        progressBar!!.visibility = View.GONE



                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_POSITION_SCAN_BARCODE, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_POSITION_SCAN_BARCODE")
                        val barcodeByScan = intent.getStringExtra("BARCODE_BY_SCAN")
                        //poBarcode = intent.getStringExtra("BARCODE") as String
                        //poLine = intent.getStringExtra("LINE") as String
                        barcodeInput!!.setText(barcodeByScan)

                        //removeTimer()
                        //stopTimer()

                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_POSITION_FRAGMENT_REFRESH, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_POSITION_FRAGMENT_REFRESH")

                        val data1 = intent.getStringExtra("data1")

                        Log.e(mTAG, "data1 = $data1")

                    }

                }
            }
        }

        if (!isRegister) {
            filter = IntentFilter()
            filter.addAction(Constants.ACTION.ACTION_BARCODE_NULL)
            filter.addAction(Constants.ACTION.ACTION_NETWORK_FAILED)
            filter.addAction(Constants.ACTION.ACTION_CONNECTION_TIMEOUT)
            filter.addAction(Constants.ACTION.ACTION_SERVER_ERROR)
            filter.addAction(Constants.ACTION.ACTION_POSITION_SCAN_BARCODE)
            filter.addAction(Constants.ACTION.ACTION_POSITION_FRAGMENT_REFRESH)
            //filter.addAction(Constants.ACTION.ACTION_RECEIPT_ALREADY_UPLOADED_SEND_TO_FRAGMENT)
            positionContext?.registerReceiver(mReceiver, filter)
            isRegister = true
            Log.d(mTAG, "registerReceiver mReceiver")
        }

        return root
    }

    override fun onDestroyView() {
        Log.i(mTAG, "onDestroyView")

        super.onDestroyView()
        _binding = null
    }
}