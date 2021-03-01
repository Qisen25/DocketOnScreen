package com.pocketdocket.view.menuadder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.pocketdocket.R

/**
 * Fragment view for adding dishes to a menu
 */
class AddItemFragment : Fragment() {

    private val listOfMenus: MutableList<String> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_addmenu_additem, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get menu name bundle sent MenuAdderFragment and add it to database
        if(this.arguments != null) {
            view.findViewById<TextView>(R.id.menuName).text = arguments?.get("MenuName").toString()
        }
    }

    companion object {
        fun newInstance(): AddItemFragment = AddItemFragment()
    }
}