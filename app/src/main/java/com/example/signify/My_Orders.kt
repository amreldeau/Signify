package com.example.signify

import android.app.Activity
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class My_Orders : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var orderList: ArrayList<Orders>
    private var db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_my__orders, container, false)


        orderList = arrayListOf()

        db = FirebaseFirestore.getInstance()
        db.collection("orders").get()
            .addOnSuccessListener {result ->
                val dataList = ArrayList<Orders>()
                for (document in result){
                    val dataModel = Orders(
                        orders_id = document.getString("orders_id") ?: "",
                        order_status = document.getString("order_status") ?: "",
                        billboard_id = document.getString("billboard_id") ?: ""
                    )
                    dataList.add(dataModel)
                }

                // finding recyclerView in the layout
                val recyclerView = view.findViewById<RecyclerView>(R.id.ordersList)

                // setting up the RecyclerView with an adapter and layout manager
                recyclerView.layoutManager = LinearLayoutManager(activity)
                recyclerView.adapter = OrderViewAdapter(dataList)

                /*if (!it.isEmpty) {
                    for (data in it.documents) {
                        val orders: Orders? = data.toObject(Orders::class.java)
                        if (orders != null) {
                            orderList.add(orders)
                        }
                    }
                    recyclerView.adapter = OrderViewAdapter(orderList)
                }*/
            }
            .addOnFailureListener {exception ->
                Log.e(TAG, "Error getting documents.", exception)
            }

        return view
    }
}