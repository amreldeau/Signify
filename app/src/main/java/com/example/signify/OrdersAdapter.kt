package com.example.signify

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import com.example.signify.databinding.OrderLayoutBinding

class OrdersAdapter(private val orders: List<Order>) :
    RecyclerView.Adapter<OrdersAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = OrderLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order = orders[position]
        holder.binding.billboardName.text = order.billboardId
        holder.binding.orderDate.text = order.date.toString()

    }

    override fun getItemCount(): Int {
        return orders.size
    }

    class ViewHolder(val binding: OrderLayoutBinding) : RecyclerView.ViewHolder(binding.root)
}