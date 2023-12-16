package com.iko.android.courier.ui.cargo.create

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.iko.android.courier.R
import com.iko.android.courier.UserManager
import com.iko.android.courier.api.RetrofitInstance
import com.iko.android.courier.ui.main.MainActivity
import com.iko.android.courier.data.model.Package
import kotlinx.coroutines.launch


class CreateCargoActivity : AppCompatActivity() {
    private lateinit var pickUpAddress: Editable
    private lateinit var deliveryAddress: Editable
    private lateinit var receiverFullName: Editable
    private lateinit var receiverPhone: Editable
    private lateinit var receiverEmail: Editable
    private lateinit var packageWeight: Editable
    private lateinit var packagePrice: Editable
    private lateinit var deliveryNote: Editable

    private lateinit var selectedOption: String
    private lateinit var btnPlaceOrder: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_cargo)
        window.navigationBarColor = resources.getColor(R.color.md_grey_200)
        window.statusBarColor = resources.getColor(R.color.colorStatusBar)

        val data = arrayOf("Standard", "Express", "Overnight")

        val adapter = ArrayAdapter(
            this,
            R.layout.spinner_dropdown_item,
            R.id.text1,
            data
        )

        val spinner = findViewById<Spinner>(R.id.spinnerOptions)
        spinner.adapter = adapter
        // Set the OnItemSelectedListener
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) {
                // Handle the selected item here
                selectedOption = data[position]
                // Do something with the selected option
                Toast.makeText(applicationContext, "Selected: $selectedOption", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // Do nothing here
            }
        }
    }

    fun backToHome(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun createPackage(view: View) {
        btnPlaceOrder = findViewById(R.id.create)

        pickUpAddress = findViewById<EditText>(R.id.pickUpEditTxt).text
        deliveryAddress = findViewById<EditText>(R.id.deliveryEditTxt).text
        receiverFullName = findViewById<EditText>(R.id.receiverNameEditTxt).text
        receiverPhone = findViewById<EditText>(R.id.receiverPhoneEditTxt).text
        receiverEmail = findViewById<EditText>(R.id.receiverMailEditTxt).text
        packageWeight = findViewById<EditText>(R.id.weightEditTxt).text
        packagePrice = findViewById<EditText>(R.id.priceEditTxt).text
        deliveryNote = findViewById<EditText>(R.id.deliveryNoteEditTxt).text


        val packageWeightAsFloat: Float
        val packagePriceAsFloat: Float

        if (pickUpAddress.isEmpty() || deliveryAddress.isEmpty() || receiverFullName.isEmpty()
            || receiverPhone.isEmpty() || receiverEmail.isEmpty() || packageWeight.isEmpty()
            || packagePrice.isEmpty() || deliveryNote.isEmpty()) {

            Toast.makeText(this, "All fields must be filled", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            packageWeightAsFloat = packageWeight.toString().toFloat()
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Invalid weight: ${e.message}", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            packagePriceAsFloat = packagePrice.toString().toFloat()
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Invalid price: ${e.message}", Toast.LENGTH_SHORT).show()
            return
        }

        val packet = Package(
            packageName="test",
            weight = packageWeightAsFloat,
            price = packagePriceAsFloat,
            deliveryMethod = selectedOption,
            deliverAddress = deliveryAddress.toString(),
            pickUpAddress = pickUpAddress.toString(),
            receiverFullName = receiverFullName.toString(),
            receiverPhone = receiverPhone.toString(),
            receiverEmail = receiverEmail.toString(),
            senderFullName = UserManager.firstname + " " + UserManager.lastname,
            senderPhone = UserManager.phone,
            senderEmail = UserManager.email,
            deliveryNote = deliveryNote.toString()
        )

        val apiService = RetrofitInstance.apiService
        lifecycleScope.launch {
            try {
                val response = apiService.createPackage(UserManager.id!!, packet)

                val message = "Your cargo with id = \n ${response.id} is \n successfully created!"

                btnPlaceOrder.visibility = View.GONE
                showCustomDialogBox(message)
            } catch (e: Exception) {
                Log.e("CreateCargoActivity", "Error creating package: ${e.message}")
            }
        }
    }

    private fun showCustomDialogBox(message: String) {

        val overlayView = findViewById<View>(R.id.overlayView)
        overlayView.visibility = View.VISIBLE

        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.layout_custom_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val tvMessage = dialog.findViewById<TextView>(R.id.tvMessage)
        val btnOk = dialog.findViewById<Button>(R.id.btnOk)

        tvMessage.text = message



        btnOk.setOnClickListener {
            dialog.dismiss()
            overlayView.visibility = View.GONE
            btnPlaceOrder.visibility = View.VISIBLE
            clearDetail()
        }


        dialog.show()
    }

    private fun clearDetail(){
        pickUpAddress.clear()
        deliveryAddress.clear()
        receiverFullName.clear()
        receiverPhone.clear()
        receiverEmail.clear()
        packageWeight.clear()
        packagePrice.clear()
        deliveryNote.clear()
    }

}