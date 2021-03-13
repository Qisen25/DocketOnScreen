package com.pocketdocket.view.order

import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import android.widget.SearchView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.pocketdocket.R
import com.pocketdocket.view.main.MainActivity

/**
 * Fragment for handling ordering of items
 */
class OrderingFragment : Fragment() {

    private lateinit var categoryAdapter: CategoryRecyclerViewAdapter
    private val categorySet = linkedSetOf<String>("All", "Test1", "Test2", "Test3", "Test4", "Test5")

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

        val menuItem = menu.findItem(R.id.searcher)
        val searchView = menuItem.actionView as SearchView

        val cart = activity?.findViewById<FloatingActionButton>(R.id.cartFab)
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        searchView.setOnSearchClickListener {
            cart?.hide()
        }

        searchView.setOnCloseListener {
            cart?.show()
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

        val categoryRecycler = view.findViewById<RecyclerView>(R.id.categoryRecyclerBar)
        categoryAdapter = CategoryRecyclerViewAdapter()
        val layoutMan = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        categoryRecycler.layoutManager = layoutMan
        categoryRecycler.adapter = categoryAdapter

        // Add dividers to list view
        val itemDec = DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL)
        itemDec.setDrawable(context?.getDrawable(R.drawable.horizontal_divider)!!)
        categoryRecycler.addItemDecoration(itemDec)

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