package com.example.interviewportal.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RegisterViewModel : ViewModel() {

    private val _textEmail = MutableLiveData<String>()
    val textEmail get() = _textEmail

    private val _textPassword = MutableLiveData<String>()
    val textPassword get() = _textPassword

    private val _textUserName = MutableLiveData<String>()
    val textUserName get() = _textUserName

    fun setEmailText(text: String) = _textEmail.postValue(text)

    fun setUserNameText(text: String) = _textUserName.postValue(text)

    fun setPasswordText(text: String) = _textPassword.postValue(text)

}