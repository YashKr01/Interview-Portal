package com.example.interviewportal.repository

import androidx.lifecycle.MutableLiveData
import com.example.interviewportal.models.InterviewEntity
import com.example.interviewportal.models.User
import com.example.interviewportal.utils.Constants.INTERVIEWS_KEY
import com.example.interviewportal.utils.Constants.PARTICIPANT_INTERVIEW_KEY
import com.example.interviewportal.utils.Constants.USERS_KEY
import com.example.interviewportal.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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
                        .addOnSuccessListener {
                            result.postValue(Resource.Success(user))
                        }
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

    fun createInterview(interview: InterviewEntity) {
        _createInterviewResult.postValue(Resource.Loading())

        if (interview.title.isEmpty() || interview.date.isEmpty() ||
            interview.startTimeInt == null || interview.endTimeInt == null
        )
            _createInterviewResult.postValue(Resource.Error(message = "Please validate all details"))
        else if (interview.numberOfParticipants < 2)
            _createInterviewResult.postValue(Resource.Error(message = "Number of participants must me more than 1"))
        else {
            database.reference.child(INTERVIEWS_KEY).child(interview.uid).setValue(interview)
                .addOnSuccessListener {
                    _createInterviewResult.postValue(Resource.Success(interview))
//                    database.getReference(PARTICIPANT_INTERVIEW_KEY).child()
                }
                .addOnFailureListener {
                    _createInterviewResult.postValue(Resource.Error(it.message.toString(), null))
                }
        }

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