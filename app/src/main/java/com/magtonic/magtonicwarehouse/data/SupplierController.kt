package com.magtonic.magtonicwarehouse.data

import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.magtonic.magtonicwarehouse.MainActivity
import com.magtonic.magtonicwarehouse.MainActivity.Companion.db
import com.magtonic.magtonicwarehouse.MainActivity.Companion.outsourcedSupplierHashMap
import com.magtonic.magtonicwarehouse.MainActivity.Companion.outsourcedSupplierNameList
import com.magtonic.magtonicwarehouse.MainActivity.Companion.supplierDataList
import com.magtonic.magtonicwarehouse.MainActivity.Companion.supplierList
import com.magtonic.magtonicwarehouse.persistence.SupplierData

class SupplierController(private val database: FirebaseDatabase, private val context: Context) {
    private val mTAG = MainActivity::class.java.name

    //var supplierList = ArrayList<Supplier>()

    private val dataListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            // Get Post object and use the values to update the UI

            supplierList.clear()
            outsourcedSupplierHashMap.clear()
            outsourcedSupplierNameList.clear()

            supplierDataList.clear()
            //clear database
            if (db != null) {
                db!!.supplierDataDao().clearTable()
            }

            for (snapshot in dataSnapshot.children) {

                //Log.e(mTAG, "snapshot : $snapshot")

                val key = snapshot.key
                val name = snapshot.child("name").value
                val uniNumber = snapshot.child("uniNumber").value

                val supplier = Supplier(name as String , uniNumber as String)
                supplier.key = key
                supplierList.add(supplier)

                outsourcedSupplierHashMap[name] = uniNumber

                outsourcedSupplierNameList.add(name)

                //for persistence database
                val supplierData = SupplierData(key as String, name, uniNumber)
                db!!.supplierDataDao().insert(supplierData)
            }

            supplierDataList = db!!.supplierDataDao().getAll() as ArrayList<SupplierData>

            val showIntent = Intent()
            showIntent.action = Constants.ACTION.ACTION_SUPPLIER_DATA_CHANGE
            context.sendBroadcast(showIntent)
            /*for (i in 0 until supplierList.size) {

                Log.e(mTAG, "supplierController.supplierList[$i] = ${supplierList[i].key}")

            }*/
        }

        override fun onCancelled(databaseError: DatabaseError) {
            // Getting Post failed, log a message
            Log.w(mTAG, "load supplier :onCancelled", databaseError.toException())
            // ...
            outsourcedSupplierHashMap["萬興"] = "24924616"
            outsourcedSupplierHashMap["信旭"] = "22549100"
            outsourcedSupplierHashMap["順升昌"] = "84431670"
            outsourcedSupplierHashMap["廈興"] = "28861417"
            outsourcedSupplierHashMap["迦賢"] = "23082263"
            outsourcedSupplierHashMap["鋐偉"] = "20294906"
            outsourcedSupplierHashMap["宏通"] = "10475032"
            outsourcedSupplierHashMap["弘福興"] = "42929389"
            outsourcedSupplierHashMap["原茂"] = "37577167"
            outsourcedSupplierHashMap["佳滿利"] = "27800461"
            outsourcedSupplierHashMap["日鋒"] = "97199386"
            outsourcedSupplierHashMap["鈺晃"] = "25081394"
            outsourcedSupplierHashMap["錦一"] = "53152589"
            outsourcedSupplierHashMap["盛豐"] = "89342228"
            outsourcedSupplierHashMap["鴻通海"] = "16660219"
            outsourcedSupplierHashMap["頡宥"] = "42914225"
            outsourcedSupplierHashMap["頡亮"] = "53610142"
            outsourcedSupplierHashMap["政泰"] = "29128266"
            outsourcedSupplierHashMap["允潔"] = "27887071"
            outsourcedSupplierHashMap["聖岱1"] = "06515434"
            outsourcedSupplierHashMap["聖岱2"] = "85031855"
            outsourcedSupplierHashMap["南隆"] = "22814493"
            outsourcedSupplierHashMap["昶太"] = "24276225"
            outsourcedSupplierHashMap["鋐偉1"] = "85008897"
            outsourcedSupplierHashMap["鋐偉2"] = "00294906"
            outsourcedSupplierHashMap["聚惠"] = "88654026"
            outsourcedSupplierHashMap["溢倫"] = "22291827"
            outsourcedSupplierHashMap["福記"] = "73637773"
            outsourcedSupplierHashMap["衫億"] = "80355469"
            outsourcedSupplierHashMap["雙龍興"] = "69750962"



            outsourcedSupplierNameList.add("萬興")
            outsourcedSupplierNameList.add("信旭")
            outsourcedSupplierNameList.add("順升昌")
            outsourcedSupplierNameList.add("廈興")
            outsourcedSupplierNameList.add("迦賢")
            outsourcedSupplierNameList.add("鋐偉")
            outsourcedSupplierNameList.add("宏通")
            outsourcedSupplierNameList.add("弘福興")
            outsourcedSupplierNameList.add("原茂")
            outsourcedSupplierNameList.add("佳滿利")
            outsourcedSupplierNameList.add("日鋒")
            outsourcedSupplierNameList.add("鈺晃")
            outsourcedSupplierNameList.add("錦一")
            outsourcedSupplierNameList.add("盛豐")
            outsourcedSupplierNameList.add("鴻通海")
            outsourcedSupplierNameList.add("頡宥")
            outsourcedSupplierNameList.add("頡亮")
            outsourcedSupplierNameList.add("政泰")
            outsourcedSupplierNameList.add("允潔")
            outsourcedSupplierNameList.add("聖岱1")
            outsourcedSupplierNameList.add("聖岱2")
            outsourcedSupplierNameList.add("南隆")
            outsourcedSupplierNameList.add("昶太")
            outsourcedSupplierNameList.add("鋐偉1")
            outsourcedSupplierNameList.add("鋐偉2")
            outsourcedSupplierNameList.add("聚惠")
            outsourcedSupplierNameList.add("溢倫")
            outsourcedSupplierNameList.add("福記")
            outsourcedSupplierNameList.add("衫億")
            outsourcedSupplierNameList.add("雙龍興")
        }
    }

    init {
        val ref = database.getReference("suppliers")

        //ref.addListenerForSingleValueEvent(dataListener)
        ref.addValueEventListener(dataListener)
    }

    fun writeNewSupplier(supplier: Supplier) : String {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        val ref = database.getReference("suppliers")
        val key = ref.push().key
        if (key == null) {
            Log.w(mTAG, "Couldn't get push key for posts")
        } else {
            val post = Supplier(supplier.name as String, supplier.uniNumber as String)
            val postValues = post.toMap()

            val childUpdates = hashMapOf<String, Any>(
                //"/posts/$key" to postValues,
                //"/user-posts/$userId/$key" to postValues
                "$key" to postValues
            )

            ///database.updateChildren(childUpdates)
            ref.updateChildren(childUpdates)
        }
        return key as String
    }

    fun updateSupplier(name: String, number: String, key: String) {
        val ref = database.getReference("suppliers/$key")

        ref.child("name").setValue(name)
            .addOnSuccessListener {
                // Write was successful!
                // ...
            }
            .addOnFailureListener {
                // Write failed
                // ...
            }

        ref.child("uniNumber").setValue(number)
            .addOnSuccessListener {
                // Write was successful!
                // ...
            }
            .addOnFailureListener {
                // Write failed
                // ...
            }
    }

    fun removeSupplier(key: String) {
        val ref = database.getReference("suppliers/$key")
        ref.removeValue()
    }

    /*fun readAllSuppliers() {
        val ref = database.getReference("suppliers")

        ref.addListenerForSingleValueEvent(dataListener)
    }*/
}