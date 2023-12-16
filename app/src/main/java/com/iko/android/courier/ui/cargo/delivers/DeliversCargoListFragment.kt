package com.iko.android.courier.ui.cargo.delivers

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.iko.android.courier.R
import com.iko.android.courier.data.model.Package
import com.iko.android.courier.ui.cargo.list.OwnCargoList
import com.iko.android.courier.ui.cargo.state.CargoManagementActivity
import java.util.UUID


class DeliversCargoListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var isCourier = true
        var deliveries_number = 1
        if(isCourier)
            if(deliveries_number == 0)
                return inflater.inflate(R.layout.item_empty_list_delivers, container, false)

        return inflater.inflate(R.layout.fragment_delivers_cargo_list, container, false)
    }
}