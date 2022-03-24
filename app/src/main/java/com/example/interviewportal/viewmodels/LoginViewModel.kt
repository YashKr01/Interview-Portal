package com.example.interviewportal.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {

    private val _textEmail = MutableLiveData<String>()
    val textEmail get() = _textEmail

    private val _textPassword = MutableLiveData<String>()
    val textPassword get() = _textPassword

    fun setEmailText(text: String) = _textEmail.postValue(text)

    fun setPasswordText(text: String) = _textPassword.postValue(text)

}