package com.hfad.newprogectforlesssix.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hfad.newprogectforlesssix.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initMainFragment()
    }

    private fun initMainFragment() {
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, MainFragment.newInstance())
                .commit()
    }
}