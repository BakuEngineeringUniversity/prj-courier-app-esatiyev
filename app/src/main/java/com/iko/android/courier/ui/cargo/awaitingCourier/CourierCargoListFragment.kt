package com.iko.android.courier.ui.cargo.awaitingCourier

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isNotEmpty
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.model.LatLng
import com.iko.android.courier.R
import com.iko.android.courier.UserManager
import com.iko.android.courier.api.RetrofitInstance
import com.iko.android.courier.data.model.DeliveryStatus
import com.iko.android.courier.data.model.Package
import kotlinx.coroutines.launch
import java.util.IllegalFormatException


class CourierCargoListFragment : Fragment() {
    private lateinit var rootView: View
    private lateinit var cargoesLayout: LinearLayout
    // Create a variable to store the last click time
    private var lastClickTime: Long = 0
    // Create a delay (in milliseconds) for double-click detection
    private val doubleClickDelay: Long = 300

    private lateinit var progressBar: ProgressBar
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_courier_cargo_list, container, false)

        progressBar = requireActivity().findViewById<ProgressBar>(R.id.awaiting_courier_cargo_progress_bar)
        progressBar.visibility = View.VISIBLE
        fetchPackages()

        return rootView
    }

    private fun fetchPackages() {
        val apiService = RetrofitInstance.apiService

        lifecycleScope.launch {
            try {
                val packages = apiService.getAllPackages()

                showPackages(packages)
                progressBar.visibility = View.GONE
            } catch (e: Exception) {
                Log.e("CargoManagementActivity", "Error fetching packages: ${e.message}")
            }
        }
    }

    private fun showPackages(cargoes: List<Package>) {

        cargoesLayout = rootView.findViewById<LinearLayout>(R.id.courier_cargo_list_layout)

        if (cargoesLayout.isNotEmpty()) {
            cargoesLayout.removeAllViews()
        }

        for (cargo in cargoes) {
            // if package is created by current user, skip it
            if (cargo.senderEmail == UserManager.email || cargo.courierEmail != null)
                continue

            val cargoView = layoutInflater.inflate(R.layout.item_cargo, null)
//            val reviewerName = cargoView.findViewById<TextView>(R.id.reviewer_name)
            val deliveryType = cargoView.findViewById<TextView>(R.id.title_delivery_type)
            val deliveryDate = cargoView.findViewById<TextView>(R.id.title_time_label)
            val pickUpAddress = cargoView.findViewById<TextView>(R.id.title_from_address)
            val deliverAddress = cargoView.findViewById<TextView>(R.id.title_to_address)
            val weight = cargoView.findViewById<TextView>(R.id.title_weight)
            val price = cargoView.findViewById<TextView>(R.id.title_price)
            val request = cargoView.findViewById<TextView>(R.id.title_request)

            deliveryType.text = cargo.deliveryMethod
            // *
            deliveryDate.text = cargo.createdDate
            pickUpAddress.text = cargo.pickUpAddress
            deliverAddress.text = cargo.deliverAddress
            weight.text = cargo.weight.toString()
            price.text = cargo.price.toString()
            // shipping cost elave edilecek intellijde fun yazilmaliir
            request.text = "*cost*"

            // Cargo_Detail
            val addressFrom = cargoView.findViewById<TextView>(R.id.address_from)
            val addressTo = cargoView.findViewById<TextView>(R.id.address_to)
            val weightDt = cargoView.findViewById<TextView>(R.id.dt_weight)
            val priceDt = cargoView.findViewById<TextView>(R.id.dt_price)
            val deliveryMethodDt = cargoView.findViewById<TextView>(R.id.dt_delivery_type)
            val senderFullNameDt = cargoView.findViewById<TextView>(R.id.sender_full_name)
            val senderPhoneDt = cargoView.findViewById<TextView>(R.id.sender_phone_number)
            val senderEmailDt = cargoView.findViewById<TextView>(R.id.sender_email)
            val receiverFullNameDt = cargoView.findViewById<TextView>(R.id.receiver_full_name)
            val receiverPhoneDt = cargoView.findViewById<TextView>(R.id.receiver_phone_number)
            val receiverEmailDt = cargoView.findViewById<TextView>(R.id.receiver_email)

            addressFrom.text = cargo.pickUpAddress
            addressTo.text = cargo.deliverAddress
            weightDt.text = cargo.weight.toString()
            priceDt.text = cargo.price.toString()
            deliveryMethodDt.text = cargo.deliveryMethod
            senderFullNameDt.text = cargo.senderFullName
            senderPhoneDt.text = cargo.senderPhone
            senderEmailDt.text = cargo.senderEmail
            receiverFullNameDt.text = cargo.receiverFullName
            receiverPhoneDt.text = cargo.receiverPhone
            receiverEmailDt.text = cargo.receiverEmail


            cargoView.setOnClickListener() {
                val clickTime = System.currentTimeMillis()
                if (clickTime - lastClickTime < doubleClickDelay) {
                    toggleCargoDetailVisibility(cargoView)
                }
                lastClickTime = clickTime
            }

            // accept Order
            val btnAcceptOrder = cargoView.findViewById<TextView>(R.id.btn_accept_order)
            btnAcceptOrder.setOnClickListener() {
                val packageId = cargoView.tag as? String ?: ""
                acceptOrder(cargo.id!!);
            }

            val btnShowRouteOnMap = cargoView.findViewById<TextView>(R.id.btn_show_route)
            btnShowRouteOnMap.setOnClickListener() {
                if (cargo.deliverLatitude == null || cargo.deliverLongitude == null ||
                    cargo.pickUpLatitude == null || cargo.pickUpLongitude == null) {
                        Toast.makeText(requireContext(), "Route can't be shown", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                } else {
                    val pickupLatLng = LatLng(cargo.pickUpLatitude!!, cargo.pickUpLongitude!!)
                    val deliveryLatLng = LatLng(cargo.deliverLatitude!!, cargo.deliverLongitude!!)
                    showRouteOnGoogleMaps(pickupLatLng, deliveryLatLng)
                }
            }


            cargoesLayout.addView(cargoView)
        }

        val emptyLayout = layoutInflater.inflate(R.layout.empty_layout, null)

        val myLinearLayout = emptyLayout.findViewById<LinearLayout>(R.id.empty_linear_layout)

        val newHeight = 10 // Change this to your desired height in pixels or dp
        val layoutParams = myLinearLayout.layoutParams
        layoutParams.height = newHeight
        myLinearLayout.layoutParams = layoutParams

        cargoesLayout.addView(emptyLayout)
    }

    fun showRouteOnGoogleMaps(pickup: LatLng, delivery: LatLng) {
        val uri = "https://www.google.com/maps/dir/?api=1" +
                "&origin=${pickup.latitude},${pickup.longitude}" +
                "&destination=${delivery.latitude},${delivery.longitude}" +
                "&travelmode=driving"  // You can change the travel mode as needed

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        intent.setPackage("com.google.android.apps.maps")

        // Check if the Google Maps app is installed
        if (intent.resolveActivity(requireContext().packageManager) != null) {
            startActivity(intent)
        } else {
            // If Google Maps app is not installed, open in a web browser
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            startActivity(webIntent)
        }
    }


    private fun acceptOrder(packageId: Long) {
        val apiService = RetrofitInstance.apiService

        lifecycleScope.launch {
            try {
                val response =apiService.addPackageToCourier(packageId, UserManager.id!!)
                if (response.isSuccessful) {
                    // Operation successful, handle accordingly
                    Toast.makeText(requireContext(), "Package added to courier successfully", Toast.LENGTH_SHORT).show()
                    if (cargoesLayout.isNotEmpty()) {
                        cargoesLayout.removeAllViews()
                    }

                    fetchPackages()

                    updatePackageState(packageId)
                } else {
                    // Operation failed, handle accordingly
                    Toast.makeText(requireContext(), "Error: ${response.code()}", Toast.LENGTH_SHORT).show()

                }
            } catch (e: Exception) {
                Log.e("Accept Order", "Error accepting order: ${e.message}")
            }
        }


    }

    fun updatePackageState(packageId: Long) {
        val apiService = RetrofitInstance.apiService

        val updatedPackage = Package(deliveryStatus = DeliveryStatus.COURIER_ON_THE_WAY)

        lifecycleScope.launch {
            try {
                val response = apiService.updatePackage(packageId, updatedPackage)

                Toast.makeText(requireContext(), "Package updated successfully with id = ${response.id}." +
                        "\nCurrent state is '${response.deliveryStatus}'", Toast.LENGTH_SHORT).show()

            } catch (e : IllegalFormatException) {
                Log.e("Update Package Step", "IllegalFormatException: ${e.message}")
            }
            catch (e: Exception) {
                Log.e("Update Package Step", "Error fetching packages: ${e.message}")
            }
        }
    }


    private fun toggleCargoDetailVisibility(cargoView: View) {
        val detailLayout = cargoView.findViewById<View>(R.id.detail)
        val shortInfoLayout = cargoView.findViewById<View>(R.id.short_info)

        if (detailLayout.visibility == View.VISIBLE) {
            detailLayout.visibility = View.GONE
            shortInfoLayout.visibility = View.VISIBLE
        } else {
            detailLayout.visibility = View.VISIBLE
            shortInfoLayout.visibility = View.GONE

            val reviewerName = cargoView.findViewById<TextView>(R.id.reviewer_name)
        }
    }

}