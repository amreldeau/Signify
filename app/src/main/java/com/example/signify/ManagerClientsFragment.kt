package com.example.signify

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.api.Api
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ManagerClientsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView

    // Create a FirebaseFirestore instance
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getClients()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_manager_clients, container, false)
        recyclerView = view.findViewById(R.id.client_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        return view
    }

    // query the "Clients" collection to get a list of Client objects:
    private fun getClients() {
        db.collection("Clients")
            .get()
            .addOnSuccessListener { result ->
                val clients = mutableListOf<Clients>()
                for (document in result) {
                    val name = document.getString("name") ?: ""
                    val email = document.getString("email") ?: ""
                    val phoneNumber = document.getString("phoneNumber") ?: ""
                    val client = Clients(name, email, phoneNumber)
                    clients.add(client)
                }
                showClients(clients)
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
            }
    }


    // Implement the showClients function to display the list of clients in the RecyclerView:
    private fun showClients(clients: List<Clients>) {
        val adapter = ClientAdapter(clients)
        recyclerView.adapter = adapter
    }
}