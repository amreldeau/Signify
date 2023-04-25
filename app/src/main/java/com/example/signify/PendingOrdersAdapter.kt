package com.example.signify

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.signify.databinding.ClientItemBinding
import com.example.signify.databinding.OrderLayoutBinding
import com.google.firebase.firestore.FirebaseFirestore

class PendingOrdersAdapter(private val orders: List<PendingOrder>) :
    RecyclerView.Adapter<PendingOrdersAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ClientItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order = orders[position]
        holder.binding.clientName.text = order.clientName
        holder.binding.info.text = order.billboardAddress


        holder.itemView.setOnClickListener {
            // Create a new instance of the OrderDetailsFragment
            val fragment = ManageOrderFragment()

            // Create a bundle to pass the orderId to the fragment
            val args = Bundle()
            args.putString("orderId", order.orderId)
            fragment.arguments = args

            // Open the OrderDetailsFragment
            val fragmentManager = (holder.itemView.context as FragmentActivity).supportFragmentManager
            fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }
    }

    override fun getItemCount(): Int {
        return orders.size
    }

    class ViewHolder(val binding: ClientItemBinding) : RecyclerView.ViewHolder(binding.root)
}