package com.example.culturex

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set the main activity layout that contains the NavHostFragment
        setContentView(R.layout.activity_main)
    }
}
