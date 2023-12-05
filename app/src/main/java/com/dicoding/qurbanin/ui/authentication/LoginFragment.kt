package com.dicoding.qurbanin.ui.authentication

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.dicoding.qurbanin.R
import com.dicoding.qurbanin.core.utils.datastore.SettingPreferences
import com.dicoding.qurbanin.core.utils.datastore.datastore
import com.dicoding.qurbanin.data.UserData
import com.dicoding.qurbanin.databinding.FragmentLoginBinding
import com.dicoding.qurbanin.ui.home.HomeFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.core.Context
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentLoginBinding.inflate(inflater,container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference.child("Users")

        binding.materialButton.setOnClickListener {
            val loginEmail = binding.edtEmail.text.toString()
            val loginPassword = binding.edtPassword.text.toString()

            if (loginEmail.isNotEmpty() && loginPassword.isNotEmpty()) {
                loginUser(loginEmail,loginPassword)
            } else {
                Toast.makeText(requireContext(),"Login Failed",Toast.LENGTH_SHORT).show()

            }
        }

        binding.tvCreateAccount.setOnClickListener {
            it.findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    private fun loginUser(email:String, password:String) {
        databaseReference.orderByChild(email).equalTo(email).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (user in snapshot.children) {
                        val userData = user.getValue(UserData::class.java)

                        if (userData != null && userData.password == password ){
                            Toast.makeText(requireContext(), "Login Successful", Toast.LENGTH_SHORT).show()

                            GlobalScope.launch {
                                SettingPreferences.getInstance(requireContext().datastore).setLoginSession(true)
                            }
                            findNavController().navigate(R.id.action_loginFragment_to_homeContainerFragment)
                            requireActivity().finish()
                            return
                        }
                    }
                }
                Toast.makeText(requireContext(), "Login Failed", Toast.LENGTH_SHORT).show()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(),"Database Error : ${error.message}", Toast.LENGTH_SHORT).show()
            }

        })
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}