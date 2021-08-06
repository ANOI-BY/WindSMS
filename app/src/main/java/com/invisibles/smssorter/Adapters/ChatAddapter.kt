package com.invisibles.smssorter.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.invisibles.smssorter.Models.SmsMessage
import com.invisibles.smssorter.R
import com.invisibles.smssorter.Tools.ConvertTools

private const val MSG_TYPE_LEFT = 1
private const val MSG_TYPE_RIGHT = 2

class ChatAddapter(private var data: ArrayList<SmsMessage>, private val context: Context, private val senderName: String) :
    RecyclerView.Adapter<ChatAddapter.ViewHolder>() {


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val iconColor: CardView = itemView.findViewById(R.id.message_left_icon_color)
        val iconLetter: TextView = itemView.findViewById(R.id.message_left_icon_letter)
        val messageText: TextView = itemView.findViewById(R.id.message_left_msg_text)
        val textBLock: CardView = itemView.findViewById(R.id.message_left_text_block)
        val time: TextView = itemView.findViewById(R.id.message_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == MSG_TYPE_LEFT){
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.chat_message_left, parent, false)
            ViewHolder(itemView)
        } else{
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.chat_message_right, parent, false)
            ViewHolder(itemView)
        }

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.iconLetter.text = senderName[0].toString()
        holder.messageText.text = data[position].messageText
        val date = ConvertTools.timestampToDate("HH:mm", data[position].messageTime)
        holder.time.text = date

        if(data[position].messageType == MSG_TYPE_RIGHT){
            holder.textBLock.layoutParams.width = holder.messageText.layoutParams.width*10
        }

    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemViewType(position: Int): Int {
        return data[position].messageType
    }

    fun update(list: ArrayList<SmsMessage>){
        this.data = list
        notifyDataSetChanged()
    }

}