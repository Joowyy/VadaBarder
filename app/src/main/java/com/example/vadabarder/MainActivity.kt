package com.example.vadabarder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.vadabarder.databinding.MainLayoutBinding
import com.example.vadabarder.ui.theme.VadaBarderTheme

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