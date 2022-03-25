package com.example.interviewportal.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.interviewportal.databinding.FragmentHomeBinding
import com.example.interviewportal.utils.Resource
import com.example.interviewportal.viewmodels.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<HomeViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.floatingActionButton.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToInterviewFragment())
        }

        viewModel.interviewList.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Loading -> Log.d("sjhbachjcbdk", "onViewCreated: LOADING")
                is Resource.Error -> Log.d("sjhbachjcbdk", "onViewCreated: ERROR")
                is Resource.Success -> Log.d("sjhbachjcbdk", "onViewCreated: SUCCESS ${result.data}")
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}