package com.iko.android.courier.ui.cargo.state

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.iko.android.courier.R
import com.iko.android.courier.UserManager
import com.iko.android.courier.api.ApiService
import com.iko.android.courier.api.RetrofitInstance
import com.iko.android.courier.data.model.DeliveryStatus
import com.iko.android.courier.data.model.Package
import com.iko.android.courier.data.model.Review
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CargoStateFragment : Fragment() {

    private lateinit var rootView: View
    private lateinit var apiService: ApiService

    private lateinit var chk_on_way :CheckBox
    private lateinit var chk_pick_up :CheckBox
    private lateinit var chk_delivered :CheckBox
    private lateinit var chk_created :CheckBox

    private lateinit var writeReviewButton: Button
    private lateinit var callBtn: Button

    private lateinit var courierId: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        rootView = inflater.inflate(R.layout.fragment_cargo_state, container, false)

        val packageId = arguments?.getString("packageId")



        callBtn = rootView.findViewById(R.id.callCourier)
        writeReviewButton = rootView.findViewById(R.id.writeReview)

        var courierPhone: String? = null


        apiService = RetrofitInstance.apiService

        lifecycleScope.launch {
            try {
                val packageDetails = apiService.getPackageById(packageId!!.toLong())

                courierPhone = packageDetails.courier?.phone
                courierId = packageDetails.courier?.id.toString()

                updateUI(packageDetails)
            } catch (e: Exception) {
                // Handle errors
                Log.e("CargoManagementActivity", "Error fetching package details: ${e.message}")
            }
        }



        callBtn.setOnClickListener {
            // Handle the button click here

            initiatePhoneCall(courierPhone?:"")
        }

        writeReviewButton.setOnClickListener {
            // Handle the button click here

            showReviewDialog()
        }


        return rootView
    }

    private fun initiatePhoneCall(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:$phoneNumber")
        startActivity(intent)
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

                writeReviewButton.visibility = View.VISIBLE
                callBtn.visibility = View.VISIBLE
            }
            DeliveryStatus.COURIER_PICK_UP_PACKAGE -> {
                chk_pick_up.isChecked = true
                chk_on_way.isChecked = true
                dividerCreated.backgroundTintList = resources.getColorStateList(R.color.md_green_A700)
                dividerOnWay.backgroundTintList = resources.getColorStateList(R.color.md_green_A700)

                createdTime.text = packageDetails.createdDate
                courierOnTheWayTime.text = packageDetails.deliveryHistory?.get(0)?.timestamp
                pickUpTime.text = packageDetails.deliveryHistory?.get(1)?.timestamp

                callBtn.visibility = View.VISIBLE
            }
            DeliveryStatus.COURIER_ON_THE_WAY -> {
                chk_on_way.isChecked = true
                dividerCreated.backgroundTintList = resources.getColorStateList(R.color.md_green_A700)

                createdTime.text = packageDetails.createdDate
                courierOnTheWayTime.text = packageDetails.deliveryHistory?.get(0)?.timestamp

                callBtn.visibility = View.VISIBLE
            }
            else -> {
                createdTime.text = packageDetails.createdDate
            }
        }
//        val textCargoState = rootView.findViewById<TextView>(R.id.textCargoState)

                // Display cargo state information
//        val txt = "Package ID: ${packageDetails.id}"
//        textCargoState.text = txt


    }


    private fun showReviewDialog() {
        Log.d("CargoStateFragment", "showReviewDialog called")  // Add this line for debugging

        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.review_custom_dialog, null)
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setTitle("Write a Review")

        val alertDialog = alertDialogBuilder.create()


        val ratingBar = dialogView.findViewById<RatingBar>(R.id.ratingBar)
        var commentEditTxt = dialogView.findViewById<EditText>(R.id.commentEditText)

        // Set an OnRatingBarChangeListener on the RatingBar
        ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
            // Handle the rating change here
            Log.d("RatingBar", "New rating: $rating")
        }

        // Find your buttons in the dialog layout
        val submitButton = dialogView.findViewById<Button>(R.id.submitReviewButton)
        val cancelButton = dialogView.findViewById<Button>(R.id.cancelButton)

        // Set an onClickListener for the submit button
        submitButton.setOnClickListener {
            Log.d("CargoStateFragment", "Submit button clicked")  // Add this line for debugging
            commentEditTxt.setText(removeExtraSpaces(commentEditTxt.text.toString()))

            if (commentEditTxt.text.toString().length < 7) {
                Toast.makeText(requireContext(), "Comment must be at least 7 characters long", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (ratingBar.rating == 0f) {
                Toast.makeText(requireContext(), "Rating must be greater than 0", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    val rating = ratingBar.rating
                    val comment = commentEditTxt.text.toString()
                    val review = Review(
                        comment = comment,
                        rating = rating,
                        reviewerFullName = UserManager.getFullName(),
                    )


                    val response = apiService.addReview(courierId.toLong(), review)

                    Toast.makeText(requireContext(), "Review submitted successfully. " +
                            "Thank you!", Toast.LENGTH_SHORT).show()

                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Failed to submit review!", Toast.LENGTH_SHORT).show()
                }
            }

            alertDialog.dismiss()
        }

        // Set an onClickListener for the cancel button
        cancelButton.setOnClickListener {
            Log.d("CargoStateFragment", "Cancel button clicked")  // Add this line for debugging

            alertDialog.dismiss()
        }

        // Show the dialog
        alertDialog.show()
    }

    fun removeExtraSpaces(input: String): String {
        // Remove leading and trailing spaces
        val trimmedString = input.trim()

        // Replace multiple spaces with a single space
        val stringWithoutExtraSpaces = trimmedString.replace("\\s+".toRegex(), " ")

        return stringWithoutExtraSpaces
    }




}