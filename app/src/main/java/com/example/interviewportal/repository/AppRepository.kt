package com.example.interviewportal.repository

import androidx.lifecycle.MutableLiveData
import com.example.interviewportal.models.User
import com.example.interviewportal.utils.Constants.USERNAME_KEY
import com.example.interviewportal.utils.Constants.USERS_KEY
import com.example.interviewportal.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import javax.inject.Inject

class AppRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val database: FirebaseDatabase
) {

    private val _result = MutableLiveData<Resource<User>>()
    val result get() = _result


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


}