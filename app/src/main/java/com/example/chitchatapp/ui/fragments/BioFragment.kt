package com.example.chitchatapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.chitchatapp.databinding.FragmentBioBinding

class BioFragment : Fragment() {
    private lateinit var binding: FragmentBioBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bioEt.requestFocus()

        binding.bioBackBtn.setOnClickListener {
            requireActivity().finish()
        }
    }
}