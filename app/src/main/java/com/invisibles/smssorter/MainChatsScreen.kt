package com.invisibles.smssorter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.invisibles.smssorter.Adapters.SmsListAdapter
import com.invisibles.smssorter.Models.Sender
import com.invisibles.smssorter.Models.SmsMessage
import com.invisibles.smssorter.Tools.AnimationTools
import com.invisibles.smssorter.Tools.SmsTools
import java.util.*
import kotlin.collections.ArrayList

private const val animationDelay = 0.3f

class MainChatsScreen : AppCompatActivity() {

    private lateinit var searchText: EditText
    private lateinit var messageList: RecyclerView
    private lateinit var adapterMessage: SmsListAdapter
    private lateinit var messages: ArrayList<Sender>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_chats_screen)

        init()

    }


    private fun init(){

        searchText = findViewById(R.id.search_main_screen)
        messageList = findViewById(R.id.message_list_main_screen)
        adapterMessage = SmsListAdapter(arrayListOf(), this)

        messageList.layoutManager = LinearLayoutManager(this)
        messageList.adapter = adapterMessage

        val anim = AnimationUtils.loadAnimation(this, R.anim.contacts_animation)
        val controller = LayoutAnimationController(anim, animationDelay)
        messageList.layoutAnimation = controller

        messages = SmsTools(this).getSms() as ArrayList<Sender>


        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

        loadContacts()

        initEvents()
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
                                text in it.firstMessage.messageText.toLowerCase(Locale.ROOT) ||
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
        val loader = Thread{

            for (i in 0 until messages.size) {

                val person = messages[i]
                val name = SmsTools(this).getContactName(person.senderNumber)
                person.senderName = name


                runOnUiThread {
                    adapterMessage.addPerson(person)
                    messageList.scheduleLayoutAnimation()
                }


            }

        }

        loader.start()
    }
}