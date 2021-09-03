package com.invisibles.smssorter.Receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent


class SmsReceiver: BroadcastReceiver() {
    override fun onReceive(p0: Context, p1: Intent) {

        val bundle = p1.extras
        var msgs = null
        var str = ""

        if (bundle != null){
            val pdus = bundle.get("pdus")

        }


    }
}