package com.example.signify

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView

import com.example.signify.databinding.OrderLayoutBinding
import com.example.signify.databinding.RequestItemBinding

class RequestListAdapter(private val requests: List<Request>) :
    RecyclerView.Adapter<RequestListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RequestItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val request = requests[position]
        holder.binding.requestType.text = request.type



        holder.itemView.setOnClickListener {
            // Create a new instance of the OrderDetailsFragment
            val fragment = RequestCancelFragment()

            // Create a bundle to pass the orderId to the fragment
            val args = Bundle()
            args.putString("requestId", request.requestId)
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
        return requests.size
    }

    class ViewHolder(val binding: RequestItemBinding) : RecyclerView.ViewHolder(binding.root)
}