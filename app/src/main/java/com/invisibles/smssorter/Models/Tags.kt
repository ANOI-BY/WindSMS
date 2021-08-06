package com.invisibles.smssorter.Models

import android.graphics.Color

data class Tags(var color: String, var name: String) {

    fun getColor(): Int {
        return Color.parseColor(color)
    }
}