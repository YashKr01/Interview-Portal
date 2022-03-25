package com.example.interviewportal.models

data class InterviewEntity(
    val uid: String = "",
    val date: String = "",
    val numberOfParticipants: Int = 0,
    val startTime: String = "",
    val endTime: String = "",
    val startTimeInt: Int? = 0,
    val endTimeInt: Int? = 0,
    val participants: String = "",
)
