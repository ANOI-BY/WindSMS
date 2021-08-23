package com.invisibles.smssorter.Attributes

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context): SQLiteOpenHelper(context, "WindSMS", null, 1) {

    private var db: SQLiteDatabase = writableDatabase

    override fun onCreate(db: SQLiteDatabase) {
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

    fun createTable(tableName: String, columns: Map<String, String>){

        var sql = "create table $tableName ( "
        var i = 0

        for ((name, data) in columns.entries){
            sql += "$name $data"
            i += 1

            if (i != columns.size){
                sql += ','
            }
        }

        sql += " );"

        db.execSQL(sql)

    }
}