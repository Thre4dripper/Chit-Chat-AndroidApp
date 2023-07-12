package com.example.chitchatapp.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.chitchatapp.Constants
import com.example.chitchatapp.R
import com.example.chitchatapp.ui.fragments.BioFragment
import com.example.chitchatapp.ui.fragments.NameFragment
import com.example.chitchatapp.ui.fragments.UsernameFragment

class SetDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_details)

        val fragmentType = intent.getStringExtra(Constants.FRAGMENT_TYPE)

        val usernameFragment = UsernameFragment()
        val nameFragment = NameFragment()
        val bioFragment = BioFragment()

        when (fragmentType) {
            Constants.FRAGMENT_USERNAME -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.set_details_frame_layout, usernameFragment)
                    .commit()
            }
            Constants.FRAGMENT_NAME -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.set_details_frame_layout, nameFragment)
                    .commit()
            }
            Constants.FRAGMENT_BIO -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.set_details_frame_layout, bioFragment)
                    .commit()
            }
        }
    }
}