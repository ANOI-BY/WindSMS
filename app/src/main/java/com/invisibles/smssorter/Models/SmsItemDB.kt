package com.invisibles.smssorter.Models

import android.database.Cursor
import com.invisibles.smssorter.Attributes.DBColums

data class SmsItemDB(private val c: Cursor?) {

    var id: Int = 0
    var name: String = ""
    var number: String = ""
    var text: String = ""
    var time: Long = 0
    var type: Int = 0

    init {
        if (c != null){
            val idColIndex = c.getColumnIndex(DBColums.SMS.ID)
            val nameColIndex = c.getColumnIndex(DBColums.SMS.NAME)
            val numberColIndex = c.getColumnIndex(DBColums.SMS.NUMBER)
            val textColIndex = c.getColumnIndex(DBColums.SMS.TEXT)
            val timeColIndex = c.getColumnIndex(DBColums.SMS.TIME)
            val typeColIndex = c.getColumnIndex(DBColums.SMS.TYPE)

            id = c.getInt(idColIndex)
            name = c.getString(nameColIndex)
            number = c.getString(numberColIndex)
            text = c.getString(textColIndex)
            time = c.getLong(timeColIndex)
            type = c.getInt(typeColIndex)
        }

    }

}