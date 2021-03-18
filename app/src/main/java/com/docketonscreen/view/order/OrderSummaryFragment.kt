package com.docketonscreen.view.order

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.print.PrintManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import com.itextpdf.text.Document
import com.itextpdf.text.PageSize
import com.itextpdf.text.pdf.PdfWriter
import com.docketonscreen.R
import com.docketonscreen.model.Cart
import com.docketonscreen.model.ItemOrder
import com.docketonscreen.print.PdfBuildHelper
import com.docketonscreen.print.PrintAdapter
import java.io.File
import java.io.FileOutputStream


/**
 * Order summary fragment
 */
class OrderSummaryFragment : Fragment() {

    private lateinit var totalCostTextView: TextView
    private lateinit var cart: Cart
    private lateinit var customerDetails: Array<String>

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

        cart = requireArguments().getParcelable<Cart>("Cart")!!

        val prompt = "${cart.getItemCount()} Items"
        view.findViewById<TextView>(R.id.itemCountText).text = prompt

        val cancelButt = view.findViewById<TextView>(R.id.cancelOrderButton)
        val confirmPrintButt = view.findViewById<TextView>(R.id.confirmAndPrint)
        totalCostTextView = view.findViewById<TextView>(R.id.totalCostTextView)

        val chipGroup = view.findViewById<ChipGroup>(R.id.chipGroup)
        val rateTextView = view.findViewById<EditText>(R.id.surchargeDiscountEditText)

        totalCostTextView.text = "Total: $${cart?.getSubTotalCost()}"

        chipGroup.setOnCheckedChangeListener { group, checkedId ->
            if (!cart.isEmpty()) {
                val rateString = rateTextView.text.toString()
                if (checkedId == R.id.surchargeChip && rateString.isNotEmpty()) {
                    applySurcharge(rateString.toDouble() / 100.0)
                } else if (checkedId == R.id.discountChip && rateString.isNotEmpty()) {
                    applyDiscount(rateString.toDouble() / 100.0)
                }
                else {
                    totalCostTextView.text = "Total: $${cart?.getSubTotalCost()}"
                }
            }
        }

        confirmPrintButt.setOnClickListener {
            try {
                parentFragmentManager.popBackStack()
                val document = Document()
                val path = File(requireContext().externalCacheDir.toString())

                val file = File(path, "Docket.pdf")

                path.mkdir()

                PdfWriter.getInstance(document, FileOutputStream(file.path))

                document.open()

                document.pageSize = PageSize.A7
                document.addCreationDate()
                document.addAuthor("Pocket Docket")

                fetchCustomerDetails(view)

                val buildPdf = PdfBuildHelper(cart, document, totalCostTextView.text.toString(), customerDetails)
                buildPdf.build()

                document.close()

                val printManager = requireActivity().getSystemService(Context.PRINT_SERVICE) as PrintManager
                val printAdapt = PrintAdapter(requireContext(), file.path)
                printManager.print("Docket print", printAdapt, null)
            }
            catch (e: Exception) {
                Log.e("File output error", e.message!!)
            }
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

    /**
     * Apply surcharge and update total cost view
     * @param rate - should be in decimal form when importing
     */
    private fun applySurcharge(rate: Double) {
        cart.rate = rate
        val subTotal = cart.getSubTotalCost()
        val surchargePrice = cart.getExtraFee()
        val total = cart.getFinalTotal(Cart.SURCHARGED)

        totalCostTextView.text = "Sub total: $$subTotal\nSurcharge: +$$surchargePrice\nTotal: $${"%.2f".format(total)}"
    }

    /**
     * Apply discount and update total cost view
     * @param rate - should be in decimal form when importing
     */
    private fun applyDiscount(rate: Double) {
        cart.rate = rate
        val subTotal = cart.getSubTotalCost()
        val discount = cart.getExtraFee()
        val total = cart.getFinalTotal(Cart.DISCOUNTED)

        totalCostTextView.text = "Sub total: $$subTotal\nDiscount: -$$discount\nTotal: $${"%.2f".format(total)}"
    }

    private fun fetchCustomerDetails(view: View) {
        val nameIdEditText = view.findViewById<EditText>(R.id.idNameDetail)
        val phoneEditText = view.findViewById<EditText>(R.id.phoneNumEditText)
        val addressEditText = view.findViewById<EditText>(R.id.addressEditText)
        val commentEditText = view.findViewById<EditText>(R.id.commentsTextEdit)
        val numOfPplEditText = view.findViewById<EditText>(R.id.numOfPplEditText)

       customerDetails = arrayOf(nameIdEditText.text.toString(), numOfPplEditText.text.toString(),  phoneEditText.text.toString(),
                            addressEditText.text.toString(), commentEditText.text.toString())
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