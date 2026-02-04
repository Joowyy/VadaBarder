package com.example.vadabarder.ui.add

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.vadabarder.R
import com.example.vadabarder.databinding.FragmentAddBinding
import com.example.vadabarder.databinding.FragmentProfileBinding
import com.google.android.material.chip.Chip



class AddFragment : Fragment() {

    private var _binding : FragmentAddBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentAddBinding.inflate(layoutInflater)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val horasDisponibles = listOf(
            "10:00", "10:30", "11:00",
            "11:30", "12:00", "12:30",
            "16:00", "16:30", "17:00"
        )

        val serviciosDisponibles = listOf(
            "Corte clásico", "Fade", "Barba", "Corts + Barba"
        )

        cargarServicios(serviciosDisponibles)

        binding.calendarView.setOnDateChangeListener { _, _, _, _ ->
            cargarHoras(horasDisponibles)
        }

    }

    private fun cargarHoras(horas: List<String>) {
        binding.chipGroupHoras.removeAllViews()
        binding.chipGroupHoras.visibility = View.VISIBLE

        horas.forEach { hora ->
            val chip = Chip(requireContext()).apply {
                text = hora
                isCheckable = true
                isClickable = true

                layoutParams = ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(8, 8, 8, 8)
                }
            }
            binding.chipGroupHoras.addView(chip)
        }
    }

    private fun cargarServicios(servicios: List<String>) {
        binding.chipGroupServicios.removeAllViews()
        binding.chipGroupServicios.visibility = View.VISIBLE

        servicios.forEach { servicio ->
            val chip = Chip(requireContext()).apply {
                text = servicio
                isCheckable = true
                isClickable = true

                layoutParams = ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(8, 8, 8, 8)
                }
            }
            binding.chipGroupServicios.addView(chip)
        }
    }

    // Memory Leaks
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}