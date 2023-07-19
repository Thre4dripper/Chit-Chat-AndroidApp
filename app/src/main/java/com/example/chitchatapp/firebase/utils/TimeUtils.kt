package com.example.chitchatapp.firebase.utils

import android.text.format.DateUtils
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class TimeUtils {
    companion object {
        fun getFormattedTime(timestamp: Timestamp): String {
            val date = timestamp.toDate()

            return if (DateUtils.isToday(date.time)) {
                val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
                dateFormat.format(date)
            } else {
                if (isYesterday(date)) {
                    "Yesterday"
                } else if (isCurrentWeek(date)) {
                    val dateFormat = SimpleDateFormat("EEEE", Locale.getDefault())
                    dateFormat.format(date)
                } else {
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    dateFormat.format(date)
                }
            }
        }

        private fun isYesterday(date: Date): Boolean {
            val currentCalendar = Calendar.getInstance()
            val yesterday = currentCalendar.get(Calendar.DATE) - 1
            val year = currentCalendar.get(Calendar.YEAR)

            val targetCalendar = Calendar.getInstance()
            targetCalendar.time = date
            val targetDate = targetCalendar.get(Calendar.DATE)
            val targetYear = targetCalendar.get(Calendar.YEAR)
            return yesterday == targetDate && year == targetYear
        }

        private fun isCurrentWeek(date: Date): Boolean {
            val currentCalendar = Calendar.getInstance()
            val week = currentCalendar.get(Calendar.WEEK_OF_YEAR)
            val year = currentCalendar.get(Calendar.YEAR)
            val targetCalendar = Calendar.getInstance()
            targetCalendar.time = date
            val targetWeek = targetCalendar.get(Calendar.WEEK_OF_YEAR)
            val targetYear = targetCalendar.get(Calendar.YEAR)
            return week == targetWeek && year == targetYear
        }
    }
}