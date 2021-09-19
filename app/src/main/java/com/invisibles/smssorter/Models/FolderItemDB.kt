package com.invisibles.smssorter.Models

import android.database.Cursor
import com.invisibles.smssorter.Attributes.DBColums

data class FolderItemDB(private val c: Cursor? = null) {

    var id: Int = 0
    var name: String = ""
    var icon: Int = 0

    init {
        if (c != null){
            val idColIndex = c.getColumnIndex(DBColums.FOLDER.ID)
            val nameColIndex = c.getColumnIndex(DBColums.FOLDER.NAME)
            val iconColIndex = c.getColumnIndex(DBColums.FOLDER.ICON)

            id = c.getInt(idColIndex)
            name = c.getString(nameColIndex)
            icon = c.getInt(iconColIndex)

        }
    }

}