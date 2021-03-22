package com.docketonscreen.view.order

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.print.PrintManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.widget.addTextChangedListener
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
 * Users can edit cart, add details and print
 */
class OrderSummaryFragment : Fragment() {

    private lateinit var totalCostTextView: TextView
    private lateinit var itemCountTextView: TextView
    private lateinit var cart: Cart

    private lateinit var nameIdEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var addressEditText: EditText
    private lateinit var commentEditText: EditText
    private lateinit var numOfPplEditText: EditText

    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {

        }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            fetchCustomerDetails()
        }
    }

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
        cart.extraFeeType = Cart.NONE

        itemCountTextView = view.findViewById<TextView>(R.id.itemCountText)

        val cancelButt = view.findViewById<TextView>(R.id.cancelOrderButton)
        val confirmPrintButt = view.findViewById<TextView>(R.id.confirmAndPrint)
        totalCostTextView = view.findViewById<TextView>(R.id.totalCostTextView)

        val chipGroup = view.findViewById<ChipGroup>(R.id.chipGroup)
        val rateTextView = view.findViewById<EditText>(R.id.surchargeDiscountEditText)

        nameIdEditText = view.findViewById<EditText>(R.id.idNameDetail)
        phoneEditText = view.findViewById<EditText>(R.id.phoneNumEditText)
        addressEditText = view.findViewById<EditText>(R.id.addressEditText)
        commentEditText = view.findViewById<EditText>(R.id.commentsTextEdit)
        numOfPplEditText = view.findViewById<EditText>(R.id.numOfPplEditText)

        setupDetailTextWatchers()

        loadCustomerDetailsIntoEditTexts()
        updateTotalSummaryView()

        chipGroup.setOnCheckedChangeListener { group, checkedId ->
            cart.extraFeeType = Cart.NONE
            if (!cart.isEmpty()) {
                val rateString = rateTextView.text.toString()
                if (checkedId == R.id.surchargeChip && rateString.isNotEmpty()) {
                    applySurcharge(rateString.toDouble() / 100.0)
                } else if (checkedId == R.id.discountChip && rateString.isNotEmpty()) {
                    applyDiscount(rateString.toDouble() / 100.0)
                }
                else {
                    updateTotalSummaryView()
                }
            }
        }

        confirmPrintButt.setOnClickListener {
            printDocketToPrinter()
        }

        cancelButt.setOnClickListener {
            fetchCustomerDetails()
            println(cart.customerDetails)
            parentFragmentManager.popBackStack()
        }

        val orderSummaryRecyclerView = view.findViewById<RecyclerView>(R.id.orderSummaryRecyclerList)
        val adapter = CartSummaryViewAdapter(cart!!)
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
        cart.extraFeeType = Cart.SURCHARGED
        cart.rate = rate
        updateTotalSummaryView()
    }

    /**
     * Apply discount and update total cost view
     * @param rate - should be in decimal form when importing
     */
    private fun applyDiscount(rate: Double) {
        cart.extraFeeType = Cart.DISCOUNTED
        cart.rate = rate
        updateTotalSummaryView()
    }

    private fun updateTotalSummaryView() {
        totalCostTextView.text = cart.finalTotalString()
        itemCountTextView.text = "${cart.getItemCount()} Items"
    }

    /**
     * Get customer details from edit texts
     */
    private fun fetchCustomerDetails() {
        // Name/ID/Table number
        cart.customerDetails[Cart.IDNAME] = nameIdEditText.text.toString()
        // Number of people
        cart.customerDetails[Cart.NUMOFPPL] = numOfPplEditText.text.toString()
        // Phone number
        cart.customerDetails[Cart.PHONENO] = phoneEditText.text.toString()
        // Address
        cart.customerDetails[Cart.ADDRESS] = addressEditText.text.toString()
        // Comments
        cart.customerDetails[Cart.COMMENTS] = commentEditText.text.toString()
    }

    /**
     * Load details into edit texts
     * This enables for details to be persistent when back to ordering fragment
     */
    private fun loadCustomerDetailsIntoEditTexts() {
        nameIdEditText.setText(cart.customerDetails[Cart.IDNAME])
        numOfPplEditText.setText(cart.customerDetails[Cart.NUMOFPPL])
        phoneEditText.setText(cart.customerDetails[Cart.PHONENO])
        addressEditText.setText(cart.customerDetails[Cart.ADDRESS])
        commentEditText.setText(cart.customerDetails[Cart.COMMENTS])
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun printDocketToPrinter() {
        try {
            parentFragmentManager.popBackStack()
            val document = Document()
            val path = File(requireContext().externalCacheDir.toString())

            val file = File(path, "Docket.pdf")

            path.mkdir()

            PdfWriter.getInstance(document, FileOutputStream(file.path))

            document.open()

            fetchCustomerDetails()

            val buildPdf = PdfBuildHelper(cart, document, "assets/serif.otf")
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

    /**
     * Set up watchers that help keep track of detail edit texts values.
     * Each edit has separate listener to avoid race condition.
     * Previously used same listener and all but 1 edit were empty when backing to
     * the order fragment and returning to this fragment after.
     */
    private fun setupDetailTextWatchers() {
        nameIdEditText.addTextChangedListener(onTextChanged = { charSequence: CharSequence?,
                                                                i: Int, i1: Int, i2: Int ->
            cart.customerDetails[Cart.IDNAME] = charSequence.toString()
        })

        phoneEditText.addTextChangedListener(onTextChanged = { charSequence: CharSequence?,
                                                               i: Int, i1: Int, i2: Int ->
            cart.customerDetails[Cart.PHONENO] = charSequence.toString()
        })

        addressEditText.addTextChangedListener(onTextChanged = { charSequence: CharSequence?,
                                                                 i: Int, i1: Int, i2: Int ->
            cart.customerDetails[Cart.ADDRESS] = charSequence.toString()
        })

        commentEditText.addTextChangedListener(onTextChanged = { charSequence: CharSequence?,
                                                                 i: Int, i1: Int, i2: Int ->
            cart.customerDetails[Cart.COMMENTS] = charSequence.toString()
        })

        numOfPplEditText.addTextChangedListener(onTextChanged = { charSequence: CharSequence?,
                                                                  i: Int, i1: Int, i2: Int ->
            cart.customerDetails[Cart.NUMOFPPL] = charSequence.toString()
        })
    }

    /**
     * Shows the list of items added to cart
     */
    inner class CartSummaryViewAdapter(private val cart: Cart) : RecyclerView.Adapter<CartSummaryViewAdapter.ItemOrderHolder>() {

        inner class ItemOrderHolder(v: View) : RecyclerView.ViewHolder(v) {

            private val nameTxtView = v.findViewById<TextView>(R.id.orderItemName)
            private val priceTxtView = v.findViewById<TextView>(R.id.orderItemPrice)
            private val amountEditText = v.findViewById<TextInputEditText>(R.id.amountEditText)
            private val increaseButton = v.findViewById<ImageView>(R.id.addItemCountButton)
            private val decreaseButton = v.findViewById<ImageView>(R.id.minusItemCountButton)

            fun bind(itemOrd: ItemOrder, position: Int) {
                nameTxtView.text = itemOrd.item.name
                priceTxtView.text = itemOrd.item.getPriceWithSign()
                amountEditText.setText(itemOrd.amount.toString())

                increaseButton.setOnClickListener {
                    itemOrd.amount++
                    amountEditText.setText(itemOrd.amount.toString())
                    notifyItemChanged(position)
                    updateTotalSummaryView()
                }

                decreaseButton.setOnClickListener {
                    itemOrd.amount--
                    amountEditText.setText(itemOrd.amount.toString())
                    if (itemOrd.amount <= 0) {
                        cart.removeOrder(itemOrd)
                        notifyItemRemoved(position)
                    }
                    else {
                        notifyItemChanged(position)
                    }
                    updateTotalSummaryView()
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemOrderHolder {
            val inflatedRowView = LayoutInflater.from(context).inflate(R.layout.order_summary_row, parent, false)

            return ItemOrderHolder(inflatedRowView)
        }

        override fun onBindViewHolder(holder: ItemOrderHolder, position: Int) {
            val item = cart.get(position)

            holder.bind(item, position)
        }

        override fun getItemCount(): Int {
            return cart.count()
        }
    }
}