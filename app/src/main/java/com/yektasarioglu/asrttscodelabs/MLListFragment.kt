package com.yektasarioglu.asrttscodelabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

import com.yektasarioglu.asrttscodelabs.databinding.FragmentMlListBinding

class MLListFragment : Fragment() {

    private var binding: FragmentMlListBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) : View? {
        binding = FragmentMlListBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.asrButton?.setOnClickListener { navigateToASRFragment() }
        binding?.ttsButton?.setOnClickListener { navigateToTTSFragment() }
    }

    private fun navigateToASRFragment() = findNavController().navigate(MLListFragmentDirections.actionMLListFragmentToASRFragment())

    private fun navigateToTTSFragment() = findNavController().navigate(MLListFragmentDirections.actionMLListFragmentToTTSFragment())

}