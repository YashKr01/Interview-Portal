package com.example.interviewportal.repository

import androidx.lifecycle.MutableLiveData
import com.example.interviewportal.models.InterviewEntity
import com.example.interviewportal.models.ParticipantInterview
import com.example.interviewportal.models.User
import com.example.interviewportal.utils.Constants.INTERVIEWS_KEY
import com.example.interviewportal.utils.Constants.PARTICIPANT_INTERVIEW_KEY
import com.example.interviewportal.utils.Constants.USERS_KEY
import com.example.interviewportal.utils.Constants.VALIDATE_FIELDS
import com.example.interviewportal.utils.Constants.VALIDATE_PARTICIPANT_NUMBER
import com.example.interviewportal.utils.Constants.VALIDATE_TIMING
import com.example.interviewportal.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.delay
import javax.inject.Inject

class AppRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val database: FirebaseDatabase
) {

    private val _result = MutableLiveData<Resource<User>>()
    val result get() = _result

    private val _participantList = MutableLiveData<Resource<List<User>>>()
    val participantList get() = _participantList

    private val _createInterviewResult = MutableLiveData<Resource<InterviewEntity>>()
    val createInterviewResult get() = _createInterviewResult

    private val _interviewList = MutableLiveData<Resource<List<InterviewEntity>>>()
    val interviewList get() = _interviewList

    private val _validationResult = MutableLiveData<Boolean>()

    fun registerUser(email: String, password: String, username: String, color: Int) {
        _result.postValue(Resource.Loading())
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {

                val userId = auth.currentUser?.uid
                userId?.let { uid ->
                    val user = User(
                        uid = userId,
                        username = username,
                        email = email,
                        color = color
                    )

                    database.getReference(USERS_KEY).child(uid).setValue(user)
                        .addOnSuccessListener { result.postValue(Resource.Success(user)) }
                        .addOnFailureListener { error ->
                            result.postValue(Resource.Error(error.message.toString(), null))
                        }
                }

            }
            .addOnFailureListener { error ->
                result.postValue(Resource.Error(error.message.toString(), null))
            }
    }

    fun getParticipantList() {
        _participantList.postValue(Resource.Loading())
        database.getReference(USERS_KEY).addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                val updatedParticipantList = mutableListOf<User>()

                for (data in snapshot.children) {
                    data.getValue(User::class.java)?.let { updatedParticipantList.add(it) }
                }

                _participantList.postValue(Resource.Success(updatedParticipantList))
            }

            override fun onCancelled(error: DatabaseError) =
                _participantList.postValue(Resource.Error("ERROR ${error.message}"))

        })
    }

    suspend fun createInterview(interview: InterviewEntity) {
        _createInterviewResult.postValue(Resource.Loading())

        if (interview.title.isEmpty() || interview.startDate.isEmpty() ||
            interview.startTimeInt == null || interview.endTimeInt == null
        ) _createInterviewResult.postValue(Resource.Error(message = VALIDATE_FIELDS))
        else if (interview.endDateLong!! < interview.startDateLong!!) _createInterviewResult.postValue(
            Resource.Error("EndTime Cannot be less than Start time")
        )
        else if (interview.numberOfParticipants < 2)
            _createInterviewResult.postValue(Resource.Error(message = VALIDATE_PARTICIPANT_NUMBER))
        else if (checkValidInterview(interview)) {
            database.reference.child(INTERVIEWS_KEY).child(interview.uid).setValue(interview)
                .addOnSuccessListener {

                    if (_validationResult.value == true) {
                        _createInterviewResult.postValue(Resource.Success(interview))
                        for (participantId in interview.participants.split(Regex(", "))) {
                            database.reference.child(PARTICIPANT_INTERVIEW_KEY).child(participantId)
                                .child(interview.uid).setValue(
                                    ParticipantInterview(
                                        interviewId = interview.uid,
                                        date = interview.startDate,
                                        startTimeInt = interview.startTimeInt,
                                        endTimeInt = interview.endTimeInt
                                    )
                                )
                        }
                    } else {
                        _createInterviewResult.postValue(Resource.Error(VALIDATE_TIMING))
                    }

                }
                .addOnFailureListener {
                    _createInterviewResult.postValue(Resource.Error(it.message.toString(), null))
                }
        }

    }

    private suspend fun checkValidInterview(interview: InterviewEntity): Boolean {

        _validationResult.postValue(true)
        delay(3000)
        val userIdList = interview.participants.split(Regex(", "))

        for (userId in userIdList) {

            database.reference.child(PARTICIPANT_INTERVIEW_KEY).child(userId)
                .addValueEventListener(object : ValueEventListener {

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (snapshot in dataSnapshot.children) {
                            val currentInterview =
                                snapshot.getValue(ParticipantInterview::class.java)!!

                            val startTimeClash =
                                (currentInterview.startTimeInt >= interview.startTimeInt!! &&
                                        currentInterview.startTimeInt < interview.endTimeInt!!)

                            val endTimeClash =
                                (currentInterview.endTimeInt <= interview.endTimeInt!! &&
                                        currentInterview.endTimeInt > interview.startTimeInt)

                            if (currentInterview.date == interview.startDate &&
                                currentInterview.interviewId != interview.uid &&
                                (startTimeClash || endTimeClash)
                            ) {
                                _validationResult.postValue(false)
                                break
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        _createInterviewResult.postValue(Resource.Error(error.message, null))
                        _validationResult.postValue(false)
                    }
                })

        }

        return true
    }

    fun getInterviewList() {
        _interviewList.postValue(Resource.Loading())
        database.reference.child(INTERVIEWS_KEY).addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val list = mutableListOf<InterviewEntity>()
                for (snapshot in dataSnapshot.children) {
                    snapshot.getValue(InterviewEntity::class.java)?.let { list.add(it) }
                }
                _interviewList.postValue(Resource.Success(list))
            }

            override fun onCancelled(error: DatabaseError) =
                _interviewList.postValue(Resource.Error("ERROR ${error.message}"))

        })
    }

}