package com.iko.android.courier.ui.cargo.state

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.iko.android.courier.R
import com.iko.android.courier.api.RetrofitInstance
import com.iko.android.courier.data.model.DeliveryStatus
import com.iko.android.courier.data.model.Package
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CargoStateFragment : Fragment() {

    private lateinit var rootView: View
    private lateinit var chk_on_way :CheckBox
    private lateinit var chk_pick_up :CheckBox
    private lateinit var chk_delivered :CheckBox
    private lateinit var chk_created :CheckBox

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        rootView = inflater.inflate(R.layout.fragment_cargo_state, container, false)

        val packageId = arguments?.getString("packageId")

        val apiService = RetrofitInstance.apiService

        lifecycleScope.launch {
            try {
                val packageDetails = apiService.getPackageById(packageId!!.toLong())

                updateUI(packageDetails)
            } catch (e: Exception) {
                // Handle errors
                Log.e("CargoManagementActivity", "Error fetching package details: ${e.message}")
            }
        }

        return rootView
    }

    private fun updateUI(packageDetails: Package) {

        chk_on_way = rootView.findViewById(R.id.chk_on_way)
        chk_pick_up = rootView.findViewById(R.id.chk_pick_up)
        chk_delivered = rootView.findViewById(R.id.chk_deliver)
        chk_created = rootView.findViewById(R.id.chk_created)

        val createdTime = rootView.findViewById<TextView>(R.id.created_time)
        val courierOnTheWayTime = rootView.findViewById<TextView>(R.id.courier_on_way_time)
        val pickUpTime = rootView.findViewById<TextView>(R.id.pick_up_time)
        val deliverTime = rootView.findViewById<TextView>(R.id.deliver_time)

        val dividerCreated = rootView.findViewById<View>(R.id.divider_created)
        val dividerOnWay = rootView.findViewById<View>(R.id.divider_courier_on_way)
        val dividerPickUp = rootView.findViewById<View>(R.id.divider_pick_up)

        when (packageDetails.deliveryStatus) {
            DeliveryStatus.PACKAGE_DELIVERED -> {
                chk_delivered.isChecked = true
                chk_pick_up.isChecked = true
                chk_on_way.isChecked = true
                dividerCreated.backgroundTintList = resources.getColorStateList(R.color.md_green_A700)
                dividerOnWay.backgroundTintList = resources.getColorStateList(R.color.md_green_A700)
                dividerPickUp.backgroundTintList = resources.getColorStateList(R.color.md_green_A700)

                createdTime.text = packageDetails.createdDate
                courierOnTheWayTime.text = packageDetails.deliveryHistory?.get(0)?.timestamp
                pickUpTime.text = packageDetails.deliveryHistory?.get(1)?.timestamp
                deliverTime.text = packageDetails.deliveryHistory?.get(2)?.timestamp

            }
            DeliveryStatus.COURIER_PICK_UP_PACKAGE -> {
                chk_pick_up.isChecked = true
                chk_on_way.isChecked = true
                dividerCreated.backgroundTintList = resources.getColorStateList(R.color.md_green_A700)
                dividerOnWay.backgroundTintList = resources.getColorStateList(R.color.md_green_A700)

                createdTime.text = packageDetails.createdDate
                courierOnTheWayTime.text = packageDetails.deliveryHistory?.get(0)?.timestamp
                pickUpTime.text = packageDetails.deliveryHistory?.get(1)?.timestamp

            }
            DeliveryStatus.COURIER_ON_THE_WAY -> {
                chk_on_way.isChecked = true
                dividerCreated.backgroundTintList = resources.getColorStateList(R.color.md_green_A700)

                createdTime.text = packageDetails.createdDate
                courierOnTheWayTime.text = packageDetails.deliveryHistory?.get(0)?.timestamp
            }
            else -> {
                createdTime.text = packageDetails.createdDate
            }
        }
        val textCargoState = rootView.findViewById<TextView>(R.id.textCargoState)

                // Display cargo state information
        val txt = "Package ID: ${packageDetails.id}"
        textCargoState.text = txt


    }


}