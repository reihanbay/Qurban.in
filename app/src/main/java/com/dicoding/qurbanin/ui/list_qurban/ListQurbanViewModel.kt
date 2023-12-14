package com.dicoding.qurbanin.ui.list_qurban

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


class ListQurbanViewModel : ViewModel() {

    private val _listQurban = MutableLiveData<ArrayList<ListEventQurbanResponseItem>>()
    val listQurban: LiveData<ArrayList<ListEventQurbanResponseItem>> = _listQurban

    private var listQurbanArray : ArrayList<ListEventQurbanResponseItem> = arrayListOf()

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
                        val data = item.getValue(ListEventQurbanResponseItem::class.java)

                        // TODO: Replace userID retrieval using Datastore
                        val userId = "aum5Pmn7NkcqGUhriXs6uR5CIrD3"
                        if (data?.UID_User == userId) {
                            listQurbanArray.add(data)
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