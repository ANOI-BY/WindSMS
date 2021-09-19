package com.invisibles.smssorter

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.drawable.GradientDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.ScrollView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.invisibles.smssorter.Adapters.FoldersListAdapter
import com.invisibles.smssorter.Adapters.SmsListAdapter
import com.invisibles.smssorter.Attributes.DBColums
import com.invisibles.smssorter.Attributes.DBHelper
import com.invisibles.smssorter.Attributes.DBTables
import com.invisibles.smssorter.Attributes.LogName
import com.invisibles.smssorter.Models.FolderItemDB
import com.invisibles.smssorter.Models.Sender
import com.invisibles.smssorter.Models.SmsItemDB
import com.invisibles.smssorter.Models.SmsMessage
import com.invisibles.smssorter.Tools.DBTools
import com.invisibles.smssorter.Tools.SmsTools
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs

private const val animationDelay = 0.3f

class MainChatsScreen : AppCompatActivity() {

    private lateinit var searchText: EditText
    private lateinit var messageList: RecyclerView
    private lateinit var adapterMessage: SmsListAdapter
    private lateinit var messages: ArrayList<Sender>
    private lateinit var addChatButton: FloatingActionButton
    private lateinit var foldersButton: CardView
    private lateinit var foldersIcon: ImageView
    private lateinit var progressBar: ConstraintLayout
    private lateinit var db: DBHelper

    private var foldersIsActive = false
    private var debugTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_chats_screen)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        init()

    }


    private fun init(){

        searchText = findViewById(R.id.search_main_screen)
        messageList = findViewById(R.id.message_list_main_screen)
        addChatButton = findViewById(R.id.add_chat_button)
        foldersButton = findViewById(R.id.folders_block_main_screen)
        foldersIcon = findViewById(R.id.folder_icon_main_screen)
        progressBar = findViewById(R.id.main_screen_progressbar)
        db = DBHelper(this)

        messageList.layoutManager = LinearLayoutManager(this)

        val anim = AnimationUtils.loadAnimation(this, R.anim.contacts_animation)
        val controller = LayoutAnimationController(anim, animationDelay)
        messageList.layoutAnimation = controller

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

        debugTime = System.currentTimeMillis()

        Thread{ initDB() }.start()

        initEvents()
    }

    private fun initDB() {

        var dbRead = db.readableDatabase

        if (!DBTools.tableIsExist(dbRead, DBTables.SMS)){
            db.createTable(DBTables.SMS, mapOf(
                DBColums.SMS.ID to "integer primary key",
                DBColums.SMS.NAME to "text",
                DBColums.SMS.NUMBER to "text",
                DBColums.SMS.TEXT to "text",
                DBColums.SMS.TIME to "integer",
                DBColums.SMS.TYPE to "integer",
                DBColums.SMS.TO_FOLDER to "integer"
            ))

            db.createTable(DBTables.FOLDERS, mapOf(
                DBColums.FOLDER.ID to "integer primary key",
                DBColums.FOLDER.NAME to "text",
                DBColums.FOLDER.ICON to "integer"
            ))

            if(!DBTools.itemIsExistById(dbRead, DBTables.FOLDERS, 1)){
                DBTools.writeData(DBTables.FOLDERS, db, mapOf(
                    DBColums.FOLDER.ID to 1,
                    DBColums.FOLDER.NAME to "INVISIBLES TEST",
                    DBColums.FOLDER.ICON to R.drawable.ic_folder
                ))
            }

        }

        val el = mutableMapOf<String, Sender>()


        val sms = DBTools.getData(DBTables.SMS, db)

        if (sms.isEmpty()){
            runOnUiThread {
                progressBar.visibility = View.VISIBLE
                addChatButton.visibility = View.GONE
            }
            val listOfMessages = SmsTools(this).getAllSms() as ArrayList<SmsMessage>
            for (message in listOfMessages){
                dbRead = db.readableDatabase
                if (!DBTools.itemIsExistById(dbRead, DBTables.SMS, message.id)){
                    val contactName = SmsTools(this).getContactName(message.address)

                    DBTools.writeData(DBTables.SMS, db, mapOf(
                        DBColums.SMS.ID to message.id,
                        DBColums.SMS.NAME to contactName,
                        DBColums.SMS.NUMBER to message.address,
                        DBColums.SMS.TEXT to message.text,
                        DBColums.SMS.TIME to message.time,
                        DBColums.SMS.TYPE to message.type,
                        DBColums.SMS.TO_FOLDER to 0
                    ))

                }

                if (dbRead.isOpen) dbRead.close()
                dbRead = db.readableDatabase

                val sms = DBTools.getItemById(dbRead, "AllSms", message.id)
                val number = sms.number
                val name = sms.name
                val nmSr = SmsTools.formatNumber(number)
                val item = el.getOrDefault(nmSr, Sender(number, name))
                item.addMessage(SmsTools.convertDBtoSMS(sms))
                el[nmSr] = item
                }

            runOnUiThread {
                progressBar.visibility = View.GONE
                addChatButton.visibility = View.GONE
            }
            }
        else{
            sms.forEach sms@{ it as SmsItemDB

                val number = it.number
                val name = it.name
                val item = el.get(name)
                if (item == null){
                    val element = Sender(number, name)
                    element.addMessage(SmsTools.convertDBtoSMS(it))
                    el[name] = element
                }
                else {
                    if (item.firstMessage.time < it.time) {
                        val element = Sender(number, name)
                        element.addMessage(SmsTools.convertDBtoSMS(it))
                        el[name] = element
                    }
                }
            }
        }
        messages = el.values.sortedBy { it.messagesList.first().time }.reversed() as ArrayList<Sender>
        loadContacts()

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initEvents() {

        searchText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                val text = searchText.text.toString().toLowerCase(Locale.ROOT)

                if (foldersIsActive){
                    Log.i("Main", text)
                    val folders = getFolders()

                    if (folders.isNullOrEmpty()) return
                    else if (text == ""){
                        loadFolders(folders)
                    }
                    else{

                        val serchResult = folders.filter { text in it.name } as ArrayList

                        if (serchResult.isEmpty()){
                            val none = FolderItemDB()
                            none.name = "NOT_FOUND"
                            none.icon = R.drawable.ic_folder
                            serchResult.add(none)
                        }

                        loadFolders(serchResult)
                    }
                }
                else{
                    if (messages.isNullOrEmpty()) return
                    else if (text == "") {
                        adapterMessage.update(messages)
                    }
                    else {

                        val searchResult = messages.filter {
                            text in it.senderName.toLowerCase(Locale.ROOT) ||
                                    text in it.firstMessage.text.toLowerCase(Locale.ROOT) ||
                                    it.searchInMessages(text).isNotEmpty()
                        } as ArrayList<Sender>

                        if (searchResult.isEmpty()){

                            val notFound = Sender("NOT_FOUND", "NOT_FOUND")
                            notFound.messagesList = arrayListOf(SmsMessage(1,"Not Found", 1, 1, "1"))

                            searchResult.add(notFound)

                        }

                        adapterMessage.update(searchResult)
                        messageList.scheduleLayoutAnimation()
                }
                }
            }

        })

        addChatButton.setOnClickListener {
            if(foldersIsActive){

                val intent = Intent(this, AddNewFolder::class.java)
                startActivity(intent)

            }
        }

        messageList.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->

            if (scrollY == 0){
                addChatButton.visibility = View.VISIBLE
            }
            else{
                addChatButton.visibility = View.GONE
            }

        }

        foldersButton.setOnClickListener {

            if (foldersIsActive){
                foldersIcon.setColorFilter(ContextCompat.getColor(this, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN)
                Thread{ loadContacts() }.start()
                addChatButton.setImageDrawable(getDrawable(R.drawable.ic_edit_square))
                foldersIsActive = false
                updateText()
            }
            else {
                foldersIsActive = true
                foldersIcon.setColorFilter(ContextCompat.getColor(this, R.color.accent), android.graphics.PorterDuff.Mode.SRC_IN)
                addChatButton.setImageDrawable(getDrawable(R.drawable.ic_folder_add))
                val folders = getFolders()
                loadFolders(folders)
                updateText()
            }

        }

        progressBar.setOnClickListener {
            Log.i(LogName.DebugLog, "1")
        }

    }

    private fun updateText() {
        searchText.text.clear()
    }

    private fun loadFolders(folders: ArrayList<FolderItemDB>) {
        val adapter = FoldersListAdapter(folders, this)
        messageList.adapter = adapter
        messageList.scheduleLayoutAnimation()
    }

    private fun getFolders(): ArrayList<FolderItemDB> {
        val folders = DBTools.getData(DBTables.FOLDERS, db)
        return folders as ArrayList<FolderItemDB>
    }

    private fun loadContacts() {

        runOnUiThread {
            adapterMessage = SmsListAdapter(messages, this)
            messageList.adapter = adapterMessage
            messageList.scheduleLayoutAnimation()
            debugTime = System.currentTimeMillis() - debugTime
            Log.i(LogName.DebugLog, "Time for start: ${debugTime.toFloat()/1000} Sec.")
        }

    }
}