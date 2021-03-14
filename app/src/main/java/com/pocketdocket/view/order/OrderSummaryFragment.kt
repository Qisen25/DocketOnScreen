package com.pocketdocket.view.order

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.pocketdocket.R
import com.pocketdocket.model.Cart


/**
 * Order summary fragment
 */
class OrderSummaryFragment : Fragment() {

    companion object {
        fun newInstance(): OrderSummaryFragment = OrderSummaryFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order_summary, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cart = arguments?.getParcelable<Cart>("Cart")

        val prompt = "Hello Blank fragment with cart item amount = ${cart?.getItemCount()}"
        view.findViewById<TextView>(R.id.testText).text = prompt
    }
}