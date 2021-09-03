package com.invisibles.smssorter

import android.annotation.SuppressLint
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
import android.widget.ScrollView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
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

private const val animationDelay = 0.3f

class MainChatsScreen : AppCompatActivity() {

    private lateinit var searchText: EditText
    private lateinit var messageList: RecyclerView
    private lateinit var adapterMessage: SmsListAdapter
    private lateinit var messages: ArrayList<Sender>
    private lateinit var listOfMessages: ArrayList<SmsMessage>
    private lateinit var dbMessages: ArrayList<SmsItemDB>
    private lateinit var addChatButton: FloatingActionButton
    private lateinit var scrollView: ScrollView
    private lateinit var foldersButton: CardView
    private lateinit var foldersIcon: ImageView
    private lateinit var db: DBHelper

    private var foldersIsActive = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_chats_screen)

        init()

    }


    private fun init(){

        searchText = findViewById(R.id.search_main_screen)
        messageList = findViewById(R.id.message_list_main_screen)
        addChatButton = findViewById(R.id.add_chat_button)
        scrollView = findViewById(R.id.chats_scroll)
        foldersButton = findViewById(R.id.folders_block_main_screen)
        foldersIcon = findViewById(R.id.folder_icon_main_screen)
        db = DBHelper(this)

        listOfMessages = arrayListOf()

        messageList.layoutManager = LinearLayoutManager(this)

        val anim = AnimationUtils.loadAnimation(this, R.anim.contacts_animation)
        val controller = LayoutAnimationController(anim, animationDelay)
        messageList.layoutAnimation = controller

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

        Log.i("wdwd", "1")


        Thread{
            initDB()

            loadContacts()
        }.start()

        initEvents()
    }

    private fun initDB() {

        listOfMessages = SmsTools(this).getAllSms() as ArrayList<SmsMessage>

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

        if (listOfMessages.isEmpty()){


            val sms = DBTools.getData(DBTables.SMS, db)

            sms.forEach { it as SmsItemDB

                val number = it.number
                val name = it.name
                val nmSr = SmsTools.formatNumber(number)
                val item = el.getOrDefault(nmSr, Sender(number, name))
                item.addMessage(SmsTools.convertDBtoSMS(it))
                el[nmSr] = item

            }

        }

        else{
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

        }

        messages = el.values.sortedBy { it.messagesList.first().time }.reversed() as ArrayList<Sender>

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

                if (messages.isNullOrEmpty()) return
                else if (text == "") {
                    adapterMessage.update(messages)
                } else {

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

        })

        addChatButton.setOnClickListener {

        }

        scrollView.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->

            val x = scrollY - oldScrollY

            if (x > 0 || x < 0){
                addChatButton.visibility = View.GONE
            }



        }

        foldersButton.setOnClickListener {

            if (foldersIsActive){
                foldersIcon.setColorFilter(ContextCompat.getColor(this, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN)
                Thread{
                    loadContacts()
                }.start()

                foldersIsActive = false
            }
            else {
                foldersIsActive = true
                foldersIcon.setColorFilter(ContextCompat.getColor(this, R.color.accent), android.graphics.PorterDuff.Mode.SRC_IN)
                val folders = getFolders()
                loadFolders(folders)
            }

        }

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
        }

    }
}