package com.example.signify

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView

import com.example.signify.databinding.OrderLayoutBinding

class OrdersAdapter(var orders: List<Order>) :
    RecyclerView.Adapter<OrdersAdapter.ViewHolder>() {

    private var lastClickedPosition: Int = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = OrderLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order = orders[position]
        holder.binding.billboardName.text = order.billboardId
        holder.binding.orderDate.text = order.status

        // Highlight the last clicked item
        if (holder.adapterPosition == lastClickedPosition) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.highlight_color))
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT)
        }

        holder.itemView.setOnClickListener {
            // Update the last clicked position and notify the adapter of the change
            val previousPosition = lastClickedPosition
            lastClickedPosition = holder.adapterPosition
            notifyItemChanged(previousPosition)
            notifyItemChanged(lastClickedPosition)

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