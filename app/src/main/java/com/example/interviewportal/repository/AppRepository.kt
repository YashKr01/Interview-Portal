package com.example.interviewportal.repository

import androidx.lifecycle.MutableLiveData
import com.example.interviewportal.models.User
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

    fun registerUser(email: String, password: String, username: String, color: Int) {
        _result.postValue(Resource.Loading())
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {

                val userId = auth.currentUser?.uid
                userId?.let { it1 ->
                    val user = User(
                        uid = userId,
                        username = username,
                        email = email,
                        color = color
                    )

                    database.getReference(USERS_KEY).child(it1).setValue(user)
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

}