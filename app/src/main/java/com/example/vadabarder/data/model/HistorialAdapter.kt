package com.example.vadabarder.data.model

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.vadabarder.R

class HistorialAdapter(private var citas: List<Cita>) :
    RecyclerView.Adapter<HistorialAdapter.CitaViewHolder>() {

    inner class CitaViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val tvServicio: TextView = view.findViewById(R.id.tvServicio)
        val tvFechaHora: TextView = view.findViewById(R.id.tvFechaHora)
        val tvPrecio: TextView = view.findViewById(R.id.tvPrecio)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CitaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_citas, parent, false)
        return CitaViewHolder(view)
    }

    override fun getItemCount(): Int = citas.size

    override fun onBindViewHolder(holder: CitaViewHolder, position: Int) {
        val cita = citas[position]
        holder.tvServicio.text = cita.servicio
        holder.tvFechaHora.text = "${cita.fecha} · ${cita.hora}"
        holder.tvPrecio.text = cita.precio
    }

    fun actualizarCitas(nuevasCitas: List<Cita>) {
        citas = nuevasCitas
        notifyDataSetChanged()
    }
}