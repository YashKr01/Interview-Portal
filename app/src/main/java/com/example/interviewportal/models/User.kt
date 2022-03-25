package com.example.interviewportal.models

data class User(
    val uid: String = "",
    val username: String = "",
    val email: String = "",
    val color: Int = 0,
    var isSelected: Boolean = false
)
