package com.dicoding.qurbanin.ui.authentication

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.dicoding.qurbanin.BuildConfig
import com.dicoding.qurbanin.R
import com.dicoding.qurbanin.databinding.FragmentRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseRef: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)

        val typeArray = resources.getStringArray(R.array.user_type)
        val typeAdapter = ArrayAdapter(
            requireActivity(),
            R.layout.dropdown_item,
            typeArray
        )
        binding?.autoTextType?.setAdapter(typeAdapter)

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = Firebase.auth
        databaseRef = FirebaseDatabase.getInstance(DATABASE_URL).reference

        binding?.ivLoginPage?.setOnClickListener {
            findNavController().popBackStack()
        }

        var type = ""
        binding?.autoTextType?.setOnItemClickListener { adapterView, _, position, _ ->
            type = adapterView.getItemAtPosition(position).toString()
            Log.i(REGISTER_TAG, "Nilai type: $type")
        }

        binding?.btnRegister?.setOnClickListener { buttonView ->
            val name = binding?.etName?.text.toString().trim()
            val email = binding?.etEmail?.text.toString().trim()
            val password = binding?.etPassword?.text.toString()
            val address = binding?.etAddress?.text.toString().trim()

            when {
                name.isEmpty() -> binding?.etName?.error = getString(R.string.field_required)
                !isValidName(name) -> binding?.etName?.error = getString(R.string.name_is_not_valid)

                email.isEmpty() -> binding?.etEmail?.error = getString(R.string.field_required)
                !isValidEmail(email) ->
                    binding?.etEmail?.error = getString(R.string.email_is_not_valid)

                password.isEmpty() -> binding?.etPassword?.error =
                    getString(R.string.field_required)
                !isValidPassword(password) ->
                    binding?.etPassword?.error = getString(R.string.password_length_is_not_valid)

                address.isEmpty() -> binding?.etAddress?.error = getString(R.string.field_required)
                type.isEmpty() -> binding?.autoTextType?.error = getString(R.string.field_required)

                else -> {
                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(requireActivity()) { task ->
                            if (task.isSuccessful) {
                                Log.i(REGISTER_TAG, "createUserWithEmail: success")
                                val user = firebaseAuth.currentUser
                                saveToDatabase(user?.uid, name, address, type)
                                buttonView.findNavController()
                                    .navigate(R.id.action_registerFragment_to_loginFragment)
                            } else {
                                Log.w(REGISTER_TAG, "createUserWithEmail: failed")
                                Toast.makeText(
                                    requireActivity(),
                                    "Register: failed",
                                    Toast.LENGTH_SHORT,
                                ).show()
                            }
                        }
                }
            }
        }

        binding?.textLogin?.setOnClickListener {
            it.findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }

    private fun saveToDatabase(uid: String?, name: String, address: String, type: String) {
        uid?.let {
            val userInfo = mapOf(
                "Alamat" to address,
                "Nama" to name,
                "Tipe" to type,
            )
            val childUpdates = hashMapOf<String, Any>(
                it to userInfo
            )
            databaseRef.child(USERS_PATH).updateChildren(childUpdates)
        }
    }

    private fun isValidName(name: String) = name.all { it.isLetter() || it.isWhitespace() }

    private fun isValidPassword(password: String) = password.length >= 8

    private fun isValidEmail(email: String) = Patterns.EMAIL_ADDRESS.matcher(email).matches()

    override fun onDetach() {
        super.onDetach()
        _binding = null
    }

    companion object {
        private const val REGISTER_TAG = "REGISTER_TAG"
        private const val USERS_PATH = "Users"
        private const val DATABASE_URL = BuildConfig.DATABASE_URL
    }
}