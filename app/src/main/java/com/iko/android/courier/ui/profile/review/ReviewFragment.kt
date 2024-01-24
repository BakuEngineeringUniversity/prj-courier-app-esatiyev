package com.iko.android.courier.ui.profile.review

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.iko.android.courier.R
import com.iko.android.courier.UserManager
import com.iko.android.courier.data.model.Review
import com.iko.android.courier.api.RetrofitInstance
import kotlinx.coroutines.launch


class ReviewFragment : Fragment() {
    private lateinit var rootView: View
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        rootView = inflater.inflate(R.layout.fragment_review, container, false)

        val apiService = RetrofitInstance.apiService

        lifecycleScope.launch {
            try {
                val reviews = apiService.getReviewsByCourierId(UserManager.id!!)

                showReviews(reviews)
            } catch (e: NoSuchElementException) {
                // basqa bir layout duzelt ve o layout gorsensin ve yazilsin ki, become courier
                // rootView =
                Log.e("ProfileFragment", "No such courier: ${e.message}")
            }
            catch (e: Exception) {
                Log.e("ProfileFragment", "Error fetching reviews: ${e.message}")
            }
        }

        return rootView

    }

    private fun showReviews(reviews: List<Review>) {
        val reviewsLayout = rootView.findViewById<LinearLayout>(R.id.reviews_layout)


        // Loop through the reviews and create review views
        for (review in reviews) {
            val reviewView = layoutInflater.inflate(R.layout.item_review, null)
            val reviewerNameTextView = reviewView.findViewById<TextView>(R.id.reviewer_name)
            val avatarPhoto = reviewView.findViewById<ImageView>(R.id.avatar)
            val reviewTextView = reviewView.findViewById<TextView>(R.id.review_text)
            val dateTextView = reviewView.findViewById<TextView>(R.id.date)

            reviewerNameTextView.text = review.reviewerFullName
//            avatarPhoto. = "Rating: ${review.rating}"
            reviewTextView.text = review.comment
            dateTextView.text = review.date


            reviewsLayout.addView(reviewView)
        }

        val emptyLayout = layoutInflater.inflate(R.layout.empty_layout, null)

        reviewsLayout.addView(emptyLayout)
    }

}