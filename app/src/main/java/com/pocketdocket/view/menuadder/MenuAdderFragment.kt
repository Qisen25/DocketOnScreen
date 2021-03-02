package com.pocketdocket.view.menuadder

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.pocketdocket.R
import com.pocketdocket.model.Catalogue
import com.pocketdocket.model.MenuRepository

/**
 * Fragment class that handles the adding and editing menus
 */
class MenuAdderFragment : Fragment() {

   private var listOfMenus: MutableList<Catalogue> = MenuRepository.menus

    companion object {
        fun newInstance(): MenuAdderFragment = MenuAdderFragment()
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_addmenu_part, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val listv = view.findViewById<ListView>(R.id.recipe_list_view)
        val submitContainer = view.findViewById<LinearLayout>(R.id.addMenuInputContainer)
        val submitButt =  view.findViewById<Button>(R.id.addMenuNameButton)
        val textInpLay = view.findViewById<TextInputLayout>(R.id.textInputLay)
        val textEditInp = view.findViewById<TextInputEditText>(R.id.textintedit)

        val floatAddButton = view.findViewById<FloatingActionButton>(R.id.fabAddMenu)

        // Setup recycle view for list menus
        val recyView = view.findViewById<RecyclerView>(R.id.recycleList)
        val menuAdapter = MenuRecyclerViewAdapter(this.listOfMenus)
        val layoutMan = LinearLayoutManager(context)
        recyView.layoutManager = layoutMan
        recyView.adapter = menuAdapter

        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        submitButt.setOnClickListener {
            // Get name input  and store name onto app
            val txt = view.findViewById<TextInputEditText>(R.id.textintedit)
//            this.listOfMenus.add(Catalogue(txt.text.toString()))

            MenuRepository.addMenu(Catalogue(txt.text.toString()))

            menuAdapter.notifyItemInserted(listOfMenus.count() - 1);

            submitContainer.visibility = View.VISIBLE
            // hide keyboard
            imm.hideSoftInputFromWindow(textEditInp.windowToken, 0)
        }

        floatAddButton.setOnClickListener {
            // Get name input  and store name onto app
            var visible = submitContainer.visibility

            if(visible == View.VISIBLE) {
                submitContainer.visibility = View.INVISIBLE
            }
            else {
                submitContainer.visibility = View.VISIBLE
                floatAddButton.visibility = View.INVISIBLE
                println(submitContainer.visibility)
                // Focus on the text input layout so the user can start typing
                textInpLay.requestFocus()
                // Show the soft keyboard to let the user type
                imm.showSoftInput(textEditInp, InputMethodManager.SHOW_IMPLICIT)
            }
//            val txt = view.findViewById<EditText>(R.id.menuNameInput)
//            this.listOfMenus.add(txt.text.toString())
//
//            this.updateListView(listv, view.context)
        }

        textEditInp.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                val menu = Catalogue(textEditInp.text.toString())

//                listOfMenus.add(menu)
                MenuRepository.addMenu(menu)

                menuAdapter.notifyItemInserted(listOfMenus.count() - 1)

                submitContainer.visibility = View.INVISIBLE
                // hide keyboard
                imm.hideSoftInputFromWindow(textEditInp.windowToken, 0)
//                floatAddButton.visibility = View.VISIBLE
                return@setOnKeyListener true
            }
            false
        }

        textEditInp.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus) {
                floatAddButton.visibility = View.INVISIBLE
            }
            else {
                floatAddButton.visibility = View.VISIBLE
            }
        }


        view.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val heightDiff: Int = view.rootView.height - view.height

                println(heightDiff)

                if (heightDiff > 400) { // Value should be less than keyboard's height
                    floatAddButton.visibility = View.INVISIBLE
                    submitContainer.visibility=View.VISIBLE
                    textInpLay.requestFocus()
                } else {
                    floatAddButton.visibility = View.VISIBLE
                    submitContainer.visibility=View.INVISIBLE
                }
            }
        })
//        listv.setOnItemClickListener { parent, view, position, id ->
            // Bundle name and send to item adder fragment
//            val itemFrag = AddItemFragment.newInstance()
//            val bundle = bundleOf(Pair("MenuName", listv.getItemAtPosition(position)))
//            itemFrag.arguments = bundle
//
//            // use previous fragment manager
//            parentFragmentManager.beginTransaction().replace(R.id.addMenuContainer, itemFrag).addToBackStack(null).commit()
//        }
//
//        this.updateListView(listv, view.context)
    }

//    private fun updateListView(listv: ListView, context: Context) {
//        // Get adapter to plug in listOfMenus
//        val adapter = ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, this.listOfMenus)
//        // update list view
//        listv.adapter = adapter
//    }

    /**
     * Recycler view class for handling list view of menu names
     * Future could add image and other details relating to organisation
     */
    inner class MenuRecyclerViewAdapter(inListOfMenus: MutableList<Catalogue>) : RecyclerView.Adapter<MenuRecyclerViewAdapter.MenuHolder>() {

        private val recycleList = inListOfMenus

        inner class MenuHolder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {
            private val textv = v.findViewById<TextView>(R.id.cardMenuName)

            init {
                v.setOnClickListener(this)
            }

            fun bind(menu: Catalogue) {
                textv.text = menu.name
            }

            override fun onClick(v: View?) {
                val itemFrag = AddItemFragment.newInstance()
                val bundle = bundleOf(Pair("MenuName", recycleList[adapterPosition].name))
                itemFrag.arguments = bundle

                // use previous fragment manager
                parentFragmentManager.beginTransaction().replace(R.id.addMenuContainer, itemFrag).addToBackStack(null).commit()
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuRecyclerViewAdapter.MenuHolder {
            val inflateRowView = LayoutInflater.from(context).inflate(R.layout.menu_recycle_row, parent, false)

            return MenuHolder(inflateRowView)
        }

        override fun onBindViewHolder(holder: MenuRecyclerViewAdapter.MenuHolder, position: Int) {
            val menu = listOfMenus.get(position)

            holder.bind(menu)
        }

        override fun getItemCount(): Int {
            return recycleList.count()
        }

    }
}