package com.ravi.skinhealthyai.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

fun formatDate(timestamp: Long): String {
    val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy, HH:mm", Locale("id", "ID"))
    val date = Date(timestamp)
    return dateFormat.format(date)
}

fun displayDate(timestamp: Long): String {
    val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
    val date = Date(timestamp)
    return dateFormat.format(date)
}