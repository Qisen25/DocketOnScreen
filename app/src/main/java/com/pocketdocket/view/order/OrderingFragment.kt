package com.pocketdocket.view.order

import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.pocketdocket.R
import com.pocketdocket.view.main.MainActivity

/**
 * Fragment for handling ordering of items
 */
class OrderingFragment : Fragment() {

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

//    override fun onPrepareOptionsMenu(menu: Menu) {
//        super.onPrepareOptionsMenu(menu)
//        val menuItem = menu.findItem(R.id.searcher)
//
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ordering, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mainAct = (activity as MainActivity)
        val bBar = view.findViewById<BottomAppBar>(R.id.bottomBar)
        val tb = view.rootView.findViewById<Toolbar>(R.id.toolbar)
        tb.visibility = View.INVISIBLE
        mainAct.setSupportActionBar(bBar)

//        bBar.setOnMenuItemClickListener {
//            println(bBar.fabAlignmentMode)
//            if (bBar.fabAlignmentMode == BottomAppBar.FAB_ALIGNMENT_MODE_CENTER) {
//                bBar.fabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_END
//                println("Clicking")
//                return@setOnMenuItemClickListener true
//            }
//            false
//        }

//        mainAct.supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}