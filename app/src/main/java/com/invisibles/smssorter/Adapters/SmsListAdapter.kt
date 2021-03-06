package com.invisibles.smssorter.Adapters

import android.annotation.SuppressLint
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
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.text.isDigitsOnly
import androidx.recyclerview.widget.RecyclerView
import com.invisibles.smssorter.Attributes.LogName
import com.invisibles.smssorter.Models.Sender
import com.invisibles.smssorter.R
import com.invisibles.smssorter.SmsChatActivity
import com.invisibles.smssorter.Tools.ConvertTools


class SmsListAdapter(private var data: ArrayList<Sender>, private var context: Context) :
    RecyclerView.Adapter<SmsListAdapter.ViewHolder>() {


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var senderName: TextView = itemView.findViewById(R.id.contact_name)
        var messageText: TextView = itemView.findViewById(R.id.short_message)
        var firstLetter: TextView = itemView.findViewById(R.id.first_letter)
        var avatar: ImageView = itemView.findViewById(R.id.contact_avatar)
        var time: TextView = itemView.findViewById(R.id.time_block)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.message_item_main_screen, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.senderName.text = data[position].senderName

        var letter = data[position].senderName[0].toString().toUpperCase()

        if (letter == "+" || letter.isDigitsOnly()) {

            holder.firstLetter.visibility = View.GONE
            holder.avatar.visibility = View.VISIBLE

        }
        else{
            holder.firstLetter.visibility = View.VISIBLE
            holder.avatar.visibility = View.GONE
        }

        holder.firstLetter.text = letter

        val date = ConvertTools.timestampToDate("HH:mm", data[position].firstMessage.time)
        holder.time.text = date


        var text = data[position].firstMessage.text

        if (text.length >= 31 && text[30].isDefined()) {

            val regex = "\n".toRegex()

            if (regex.find(text) != null) {
                text = text.split("\n".toRegex())[0] + "..."
            } else {
                text = text.substring(0, 30) + "..."
            }
        }

        holder.messageText.text = text

        holder.itemView.setOnClickListener {

            val intent = Intent(context, SmsChatActivity::class.java)

            val options = ActivityOptions.makeSceneTransitionAnimation(
                context as Activity,
                Pair(holder.senderName, "ContactNameTransition")
            )

            intent.putExtra("contactName", data[position].senderName)
            intent.putExtra("contactNumber", data[position].senderNumber)

            context.startActivity(intent, options.toBundle())

        }

        holder.itemView.setOnLongClickListener {
            Log.i(LogName.DebugLog, "YES")
            return@setOnLongClickListener true }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun update(list: ArrayList<Sender>) {
        this.data = list
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addPerson(el: Sender) {
        data.add(el)
        notifyDataSetChanged()
    }
}