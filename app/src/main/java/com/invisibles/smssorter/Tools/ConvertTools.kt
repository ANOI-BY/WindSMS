package com.invisibles.smssorter.Tools

import android.content.Context
import java.text.SimpleDateFormat
import java.util.*

class ConvertTools {

    companion object{
        fun pixelsToDp(context: Context, px: Float): Int {
            val res = context.resources
            val metrics = res.displayMetrics
            val dp = px / (metrics.densityDpi/160f)
            return dp.toInt()
        }

        fun timestampToDate(format: String, timestamp: Long): String {
            val calendar = Calendar.getInstance()
            val date = SimpleDateFormat(format)
            val tz = calendar.timeZone
            date.timeZone = tz

            val strDate = date.format(Date(timestamp))

            return strDate
        }

    }
}