package com.invisibles.smssorter.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.invisibles.smssorter.Models.FolderItemDB
import com.invisibles.smssorter.R

class FoldersListAdapter(private var data: ArrayList<FolderItemDB>, private val context: Context):
    RecyclerView.Adapter<FoldersListAdapter.ViewHolder>(){

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var folderName: TextView = itemView.findViewById(R.id.folder_name)
        var folderIcon: ImageView = itemView.findViewById(R.id.folder_icon)
        var folderChats: RecyclerView = itemView.findViewById(R.id.folder_first_avatars)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.folder_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val el = data[position]

        holder.folderName.text = el.name
        holder.folderIcon.setImageDrawable(context.getDrawable(el.icon))
    }

    override fun getItemCount(): Int {
        return data.size
    }
}