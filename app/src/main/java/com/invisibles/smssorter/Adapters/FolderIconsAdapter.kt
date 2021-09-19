package com.invisibles.smssorter.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.invisibles.smssorter.AddNewFolder
import com.invisibles.smssorter.R

class FolderIconsAdapter(private var data: ArrayList<Int>, private var context: AddNewFolder, private var folderIcon: ImageView): RecyclerView.Adapter<FolderIconsAdapter.ViewHolder>(){

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var icon: ImageButton = itemView.findViewById(R.id.folder_icon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.folder_icon_item, parent, false)
        return ViewHolder(itemView)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.icon.setImageDrawable(context.getDrawable(data[position]))
        holder.icon.setOnClickListener {

            folderIcon.setImageDrawable(context.getDrawable(data[position]))
            context.closeIconsMenu()

        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
}