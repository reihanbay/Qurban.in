package com.dicoding.qurbanin.ui.authentication

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.qurbanin.data.model.EventRegisteredResponse
import com.dicoding.qurbanin.data.model.EventRegisteredResponseItem
import com.dicoding.qurbanin.data.repository.AuthRepository
import com.dicoding.qurbanin.data.repository.QurbanRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfileQurbanViewModel() : ViewModel() {

    private val _registeredQurban = MutableLiveData<ArrayList<EventRegisteredResponse>>()
    val registeredQurban: LiveData<ArrayList<EventRegisteredResponse>> = _registeredQurban

    private var registeredQurbanArray : ArrayList<EventRegisteredResponse> = arrayListOf()

    private lateinit var databaseReference: DatabaseReference

    init{
        getRegisteredQurban()
    }

    private fun getRegisteredQurban() {
        databaseReference = FirebaseDatabase.getInstance("https://qurban-in-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("EventRegistered")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (item in snapshot.children) {
                        val data = item.getValue(EventRegisteredResponseItem::class.java)

                        // TODO: Replace userID retrieval using Datastore
                        val userId = FirebaseAuth.getInstance().currentUser?.uid
                        if (data?.uid_User == userId && data?.statusStock == "ongoing") {
                            registeredQurbanArray.add(EventRegisteredResponse(item.key.toString(), data))
                        }
                    }
                }
                _registeredQurban.value = registeredQurbanArray
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Operasi database dibatalkan: " + error.message);
            }
        })
    }
}