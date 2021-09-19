package com.invisibles.smssorter

import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.invisibles.smssorter.Adapters.FolderIconsAdapter
import com.invisibles.smssorter.Attributes.LogName

class AddNewFolder : AppCompatActivity() {

    private lateinit var backButton: ImageButton
    private lateinit var editIconButton: ImageButton
    private lateinit var mainView: RelativeLayout
    private lateinit var editIconsMenu: RelativeLayout
    private lateinit var iconsView: RecyclerView
    private lateinit var iconsAdapter: FolderIconsAdapter
    private lateinit var folderIcon: ImageView

    private var iconsList: ArrayList<Int> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_folder)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        init()
        setupListeners()

    }

    private fun initIcons(){
        iconsList.add(R.drawable.ic_profile)
        iconsList.add(R.drawable.ic_folder)
        iconsList.add(R.drawable.ic_bag)
        iconsList.add(R.drawable.ic_game)
        iconsList.add(R.drawable.ic_buy)
        iconsList.add(R.drawable.ic_work)
        iconsList.add(R.drawable.ic_wallet)
        iconsList.add(R.drawable.ic_star)


    }

    fun closeIconsMenu(){
        mainView.alpha = 1f
        editIconsMenu.visibility = View.GONE
        editIconsMenu.isFocusable = false
    }

    private fun setupListeners() {
        backButton.setOnClickListener { finish() }
        editIconButton.setOnClickListener {

            mainView.alpha = 0.8f
            editIconsMenu.visibility = View.VISIBLE
            editIconsMenu.isFocusable = true


        }
        editIconsMenu.setOnClickListener {

        }
    }

    private fun init(){

        backButton = findViewById(R.id.back_button)
        editIconButton = findViewById(R.id.edit_icon)
        mainView = findViewById(R.id.main_view)
        editIconsMenu = findViewById(R.id.edit_icons_menu)
        iconsView = findViewById(R.id.icons_view)
        folderIcon = findViewById(R.id.folder_icon)

        initIcons()

        iconsView.layoutManager = GridLayoutManager(this, 8)
        iconsAdapter = FolderIconsAdapter(iconsList, this, folderIcon)
        iconsView.adapter = iconsAdapter

    }

}