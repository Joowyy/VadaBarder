package com.example.vadabarder.ui.main

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.example.vadabarder.R
import com.example.vadabarder.data.prefs.SessionPreferences
import com.example.vadabarder.data.repository.FirestoreConfig
import com.example.vadabarder.databinding.MainLayoutBinding
import com.example.vadabarder.notifications.NotificationHelper
import com.example.vadabarder.utils.LocaleHelper
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private var _binding : MainLayoutBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController : NavController
    private lateinit var appBarConfiguration : AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirestoreConfig.configurarOffline()

        // Si el usuario no marcó "Recordar", forzamos signOut antes de inflar la UI
        // para que LoginFragment vea getCurrentUser() == null y no auto-redirija.
        if (!SessionPreferences.isRecordar(this)) {
            FirebaseAuth.getInstance().signOut()
        }

        _binding = MainLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        NotificationHelper.crearCanal(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 100)
        }

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Fragments a manejar en el BottomMenu
        appBarConfiguration = AppBarConfiguration.Builder (

            R.id.homeFragment,
            R.id.addFragment,
            R.id.profileFragment

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

    // Aplica el locale guardado antes de crear cualquier vista
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.wrap(newBase))
    }

    // Memory Leaks
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}