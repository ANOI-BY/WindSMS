package com.invisibles.smssorter.Tools

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import com.invisibles.smssorter.Attributes.DBHelper
import com.invisibles.smssorter.Attributes.DBTables
import com.invisibles.smssorter.Models.FolderItemDB
import com.invisibles.smssorter.Models.SmsItemDB

class DBTools {

    companion object{

        fun writeData(tableName: String, dbHelper: DBHelper, map: Map<String, Any>): Long {

            val db = dbHelper.writableDatabase
            val cv = ContentValues()

            for ((name, data) in map.entries) {
                cv.put(name, data)
            }

            val rowId = db.insert(tableName, null, cv)
            db.close()

            return rowId
        }

        fun getData(tableName: String, dbHelper: DBHelper): ArrayList<Any> {

            if (tableIsExist(dbHelper.readableDatabase, tableName)){

                val db = dbHelper.readableDatabase
                val cursor = db.query(tableName, null, null, null, null, null, null)

                val elements = arrayListOf<Any>()

                if (cursor.moveToFirst()){

                    do {

                        if (tableName == DBTables.SMS){
                            elements.add(SmsItemDB(cursor))
                        }
                        else if (tableName == DBTables.FOLDERS){
                            elements.add(FolderItemDB(cursor))
                        }

                    } while (cursor.moveToNext())

                }

                return elements
            }

            return arrayListOf()

        }

        fun tableIsExist(db: SQLiteDatabase, tableName: String): Boolean {

            try {
                val cursor = db.rawQuery("select COUNT(*) from $tableName", null)
                return cursor.use { return it.count > 0 }

            }
            catch (e: SQLiteException){
                return false
            }



        }

        fun itemIsExistById(db: SQLiteDatabase, tableName: String, id: Int): Boolean {

            try {
                val cursor = db.rawQuery("select * from $tableName where id = '$id'", null)
                return cursor.use { return it.count > 0 }

            }
            catch (e: SQLiteException){
                return false
            }
        }

        fun getItemById(db: SQLiteDatabase, tableName: String, id: Int): SmsItemDB {
            if (itemIsExistById(db, tableName, id)){
                val cursor = db.rawQuery("select * from $tableName where id = '$id'", null)
                if (cursor.moveToFirst()) return SmsItemDB(cursor)
            }
            return SmsItemDB(null)
        }

    }

}

private fun ContentValues.put(name: String, data: Any) {

    if (data is Int){
        this.put(name, data)
    }
    else if (data is String){
        this.put(name, data)
    }
    else if (data is Long){
        this.put(name, data)
    }
    else{
        this.put(name, data as String)
    }

}
