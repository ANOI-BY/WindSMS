package com.invisibles.smssorter.Adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.invisibles.smssorter.Attributes.LogName
import com.invisibles.smssorter.Models.Date
import com.invisibles.smssorter.Models.SmsMessage
import com.invisibles.smssorter.R
import com.invisibles.smssorter.Tools.ConvertTools

private const val MSG_TYPE_LEFT = 1
private const val MSG_TYPE_RIGHT = 2
private const val DATE_TYPE = 3

class ChatAddapter(private var data: ArrayList<SmsMessage>, private val context: Context, private val senderName: String) :
    RecyclerView.Adapter<ChatAddapter.ViewHolder>() {


    class ViewHolder(itemView: View, viewType: Int) : RecyclerView.ViewHolder(itemView) {

        lateinit var messageText: TextView
        lateinit var textBlock: CardView
        lateinit var time: TextView
        lateinit var dateText: TextView

        init {
            if (viewType == MSG_TYPE_LEFT){
                messageText = itemView.findViewById(R.id.message_left_msg_text)
                textBlock = itemView.findViewById(R.id.message_left_text_block)
                time = itemView.findViewById(R.id.message_time)
            }
            else if (viewType == MSG_TYPE_RIGHT){
                messageText = itemView.findViewById(R.id.message_left_msg_text)
                textBlock = itemView.findViewById(R.id.message_left_text_block)
                time = itemView.findViewById(R.id.message_time)
            }
            else if (viewType == DATE_TYPE){
                dateText = itemView.findViewById(R.id.chat_date)
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == MSG_TYPE_LEFT){
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.chat_message_left, parent, false)
            ViewHolder(itemView, viewType)
        }
        else if (viewType == MSG_TYPE_RIGHT) {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.chat_message_right, parent, false)
            ViewHolder(itemView, viewType)
        }
        else{
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.chat_message_date, parent, false)
            ViewHolder(itemView, viewType)
        }

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (data[position].type == MSG_TYPE_RIGHT || data[position].type == MSG_TYPE_LEFT){
            holder.messageText.text = data[position].text
            val date = ConvertTools.timestampToDate("HH:mm", data[position].time)
            holder.time.text = date
        }

        if(data[position].type == MSG_TYPE_RIGHT){
            holder.textBlock.setBackgroundResource(R.drawable.message_background_right)
        }
        else if(data[position].type == MSG_TYPE_LEFT){
            holder.textBlock.setBackgroundResource(R.drawable.message_background)
        }
        else if (data[position].type == DATE_TYPE){
            holder.dateText.text = ConvertTools.timestampToDate("dd MMMM", data[position].time)
        }

    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemViewType(position: Int): Int {
        return data[position].type
    }

    fun update(list: ArrayList<SmsMessage>){
        this.data = list
        notifyDataSetChanged()
    }

    fun createDateMarks(){

        val x = true
        var i = 0

        while (x){

            if (i == data.size){
                break
            }

            val el = data[i]

            if (i == 0){
                data.add(0, SmsMessage(0, "DATE", el.time, type = 3, address = "1"))
            }

            if (i+1 < data.size){
                val el2 = data[i+1]

                val day = ConvertTools.timestampToDate("dd", el.time).toInt()
                val day2 = ConvertTools.timestampToDate("dd", el2.time).toInt()

                if (day != day2){
                    data.add(i+1, SmsMessage(0, "DATE", el2.time, type = 3, address = "1"))
                }

            }
            else{
            }

            i += 1



        }

    }

}