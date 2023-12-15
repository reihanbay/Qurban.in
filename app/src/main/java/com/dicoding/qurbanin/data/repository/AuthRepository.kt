package com.dicoding.qurbanin.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.dicoding.qurbanin.data.Result
import com.dicoding.qurbanin.data.model.UserResponse
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class AuthRepository private constructor(private val dbReference: DatabaseReference, private val auth: FirebaseAuth){

    fun loginUser(email: String, password: String) : Flow<Result<Boolean>> = callbackFlow{
        trySendBlocking(Result.Loading)
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                trySendBlocking(Result.Success(task.isSuccessful))
            } else {
                trySendBlocking(Result.Error(task.exception?.message?:"Login Failed"))
            }
        }.addOnFailureListener {
            trySendBlocking(Result.Error(it.message?:"Login Failed"))
        }
        awaitClose {
            auth.signInWithEmailAndPassword(email, password).addOnCanceledListener {
                trySendBlocking(Result.Error("Close Auth Firebase"))
            }
        }
    }

    fun getDataUser() : Flow<Result<UserResponse>> = callbackFlow {
       trySendBlocking(Result.Loading)
        val user = auth.currentUser
        user?.uid?.let { uid ->
            dbReference.child("Users").child(uid).get().addOnSuccessListener { snapshot->
                val data = snapshot.getValue(UserResponse::class.java)
                trySendBlocking( Result.Success(data as UserResponse))
            }.addOnFailureListener {error ->
                trySendBlocking( Result.Error(error.message?:"Failed Get Data"))
            }
            awaitClose {
                dbReference.child("Users").child(uid).get().addOnCanceledListener {
                    trySendBlocking(Result.Error("Close Auth Firebase"))
                }
            }
        }
    }
    companion object {
        @Volatile
        private var instance : AuthRepository? = null
        fun getInstance(
            dbRef: DatabaseReference,
            auth: FirebaseAuth
        ) : AuthRepository =
            instance ?: synchronized(this) {
                instance ?: AuthRepository(
                    dbRef, auth
                )
            }.also { instance = it }
    }
}