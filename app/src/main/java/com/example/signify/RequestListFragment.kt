package com.example.signify

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.signify.databinding.FragmentOrderDetailsBinding
import com.example.signify.databinding.FragmentRequestListBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class RequestListFragment : Fragment() {
    private lateinit var binding: FragmentRequestListBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var adapter: RequestListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRequestListBinding.inflate(inflater, container, false)
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Initialize the adapter with an empty list
        adapter = RequestListAdapter(emptyList())

        // Set up RecyclerView with LinearLayoutManager and adapter
        binding.recycler.layoutManager = LinearLayoutManager(requireContext())
        binding.recycler.adapter = adapter

        val currentManagerId = auth.currentUser!!.uid
        getRequestsByManagerId(currentManagerId)

        return binding.root
    }
    private fun getRequestsByManagerId(managerId: String) {
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("client_to_manager").document(managerId)

        docRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val data = documentSnapshot.data
                val clientsIdArray = data?.get("clients_id") as ArrayList<String>

                val masterRequestsArray = ArrayList<String>()
                clientsIdArray.forEach { clientId ->
                    val authDocRef = db.collection("authorization").document(clientId)
                    authDocRef.get().addOnSuccessListener { authDocumentSnapshot ->
                        if (authDocumentSnapshot.exists()) {
                            val authData = authDocumentSnapshot.data
                            val requestsArray = authData?.get("requests") as ArrayList<String>
                            masterRequestsArray.addAll(requestsArray)
                            if (masterRequestsArray.size == clientsIdArray.size) {
                                val requests = ArrayList<Request>()
                                masterRequestsArray.forEach { requestId ->
                                    val requestDocRef = db.collection("requests").document(requestId)
                                    requestDocRef.get().addOnSuccessListener { requestDocumentSnapshot ->
                                        if (requestDocumentSnapshot.exists()) {
                                            val requestData = requestDocumentSnapshot.data
                                            val orderId = requestData?.get("orderId") as String
                                            val type = requestData?.get("type") as String
                                            requests.add(Request(orderId, type, requestId))
                                            if (requests.size == masterRequestsArray.size) {
                                                displayOrders(requests)
                                            }
                                        } else {
                                            // Handle the case where the document doesn't exist
                                        }
                                    }.addOnFailureListener { exception ->
                                        // Handle any exceptions that occur while fetching the document
                                    }
                                }
                            }
                        } else {
                            // Handle the case where the document doesn't exist
                        }
                    }.addOnFailureListener { exception ->
                        // Handle any exceptions that occur while fetching the document
                    }
                }
            } else {
                // Handle the case where the document doesn't exist
            }
        }.addOnFailureListener { exception ->
            // Handle any exceptions that occur while fetching the document
        }
    }

    private fun displayOrders(requests: List<Request>) {
        val adapter = RequestListAdapter(requests)
        binding.recycler.adapter = adapter
        binding.recycler.layoutManager = LinearLayoutManager(context)
    }
}
