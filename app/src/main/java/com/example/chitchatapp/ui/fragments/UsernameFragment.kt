package com.example.chitchatapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.chitchatapp.Constants
import com.example.chitchatapp.databinding.FragmentUsernameBinding
import com.example.chitchatapp.store.UserDetails
import com.example.chitchatapp.viewModels.UserDetailsViewModel

class UsernameFragment : Fragment() {
    private lateinit var binding: FragmentUsernameBinding
    private lateinit var userDetailsViewModel: UserDetailsViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUsernameBinding.inflate(inflater, container, false)
        userDetailsViewModel = ViewModelProvider(this)[UserDetailsViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.usernameEt.requestFocus()

        binding.usernameBackBtn.setOnClickListener {
            requireActivity().finish()
        }

        binding.usernameSaveBtn.setOnClickListener {
            saveUsername()
        }

        binding.usernameProgressBar.visibility = View.GONE
        getUsername()
    }

    private fun getUsername() {
        userDetailsViewModel.userDetails.observe(viewLifecycleOwner) { userDetails ->
            if (userDetails != null) {
                binding.usernameEt.setText(userDetails.username)
            }
        }
    }

    private fun saveUsername() {
        binding.usernameProgressBar.visibility = View.VISIBLE
        binding.usernameSaveBtn.visibility = View.GONE
        val username = binding.usernameEt.text.toString()

        userDetailsViewModel.updateUsername(username) { message ->
            if (message == Constants.USERNAME_UPDATED_SUCCESSFULLY) {
                //saving username in shared preferences
                UserDetails.saveUsername(requireActivity(), username)

                requireActivity().finish()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Something went wrong",
                    Toast.LENGTH_SHORT
                ).show()
            }
            binding.usernameProgressBar.visibility = View.GONE
            binding.usernameSaveBtn.visibility = View.VISIBLE
        }
    }
}