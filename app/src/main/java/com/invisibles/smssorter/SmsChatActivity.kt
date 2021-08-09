package com.invisibles.smssorter

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.invisibles.smssorter.Adapters.ChatAddapter
import com.invisibles.smssorter.Models.Sender
import com.invisibles.smssorter.Models.SmsMessage
import com.invisibles.smssorter.Tools.AnimationTools
import com.invisibles.smssorter.Tools.SmsTools

class SmsChatActivity : AppCompatActivity() {

    private lateinit var editText: EditText
    private lateinit var buttonBack: ImageButton
    private lateinit var contactNameView: TextView
    private lateinit var contactNameText: String
    private lateinit var contactNumber: String
    private lateinit var messageList: ArrayList<SmsMessage>
    private lateinit var sender: Sender
    private lateinit var mainPanel: CardView
    private lateinit var rightDownPanel: RelativeLayout
    private lateinit var messageChatList: RecyclerView
    private lateinit var chatAddapter: ChatAddapter
    private lateinit var searchButton: ImageButton
    private lateinit var standartTopPanel: RelativeLayout
    private lateinit var searchTopPanel: RelativeLayout
    private lateinit var searchField: EditText
    private var isSearch = false
    private var intForUp: Int = 0
    private var mainPanelHeight: Int = 0
    private var rightDownPanelHeight: Int = 0
    private var lineCount: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sms_chat)

        init()
        setupEvents()

    }

    private fun setupEvents() {
        buttonBack.setOnClickListener {

            if (isSearch){

                searchTopPanel.visibility = View.GONE
                standartTopPanel.visibility = View.VISIBLE
                searchField.clearFocus()
                keyboardControl(false)
                isSearch = false


            }
            else {
                onBackPressed()
            }
        }

        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                if (editText.text.isNotEmpty()) {

                    if (editText.lineCount != lineCount && editText.lineCount < 6 && editText.lineCount > 1) {

                        if (lineCount < editText.lineCount) {


                            val animBlock = AnimationTools.simpleTransition(
                                mainPanel.measuredHeight,
                                mainPanel.measuredHeight + intForUp,
                                mainPanel,
                                height = true,
                                duration = 200
                            )

                            animBlock.start()

                            lineCount += 1
                        } else if (lineCount > editText.lineCount) {
                            val animBlock = AnimationTools.simpleTransition(
                                mainPanel.measuredHeight,
                                mainPanel.measuredHeight - intForUp,
                                mainPanel,
                                height = true,
                                duration = 200
                            )

                            animBlock.start()
                            lineCount -= 1
                        }


                    }
                    else if(editText.lineCount == 1){
                        val animBlock = AnimationTools.simpleTransition(mainPanel.measuredHeight, mainPanelHeight, mainPanel, height = true)

                        animBlock.start()
                        lineCount = 1
                    }
                }
                else if (editText.text.isEmpty()) {
                    lineCount = 1
                    val animBlock = AnimationTools.simpleTransition(mainPanel.measuredHeight, mainPanelHeight, mainPanel, height = true)

                    animBlock.start()

                }
            }

        })

        searchButton.setOnClickListener {

            standartTopPanel.visibility = View.GONE
            searchTopPanel.visibility = View.VISIBLE
            searchField.requestFocus()
            searchField.isFocusableInTouchMode = true
            keyboardControl(true)
            isSearch = true


        }

        searchField.setOnEditorActionListener { textView, i, keyEvent ->

            if (i == EditorInfo.IME_ACTION_SEARCH){
                keyboardControl(false)

                return@setOnEditorActionListener true
            }
            else return@setOnEditorActionListener false

        }

    }

    private fun init() {
        editText = findViewById(R.id.msg_text_chat)
        buttonBack = findViewById(R.id.button_back_chat)
        contactNameView = findViewById(R.id.contact_name_chat)
        mainPanel = findViewById(R.id.main_text_edit_panel_chat)
        rightDownPanel = findViewById(R.id.down_panel_chat)
        messageChatList = findViewById(R.id.msg_space_chat)
        searchButton = findViewById(R.id.search_button_chat)
        searchTopPanel = findViewById(R.id.search_top_panel_chat)
        standartTopPanel = findViewById(R.id.standart_top_panel_chat)
        searchField = findViewById(R.id.edit_text_search_top_panel_chat)

        mainPanelHeight = mainPanel.layoutParams.height
        rightDownPanelHeight = rightDownPanel.layoutParams.height
        intForUp = editText.layoutParams.height
        lineCount = 1

        contactNameText = intent.getStringExtra("contactName").toString()
        contactNumber = intent.getStringExtra("contactNumber").toString()

        messageList = SmsTools(this).getAllSmsWithContact(contactNumber).sortedBy { it.messageTime }.toMutableList() as ArrayList<SmsMessage>

        chatAddapter = ChatAddapter(messageList, this, contactNameText)
        messageChatList.layoutManager = LinearLayoutManager(this)
        messageChatList.adapter = chatAddapter
        messageChatList.scrollToPosition(messageList.size-1)


        sender = Sender(contactNumber, contactNameText)

        contactNameView.text = sender.senderName

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }


    private fun keyboardControl(controller: Boolean){

        if (controller){
            val imn = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imn.showSoftInput(searchField, InputMethodManager.SHOW_FORCED)
        }
        else{
            val imn = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imn.hideSoftInputFromWindow(searchField.windowToken, 0)
        }

    }
}