package com.example.interviewportal.utils

import com.google.android.material.timepicker.MaterialTimePicker

object Constants {

    const val USERS_KEY = "users"
    const val INTERVIEWS_KEY = "interviews"
    const val PARTICIPANT_INTERVIEW_KEY = "participant-interviews"

    const val COLOR_0 = 0
    const val COLOR_1 = 1
    const val COLOR_2 = 2
    const val COLOR_3 = 3

    fun getFormattedTime(pickedHour: Int, pickedMinute: Int, picker: MaterialTimePicker): String {
        var result: String
        when {
            pickedHour > 12 -> {
                result = if (pickedMinute < 10) "${picker.hour - 12}:0${picker.minute} PM"
                else "${picker.hour - 12}:${picker.minute} PM"
            }
            pickedHour == 12 -> {
                result = if (pickedMinute < 10) "${picker.hour}:${picker.minute} PM"
                else "${picker.hour}:${picker.minute} PM"
            }
            pickedHour == 0 -> {
                result = if (pickedMinute < 10) "${picker.hour + 12}:${picker.minute} AM"
                else "${picker.hour + 12}:${picker.minute} AM"
            }
            else ->
                result = if (pickedMinute < 10) "${picker.hour}:${picker.minute} AM"
                else "${picker.hour}:${picker.minute} AM"
        }

        if (result.length < 8) result = "0${result}"

        return result
    }

    fun getFormattedTime(time: String): Int {
        val hour = "${time[0]}${time[1]}"
        return if(time.contains("PM")) (hour.toInt() % 12) + 12
        else hour.toInt() % 12
    }

}