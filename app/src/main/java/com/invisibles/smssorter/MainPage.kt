package com.invisibles.smssorter

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.invisibles.smssorter.Adapters.SmsListAdapter
import com.invisibles.smssorter.Adapters.TagsAdapter
import com.invisibles.smssorter.Attributes.LogName
import com.invisibles.smssorter.Models.Sender
import com.invisibles.smssorter.Models.Tags
import com.invisibles.smssorter.Tools.ConvertTools
import com.invisibles.smssorter.Tools.SmsTools
import java.util.*
import kotlin.collections.ArrayList


class MainPage(private var appContext: Context) : Fragment() {

    private lateinit var mainView: View
    private lateinit var recycleMessageList: RecyclerView
    private lateinit var adapterMessage: SmsListAdapter
    private lateinit var adapterTags: TagsAdapter
    private lateinit var search: EditText
    private lateinit var smsList: ArrayList<Sender>
    private lateinit var recycleTagsList: RecyclerView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainView = inflater.inflate(R.layout.fragment_main_page, container, false)

        init()
        initEvents()

        return mainView
    }

    private fun init() {
        recycleMessageList = mainView.findViewById(R.id.message_list_main_page)
        search = mainView.findViewById(R.id.sms_search_text_main_page)
        recycleTagsList = mainView.findViewById(R.id.tags_list_main_page)

        smsList = SmsTools(appContext).getSms() as ArrayList<Sender>

        adapterMessage = SmsListAdapter(arrayListOf(), appContext)
        adapterTags = TagsAdapter(
            arrayListOf(
                Tags("#93f011", "test"),
                Tags("#f02b11", "test2"),
                Tags("#372ea3", "test3"),
                Tags("#e31ebb", "test4"),
                Tags("#93f011", "test5"),
                Tags("#f02b11", "test6"),
                Tags("#372ea3", "test7"),
                Tags("#e31ebb", "test8")
            ), appContext
        )

        recycleMessageList.layoutManager = LinearLayoutManager(mainView.context)
        recycleMessageList.adapter = adapterMessage

        recycleTagsList.layoutManager = LinearLayoutManager(mainView.context, RecyclerView.HORIZONTAL, false)
        recycleTagsList.adapter = adapterTags


        loadContactsName()

    }

    private fun loadContactsName() {
        val thread = Thread {

            for (i in 0 until smsList.size) {

                val person = smsList[i]
                val name = SmsTools(appContext).getContactName(person.senderNumber)
                person.senderName = name


                requireActivity().runOnUiThread {
                    adapterMessage.addPerson(person)
                }


            }

        }
        thread.start()
    }

    private fun initEvents() {

        search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                val text = search.text.toString().toLowerCase()

                if (smsList.isNullOrEmpty()) return
                else if (text == "") {
                    adapterMessage.update(smsList)
                } else {

                    val searchResult = smsList.filter {
                        text in it.senderName.toLowerCase(Locale.ROOT) ||
                                text in it.firstMessage.messageText.toLowerCase(Locale.ROOT) ||
                                it.searchInMessages(text).isNotEmpty()
                    } as ArrayList<Sender>

                    if (searchResult.isEmpty()){

                        searchResult.add(Sender("NOT_FOUND", "NOT_FOUND"))

                    }

                    adapterMessage.update(searchResult)
                }
            }

        })
    }



}


