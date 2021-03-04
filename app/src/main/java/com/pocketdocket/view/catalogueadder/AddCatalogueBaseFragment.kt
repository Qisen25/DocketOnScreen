package com.pocketdocket.view.catalogueadder

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toolbar
import androidx.annotation.RequiresApi
import com.pocketdocket.R


/**
 * Base fragment for enabling nesting of adding/editing menu fragment and add item/dish fragment
 */
class AddCatalogueBaseFragment : Fragment() {

    private val listOfMenus: MutableList<String> = mutableListOf()

    companion object {
        fun newInstance(): AddCatalogueBaseFragment = AddCatalogueBaseFragment()
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_catalogue_base, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get action bar with back button
        val tb = view.findViewById<Toolbar>(R.id.toolbar)
        activity?.setActionBar(tb)
        activity?.actionBar?.setDisplayHomeAsUpEnabled(true)
        activity?.actionBar?.setDisplayShowHomeEnabled(true)
//        activity?.actionBar?.setDisplayShowTitleEnabled(false)
        tb.title = ""

        tb.setNavigationOnClickListener {
            // Go back to previous fragment (Should be the Main Menu
            parentFragmentManager.popBackStack()
        }

        this.setupMenuAdder()
    }

    /**
     * Set up a nested fragment that allows user to add a menu
     */
    private fun setupMenuAdder() {
        val adderFrag = AddCatalogueFragment.newInstance()
        parentFragmentManager.beginTransaction().replace(R.id.addMenuContainer, adderFrag).commit()
    }

}