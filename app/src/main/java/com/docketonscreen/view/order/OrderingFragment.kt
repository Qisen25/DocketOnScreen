package com.docketonscreen.view.order

import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.andremion.counterfab.CounterFab
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.tabs.TabLayout
import com.docketonscreen.R
import com.docketonscreen.behaviour.FABScrollBehaviour
import com.docketonscreen.model.Cart
import com.docketonscreen.model.Catalogue
import com.docketonscreen.model.Item
import com.docketonscreen.model.ItemOrder
import com.docketonscreen.view.main.MainActivity

/**
 * Fragment for handling ordering of items
 */
class OrderingFragment : Fragment() {

    private val defaultTab = "All"
    private lateinit var categoryAdapter: CategoryRecyclerViewAdapter
    private lateinit var orderAdapter: OrderItemViewAdapter
    private val categorySet = linkedSetOf<String>(defaultTab)
    private lateinit var currMenu: Catalogue
    private val cart = Cart()
    private lateinit var cartCountFab: CounterFab
    private var selectedTabName = defaultTab
    private lateinit var bottomAppBar: BottomAppBar

    companion object {
        fun newInstance(): OrderingFragment = OrderingFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        super.onCreateOptionsMenu(menu, inflater)

//        inflater.inflate(R.menu.menu_main, menu)
        inflater.inflate(R.menu.order_helper_items, menu)

        val searchItem = menu.findItem(R.id.searcher)
        val categoryItem = menu.findItem(R.id.showCategories)
        val hideItem = menu.findItem(R.id.hideBar)
        val searchView = searchItem.actionView as SearchView

//        val cartFab = activity?.findViewById<FloatingActionButton>(R.id.cartFab)
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        searchView.setOnSearchClickListener {
            cartCountFab.hide()
        }

        searchView.setOnCloseListener {
            cartCountFab.show()
            false
        }

        categoryItem.setOnMenuItemClickListener {
            cart.clear()
            notifyFABbadge()
            false
        }

        hideItem.setOnMenuItemClickListener {
            cartCountFab.visibility = View.INVISIBLE
            bottomAppBar.performHide()
            false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        // Make sure default tab when view is created
        selectedTabName = defaultTab
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ordering, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Fully hide support tool bar and use the bottom app bar as the support bar in order view
        view.rootView.findViewById<FrameLayout>(R.id.manageMenuContainer).updateLayoutParams<ConstraintLayout.LayoutParams> {
            topToTop = R.id.toolbar
        }
        val mainAct = (activity as MainActivity)
        bottomAppBar = view.findViewById<BottomAppBar>(R.id.bottomBar)
        val tb = view.rootView.findViewById<Toolbar>(R.id.toolbar)
        tb.visibility = View.INVISIBLE
        mainAct.setSupportActionBar(bottomAppBar)
        val bringBackAppBar = view.findViewById<ImageView>(R.id.bringBackAppbarButton)

        cartCountFab = view.findViewById<CounterFab>(R.id.cartFab)

        bringBackAppBar.setOnClickListener{
            cartCountFab.show()
            cartCountFab.visibility = View.VISIBLE
            bottomAppBar.performShow()
        }

//        val categoryRecycler = view.findViewById<RecyclerView>(R.id.categoryRecyclerBar)
//        categoryAdapter = CategoryRecyclerViewAdapter()
//        val categoryLayoutMan = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
//        categoryRecycler.layoutManager = categoryLayoutMan
//        categoryRecycler.adapter = categoryAdapter
//
//        // Add dividers to list view
//        val itemDec = DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL)
//        itemDec.setDrawable(context?.getDrawable(R.drawable.horizontal_divider)!!)
//        categoryRecycler.addItemDecoration(itemDec)

        if(this.arguments != null) {
            currMenu = arguments?.getParcelable<Catalogue>("Menu") as Catalogue
            cart.menuName = currMenu.name!!
            gatherCategories()
            val tabLayout = view.findViewById<TabLayout>(R.id.categoryTabLayout)
            setupTabs(tabLayout)

            val orderRecyclerView = view.findViewById<RecyclerView>(R.id.orderItemRecyclerList)
            orderAdapter = OrderItemViewAdapter(currMenu.getItems())
            val orderLayoutMan = LinearLayoutManager(context)
            orderRecyclerView.adapter = orderAdapter
            orderRecyclerView.layoutManager = orderLayoutMan

            // Add dividers to list view
            val itemDec = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
            itemDec.setDrawable(context?.getDrawable(R.drawable.divider)!!)
            orderRecyclerView.addItemDecoration(itemDec)

            // Let the FABScrollBehaviour know that the Button is anchored
            // to make sure FAB hide/shows in sync with bottom app bar
            val fabParams = cartCountFab.layoutParams as CoordinatorLayout.LayoutParams
            val fabBehav = fabParams.behavior
            (fabBehav as FABScrollBehaviour).setIsAnchored(true)

            notifyFABbadge()
            cartCountFab.setOnClickListener{
                val summaryFragment = OrderSummaryFragment.newInstance()
                val bundle = Bundle().apply { putParcelable("Cart", cart) }
                summaryFragment.arguments = bundle

                parentFragmentManager.beginTransaction().replace(R.id.manageMenuContainer, summaryFragment).addToBackStack(null).commit()
            }

            orderRecyclerView
        }

    }

    /**
     * Function grab categories from item list to a set.
     * This is to allow easy indexing categories
     */
    private fun gatherCategories() {
        for (item in currMenu.getItems()) {
            if (!item.category.isNullOrEmpty())
                categorySet.add(item.category)
        }
    }

    /**
     * Func to setup each category tab
     */
    private fun setupTabs(tabLayout: TabLayout) {
        for (category in categorySet){
            val tab = tabLayout.newTab()
            tab.text = category
            tabLayout.addTab(tab)

            // Set up tab listener to help with filtering contents by category
            tab.view.setOnClickListener {
                println("tab text ${tab.text.toString()} prev tab ${selectedTabName}")
                if (!tab.text.toString().equals(selectedTabName)) {
                    /**
                     * Only apply filter if selected tab is different from previous
                     * selectedTabName.
                     * The filter function could be expensive at the moment
                     * with larger data set and should not be called
                     * if user clicks on the same tab
                     */
                    selectedTabName = tab.text.toString()
                    orderAdapter.filterItemsWhenTabSelected(currMenu.getItems())
                    /**
                     * Make FAB and bottom bar reappear.
                     * Hide then show FAB since we are using a custom scroll behaviour
                     * for fab and Fragment does not know if FAB is actually visible or not.
                     */
                    cartCountFab.visibility = View.VISIBLE
                    cartCountFab.hide()
                    cartCountFab.show()
                    bottomAppBar.performShow()
                    println("Is cart butt shown ? ${cartCountFab.isShown}")

                }
            }
        }
    }

    /**
     * Function to display FAB item count on View
     */
    private fun notifyFABbadge() {
        val count = cart.getItemCount()
        if (count > 0) {
            cartCountFab.count = count
        }
        else {
            cartCountFab.count = 0
        }
    }

    /**
     * Recycler view that shows items to order available in menu
     */
    inner class OrderItemViewAdapter(itemList: MutableList<Item>) : RecyclerView.Adapter<OrderItemViewAdapter.ItemHolder>() {

        private val orderItemList = mutableListOf<Item>()

        init {
            orderItemList.addAll(itemList)
        }

        inner class ItemHolder(v: View) : RecyclerView.ViewHolder(v) {

            private val nameTxtView = v.findViewById<TextView>(R.id.itemNameOnList)
            private val priceTxtView = v.findViewById<TextView>(R.id.priceBox)
            private val descTxtView = v.findViewById<TextView>(R.id.descriptBox)
            private val addToCart = v.findViewById<ImageView>(R.id.addToCartButton)

            fun bind(item: Item) {
                nameTxtView.text = item.name
                priceTxtView.text = item.getPriceWithSign()
                descTxtView.text = item.description
                addToCart.visibility = View.VISIBLE

                addToCart.setOnClickListener {
                    // track count of current item
                    cart.addOrder(ItemOrder(item))

                    notifyFABbadge()
                    println("add to cart clicked")
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
            val inflatedRowView = LayoutInflater.from(context).inflate(R.layout.item_recycler_row, parent, false)

            return ItemHolder(inflatedRowView)
        }

        override fun onBindViewHolder(holder: ItemHolder, position: Int) {
            val item = orderItemList[position]

            holder.bind(item)

            println("Update list view")

        }

        override fun getItemCount(): Int {
            return orderItemList.count()
        }

        /**
         * This functions filters based on what tab is selected in this fragment
         * Note: Should be called in a tab button click listener
         */
        fun filterItemsWhenTabSelected(toFilterList: MutableList<Item>) {
            orderItemList.clear()
            for (item in toFilterList) {
                if (selectedTabName.equals(defaultTab)) {
                    // Get all items if selected tab is All
                    orderItemList.add(item)
                }
                else if (item.category.equals(selectedTabName)) {
                    // Selected tab not default category then get items with relevant category
                    orderItemList.add(item)
                }
            }
            notifyDataSetChanged()

        }
    }

    /**
     * A horizontal category list that enables the user to filter items by category
     */
    inner class CategoryRecyclerViewAdapter : RecyclerView.Adapter<CategoryRecyclerViewAdapter.CategoryHolder>() {

        inner class CategoryHolder(v: View) : RecyclerView.ViewHolder(v) {

            private val textV = v.findViewById<TextView>(R.id.categoryItemName)

            fun bind(category: String) {
                textV.text = category
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryHolder {
            val inflatedRowView = LayoutInflater.from(context).inflate(R.layout.category_recycle_row, parent, false)

            return CategoryHolder(inflatedRowView)
        }

        override fun onBindViewHolder(holder: CategoryHolder, position: Int) {
            val indexArr = categorySet.toArray()

            holder.bind(indexArr[position].toString())
        }

        override fun getItemCount(): Int {
            return categorySet.count()
        }
    }
}