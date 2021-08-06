package com.invisibles.smssorter.Tools

import android.content.Context
import android.net.Uri
import android.provider.ContactsContract
import android.provider.Telephony
import android.util.Log
import android.widget.Toast
import com.invisibles.smssorter.Attributes.LogName
import com.invisibles.smssorter.Models.Sender
import com.invisibles.smssorter.Models.SmsMessage

class SmsTools(private var context: Context) {

    fun getSms(): List<Sender> {
        val allSms = getAllSms()
        val smsList = mutableMapOf<String, Sender>()
        allSms.forEach {

            val number = it.address

            var numberSearch = number

            if (number[0].equals('+')) numberSearch = number.substring(2, number.length)
            else if (number[0].equals('7')) numberSearch = number.substring(1, number.length)
            else if (number[0].equals('8')) numberSearch = number.substring(1, number.length)

            val item = smsList.getOrDefault(numberSearch, Sender(number, number))
            item.addMessage(it)
            smsList[numberSearch] = item

        }
        return smsList.values.sortedBy { it.messagesList.first().messageTime }.reversed()

    }

    private fun getAllSms(): List<SmsMessage> {
        val cr = context.contentResolver
        val c = cr.query(
            Telephony.Sms.CONTENT_URI,
            arrayOf(
                Telephony.Sms.BODY,
                Telephony.Sms.ADDRESS,
                Telephony.Sms.DATE,
                Telephony.Sms._ID,
                Telephony.Sms.TYPE
            ),
            null, null, Telephony.Sms.DEFAULT_SORT_ORDER
        )

        val smsList = arrayListOf<SmsMessage>()

        if (c != null) {
            val totalSms = c.count
            if (c.moveToFirst()) {
                for (i in 0 until totalSms) {
                    val body = c.getString(0)
                    var address = c.getString(1)
                    val date = c.getString(2)
                    val id = c.getString(3)
                    val type = c.getString(4)

                    if (address.isNullOrEmpty()){
                        address = "null"
                    }

                    smsList.add(
                        SmsMessage(
                            id.toInt(),
                            body,
                            date.toLong(),
                            type.toInt(),
                            address
                        )
                    )

                    c.moveToNext()
                }
            }
        }

        return smsList.sortedBy { it.messageTime }.reversed()
    }

    fun getAllSmsWithContact(number: String): List<SmsMessage> {

        return getAllSms().filter {
            it.address == number || it.address.substring(
                2,
                it.address.length
            ) == number.substring(2, number.length) || it.address.substring(
                1,
                it.address.length
            ) == number.substring(2, number.length)
        }

    }

    fun getContactName(phoneNumber: String): String {
        val cr = context.contentResolver
        val uri = Uri.withAppendedPath(
            ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
            Uri.encode(phoneNumber)
        )
        val c = cr.query(
            uri,
            arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME),
            null, null, null
        )
        var contactName = ""

        if (c != null) {
            if (c.moveToFirst()) {
                contactName =
                    c.getString(c.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME))

            }
        }

        if (c != null && !c.isClosed()) {
            c.close()
        }

        if (contactName.isEmpty()) return phoneNumber

        return contactName
    }

}