package com.invisibles.smssorter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.invisibles.smssorter.Adapters.SmsListAdapter
import com.invisibles.smssorter.Attributes.DBColums
import com.invisibles.smssorter.Attributes.DBHelper
import com.invisibles.smssorter.Models.Sender
import com.invisibles.smssorter.Models.SmsItemDB
import com.invisibles.smssorter.Models.SmsMessage
import com.invisibles.smssorter.Tools.AnimationTools
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
    private lateinit var db: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_chats_screen)

        init()

    }


    private fun init(){

        searchText = findViewById(R.id.search_main_screen)
        messageList = findViewById(R.id.message_list_main_screen)
        db = DBHelper(this)

        listOfMessages = arrayListOf()

        messageList.layoutManager = LinearLayoutManager(this)

        val anim = AnimationUtils.loadAnimation(this, R.anim.contacts_animation)
        val controller = LayoutAnimationController(anim, animationDelay)
        messageList.layoutAnimation = controller

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

        Thread{
            initDB()

            loadContacts()
        }.start()

        initEvents()
    }

    private fun initDB() {

        listOfMessages = SmsTools(this).getAllSms() as ArrayList<SmsMessage>

        var dbRead = db.readableDatabase

        if (!DBTools.tableIsExist(dbRead, "AllSms")){
            db.createTable("AllSms", mapOf(
                "id" to "integer primary key",
                "contactName" to "text",
                "contactNumber" to "text",
                "text" to "text",
                "time" to "integer",
                "type" to "integer"
                ))

        }

        val el = mutableMapOf<String, Sender>()

        if (listOfMessages.isEmpty()){


            val sms = DBTools.getData("AllSms", db)

            sms.forEach {

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

                if (!DBTools.itemIsExistById(dbRead, "AllSms", message.id)){
                    dbRead.close()
                    val contactName = SmsTools(this).getContactName(message.address)

                    DBTools.writeData("AllSms", db, mapOf(
                        DBColums.SMS.ID to message.id,
                        DBColums.SMS.NAME to contactName,
                        DBColums.SMS.NUMBER to message.address,
                        DBColums.SMS.TEXT to message.text,
                        DBColums.SMS.TIME to message.time,
                        DBColums.SMS.TYPE to message.type
                    ))

                }

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

    }

    private fun loadContacts() {

        runOnUiThread {
            adapterMessage = SmsListAdapter(messages, this)
            messageList.adapter = adapterMessage
            messageList.scheduleLayoutAnimation()
        }

    }
}