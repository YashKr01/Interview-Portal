package com.example.interviewportal.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.interviewportal.databinding.FragmentRegisterBinding
import com.example.interviewportal.utils.Resource
import com.example.interviewportal.viewmodels.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.random.Random

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<RegisterViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.txtSignIn.setOnClickListener {
            findNavController().popBackStack()
        }

        viewModel.result.observe(viewLifecycleOwner){ res ->
            when(res) {
                is Resource.Loading -> Log.d("AUTH STATUS", "onViewCreated: LOADING")
                is Resource.Error -> Log.d("AUTH STATUS", "onViewCreated: ERROR")
                is Resource.Success -> Log.d("AUTH STATUS", "onViewCreated: SUCCESS")
            }
        }

        binding.btnSignUp.setOnClickListener {
            viewModel.registerUser(
                email = binding.signUpUserEmail.text.toString(),
                password = binding.signUpPassword.text.toString(),
                username = binding.sighUpUserName.text.toString(),
                color = Random(0).nextInt(0, 4)
            )
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}