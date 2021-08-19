package com.invisibles.smssorter.Models

data class Date(val timestamp: Long) {

    val seconds = timestamp / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24

}