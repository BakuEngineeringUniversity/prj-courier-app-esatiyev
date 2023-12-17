package com.iko.android.courier.ui.cargo.ownCargo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.iko.android.courier.R
import com.iko.android.courier.api.RetrofitInstance
import com.iko.android.courier.data.model.DeliveryStatus
import com.iko.android.courier.data.model.Package
import com.iko.android.courier.ui.cargo.state.CargoManagementActivity
import com.iko.android.courier.viewmodel.CargoViewModel
import com.iko.android.courier.viewmodel.factory.CargoViewModelFactory
import kotlinx.coroutines.launch

class OwnCargoScrollFragment : Fragment() {

    private lateinit var rootView: View
    private lateinit var cargoesLayout: LinearLayout
    private lateinit var viewModel: CargoViewModel



    //    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Lazily initialize ViewModel
        viewModel = ViewModelProvider(
            this,
            CargoViewModelFactory(RetrofitInstance.apiService)
        )[CargoViewModel::class.java]

        rootView = inflater.inflate(R.layout.fragment_own_cargo_scroll, container, false)

        fetchPackages()

        return rootView

    }

    private fun fetchPackages() {
        // Observe changes in customer packages
        viewModel.customerPackages.observe(viewLifecycleOwner, Observer { packages ->
            // Update UI with the list of packages
            // For now, you can log the packages to check if they are fetched correctly

            Log.d("CargoFragment", "Packages: ${packages.size}")
            packages.forEach { packet ->
                Log.d("CargoFragment", "Package: ${packet.id}, ${packet.deliveryMethod}, ${packet.createdDate}, ${packet.pickUpAddress}, ${packet.deliverAddress}, ${packet.weight}, ${packet.price}, ${packet.customer}")
            }
            // Fetch or generate review data (e.g., from an API or a local data source)
            showPackages(packages)
        })

        // Fetch customer packages
        viewModel.fetchCustomerPackages()
    }

    private fun showPackages(packages: List<Package>) {
        //         Fetch or generate review data (e.g., from an API or a local data source)
        cargoesLayout = rootView.findViewById<LinearLayout>(R.id.own_cargo_list_layout)


        cargoesLayout.removeAllViews()


        // Loop through the reviews and create review views
        for (packet in packages) {

            val cargoView = layoutInflater.inflate(R.layout.item_own_cargo, null)
//            val reviewerName = cargoView.findViewById<TextView>(R.id.reviewer_name)
            val deliveryType = cargoView.findViewById<TextView>(R.id.delivery_type)
            val deliveryDate = cargoView.findViewById<TextView>(R.id.delivery_date)
            val pickUpAddress = cargoView.findViewById<TextView>(R.id.pick_up_address)
            val deliverAddress = cargoView.findViewById<TextView>(R.id.deliver_address)
            val weight = cargoView.findViewById<TextView>(R.id.weight)
            val price = cargoView.findViewById<TextView>(R.id.title_price)
            val request = cargoView.findViewById<TextView>(R.id.request)

            deliveryType.text = packet.deliveryMethod
            // duzelis
            deliveryDate.text = packet.createdDate
            pickUpAddress.text = packet.pickUpAddress
            deliverAddress.text = packet.deliverAddress
            weight.text = packet.weight.toString()
            price.text = packet.price.toString()
            // duzelis
            request.text = packet.id.toString()

            cargoView.tag = packet.id
            val trackBtn = cargoView.findViewById<TextView>(R.id.btn_track)
            val deleteBtn = cargoView.findViewById<TextView>(R.id.btn_delete)

            trackBtn.setOnClickListener {
//                    val packageId = cargoView.tag as? String ?: ""
//                if (packet.id != null)
                val packageId = packet.id.toString()

                navigateToCargoStateActivity(packageId)
            }

            deleteBtn.setOnClickListener {
                if (packet.deliveryStatus == DeliveryStatus.PACKAGE_CREATED) {
                    deletePackage(packet.id!!)
                }
                else {
                    Toast.makeText(requireContext(), "Package can't be deleted.\nPlease contact support", Toast.LENGTH_SHORT).show()
                }
            }
            cargoesLayout.addView(cargoView)
        }

        val emptyLayout = layoutInflater.inflate(R.layout.empty_layout, null)
        cargoesLayout.addView(emptyLayout)
    }

    private fun deletePackage(packageId: Long) {
        val apiService = RetrofitInstance.apiService

        lifecycleScope.launch {
            try {
                val response = apiService.deletePackage(packageId)
                if (response.isSuccessful) {
                    cargoesLayout.removeAllViews()
                    fetchPackages()

                    Toast.makeText(requireContext(), "Package deleted successfully", Toast.LENGTH_SHORT).show()
//                        cargoesLayout.removeAllViewsInLayout()
                } else {
                    Toast.makeText(requireContext(), "Failed to delete package", Toast.LENGTH_SHORT).show()
                }
            } catch (e: NoSuchElementException) {
                Log.e("ProfileFragment", "No such package: ${e.message}")
            } catch (e: Exception) {
                Log.e("ProfileFragment", "Error deleting package: ${e.message}")
            }
        }
    }

    private fun navigateToCargoStateActivity(packageId: String) {
        val intent = Intent(context, CargoManagementActivity::class.java)
        intent.putExtra("packageId", packageId) // Pass cargo information to the next activity
        startActivity(intent)
    }


}