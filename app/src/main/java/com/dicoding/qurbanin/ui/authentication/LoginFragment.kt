package com.dicoding.qurbanin.ui.authentication

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dicoding.qurbanin.R
import com.dicoding.qurbanin.core.utils.utility.DialogUtils
import com.dicoding.qurbanin.data.Result
import com.dicoding.qurbanin.databinding.FragmentLoginBinding
import com.dicoding.qurbanin.ui.ViewModelFactory

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val factory by lazy { ViewModelFactory.getInstance(requireContext().applicationContext) }
    private val viewModel: AuthenticationViewModel by viewModels {
        factory
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater,container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAction()
        initObservable()

    }

    private fun validateInput(email: String, password: String): Boolean {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter email and password", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
    //
    private fun initAction() {
        binding.materialButton.setOnClickListener {
            val email = binding.edtEmail.text.toString()
            val password = binding.edtPassword.text.toString()

            if (validateInput(email, password)) {
                viewModel.loginUser(email,password)
            }
        }

        binding.tvCreateAccount.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    private fun initObservable() {
        viewModel.isLoginDone.observe(viewLifecycleOwner) {
            when(it) {
                is Result.Success -> {
                    viewModel.getDataUser()
                }
                is Result.Loading -> {
                }

                is Result.Error -> {
                    Toast.makeText(requireContext(), it.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
        viewModel.getDataUser.observe(viewLifecycleOwner) { dataSnap ->
            when (dataSnap) {
                is Result.Success -> {
                    viewModel.setDataUserLocal(dataSnap.data)
                    findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToHomeContainerFragment())
                }

                is Result.Loading -> {
                }

                is Result.Error -> {
                    Toast.makeText(requireContext(), dataSnap.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        //to check user login session
        viewModel.isLogin().observe(viewLifecycleOwner) {
            if (it) {
                findNavController().navigate(R.id.action_loginFragment_to_homeContainerFragment)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}