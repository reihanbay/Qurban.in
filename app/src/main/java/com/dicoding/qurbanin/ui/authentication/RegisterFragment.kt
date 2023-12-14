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
    private val TAG = RegisterFragment::class.java.simpleName

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
            Log.i(TAG, "Nilai type: $type")
        }

        binding?.btnRegister?.setOnClickListener { buttonView ->
            val name = binding?.etName?.text.toString().trim()
            val email = binding?.etEmail?.text.toString().trim()
            val password = binding?.etPassword?.text.toString()
            val address = binding?.etAddress?.text.toString().trim()

            when {
                name.isEmpty() -> binding?.textInputName?.error = getString(R.string.field_required)
                !isValidName(name) -> {
                    binding?.textInputName?.error = getString(R.string.name_is_not_valid)
                }

                email.isEmpty() -> {
                    binding?.textInputEmail?.error = getString(R.string.field_required)
                    binding?.textInputName?.error = null
                }
                !isValidEmail(email) -> {
                    binding?.textInputEmail?.error = getString(R.string.email_is_not_valid)
                    binding?.textInputName?.error = null
                }
                password.isEmpty() -> {
                    binding?.textInputPassword?.error = getString(R.string.field_required)
                    binding?.textInputEmail?.error = null
                }
                !isValidPassword(password) -> {
                    binding?.textInputPassword?.error =
                        getString(R.string.password_length_is_not_valid)
                }
                address.isEmpty() -> {
                    binding?.textInputAddress?.error = getString(R.string.field_required)
                    binding?.textInputPassword?.error = null
                }
                type.isEmpty() -> {
                    binding?.textInputType?.error = getString(R.string.field_required)
                    binding?.textInputAddress?.error = null
                }

                else -> {
                    binding?.textInputType?.error = null

                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(requireActivity()) { task ->
                            if (task.isSuccessful) {
                                val user = firebaseAuth.currentUser
                                saveToDatabase(user?.uid, name, address, type)

                                showToast("Register berhasil!")

                                buttonView.findNavController()
                                    .navigate(R.id.action_registerFragment_to_loginFragment)

                                Log.i(TAG, "createUserWithEmail: success")
                            } else {
                                val message = checkString(task.exception!!)

                                showToast(message)

                                Log.e(TAG, "createUserWithEmail: ${task.exception}")
                            }
                        }
                }
            }
        }

        binding?.textLogin?.setOnClickListener {
            it.findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }

    private fun checkString(exception: Exception): String {
        return if (exception.message!!.contains("already in use")) {
            getString(R.string.email_already_used)
        } else {
            getString(R.string.register_failed)
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

    private fun showToast(msg: String) {
        Toast.makeText(requireActivity(), msg, Toast.LENGTH_SHORT).show()
    }

    override fun onDetach() {
        super.onDetach()
        _binding = null
    }

    companion object {
        private const val USERS_PATH = "Users"
        private const val DATABASE_URL = BuildConfig.DATABASE_URL
    }
}