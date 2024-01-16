package com.iko.android.courier.ui.cargo.create

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.location.Geocoder
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
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.iko.android.courier.R
import com.iko.android.courier.UserManager
import com.iko.android.courier.api.RetrofitInstance
import com.iko.android.courier.ui.main.MainActivity
import com.iko.android.courier.data.model.Package
import kotlinx.coroutines.launch
import java.io.IOException


class CreateCargoActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var map: GoogleMap

//    private lateinit var pickUpAddress: TextView
//    private lateinit var deliveryAddress: TextView
    private lateinit var receiverFullName: Editable
    private lateinit var receiverPhone: Editable
    private lateinit var receiverEmail: Editable
    private lateinit var packageWeight: Editable
    private lateinit var packagePrice: Editable
    private lateinit var deliveryNote: Editable

    private lateinit var selectedOption: String
    private lateinit var data: Array<String>
    private lateinit var btnPlaceOrder: Button

    private lateinit var selectedLocation: LatLng  // Add this line


    private var pickUpLocation: LatLng? = null
    private var deliveryLocation: LatLng? = null
    private lateinit var pickUpAddressTextView: TextView
    private lateinit var deliveryAddressTextView: TextView
    companion object {
        private const val PICK_UP_LOCATION_REQUEST_CODE = 1
        private const val DELIVERY_LOCATION_REQUEST_CODE = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_cargo)
        window.navigationBarColor = resources.getColor(R.color.md_grey_200)
        window.statusBarColor = resources.getColor(R.color.colorStatusBar)

        pickUpAddressTextView = findViewById(R.id.pickUpEditTxt)
        deliveryAddressTextView = findViewById(R.id.deliveryEditTxt)

        // Initialize map fragment
//        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
//        mapFragment.getMapAsync(this)

        data = arrayOf("Standard", "Express", "Overnight")

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

        // Other initialization code...

// Set click listeners for pickup and delivery location buttons
        findViewById<Button>(R.id.btnSelectPickUpLocation).setOnClickListener {
            openMapActivity(PICK_UP_LOCATION_REQUEST_CODE)
        }

        findViewById<Button>(R.id.btnSelectDeliveryLocation).setOnClickListener {
            openMapActivity(DELIVERY_LOCATION_REQUEST_CODE)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        // You can customize map settings and add markers here if needed
    }


    // Open MapActivity to let the user select a location
    private fun openMapActivity(requestCode: Int) {
        val intent = Intent(this, MapActivity::class.java)
        if (requestCode == PICK_UP_LOCATION_REQUEST_CODE) {
//            if (pickUpLocation?.latitude != null && pickUpLocation?.longitude != null) {
                intent.putExtra("currentLatitude", pickUpLocation?.latitude)
                intent.putExtra("currentLongitude", pickUpLocation?.longitude)
//            }
        } else if (requestCode == DELIVERY_LOCATION_REQUEST_CODE) {
//            if (deliveryLocation?.latitude != null && deliveryLocation?.longitude != null) {
                intent.putExtra("currentLatitude", deliveryLocation?.latitude)
                intent.putExtra("currentLongitude", deliveryLocation?.longitude)
//            }
        }
        startActivityForResult(intent, requestCode)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("CreateCargoActivity", "onActivityResult called. RequestCode: $requestCode, ResultCode: $resultCode")

        if (resultCode == RESULT_OK && data != null) {
            val latitude = data.getDoubleExtra("latitude", 0.0)
            val longitude = data.getDoubleExtra("longitude", 0.0)
            val address = data.getStringExtra("address")?:"Address not available"
            val location = LatLng(latitude, longitude)

            when (requestCode) {
                PICK_UP_LOCATION_REQUEST_CODE -> {
                    pickUpLocation = location
                    pickUpAddressTextView.text = getAddress(address)
                }
                DELIVERY_LOCATION_REQUEST_CODE -> {
                    deliveryLocation = location
                    deliveryAddressTextView.text = getAddress(address)
                }
            }
        }
    }


    private fun getAddress(address: String): String {
        return address
    }


    fun backToHome(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun createPackage(view: View) {
        btnPlaceOrder = findViewById(R.id.create)

//        val pickUpAddress: String = pickUpAddressTextView.text.toString()
//        val deliveryAddress = deliveryAddressTextView.text.toString()
        receiverFullName = findViewById<EditText>(R.id.receiverNameEditTxt).text
        receiverPhone = findViewById<EditText>(R.id.receiverPhoneEditTxt).text
        receiverEmail = findViewById<EditText>(R.id.receiverMailEditTxt).text
        packageWeight = findViewById<EditText>(R.id.weightEditTxt).text
        packagePrice = findViewById<EditText>(R.id.priceEditTxt).text
        deliveryNote = findViewById<EditText>(R.id.deliveryNoteEditTxt).text


        val packageWeightAsFloat: Float
        val packagePriceAsFloat: Float

        if (receiverFullName.isEmpty() || receiverPhone.isEmpty() || receiverEmail.isEmpty() || packageWeight.isEmpty()
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

        if (pickUpLocation == null || pickUpAddressTextView.text.isEmpty()) {
            Toast.makeText(this, "Please select a pick-up location on the map", Toast.LENGTH_SHORT).show()
            return
        }

        if (deliveryLocation == null || deliveryAddressTextView.text.isEmpty()) {
            Toast.makeText(this, "Please select a delivery location on the map", Toast.LENGTH_SHORT).show()
            return
        }

        val packet = Package(
            packageName="test",
            weight = packageWeightAsFloat,
            price = packagePriceAsFloat,
            deliveryMethod = selectedOption,
            deliverLatitude = deliveryLocation?.latitude,
            deliverLongitude = deliveryLocation?.longitude,
            deliverAddress = deliveryAddressTextView.text.toString(),
            pickUpLatitude = pickUpLocation?.latitude,
            pickUpLongitude = pickUpLocation?.longitude,
            pickUpAddress = pickUpAddressTextView.text.toString(),
            receiverFullName = receiverFullName.toString(),
            receiverPhone = receiverPhone.toString(),
            receiverEmail = receiverEmail.toString(),
            senderFullName = UserManager.getFullName(),
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
        deliveryAddressTextView.text = ""
        pickUpAddressTextView.text = ""
        pickUpLocation = null
        deliveryLocation = null

        selectedOption = data[0]
        receiverFullName.clear()
        receiverPhone.clear()
        receiverEmail.clear()
        packageWeight.clear()
        packagePrice.clear()
        deliveryNote.clear()
    }

}