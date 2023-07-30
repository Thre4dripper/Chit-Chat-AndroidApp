package com.example.chitchatapp.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import com.example.chitchatapp.constants.Constants
import com.example.chitchatapp.R
import com.example.chitchatapp.enums.FragmentType
import com.example.chitchatapp.ui.fragments.BioFragment
import com.example.chitchatapp.ui.fragments.NameFragment
import com.example.chitchatapp.ui.fragments.UsernameFragment

class SetDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_details)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        val fragmentType = intent.getStringExtra(Constants.FRAGMENT_TYPE)

        val usernameFragment = UsernameFragment()
        val nameFragment = NameFragment()
        val bioFragment = BioFragment()

        when (fragmentType) {
            FragmentType.FRAGMENT_USERNAME.name -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.set_details_frame_layout, usernameFragment)
                    .commit()
            }

            FragmentType.FRAGMENT_NAME.name -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.set_details_frame_layout, nameFragment)
                    .commit()
            }

            FragmentType.FRAGMENT_BIO.name -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.set_details_frame_layout, bioFragment)
                    .commit()
            }
        }
    }
}