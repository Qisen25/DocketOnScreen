package com.pocketdocket.view.order

import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.andremion.counterfab.CounterFab
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.tabs.TabLayout
import com.pocketdocket.R
import com.pocketdocket.model.Cart
import com.pocketdocket.model.Catalogue
import com.pocketdocket.model.Item
import com.pocketdocket.model.ItemOrder
import com.pocketdocket.view.main.MainActivity

/**
 * Fragment for handling ordering of items
 */
class OrderingFragment : Fragment() {

    private lateinit var categoryAdapter: CategoryRecyclerViewAdapter
    private lateinit var orderAdapter: OrderItemViewAdapter
    private val categorySet = linkedSetOf<String>("All")
    private lateinit var currMenu: Catalogue
    private val cart = Cart()
    private lateinit var cartCountFab: CounterFab

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
            cartCountFab?.hide()
        }

        searchView.setOnCloseListener {
            cartCountFab?.show()
            false
        }

        categoryItem.setOnMenuItemClickListener {
            cart.clear()
            notifyFABbadge()
            false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
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
        val bBar = view.findViewById<BottomAppBar>(R.id.bottomBar)
        val tb = view.rootView.findViewById<Toolbar>(R.id.toolbar)
        tb.visibility = View.INVISIBLE
        mainAct.setSupportActionBar(bBar)

        cartCountFab = view.findViewById<CounterFab>(R.id.cartFab)

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
            gatherCategories()

            val orderRecyclerView = view.findViewById<RecyclerView>(R.id.orderItemRecyclerList)
            orderAdapter = OrderItemViewAdapter()
            val orderLayoutMan = LinearLayoutManager(context)
            orderRecyclerView.adapter = orderAdapter
            orderRecyclerView.layoutManager = orderLayoutMan

            // Add dividers to list view
            val itemDec = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
            itemDec.setDrawable(context?.getDrawable(R.drawable.divider)!!)
            orderRecyclerView.addItemDecoration(itemDec)

            val tabLayout = view.findViewById<TabLayout>(R.id.categoryTabLayout)
            setupTabs(tabLayout)

            cartCountFab.setOnClickListener{
                val summaryFragment = OrderSummaryFragment.newInstance()
                val bundle = Bundle().apply { putParcelable("Cart", cart) }
                summaryFragment.arguments = bundle

                parentFragmentManager.beginTransaction().replace(R.id.manageMenuContainer, summaryFragment).addToBackStack(null).commit()
            }

        }

    }

    private fun gatherCategories() {
        for (item in currMenu.getItems()) {
            if (!item.category.isNullOrEmpty())
                categorySet.add(item.category)
        }
    }

    private fun setupTabs(tabLayout: TabLayout) {
        for (category in categorySet){
            tabLayout.addTab(tabLayout.newTab().setText(category))
        }
    }

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

    /**
     * Recycler view that shows items to order available in menu
     */
    inner class OrderItemViewAdapter : RecyclerView.Adapter<OrderItemViewAdapter.ItemHolder>() {

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
            holder.bind(currMenu.getItems()[position])
        }

        override fun getItemCount(): Int {
            return currMenu.getItems().count()
        }
    }
}