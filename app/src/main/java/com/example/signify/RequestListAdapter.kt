package com.example.signify

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView

import com.example.signify.databinding.RequestItemBinding

class RequestListAdapter(private val requests: List<Request>) :
    RecyclerView.Adapter<RequestListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RequestItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val request = requests[position]
        holder.binding.name.text = request.clientName

        holder.itemView.setOnClickListener {
            // Create a new instance of the RequestRespondFragment
            val fragment = RequestRespondFragment()

            // Create a bundle to pass the Request object to the fragment
            val args = Bundle()
            args.putString("requestId", request.requestID) // assuming request is the Request object you want to pass
            fragment.arguments = args

            // Open the RequestRespondFragment
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