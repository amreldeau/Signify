package com.example.signify

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.api.Api

class ClientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val nameTextView: TextView = itemView.findViewById(R.id.client_name)
    private val emailTextView: TextView = itemView.findViewById(R.id.client_email)
    private val phoneNumberTextView: TextView = itemView.findViewById(R.id.client_phone_number)

    fun bind(client: Clients) {
        nameTextView.text = client.name
        emailTextView.text = client.email
        phoneNumberTextView.text = client.phoneNumber
    }

}