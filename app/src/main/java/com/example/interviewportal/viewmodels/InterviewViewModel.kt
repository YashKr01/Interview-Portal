package com.example.interviewportal.viewmodels

import androidx.lifecycle.ViewModel
import com.example.interviewportal.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class InterviewViewModel @Inject constructor(private val repository: AppRepository) : ViewModel() {

    init {
        repository.getParticipantList()
    }

    val participantList get() = repository.participantList

}