package com.example.interviewportal.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class InterviewEntity(
    val title: String = "",
    val uid: String = "",
    val date: String = "",
    val numberOfParticipants: Int = 0,
    val startTime: String = "",
    val endTime: String = "",
    val startTimeInt: Int? = 0,
    val endTimeInt: Int? = 0,
    val participants: String = "",
) : Parcelable
