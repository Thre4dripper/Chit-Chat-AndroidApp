package com.example.chitchatapp.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.chitchatapp.databinding.FragmentUsernameBinding
import com.example.chitchatapp.firebase.firestore.FirestoreUtils
import com.example.chitchatapp.store.UserDetails
import com.google.firebase.auth.FirebaseAuth

class UsernameFragment : Fragment() {
    private lateinit var binding: FragmentUsernameBinding
    val auth = FirebaseAuth.getInstance()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUsernameBinding.inflate(inflater, container, false)
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
        val cachedUsername = UserDetails.getUsername(requireActivity())

        if (!cachedUsername.isNullOrEmpty())
            binding.usernameEt.setText(cachedUsername)
    }

    private fun saveUsername() {
        binding.usernameProgressBar.visibility = View.VISIBLE
        binding.usernameSaveBtn.visibility = View.GONE
        val username = binding.usernameEt.text.toString()

        FirestoreUtils.checkAvailableUsername(username) {
            if (it) {
                FirestoreUtils.updateUsername(username, auth.currentUser!!) { isSuccessful ->
                    if (isSuccessful) {
                        requireActivity().finish()

                        //saving username in shared preferences
                        UserDetails.saveUsername(requireActivity(), username)
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
            } else {
                binding.usernameEt.error = "Username already taken"
                binding.usernameProgressBar.visibility = View.GONE
                binding.usernameSaveBtn.visibility = View.VISIBLE
            }
        }
    }
}