package com.example.interviewportal.utils

import android.content.Context
import android.view.View
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat

object Constants {

    const val USERS_KEY = "users"
    const val INTERVIEWS_KEY = "interviews"
    const val PARTICIPANT_INTERVIEW_KEY = "participant-interviews"

    const val COLOR_0 = 0
    const val COLOR_1 = 1
    const val COLOR_2 = 2
    const val COLOR_3 = 3

    const val VALIDATE_FIELDS = "Please validate all details"
    const val VALIDATE_PARTICIPANT_NUMBER = "Number of participants must me more than 1"
    const val VALIDATE_TIMING= "One or more participants are not available for the schedule time"

    private const val HOUR = 12
    private const val MINUTE = 10

    const val FILE_TYPE = "application/pdf"
    const val FILE_LOCATION = "uploads/"

    fun getFormattedTime(pickedHour: Int, pickedMinute: Int, picker: MaterialTimePicker): String {
        var result: String
        when {
            pickedHour > 12 -> {
                result = if (pickedMinute < 10) "${picker.hour - 12}:0${picker.minute} PM"
                else "${picker.hour - 12}:${picker.minute} PM"
            }
            pickedHour == 12 -> {
                result = if (pickedMinute < 10) "${picker.hour}:0${picker.minute} PM"
                else "${picker.hour}:${picker.minute} PM"
            }
            pickedHour == 0 -> {
                result = if (pickedMinute < 10) "${picker.hour + 12}:0${picker.minute} AM"
                else "${picker.hour + 12}:${picker.minute} AM"
            }
            else ->
                result = if (pickedMinute < 10) "${picker.hour}:0${picker.minute} AM"
                else "${picker.hour}:${picker.minute} AM"
        }

        if (result.length < 8) result = "0${result}"

        return result
    }

    fun getFormattedTime(time: String): Int {
        val hour = "${time[0]}${time[1]}"
        return if (time.contains("PM")) (hour.toInt() % 12) + 12
        else hour.toInt() % 12
    }

    fun showSnackBar(context: Context, view: View, text: CharSequence) {
        Snackbar.make(
            context,
            view,
            text,
            Snackbar.LENGTH_SHORT
        ).show()
    }

    fun buildMaterialTimePicker() = MaterialTimePicker.Builder()
        .setTimeFormat(TimeFormat.CLOCK_12H)
        .setHour(HOUR)
        .setMinute(MINUTE)
        .build()

    fun buildMaterialDatePicker() = MaterialDatePicker.Builder.datePicker()
        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
        .setCalendarConstraints(
            CalendarConstraints.Builder()
                .setValidator(DateValidatorPointForward.now()).build()
        )
        .build()

}