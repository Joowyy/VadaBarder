package com.example.vadabarder.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.vadabarder.databinding.MainLayoutBinding

class MainActivity : AppCompatActivity() {

    private var _binding : MainLayoutBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = MainLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

    // Memory Leaks
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}