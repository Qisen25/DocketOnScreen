package com.pocketdocket.view.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.pocketdocket.R
import com.pocketdocket.view.catalogueview.CatalogueBaseFragment

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class MainMenuFragment : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mainmenu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        view.findViewById<Button>(R.id.button_first).setOnClickListener {
//            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
//        }

        view.findViewById<Button>(R.id.addEditMenuButton).setOnClickListener { view ->
            val frag = CatalogueBaseFragment.newInstance()
            this.openAddMenuFrag(frag)
        }
    }

    companion object {
        fun newInstance(): MainMenuFragment = MainMenuFragment()
    }

    /**
     * Load add/edit menu fragment, (take over mainscreen)
     */
    private fun openAddMenuFrag(fragment: Fragment) {
        // Get fragment manager
        // This replaces main activity's frame layout container which allows main activity to be used without creating more activities
        parentFragmentManager.beginTransaction().replace(R.id.mainContainer, fragment).addToBackStack(null).commit()
    }
}