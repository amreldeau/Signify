package com.example.signify

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class OrderViewAdapter(private val orderList: ArrayList<Orders>): RecyclerView.Adapter<OrderViewAdapter.ViewHolder>() {
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val orderID: TextView = itemView.findViewById(R.id.order_id)
        val billboardID: TextView = itemView.findViewById(R.id.billboard_id)
        val orderStatus: TextView = itemView.findViewById(R.id.order_status)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder
    }
}