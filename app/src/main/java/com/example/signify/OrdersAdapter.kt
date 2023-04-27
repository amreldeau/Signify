package com.example.signify

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
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
        holder.binding.orderDate.text = order.status
        holder.binding.address.text = order.location


        holder.itemView.setOnClickListener {
            // Create a new instance of the OrderDetailsFragment
            val fragment = OrderDetailsFragment()

            // Create a bundle to pass the orderId to the fragment
            val args = Bundle()
            args.putString("orderId", order.orderId)
            fragment.arguments = args

            // Open the OrderDetailsFragment
            val fragmentManager = (holder.itemView.context as FragmentActivity).supportFragmentManager
            fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(null)
                .commit()
        }
    }

    override fun getItemCount(): Int {
        return orders.size
    }

    class ViewHolder(val binding: OrderLayoutBinding) : RecyclerView.ViewHolder(binding.root)
}