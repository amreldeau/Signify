package com.example.signify

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.api.Api

class ClientAdapter(private val clients: ArrayList<Clients>) : RecyclerView.Adapter<ClientAdapter.ClientViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_client, parent, false)
        return ClientViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClientViewHolder, position: Int) {
        val client = clients[position]
        holder.clientName.text = client.name
        holder.clientEmail.text = client.email
    }

    override fun getItemCount(): Int {
        return clients.size
    }

    class ClientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val clientName: TextView = itemView.findViewById(R.id.client_name)
        val clientEmail: TextView = itemView.findViewById((R.id.client_email))
    }
}