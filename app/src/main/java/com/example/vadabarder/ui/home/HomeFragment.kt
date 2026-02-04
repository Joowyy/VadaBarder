package com.example.vadabarder.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.vadabarder.R
import com.example.vadabarder.databinding.FragmentHomeBinding
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import com.example.vadabarder.viewmodel.UserViewModel
import com.google.android.material.card.MaterialCardView

class HomeFragment : Fragment() {

    private var _binding : FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val userViewModel : UserViewModel by activityViewModels()

    // Guardar las instancias cuando navegamos entre Fragments
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {

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
        binding.bienvenida.text = "¡Bienvenido ${userViewModel.user}!"

        val verHorario = view.findViewById<TextView>(R.id.verHorario)
        val tablaHorario = view.findViewById<MaterialCardView>(R.id.cardHorario)

        // Desplegable del horario
        verHorario.setOnClickListener {
            if (tablaHorario.visibility == View.GONE) {
                tablaHorario.visibility = View.VISIBLE
                verHorario.text = "OCULTAR HORARIO"
            } else {
                tablaHorario.visibility = View.GONE
                verHorario.text = "VER HORARIO"
            }
        }

        binding.btnLlamar.setOnClickListener {
            val phoneNumber = "+34123456789"
            val intent = android.content.Intent(android.content.Intent.ACTION_DIAL)
            intent.data = android.net.Uri.parse("tel:$phoneNumber")
            startActivity(intent)
        }

        binding.btnMapa.setOnClickListener {
            // Coordenadas de la barbería o dirección en texto
            val geoUri = "geo:0,0?q=Calle+Ejemplo+123,+Ciudad,+España"
            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(geoUri))
            intent.setPackage("com.google.android.apps.maps")
            startActivity(intent)
        }

    }

    // Memory Leaks
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}