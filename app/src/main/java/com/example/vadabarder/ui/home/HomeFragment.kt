package com.example.vadabarder.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.vadabarder.R
import com.example.vadabarder.databinding.FragmentHomeBinding
import android.widget.TextView
import android.widget.TableLayout
import com.google.android.material.card.MaterialCardView

class HomeFragment : Fragment() {

    private var _binding : FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var user: String? = null
    private var correo: String? = null
    private var psswd: String? = null

    // Guardar las instancias cuando navegamos entre Fragments
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("user", user)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {

            // Guardamos su instancia y recogemos su valor de argumento
            user = savedInstanceState?.getString("user")
                ?: it.getString("user")

            correo = it.getString("correo")
            psswd = it.getString("psswd")
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Mirar bien lo del correo, ya que si inicia sesion tendremos que recogerlo de la ROOM (La BDD)
        binding.bienvenida.text = "¡Bienvenido $user!"

        val verHorario = view.findViewById<TextView>(R.id.verHorario)
        val tablaHorario = view.findViewById<MaterialCardView>(R.id.cardHorario)

        verHorario.setOnClickListener {
            if (tablaHorario.visibility == View.GONE) {
                tablaHorario.visibility = View.VISIBLE
                verHorario.text = "OCULTAR HORARIO"
            } else {
                tablaHorario.visibility = View.GONE
                verHorario.text = "VER HORARIO"
            }
        }

    }

    // Memory Leaks
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}