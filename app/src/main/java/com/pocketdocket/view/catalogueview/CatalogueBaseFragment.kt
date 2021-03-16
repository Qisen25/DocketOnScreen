package com.pocketdocket.view.catalogueview

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.Toolbar
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import com.pocketdocket.R
import com.pocketdocket.view.main.MainActivity


/**
 * Base fragment for enabling nesting of adding/editing menu fragment and add item/dish fragment
 */
class CatalogueBaseFragment : Fragment() {

    private val listOfMenus: MutableList<String> = mutableListOf()

    companion object {
        fun newInstance(): CatalogueBaseFragment = CatalogueBaseFragment()
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

        // Make sure frame under support tool bar
//        view.findViewById<FrameLayout>(R.id.manageMenuContainer).updateLayoutParams<ConstraintLayout.LayoutParams> {
//            topToBottom = R.id.toolbar
//        }

        val mainAct = (activity as MainActivity)

        // Get action bar with back button
        val tb = view.findViewById<Toolbar>(R.id.toolbar)
        mainAct.setSupportActionBar(tb)
        mainAct.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mainAct.supportActionBar?.setDisplayShowHomeEnabled(true)
        mainAct.supportActionBar?.setDisplayShowTitleEnabled(true)
        tb.title = ""

        tb.setNavigationOnClickListener {
            // Go back to previous fragment
            parentFragmentManager.popBackStack()
        }

        this.setupMenuAdder()
    }

    /**
     * Set up a nested fragment that allows user to add a menu
     */
    private fun setupMenuAdder() {
        val adderFrag = CatalogueManagerFragment.newInstance()
        parentFragmentManager.beginTransaction().replace(R.id.manageMenuContainer, adderFrag).commitAllowingStateLoss()
    }

}