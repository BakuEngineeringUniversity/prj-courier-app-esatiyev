package com.iko.android.courier.ui.cargo.delivers

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isNotEmpty
import androidx.lifecycle.lifecycleScope
import com.iko.android.courier.R
import com.iko.android.courier.UserManager
import com.iko.android.courier.api.RetrofitInstance
import com.iko.android.courier.data.model.DeliveryStatus
import com.iko.android.courier.data.model.Package
import kotlinx.coroutines.launch
import java.util.IllegalFormatException

class DeliversCargoScrollFragment : Fragment() {

    private lateinit var rootView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootView = inflater.inflate(R.layout.fragment_delivers_cargo_scroll, container, false)

        fetchPackages()

        return rootView
    }

    private fun fetchPackages() {
        val apiService = RetrofitInstance.apiService

        lifecycleScope.launch {
            try {
                val packages = apiService.getPackagesByCourierId(UserManager.id!!)
                Log.d("DeliversCargoScrollFragment", "packages size ${packages.size}")

                if (packages.isNotEmpty()) {
                    showPackages(packages)
                } else {
                    showEmptyLayout()
                }
            } catch (e: Exception) {
                Log.e("CargoManagementActivity", "Error fetching packages: ${e.message}")
                showEmptyLayout()
            }
        }

    }

    private fun showEmptyLayout() {
        if (rootView != null) {
            val cargoesLayout = rootView.findViewById<ConstraintLayout>(R.id.empty_fragment)
            cargoesLayout?.visibility = View.VISIBLE

            val overlayView = activity?.findViewById<View>(R.id.delivers_overlayView)
            overlayView?.visibility = View.GONE
        }
    }



    private fun showPackages(cargoes: List<Package>) {
        val cargoesLayout = rootView.findViewById<LinearLayout>(R.id.delivers_cargo_list_layout)

        if (cargoesLayout.isNotEmpty()) {
            cargoesLayout.removeAllViews()
//            cargoesLayout.removeAllViewsInLayout()
        }
        for (cargo in cargoes) {
            val cargoView = layoutInflater.inflate(R.layout.item_deliver_cargo, null)
//            val reviewerName = cargoView.findViewById<TextView>(R.id.reviewer_name)
//            val reviewerName = cargoView.findViewById<TextView>(R.id.reviewer_name)
            val deliveryType = cargoView.findViewById<TextView>(R.id.delivers_delivery_type)
            val deliveryDate = cargoView.findViewById<TextView>(R.id.delivers_delivery_date)
            val pickUpAddress = cargoView.findViewById<TextView>(R.id.delivers_pick_up_address)
            val deliverAddress = cargoView.findViewById<TextView>(R.id.delivers_deliver_address)
            val weight = cargoView.findViewById<TextView>(R.id.delivers_weight)
            val price = cargoView.findViewById<TextView>(R.id.delivers_title_price)
            val request = cargoView.findViewById<TextView>(R.id.delivers_request)

//            reviewerName.text = cargo.reviewerName
            deliveryType.text = cargo.deliveryMethod
            // *
            deliveryDate.text = cargo.createdDate
            pickUpAddress.text = cargo.pickUpAddress
            deliverAddress.text = cargo.deliverAddress
            weight.text = cargo.weight.toString()
            price.text = cargo.price.toString()
            // *
            request.text = "*cost*"

            cargoView.tag = cargo.id
            val btnUpdateCargoState = cargoView.findViewById<TextView>(R.id.btn_update_cargo_state)
            if (cargo.deliveryStatus == DeliveryStatus.PACKAGE_DELIVERED) {
                btnUpdateCargoState.isClickable = false
                btnUpdateCargoState.isLongClickable = false
                btnUpdateCargoState.text = "PACKAGE IS DELIVERED!"
            }
            else {
                btnUpdateCargoState.setOnClickListener() {
                    val packageId = cargoView.tag as? String ?: ""

                    showCustomDialogBox(cargo.id.toString(), cargo.deliveryStatus)
                }
            }
            cargoesLayout.addView(cargoView)
        }

        val emptyLayout = layoutInflater.inflate(R.layout.empty_layout, null)
        cargoesLayout.addView(emptyLayout)

    }

    private fun showCustomDialogBox(packageId: String, status: DeliveryStatus) {
        val overlayView = activity?.findViewById<View>(R.id.delivers_overlayView)
        overlayView?.visibility = View.VISIBLE

        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.update_cargo_state_custom_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val stateMessage = dialog.findViewById<TextView>(R.id.stateMessage)
        val btnUpdateCargoState = dialog.findViewById<Button>(R.id.option_update_cargo_state)
        val btnCancel = dialog.findViewById<Button>(R.id.option_cancel)

        val message =
            "Your delivery package with id =\n$packageId is \n at '${status.toString()}' state"
        stateMessage.text = message



        btnUpdateCargoState.setOnClickListener {
            val apiService = RetrofitInstance.apiService

            val updatedPackageStatus = when (status) {
                DeliveryStatus.PACKAGE_CREATED -> {
                    DeliveryStatus.COURIER_ON_THE_WAY
                }
                DeliveryStatus.COURIER_ON_THE_WAY -> {
                    DeliveryStatus.COURIER_PICK_UP_PACKAGE
                }
                DeliveryStatus.COURIER_PICK_UP_PACKAGE -> {
                    DeliveryStatus.PACKAGE_DELIVERED
                }
                else -> {
                    DeliveryStatus.PACKAGE_DELIVERED
                }
            }

            val updatedPackage = Package(deliveryStatus = updatedPackageStatus)

            lifecycleScope.launch {
                try {
                    val response = apiService.updatePackage(packageId.toLong(), updatedPackage)

                    Toast.makeText(requireContext(), "Package updated successfully with id = ${response.id}." +
                            "\nCurrent state is '${response.deliveryStatus}'", Toast.LENGTH_SHORT).show()

                    dialog.dismiss()

                    overlayView?.visibility = View.GONE
                    fetchPackages()

                } catch (e : IllegalFormatException) {
                    Log.e("Update Package Step", "IllegalFormatException: ${e.message}")
                }
                catch (e: Exception) {
                    Log.e("Update Package Step", "Error fetching packages: ${e.message}")
                }
            }

        }

        btnCancel.setOnClickListener {
            Toast.makeText(requireContext(), "Cancel clicked", Toast.LENGTH_SHORT).show()
            dialog.dismiss()

            overlayView?.visibility = View.GONE
        }

        dialog.show()
    }

}