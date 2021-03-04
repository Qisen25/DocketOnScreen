package com.pocketdocket.view.catalogueadder

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toolbar
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.pocketdocket.R
import com.pocketdocket.model.Catalogue
import com.pocketdocket.model.Item

/**
 * Fragment view for adding dishes to a menu
 */
class AddItemFragment : Fragment() {

    private lateinit var currMenu: Catalogue
    private lateinit var itemAdapter: ItemRecyclerViewAdapter

    companion object {
        fun newInstance(): AddItemFragment = AddItemFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_catalogue_additem, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Access toolbar via root view and change title to Items since this is the item fragment
        val tb = view.rootView.findViewById<Toolbar>(R.id.toolbar)
        tb.title = "Items"

        // Get menu name bundle sent MenuAdderFragment and add it to database
        if(this.arguments != null) {
            currMenu = arguments?.getParcelable<Catalogue>("Menu") as Catalogue

            val fabAdditem = view.findViewById<FloatingActionButton>(R.id.fabAddItem)
            // Setup recycle view for list menus
            val recycView = view.rootView.findViewById<RecyclerView>(R.id.itemRecycleList)
            itemAdapter = ItemRecyclerViewAdapter(currMenu.getItems())
            val layoutMan = LinearLayoutManager(context)
            recycView.layoutManager = layoutMan
            recycView.adapter = itemAdapter

            // Add dividers to list view
            val itemDec = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
            itemDec.setDrawable(context?.getDrawable(R.drawable.divider)!!)
            recycView.addItemDecoration(itemDec)


            fabAdditem.setOnClickListener {
                showAddItemPopUp()
            }
        }
    }

    private fun showAddItemPopUp() {
        val addItemDialog = Dialog(requireContext())
        addItemDialog.setContentView(R.layout.additem_dialog)
        val saveItemButt = addItemDialog.findViewById<Button>(R.id.saveItemButt)
        val cancelButt = addItemDialog.findViewById<Button>(R.id.cancelButt)
        val nameEditText = addItemDialog.findViewById<TextInputEditText>(R.id.nameEditText)
        val priceEditText = addItemDialog.findViewById<TextInputEditText>(R.id.pricenEditText)
        val descEditText = addItemDialog.findViewById<TextInputEditText>(R.id.descriptionEditText)
        val categoryEditText = addItemDialog.findViewById<TextInputEditText>(R.id.categoryEditText)

        saveItemButt.setOnClickListener {
            val name = nameEditText.text.toString()
            val price = priceEditText.text.toString().toDouble()
            val categ = if (categoryEditText.text.isNullOrEmpty()) "" else categoryEditText.text.toString()
            val desc = if (descEditText.text.isNullOrEmpty()) "" else descEditText.text.toString()

            currMenu.getItems().add(Item(name, price, categ, desc))

            itemAdapter.notifyItemInserted(currMenu.getItems().count() - 1)

            addItemDialog.dismiss()
        }

        cancelButt.setOnClickListener {
            addItemDialog.dismiss()
        }

        addItemDialog.show()
    }

    /**
     * Recycle view for displaying items of a catalogue
     */
    inner class ItemRecyclerViewAdapter(inListOfMenus: MutableList<Item>) : RecyclerView.Adapter<ItemRecyclerViewAdapter.ItemHolder>() {

        private val recycleList = inListOfMenus

        inner class ItemHolder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {
            private val nameTxtView = v.findViewById<TextView>(R.id.itemNameOnList)
            private val priceTxtView = v.findViewById<TextView>(R.id.priceBox)
            private val descTxtView = v.findViewById<TextView>(R.id.descriptBox)

            init {
                v.setOnClickListener(this)
            }

            fun bind(item: Item) {
                nameTxtView.text = item.name
                priceTxtView.text = item.getPriceWithSign()
                descTxtView.text = item.description
            }

            override fun onClick(v: View?) {

            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddItemFragment.ItemRecyclerViewAdapter.ItemHolder {
            val inflateRowView = LayoutInflater.from(context).inflate(R.layout.item_recycle_row, parent, false)

            return ItemHolder(inflateRowView)
        }

        override fun onBindViewHolder(holder: AddItemFragment.ItemRecyclerViewAdapter.ItemHolder, position: Int) {
            val item = recycleList.get(position)

            holder.bind(item)
        }

        override fun getItemCount(): Int {
            return recycleList.count()
        }
    }
}