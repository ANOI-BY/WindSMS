package com.invisibles.smssorter.Tools

import android.content.Context

class ConvertTools {

    companion object{
        fun pixelsToDp(context: Context, px: Float): Int {
            val res = context.resources
            val metrics = res.displayMetrics
            val dp = px / (metrics.densityDpi/160f)
            return dp.toInt()
        }
    }
}