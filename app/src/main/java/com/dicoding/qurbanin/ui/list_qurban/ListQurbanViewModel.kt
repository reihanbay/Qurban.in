package com.dicoding.qurbanin.ui.list_qurban

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.qurbanin.data.model.EventRegisteredResponse
import com.dicoding.qurbanin.data.model.EventRegisteredResponseItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class ListQurbanViewModel : ViewModel() {

    private val _listQurban = MutableLiveData<ArrayList<EventRegisteredResponse>>()
    val listQurban: LiveData<ArrayList<EventRegisteredResponse>> = _listQurban

    private var listQurbanArray : ArrayList<EventRegisteredResponse> = arrayListOf()

    private lateinit var databaseReference: DatabaseReference

    init{
        getListQurban()
    }

    // Get data from Database -> filter by UID -> save in listQurban viewModel
    private fun getListQurban() {

        databaseReference = FirebaseDatabase.getInstance("https://qurban-in-default-rtdb.asia-southeast1.firebasedatabase.app").
        getReference("EventRegistered")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (item in snapshot.children) {
                        val data = item.getValue(EventRegisteredResponseItem::class.java)

                        // TODO: Replace userID retrieval using Datastore
                        val userId = FirebaseAuth.getInstance().currentUser?.uid
                        if (data?.uid_User == userId) {
                            listQurbanArray.add(EventRegisteredResponse(item.key.toString(), data))
                        }
                    }
                }
                _listQurban.value = listQurbanArray
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Operasi database dibatalkan: " + error.message)
            }
        })
    }
}