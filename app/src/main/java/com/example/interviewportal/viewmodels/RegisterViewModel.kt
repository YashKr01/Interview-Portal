package com.example.interviewportal.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.interviewportal.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(private val repository: AppRepository) : ViewModel() {

    val result get() = repository.result

    fun registerUser(email: String, password: String, username: String, color: Int) =
        viewModelScope.launch {
            repository.registerUser(
                email = email,
                password = password,
                username = username,
                color = color
            )
        }

}