package com.dicoding.qurbanin.ui.authentication

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.qurbanin.data.model.ListEventQurbanResponseItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfileQurbanViewModel : ViewModel() {

    private val _registeredQurban = MutableLiveData<ArrayList<ListEventQurbanResponseItem>>()
    val registeredQurban: LiveData<ArrayList<ListEventQurbanResponseItem>> = _registeredQurban

    private var registeredQurbanArray : ArrayList<ListEventQurbanResponseItem> = arrayListOf()

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
                        val data = item.getValue(ListEventQurbanResponseItem::class.java)

                        // TODO: Replace userID retrieval using Datastore
                        val userId = "aum5Pmn7NkcqGUhriXs6uR5CIrD3"
                        if (data?.UID_User == userId && data.StatusEvent == "Sedang Berlangsung") {
                            registeredQurbanArray.add(data)
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