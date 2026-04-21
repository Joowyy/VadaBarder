package com.example.vadabarder.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.vadabarder.R
import com.example.vadabarder.data.BarberiaData
import com.example.vadabarder.databinding.FragmentHomeBinding
import com.example.vadabarder.viewmodel.UserViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val userViewModel: UserViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bienvenida.text = "¡Bienvenido ${userViewModel.user}!"

        val verHorario   = view.findViewById<MaterialButton>(R.id.verHorario)
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

        binding.btnLlamar.setOnClickListener {
            val intent = android.content.Intent(android.content.Intent.ACTION_DIAL)
            intent.data = android.net.Uri.parse("tel:+34123456789")
            startActivity(intent)
        }

        binding.btnMapa.setOnClickListener {
            val intent = android.content.Intent(
                android.content.Intent.ACTION_VIEW,
                android.net.Uri.parse("geo:0,0?q=Calle+Ejemplo+123,+Ciudad,+España")
            )
            intent.setPackage("com.google.android.apps.maps")
            startActivity(intent)
        }

        construirHorario()
        construirServicios()
    }

    private fun construirHorario() {
        val contenedor = binding.contenedorHorario
        contenedor.removeAllViews()
        val entradas = BarberiaData.horarioSemana.entries.toList()

        entradas.forEachIndexed { index, (dia, horario) ->
            val fila = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = android.view.Gravity.CENTER_VERTICAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { setMargins(0, 0, 0, dp(10)) }
            }

            val tvDia = TextView(requireContext()).apply {
                text = dia
                textSize = 13f
                setTypeface(ResourcesCompat.getFont(requireContext(), R.font.grotesk),
                    android.graphics.Typeface.BOLD)
                setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary_light))
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }

            val tvHoras = TextView(requireContext()).apply {
                textSize = 12f
                if (horario != null) {
                    text = horario
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary_light))
                } else {
                    text = "CERRADO"
                    setTypeface(null, android.graphics.Typeface.BOLD)
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.red_500))
                }
            }

            fila.addView(tvDia)
            fila.addView(tvHoras)
            contenedor.addView(fila)

            // Divider entre filas (no después de la última)
            if (index < entradas.size - 1) {
                contenedor.addView(buildDividerHorario())
            }
        }
    }

    private fun construirServicios() {
        val contenedor = binding.contenedorServicios
        contenedor.removeAllViews()
        val populares = BarberiaData.serviciosPopulares

        populares.forEachIndexed { index, nombre ->
            val precio = BarberiaData.servicios[nombre] ?: 0
            val fila = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { setMargins(0, 0, 0, dp(10)) }
            }

            val tvNombre = TextView(requireContext()).apply {
                text = nombre
                textSize = 15f
                setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary_light))
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }

            val tvPrecio = TextView(requireContext()).apply {
                text = "${precio}€"
                textSize = 15f
                setTypeface(null, android.graphics.Typeface.BOLD)
                setTextColor(ContextCompat.getColor(requireContext(), R.color.deep_purple_500))
            }

            fila.addView(tvNombre)
            fila.addView(tvPrecio)
            contenedor.addView(fila)

            if (index < populares.size - 1) {
                contenedor.addView(buildDividerServicios())
            }
        }
    }

    private fun buildDividerHorario() = View(requireContext()).apply {
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, dp(1)
        ).apply { setMargins(0, 0, 0, dp(10)) }
        setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.divider_light))
    }

    private fun buildDividerServicios() = View(requireContext()).apply {
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, dp(1)
        ).apply { setMargins(0, 0, 0, dp(10)) }
        setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.purple_300))
        alpha = 0.4f
    }

    private fun dp(value: Int) = (value * resources.displayMetrics.density).toInt()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
