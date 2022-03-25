package com.example.interviewportal.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.interviewportal.models.InterviewEntity
import com.example.interviewportal.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InterviewViewModel @Inject constructor(private val repository: AppRepository) : ViewModel() {

    init {
        repository.getParticipantList()
    }

    val participantList get() = repository.participantList

    val interviewCreationResult get() = repository.createInterviewResult

    fun createInterview(interEntity: InterviewEntity) =
        viewModelScope.launch { repository.createInterview(interEntity) }


}