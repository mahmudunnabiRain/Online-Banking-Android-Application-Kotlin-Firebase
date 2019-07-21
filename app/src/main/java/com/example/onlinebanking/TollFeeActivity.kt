package com.example.onlinebanking

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Adapter
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_send_money_activity.*
import kotlinx.android.synthetic.main.activity_toll_fee.*
import kotlinx.android.synthetic.main.activity_toll_fee.progressBar_sm_proceed

class TollFeeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_toll_fee)


        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            this,
            R.array.planets_array,
            R.layout.spinner_select_vehicle
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner_tf_select_vehicle_type.adapter = adapter
        }
        spinner_tf_select_vehicle_type.setSelection(0)

        //search toll booth start
        val adapter = ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line)


        val db = FirebaseFirestore.getInstance()
        db.collection("toll_booth")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents)
                {
                    adapter.add(document.id)
                }
            }
            .addOnFailureListener {
                Log.d("TollFeeActivity","toll_booths_search failed")
                Toast.makeText(this,"Search failed",Toast.LENGTH_SHORT).show()
            }

        editText_tf_toll_booth.setAdapter(adapter)

        editText_tf_toll_booth.setOnDismissListener {
            //hide keyboard
            val inputManager: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(currentFocus?.windowToken, InputMethodManager.SHOW_FORCED)
        }


        button_tf_qr.setOnTouchListener OnTouchListener@{ v, event ->
            when (event.action){
                MotionEvent.ACTION_DOWN -> {
                    button_tf_qr.setBackgroundResource(R.drawable.icon_menu_bg_custom_2)
                }
                MotionEvent.ACTION_UP -> {
                    button_tf_qr.setBackgroundResource(R.drawable.button_bg_custom)

                    //initiate barcode scanner
                    IntentIntegrator(this).initiateScan()


                }
            }
            return@OnTouchListener true
        }


        button_tf_proceed.setOnTouchListener OnTouchListener@{ v, event ->
            when (event.action){
                MotionEvent.ACTION_DOWN -> {
                    button_tf_proceed.setBackgroundResource(R.drawable.icon_menu_bg_custom_2)
                }
                MotionEvent.ACTION_UP -> {
                    button_tf_proceed.setBackgroundResource(R.drawable.button_bg_custom)

                }
            }
            return@OnTouchListener true
        }



    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if(result != null)
        {
            if(result.contents == null)
            {
                Toast.makeText(this,"barcode scanning cancelled",Toast.LENGTH_SHORT).show()
            }
            else
            {
                editText_tf_toll_booth.setText(result.contents.toString())
                Toast.makeText(this,"scan complete",Toast.LENGTH_SHORT).show()
                editText_tf_toll_booth.dismissDropDown()
            }
        }
        else
        {
            //the camera will not closed if the result is not found
            super.onActivityResult(requestCode, resultCode, data)
        }
    }


}
