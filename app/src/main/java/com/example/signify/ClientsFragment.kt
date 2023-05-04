package com.example.signify

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.signify.databinding.FragmentClientsBinding
import com.example.signify.databinding.FragmentOrdersBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ClientsFragment : Fragment() {

    private lateinit var binding: FragmentClientsBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var recyclerView: RecyclerView
    private val db = Firebase.firestore

//<<<<<<< HEAD
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getClients()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_clients, container, false)
        recyclerView = view.findViewById(R.id.client_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        return view

        //binding = FragmentClientsBinding.inflate(inflater, container, false)
        //firestore = FirebaseFirestore.getInstance()
        //auth = FirebaseAuth.getInstance()

    }

    // query the "Clients" collection to get a list of Client objects:
    private fun getClients() {
        db.collection("clients")
            .get()
            .addOnSuccessListener { result ->
                val clients = mutableListOf<Clients>()
                for (document in result) {
                    if (document.id == document.getString("UID")) {
                        val name = document.getString("name") ?: ""
                        val email = document.getString("email") ?: ""
                        val client = Clients(name, email)
                        clients.add(client)
                    }
                }
                showClients(clients)
            }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "Error getting documents: ", exception)
            }
    }
/*

=======


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentClientsBinding.inflate(inflater, container, false)
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        binding.add.setOnClickListener{
            val fragment = AddClientFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .add(R.id.container, fragment)
                .addToBackStack(null)
                .commit()
        }
>>>>>>> 0eac1abcccfbb9e7a96eb83159be5c45350fbd23

*/

    // Implement the showClients function to display the list of clients in the RecyclerView:
    private fun showClients(clients: List<Clients>) {
        val adapter = ClientAdapter(clients)
        recyclerView.adapter = adapter
    }
}