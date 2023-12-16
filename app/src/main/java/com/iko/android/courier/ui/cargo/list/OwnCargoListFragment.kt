package com.iko.android.courier.ui.cargo.list


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.iko.android.courier.R
import com.iko.android.courier.UserManager


class OwnCargoListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

//        if (UserManager.packages!!.isEmpty())
//            return inflater.inflate(R.layout.item_empty_list, container, false)
//        //  return inflater.inflate(R.layout.fragment_own_cargo_list, container, false)


        return inflater.inflate(R.layout.fragment_own_cargo_list, container, false)
    }

}