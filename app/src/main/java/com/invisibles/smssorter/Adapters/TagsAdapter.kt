package com.invisibles.smssorter.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.invisibles.smssorter.Models.Tags
import com.invisibles.smssorter.R

class TagsAdapter(private var data: ArrayList<Tags>, private var context: Context): RecyclerView.Adapter<TagsAdapter.ViewHolder>(){


    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var tagName: TextView = itemView.findViewById(R.id.tags_name)
        var tagColor: CardView = itemView.findViewById(R.id.tags_circle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.tags_design_layout, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.tagName.text = data[position].name
        holder.tagColor.setCardBackgroundColor(data[position].getColor())

    }

    override fun getItemCount(): Int {
        return data.size
    }

}