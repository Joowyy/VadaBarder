package com.example.vadabarder.ui.main

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.example.vadabarder.R
import com.example.vadabarder.databinding.MainLayoutBinding

class MainActivity : AppCompatActivity() {

    private var _binding : MainLayoutBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController : NavController
    private lateinit var appBarConfiguration : AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = MainLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Fragments a manejar en el BottomMenu
        appBarConfiguration = AppBarConfiguration.Builder (

            R.id.homeFragment,

        ).build()

        // Controlamos la visibilidad del BottomView
        navController.addOnDestinationChangedListener { _, destination, _ ->

            when (destination.id) {

                // Ocultamos el BottomMenu en Login y Register
                R.id.loginFragment,
                R.id.registroFragment -> {

                    binding.bottomNavigationView.visibility = View.GONE

                }
                // Y en el resto lo mostramos
                else -> {

                    binding.bottomNavigationView.visibility = View.VISIBLE

                }

            }

        }

        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController)

    }

    // Memory Leaks
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}