package com.pocketdocket.view.catalogueadder

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daimajia.swipe.SwipeLayout
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.pocketdocket.R
import com.pocketdocket.model.Catalogue
import com.pocketdocket.model.CatalogueRepository
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

            println("Current cata " + currMenu.dbPrimaryId)

            fabAdditem.setOnClickListener {
                // import this class addNewItem method to save an item
                showUpdateItemPopUp(title = "Add Menu", itemEntryManipulation = ::addNewItem)
            }
        }
    }

    /**
     * Shows a dialog to add item details
     * NOTE: currPosition cannot count - 1 since list position cannot be -1 when starting
     */
    private fun showUpdateItemPopUp(title: String = "", name: String = "", price: Double = 0.0,
                                    category: String = "", description: String = "",
                                    itemEntryManipulation: (menu: Catalogue, item: Item, index: Int) -> Unit,
                                    currPosition: Int = currMenu.getItems().count()) {

        val addItemDialog = Dialog(requireContext())
        addItemDialog.setContentView(R.layout.additem_dialog)
        addItemDialog.setTitle(title)
        val saveItemButt = addItemDialog.findViewById<Button>(R.id.saveItemButt)
        val cancelButt = addItemDialog.findViewById<Button>(R.id.cancelButt)
        val nameEditText = addItemDialog.findViewById<TextInputEditText>(R.id.nameEditText)
        val priceEditText = addItemDialog.findViewById<TextInputEditText>(R.id.pricenEditText)
        val descEditText = addItemDialog.findViewById<TextInputEditText>(R.id.descriptionEditText)
        val categoryEditText = addItemDialog.findViewById<TextInputEditText>(R.id.categoryEditText)
        val titleView = addItemDialog.findViewById<TextView>(R.id.addItemTitle)

        nameEditText.requestFocus()

        // Check if price is zero and set empty string if zero to prevent presetting
        // edit text with a number which could be annoying to backspace, this enhances usability
        val priceView = if (price == 0.0) "" else price.toString()

        titleView.text = title
        nameEditText.setText(name)
        priceEditText.setText(priceView)
        categoryEditText.setText(category)
        descEditText.setText(description)

        saveItemButt.setOnClickListener {
            val name = nameEditText.text.toString()
            val price = if(priceEditText.text.toString().isNullOrEmpty()) 0.0 else priceEditText.text.toString().toDouble()
            val categ = if (categoryEditText.text.isNullOrEmpty()) "" else categoryEditText.text.toString()
            val desc = if (descEditText.text.isNullOrEmpty()) "" else descEditText.text.toString()

//            CatalogueRepository.addItem(currMenu, Item(name, price, categ, desc))
//
//            itemAdapter.notifyItemInserted(currMenu.getItems().count() - 1)

            itemEntryManipulation(currMenu, Item(name, price, categ, desc), currPosition)

            addItemDialog.dismiss()
        }

        cancelButt.setOnClickListener {
            addItemDialog.dismiss()
        }

        addItemDialog.show()
    }

    private fun addNewItem(menu: Catalogue, newItem: Item, index: Int) {
        CatalogueRepository.addItem(menu, newItem)
        itemAdapter.notifyItemInserted(index)
    }

    fun updateItem(menu: Catalogue, newItem: Item, index: Int) {
        val currItem = menu.getItems()[index]
        currItem.let {
            it.name = newItem.name
            it.price = newItem.price
            it.category = newItem.category
            it.description = newItem.description
        }

        CatalogueRepository.updateItem(currItem)

        itemAdapter.notifyItemChanged(index)
    }

    /**
     * Recycle view for displaying items of a catalogue
     */
    inner class ItemRecyclerViewAdapter(inListOfMenus: MutableList<Item>) : RecyclerSwipeAdapter<ItemRecyclerViewAdapter.ItemHolder>() {

        private val recycleList = inListOfMenus

        inner class ItemHolder(v: View) : RecyclerView.ViewHolder(v) {
            private val nameTxtView = v.findViewById<TextView>(R.id.itemNameOnList)
            private val priceTxtView = v.findViewById<TextView>(R.id.priceBox)
            private val descTxtView = v.findViewById<TextView>(R.id.descriptBox)
            private val deleteButt = v.findViewById<TextView>(R.id.deleteButton)
            private val editButt = v.findViewById<TextView>(R.id.editButton)
            private val deleteConfirmText = v.findViewById<TextView>(R.id.deleteConfirmText)

            private val swipeLayout = v.findViewById<SwipeLayout>(R.id.itemListSwiper)

            private var showDeleteConfirmation = false

            // Delete listener for reusability
            // This used by the delete button and delete confirmation
            private val deleteListener = object: View.OnClickListener{
                private lateinit var item: Item

                override fun onClick(v: View?) {
                    // First click must show the confirm text to user
                    if (!showDeleteConfirmation) {
                        // Move bin icon to left and show word confirm
                        deleteConfirmText.text = "Confirm?"
                        // Allow user to also press the confirm text area for better usability
                        deleteConfirmText.setOnClickListener {
                            removeItem(item)
                        }
                        showDeleteConfirmation = true
                    }
                    else {
                        // If user confirms then delete
                        removeItem(item)
                        showDeleteConfirmation = false
                    }
                }

                // set item before using this listener
                fun setItemForDelete(it: Item) {
                    item = it
                }
            }


            fun bind(item: Item, position: Int) {
                nameTxtView.text = item.name
                priceTxtView.text = item.getPriceWithSign()
                descTxtView.text = item.description

                deleteListener.setItemForDelete(item)
                deleteButt.setOnClickListener(deleteListener)
                deleteConfirmText.setOnClickListener(deleteListener)


                editButt.setOnClickListener {
                    Toast.makeText(it.context, "Clicked Edit", Toast.LENGTH_SHORT).show()

                    showUpdateItemPopUp("Edit Item", item.name, item.price, item.category, item.description, ::updateItem, position)
                }

                // Swipe listener
                swipeLayout.addSwipeListener(object: SwipeLayout.SwipeListener {
                    override fun onStartOpen(layout: SwipeLayout?) {

                    }

                    override fun onOpen(layout: SwipeLayout?) {

                    }

                    override fun onStartClose(layout: SwipeLayout?) {

                    }

                    override fun onClose(layout: SwipeLayout?) {
                        // Revert delete button back to original state
                        deleteConfirmText.text = ""
                        deleteConfirmText.setOnClickListener(deleteListener)
                        showDeleteConfirmation = false
                    }

                    override fun onUpdate(layout: SwipeLayout?, leftOffset: Int, topOffset: Int) {

                    }

                    override fun onHandRelease(layout: SwipeLayout?, xvel: Float, yvel: Float) {

                    }
                })
            }

            private fun removeItem(item: Item) {
                val index = CatalogueRepository.removeItem(currMenu, item)

                itemAdapter.notifyItemRemoved(index)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddItemFragment.ItemRecyclerViewAdapter.ItemHolder {
            val inflateRowView = LayoutInflater.from(context).inflate(R.layout.item_recycle_row, parent, false)

            return ItemHolder(inflateRowView)
        }

        override fun onBindViewHolder(holder: AddItemFragment.ItemRecyclerViewAdapter.ItemHolder, position: Int) {
            val item = recycleList.get(position)

            holder.bind(item, position)
        }

        override fun getItemCount(): Int {
            return recycleList.count()
        }

        override fun getSwipeLayoutResourceId(position: Int): Int {
            return R.id.itemListSwiper
        }
    }
}