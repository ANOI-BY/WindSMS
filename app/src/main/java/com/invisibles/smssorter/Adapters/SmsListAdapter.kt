package com.invisibles.smssorter.Adapters

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.invisibles.smssorter.Attributes.LogName
import com.invisibles.smssorter.Models.Sender
import com.invisibles.smssorter.R
import com.invisibles.smssorter.SmsChatActivity
import com.invisibles.smssorter.Tools.ConvertTools

private val colorList = arrayListOf(
    "#FFB64B", "#A7FFB8", "#C1C9FF"
)

class SmsListAdapter(private var data: ArrayList<Sender>, private var context: Context) :
    RecyclerView.Adapter<SmsListAdapter.ViewHolder>() {


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var senderName: TextView = itemView.findViewById(R.id.message_sender_name)
        var messageText: TextView = itemView.findViewById(R.id.message_sender_text)
        var firstLetter: TextView = itemView.findViewById(R.id.first_letter_msg_avatar)
        var circle: CardView = itemView.findViewById(R.id.card_view_msg)
        var notFound: TextView = itemView.findViewById(R.id.not_found_msg)
        var time: TextView = itemView.findViewById(R.id.message_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.message_layout, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (data[position].senderName == "NOT_FOUND"){

            holder.circle.visibility = View.GONE
            holder.senderName.visibility = View.GONE
            holder.messageText.visibility = View.GONE
            holder.notFound.visibility = View.VISIBLE
            holder.notFound.text = "Ничего не найдено!"
            holder.time.visibility = View.GONE
            holder.itemView.setOnClickListener {  }

        }
        else{
            turnOnBlock(holder)
            holder.senderName.text = data[position].senderName
            holder.firstLetter.text = data[position].senderName[0].toString().toUpperCase()
            holder.circle.setCardBackgroundColor(Color.parseColor(colorList.random()))

            val date = ConvertTools.timestampToDate("HH:mm", data[position].firstMessage.messageTime)
            holder.time.text = date


            var text = data[position].firstMessage.messageText

            if (text.length >= 77 && text[76].isDefined()) {
                text = text.substring(0, 76) + "..."
            }

            holder.messageText.text = text

            holder.itemView.setOnClickListener {

                var intent = Intent(context, SmsChatActivity::class.java)

                var options = ActivityOptions.makeSceneTransitionAnimation(
                    context as Activity,
                    Pair(holder.senderName, "ContactNameTransition")
                )

                intent.putExtra("contactName", data[position].senderName)
                //intent.putExtra("messageList", data[position].messagesList)
                intent.putExtra("contactNumber", data[position].senderNumber)

                context.startActivity(intent, options.toBundle())

            }
        }


    }

    private fun turnOnBlock(holder: ViewHolder) {
        holder.notFound.visibility = View.GONE
        holder.circle.visibility = View.VISIBLE
        holder.senderName.visibility = View.VISIBLE
        holder.messageText.visibility = View.VISIBLE
        holder.time.visibility = View.VISIBLE
    }

    override fun getItemCount(): Int {
        return data.size
    }


    fun update(list: ArrayList<Sender>) {
        this.data = list
        notifyDataSetChanged()
    }

    fun addPerson(el: Sender) {
        data.add(el)
        notifyDataSetChanged()
    }
}