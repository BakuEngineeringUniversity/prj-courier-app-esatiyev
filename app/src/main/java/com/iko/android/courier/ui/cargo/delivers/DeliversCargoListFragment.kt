package com.iko.android.courier.ui.cargo.delivers

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.lifecycleScope
import com.iko.android.courier.R
import com.iko.android.courier.UserManager
import com.iko.android.courier.api.RetrofitInstance
import com.iko.android.courier.ui.cargo.PackageFetchCallback
import kotlinx.coroutines.launch



class DeliversCargoListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("DeliversCargoListFragment", "${UserManager.isCourier}")
        if (UserManager.isCourier != true) {
            val rootView = inflater.inflate(R.layout.item_empty_list_delivers, container, false)
            val deliversListTitle = rootView.findViewById<LinearLayout>(R.id.delivers_list_title)
            deliversListTitle.visibility = View.VISIBLE
            val title = rootView.findViewById<TextView>(R.id.title_delivers)
            val subtitle = rootView.findViewById<TextView>(R.id.subtitle_delivers)
            title.text = "You are not Courier"
            subtitle.text = "Become courier, enjoy delivering and earn money!"
            return rootView
        }
        val rootView = inflater.inflate(R.layout.fragment_delivers_cargo_list, container, false)
        val progressBar = rootView.findViewById<ProgressBar>(R.id.deliver_cargo_progress_bar)
        progressBar.visibility = View.VISIBLE

        val deliversCargoListFragmentContainerView =
            rootView.findViewById<FragmentContainerView>(R.id.DeliversCargoListFragmentContainerView)

        // Check if the package list is empty
        fetchPackages(object : PackageFetchCallback {
            override fun onPackageSizeFetched(size: Int) {
                progressBar.visibility = View.GONE
                if (size != 0) {
                    deliversCargoListFragmentContainerView.visibility = View.VISIBLE
                } else {
                    deliversCargoListFragmentContainerView.visibility = View.GONE
                    val emptyLayout =
                        inflater.inflate(R.layout.item_empty_list_delivers, container, false)
                    (rootView as ViewGroup).addView(emptyLayout)
                }
            }
        })

        return rootView
    }


    private fun fetchPackages(callback: PackageFetchCallback) {
        val apiService = RetrofitInstance.apiService

        lifecycleScope.launch {
            try {
                val packages = apiService.getPackagesByCourierId(UserManager.id!!)
                Log.d("DeliversCargoListFragment", "packages size ${packages.size}")
                callback.onPackageSizeFetched(packages.size)
            } catch (e: Exception) {
                Log.e("CargoManagementActivity", "Error fetching packages: ${e.message}")
                callback.onPackageSizeFetched(0)
            }
        }
    }

}