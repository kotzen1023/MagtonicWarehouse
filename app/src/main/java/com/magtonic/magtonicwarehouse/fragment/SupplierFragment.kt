package com.magtonic.magtonicwarehouse.fragment

import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.*
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleObserver

import com.magtonic.magtonicwarehouse.MainActivity
import com.magtonic.magtonicwarehouse.MainActivity.Companion.supplierList
import com.magtonic.magtonicwarehouse.R
import com.magtonic.magtonicwarehouse.data.*


class SupplierFragment : Fragment(), LifecycleObserver {
    private val mTAG = SupplierFragment::class.java.name
    private var supplierContext: Context? = null

    private var listView: ListView?= null
    private var btnAdd: Button? = null
    private var progressBar: ProgressBar? = null
    private var relativeLayout: RelativeLayout? = null

    private var supplierAdapter: SupplierAdapter? = null

    private var mReceiver: BroadcastReceiver? = null
    private var isRegister = false

    //private val colorCodePink = Color.parseColor("#D81B60")

    private var toastHandle: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(mTAG, "onCreate")

        supplierContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(mTAG, "onCreateView")

        val view = inflater.inflate(R.layout.fragment_supplier, container, false)

        relativeLayout = view.findViewById(R.id.supplier_list_container)
        progressBar = ProgressBar(supplierContext, null, android.R.attr.progressBarStyleLarge)
        val params = RelativeLayout.LayoutParams(MainActivity.screenHeight / 4, MainActivity.screenWidth / 4)
        params.addRule(RelativeLayout.CENTER_IN_PARENT)

        val localRelativeLayout: RelativeLayout? = relativeLayout
        if (localRelativeLayout != null) {
            localRelativeLayout.addView(progressBar, params)
        } else {
            Log.e(mTAG, "localRelativeLayout = null")
        }
        progressBar!!.visibility = View.GONE

        //layoutBottom = view.findViewById(R.id.layoutBottomProperty)
        //layoutBottom!!.visibility = View.GONE
        btnAdd = view.findViewById(R.id.btnAddSupplier)
        listView = view.findViewById(R.id.listViewSupplier)

        supplierAdapter = SupplierAdapter(supplierContext as Context, R.layout.fragment_supplier_item, supplierList)
        listView!!.adapter = supplierAdapter

        btnAdd!!.setOnClickListener {
            showAddSupplierDialog()

        }

        listView!!.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            Log.d(mTAG, "click $position")
        }

        listView!!.onItemLongClickListener = AdapterView.OnItemLongClickListener { _, _, position, _ ->
            Log.d(mTAG, "click $position")

            supplierList[position]

            showDeleteSupplierDialog(supplierList[position].name as String, supplierList[position].uniNumber as String, supplierList[position].key as String)


            true
        }

        val filter: IntentFilter

        mReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action != null) {
                    when {
                        intent.action!!.equals(Constants.ACTION.ACTION_SUPPLIER_DATA_CHANGE, ignoreCase = true) -> {
                            Log.d(mTAG, "ACTION_SUPPLIER_DATA_CHANGE")

                            supplierAdapter!!.notifyDataSetChanged()

                        }

                    }

                }
            }
        }

        if (!isRegister) {
            filter = IntentFilter()
            filter.addAction(Constants.ACTION.ACTION_SUPPLIER_DATA_CHANGE)

            supplierContext?.registerReceiver(mReceiver, filter)
            isRegister = true
            Log.d(mTAG, "registerReceiver mReceiver")
        }

        return view
    }

    override fun onDestroyView() {
        Log.i(mTAG, "onDestroy")

        if (isRegister && mReceiver != null) {
            try {
                supplierContext!!.unregisterReceiver(mReceiver)
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            }

            isRegister = false
            mReceiver = null
            Log.d(mTAG, "unregisterReceiver mReceiver")
        }

        super.onDestroyView()
    }

    /*override fun onActivityCreated(savedInstanceState: Bundle?) {
        Log.i(mTAG, "onActivityCreated")
        super.onActivityCreated(savedInstanceState)

    }*/

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity?.lifecycle?.addObserver(this)
    }

    private fun toast(message: String) {

        if (toastHandle != null)
            toastHandle!!.cancel()

        val toast = Toast.makeText(supplierContext, HtmlCompat.fromHtml("<h1>$message</h1>", HtmlCompat.FROM_HTML_MODE_COMPACT), Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER_HORIZONTAL or Gravity.CENTER_VERTICAL, 0, 0)

        /*val toast = Toast.makeText(propertyContext, message, Toast.LENGTH_SHORT)
         toast.setGravity(Gravity.CENTER_HORIZONTAL or Gravity.CENTER_VERTICAL, 0, 0)
         val group = toast.view as ViewGroup
         val textView = group.getChildAt(0) as TextView
         textView.textSize = 30.0f*/
        toast.show()

        toastHandle = toast
    }

    private fun showAddSupplierDialog() {

        Log.e(mTAG, "=== showAddSupplierDialog start ===")



        // get prompts.xml view
        /*LayoutInflater layoutInflater = LayoutInflater.from(Nfc_read_app.this);
        View promptView = layoutInflater.inflate(R.layout.input_dialog, null);*/
        val promptView = View.inflate(supplierContext, R.layout.fragment_supplier_add_supplier_dialog, null)

        val alertDialogBuilder = AlertDialog.Builder(supplierContext).create()
        alertDialogBuilder.setView(promptView)

        //final EditText editFileName = (EditText) promptView.findViewById(R.id.editFileName);

        val editTextSupplierName = promptView.findViewById<EditText>(R.id.editTextSupplierName)
        val editTextSupplierNumber = promptView.findViewById<EditText>(R.id.editTextSupplierNumber)

        editTextSupplierName.inputType = InputType.TYPE_CLASS_TEXT

        val btnCancel = promptView.findViewById<Button>(R.id.btnSupplierDialogCancel)
        val btnConfirm = promptView.findViewById<Button>(R.id.btnSupplierDialogConfirm)

        alertDialogBuilder.setCancelable(false)
        btnCancel!!.setOnClickListener {
            alertDialogBuilder.dismiss()
        }
        btnConfirm!!.setOnClickListener {
            /*progressBar!!.indeterminateTintList = ColorStateList.valueOf(colorCodePink)
            progressBar!!.visibility = View.VISIBLE

            btnConfirm!!.isEnabled = false
            */

            if (editTextSupplierName.text.toString() == "" || editTextSupplierNumber.text.toString() == "") {
                toast(getString(R.string.supplier_add_empty_name_or_number))
            } else {
                var found = false
                for (i in 0 until supplierList.size) {
                    if (editTextSupplierName.text.toString() == supplierList[i].name ||
                        editTextSupplierNumber.text.toString() == supplierList[i].uniNumber) {
                        found = true
                        break
                    }
                }

                if (!found) {
                    val addIntent = Intent()
                    addIntent.action = Constants.ACTION.ACTION_SUPPLIER_DATA_ADD
                    addIntent.putExtra("SUPPLIER_NAME", editTextSupplierName.text.toString())
                    addIntent.putExtra("SUPPLIER_NUMBER", editTextSupplierNumber.text.toString())
                    supplierContext!!.sendBroadcast(addIntent)

                    alertDialogBuilder.dismiss()
                } else {
                    toast("已有相同的廠商名稱或統編")
                }
            }





        }
        alertDialogBuilder.show()
    }

    private fun showDeleteSupplierDialog(name: String, number: String, key: String) {

        Log.e(mTAG, "=== showDeleteSupplierDialog start ===")



        // get prompts.xml view
        /*LayoutInflater layoutInflater = LayoutInflater.from(Nfc_read_app.this);
        View promptView = layoutInflater.inflate(R.layout.input_dialog, null);*/
        val promptView = View.inflate(supplierContext, R.layout.fragment_supplier_add_supplier_dialog, null)

        val alertDialogBuilder = AlertDialog.Builder(supplierContext).create()
        alertDialogBuilder.setView(promptView)

        //final EditText editFileName = (EditText) promptView.findViewById(R.id.editFileName);
        val textViewSupplierDialog = promptView.findViewById<TextView>(R.id.textViewSupplierDialog)

        textViewSupplierDialog.setText(R.string.supplier_delete_dialog_title)

        val editTextSupplierName = promptView.findViewById<EditText>(R.id.editTextSupplierName)
        val editTextSupplierNumber = promptView.findViewById<EditText>(R.id.editTextSupplierNumber)
        editTextSupplierName.setText(name)
        editTextSupplierName.inputType = InputType.TYPE_NULL
        editTextSupplierNumber.setText(number)
        editTextSupplierNumber.inputType = InputType.TYPE_NULL

        val btnCancel = promptView.findViewById<Button>(R.id.btnSupplierDialogCancel)
        val btnConfirm = promptView.findViewById<Button>(R.id.btnSupplierDialogConfirm)
        //val btnDelete = promptView.findViewById<Button>(R.id.btnSupplierDialogDelete)
        //btnDelete.visibility = View.VISIBLE



        alertDialogBuilder.setCancelable(false)

        //btnDelete!!.setOnClickListener {
        //    alertDialogBuilder.dismiss()
        //}

        btnCancel!!.setOnClickListener {
            alertDialogBuilder.dismiss()
        }
        btnConfirm!!.setOnClickListener {

            val deleteIntent = Intent()
            deleteIntent.action = Constants.ACTION.ACTION_SUPPLIER_DATA_DELETE
            deleteIntent.putExtra("SUPPLIER_KEY", key)
            supplierContext!!.sendBroadcast(deleteIntent)

            alertDialogBuilder.dismiss()
        }
        alertDialogBuilder.show()
    }
}