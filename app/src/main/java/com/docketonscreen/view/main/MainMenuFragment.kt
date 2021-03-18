package com.docketonscreen.view.main

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.docketonscreen.R
import com.docketonscreen.view.catalogueview.CatalogueBaseFragment

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

        // Load screen using handler to prevent blocking background thread
        Handler(Looper.getMainLooper()).postDelayed({
            val frag = CatalogueBaseFragment.newInstance()
            this.openAddMenuFrag(frag)
        }, 3000)
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
        parentFragmentManager.beginTransaction().replace(R.id.mainContainer, fragment).commitAllowingStateLoss()
    }
}