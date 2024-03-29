package com.docketonscreen.view.catalogueview

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.docketonscreen.R
import com.docketonscreen.model.Catalogue
import com.docketonscreen.model.CatalogueRepository
import com.docketonscreen.myswiper.helper.MyButton
import com.docketonscreen.myswiper.helper.MySwipeHelper
import com.docketonscreen.myswiper.listeners.MyButtonClickListener
import com.docketonscreen.view.order.OrderingFragment
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams

//import com.pocketdocket.view.order.OrderingFragment

/**
 * Fragment class that handles the adding and editing menus
 */
class CatalogueManagerFragment : Fragment() {

    private var listOfMenus: MutableList<Catalogue> = CatalogueRepository.menus
    private lateinit var menuAdapter: MenuRecyclerViewAdapter
    private var isEditMenu = false

    companion object {
        fun newInstance(): CatalogueManagerFragment = CatalogueManagerFragment()
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_catalogue_addmenu, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tb = view.rootView.findViewById<Toolbar>(R.id.toolbar)
        tb.visibility = View.VISIBLE
        tb.title = "Manage Menus"

        // Make sure frame under support tool bar
        view.rootView.findViewById<FrameLayout>(R.id.manageMenuContainer).updateLayoutParams<ConstraintLayout.LayoutParams> {
            topToTop = ConstraintLayout.LayoutParams.UNSET
            topToBottom = ConstraintLayout.LayoutParams.UNSET
            topToBottom = R.id.toolbar
        }

        val submitContainer = view.findViewById<LinearLayout>(R.id.addMenuInputContainer)
        val submitButt =  view.findViewById<Button>(R.id.addMenuNameButton)
        val textInpLay = view.findViewById<TextInputLayout>(R.id.textInputLay)
        val textEditInp = view.findViewById<TextInputEditText>(R.id.textintedit)

        val floatAddButton = view.findViewById<FloatingActionButton>(R.id.fabAddMenu)

        // Setup recycle view for list menus
        val recyView = view.findViewById<RecyclerView>(R.id.menuRecycleList)
        menuAdapter = MenuRecyclerViewAdapter(this.listOfMenus)
        val layoutMan = LinearLayoutManager(context)
        recyView.layoutManager = layoutMan
        recyView.adapter = menuAdapter

        // Add My own Swiper
        val swipe = object: MySwipeHelper(context, recyView, 200) {
            override fun instantiateMyButton(viewHolder: RecyclerView.ViewHolder, buffer: MutableList<MyButton>) {
                addSwipeButtons(buffer)
            }
        }

        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        submitButt.setOnClickListener {
            CatalogueRepository.addMenu(Catalogue(textEditInp.text.toString()))

            menuAdapter.notifyItemInserted(listOfMenus.count() - 1);

            submitContainer.visibility = View.INVISIBLE
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
                // Focus on the text input layout so the user can start typing
                textInpLay.requestFocus()
                // Show the soft keyboard to let the user type
                imm.showSoftInput(textEditInp, InputMethodManager.SHOW_IMPLICIT)
            }
        }

        textEditInp.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                val menu = Catalogue(textEditInp.text.toString())

                CatalogueRepository.addMenu(menu)

                menuAdapter.notifyItemInserted(listOfMenus.count() - 1)

                submitContainer.visibility = View.INVISIBLE
                // hide keyboard
                imm.hideSoftInputFromWindow(textEditInp.windowToken, 0)
                return@setOnKeyListener true
            }
            false
        }

        // Set focus listener to make sure Floating button is hidden from user when typing
        textEditInp.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus) {
                floatAddButton.visibility = View.INVISIBLE
            }
            else {
                floatAddButton.visibility = View.VISIBLE
            }
        }

        // Hide keyboard when user clicks back button
        view.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val heightDiff: Int = view.rootView.height - view.height

                if (heightDiff > 400 && !isEditMenu) { // Value should be at least keyboard's height
                    // Show input box as key board rises
                    floatAddButton.visibility = View.INVISIBLE
                    submitContainer.visibility = View.VISIBLE
                    textInpLay.requestFocus()
                } else {
                    submitContainer.visibility = View.INVISIBLE
                }
            }
        })
    }

    /**
     * Set up buttons when on swipe
     */
    private fun addSwipeButtons(list: MutableList<MyButton>) {
        // Delete swiper
        val deleteSwipeButton = MyButton(context,
                "Delete",
                30,
                R.drawable.ic_baseline_delete_forever_24,
                Color.parseColor("#FF3C30"),
                object: MyButtonClickListener{
                    override fun onClick(pos: Int) {
                        val alertDialog = AlertDialog.Builder(context)
                        alertDialog.setTitle("Confirm Delete")
                        alertDialog.setMessage("Are sure you want delete?")
                        alertDialog.setCancelable(true)

                        alertDialog.setPositiveButton("Yes", object: DialogInterface.OnClickListener{
                            override fun onClick(dialog: DialogInterface?, which: Int) {
                                CatalogueRepository.removeMenu(listOfMenus[pos])
                                menuAdapter.notifyItemRemoved(pos)
                            }
                        })

                        alertDialog.setNegativeButton("No", object: DialogInterface.OnClickListener{
                            override fun onClick(dialog: DialogInterface?, which: Int) {
                                dialog!!.cancel()
                            }
                        })

                        val alert = alertDialog.create()
                        alert.show()
                    }
                })

        // Manage item swiper
        val manageItemSwipeButton = MyButton(context,
                "Manage Items",
                30,
                R.drawable.ic_baseline_playlist_add_30,
                Color.parseColor("#ffa500"),
                object: MyButtonClickListener{
                    override fun onClick(pos: Int) {
                        val itemFrag = AddItemFragment.newInstance()
                        val bundle = Bundle().apply { putParcelable("Menu", listOfMenus[pos]) }
                        itemFrag.arguments = bundle

                        // use previous fragment manager
                        parentFragmentManager.beginTransaction().replace(R.id.manageMenuContainer, itemFrag).addToBackStack(null).commit()
                    }
                })

        // Edit swiper
        val editSwipeButton = MyButton(context,
                "Edit",
                30,
                R.drawable.ic_baseline_edit_24,
                Color.parseColor("#1E90FF"),
                object: MyButtonClickListener{
                    override fun onClick(pos: Int) {
                        val dialog = AlertDialog.Builder(context)
                        isEditMenu = true
                        dialog.setTitle("Edit Menu")

                        val inputView = LayoutInflater.from(context)
                            .inflate(R.layout.edit_menu_name, view as ViewGroup, false)

                        val nameInput = inputView.findViewById<TextInputEditText>(R.id.editMenuNameInput)
                        dialog.setView(inputView)

                        // Save menu name change
                        dialog.setPositiveButton("Save", object: DialogInterface.OnClickListener {
                            override fun onClick(dialog: DialogInterface?, which: Int) {
                                if (nameInput.text!!.isNotEmpty()) {
                                    val editedMenu = listOfMenus[pos]
                                    editedMenu.name = nameInput.text.toString()
                                    CatalogueRepository.updateMenu(editedMenu)
                                    menuAdapter.notifyItemChanged(pos)
                                }

                                // Wait for notify recycler to finish since it takes time
                                // and may force up keyboard add menu pop up event
                                Handler(Looper.getMainLooper()).postDelayed({
                                    isEditMenu = false
                                }, 80)
                            }
                        })

                        dialog.setNegativeButton("Cancel", object: DialogInterface.OnClickListener {
                            override fun onClick(dialog: DialogInterface?, which: Int) {
                                dialog!!.cancel()
                                isEditMenu = false
                            }
                        })

                        dialog.show()
                    }
                })

        list.addAll(listOf(editSwipeButton, manageItemSwipeButton, deleteSwipeButton))
    }

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
                val ordFrag = OrderingFragment.newInstance()
                val bundle = Bundle().apply { putParcelable("Menu", recycleList[adapterPosition]) }
                ordFrag.arguments = bundle

                // use previous fragment manager
                parentFragmentManager.beginTransaction().replace(R.id.manageMenuContainer, ordFrag).addToBackStack(null).commit()
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