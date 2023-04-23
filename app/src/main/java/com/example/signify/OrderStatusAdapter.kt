package com.example.signify

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.signify.databinding.OrderStepItemBinding

class OrderStatusAdapter(private val orderStatusList: List<OrderStatus>) :
RecyclerView.Adapter<OrderStatusAdapter.OrderStatusViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderStatusViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = OrderStepItemBinding.inflate(inflater, parent, false)
        return OrderStatusViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderStatusViewHolder, position: Int) {
        val orderStatus = orderStatusList[position]
        holder.bind(orderStatus)
    }

    override fun getItemCount(): Int {
        return orderStatusList.size
    }

    inner class OrderStatusViewHolder(private val binding: OrderStepItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(orderStatus: OrderStatus) {
            binding.steptextStatus.text = orderStatus.status
            binding.steptextDate.text = orderStatus.date
        }
    }
}


