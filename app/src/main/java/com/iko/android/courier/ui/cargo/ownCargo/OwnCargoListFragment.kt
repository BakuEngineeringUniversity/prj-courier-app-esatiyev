package com.iko.android.courier.ui.cargo.ownCargo


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.lifecycleScope
import com.iko.android.courier.R
import com.iko.android.courier.UserManager
import com.iko.android.courier.api.RetrofitInstance
import com.iko.android.courier.ui.cargo.PackageFetchCallback
import kotlinx.coroutines.launch


class OwnCargoListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_own_cargo_list, container, false)
        val progressBar = rootView.findViewById<ProgressBar>(R.id.own_cargo_progress_bar)
        progressBar.visibility = View.VISIBLE

        // Fetch packages and determine whether to show regular or empty layout
        fetchPackages(object : PackageFetchCallback {
            override fun onPackageSizeFetched(size: Int) {
                progressBar.visibility = View.GONE
                if (size != 0) {
                    // Packages exist, show regular layout
                    // You can add your logic here to display the packages
                    // For now, I'll just log a message
                    Log.d("OwnCargoListFragment", "Packages exist")
                } else {
                    // No packages, show empty layout
                    val emptyLayout = inflater.inflate(R.layout.item_empty_list, container, false)
                    (rootView as ViewGroup).addView(emptyLayout)
                }
            }
        })

        return rootView
    }

    private fun fetchPackages(callback: PackageFetchCallback) {
        // Your code to fetch packages...
        val apiService = RetrofitInstance.apiService

        lifecycleScope.launch {
            try {
                val packages = apiService.getPackagesByCustomerId(UserManager.id!!)
                if (packages.isSuccessful){
                    Log.d("OwnCargoListFragment", "Packages fetched successfully.")

                    Log.d("OwnCargoListFragment", "Packages size: ${packages.body()?.size?:0}")
                    callback.onPackageSizeFetched(packages.body()?.size?:0)
                }
            } catch (e: Exception) {
                Log.e("OwnCargoListFragment", "Error fetching packages: ${e.message}")
                callback.onPackageSizeFetched(0)
            }
        }
    }

}