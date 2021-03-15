package com.pocketdocket.view.order

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.pocketdocket.R
import com.pocketdocket.model.Cart
import com.pocketdocket.model.Item
import com.pocketdocket.model.ItemOrder


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

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cart = requireArguments().getParcelable<Cart>("Cart")

        val prompt = "${cart?.getItemCount()} Items"
        view.findViewById<TextView>(R.id.itemCountText).text = prompt

        val cancelButt = view.findViewById<TextView>(R.id.cancelOrderButton)
        val confirmPrintButt = view.findViewById<TextView>(R.id.confirmAndPrint)

        confirmPrintButt.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        cancelButt.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        val orderSummaryRecyclerView = view.findViewById<RecyclerView>(R.id.orderSummaryRecyclerList)
        val adapter = cart?.let { CartSummaryViewAdapter(it) }
        val orderLayoutMan = LinearLayoutManager(context)
        orderSummaryRecyclerView.adapter = adapter
        orderSummaryRecyclerView.layoutManager = orderLayoutMan

        // Add dividers to list view
        val itemDec = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        itemDec.setDrawable(context?.getDrawable(R.drawable.divider)!!)
        orderSummaryRecyclerView.addItemDecoration(itemDec)
    }

    inner class CartSummaryViewAdapter(private val cart: Cart) : RecyclerView.Adapter<CartSummaryViewAdapter.ItemOrderHolder>() {

        inner class ItemOrderHolder(v: View) : RecyclerView.ViewHolder(v) {

            private val nameTxtView = v.findViewById<TextView>(R.id.orderItemName)
            private val priceTxtView = v.findViewById<TextView>(R.id.orderItemPrice)
            private val amountEditText = v.findViewById<TextInputEditText>(R.id.amountEditText)


            fun bind(itemOrd: ItemOrder) {
                nameTxtView.text = itemOrd.item.name
                priceTxtView.text = itemOrd.item.getPriceWithSign()
                amountEditText.setText(itemOrd.amount.toString())
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemOrderHolder {
            val inflatedRowView = LayoutInflater.from(context).inflate(R.layout.order_summary_row, parent, false)

            return ItemOrderHolder(inflatedRowView)
        }

        override fun onBindViewHolder(holder: ItemOrderHolder, position: Int) {
            val item = cart.get(position)

            holder.bind(item)
        }

        override fun getItemCount(): Int {
            return cart.count()
        }
    }
}