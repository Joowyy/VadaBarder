package com.example.vadabarder.ui.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.vadabarder.R
import com.example.vadabarder.data.BarberiaData
import com.example.vadabarder.data.model.Cita

class HistorialAdapter(private var citas: List<Cita>) :
    RecyclerView.Adapter<HistorialAdapter.CitaViewHolder>() {

    inner class CitaViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val tvServicio: TextView = view.findViewById(R.id.tvServicio)
        val tvFechaHora: TextView = view.findViewById(R.id.tvFechaHora)
        val tvPrecio: TextView = view.findViewById(R.id.tvPrecio)
        val divider: View = view.findViewById(R.id.dividerItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CitaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_citas, parent, false)
        return CitaViewHolder(view)
    }

    override fun getItemCount(): Int = citas.size

    override fun onBindViewHolder(holder: CitaViewHolder, position: Int) {
        val cita = citas[position]
        holder.tvServicio.text = BarberiaData.resolverServicio(holder.itemView.context, cita.servicio)
        holder.tvFechaHora.text = "${cita.fecha} · ${cita.hora}"
        holder.tvPrecio.text = cita.precio
        holder.divider.visibility = if (position == itemCount - 1) View.GONE else View.VISIBLE
    }

    fun actualizarCitas(nuevasCitas: List<Cita>) {
        citas = nuevasCitas
        notifyDataSetChanged()
    }
}
