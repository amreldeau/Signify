package com.example.signify.repository

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.signify.Description
import com.example.signify.Order
import com.example.signify.OrderDetails
import com.example.signify.Request
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.HashMap

class FirestoreRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    fun generateID() : LiveData<String>{
        val uniqueId = MutableLiveData<String>()
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("orders").document()
        uniqueId.value = docRef.id
        return uniqueId
    }
    fun getOrdersForClient(clientId: String): LiveData<List<Order>> {
        val ordersLiveData = MutableLiveData<List<Order>>()

        val ordersCollection = firestore.collection("orders")
        val query = ordersCollection
            .whereEqualTo("clientID", firestore.document("clients/$clientId"))
            .whereNotEqualTo("status", "Require payment") // add this filter to exclude orders with status "Require payment"

        query.addSnapshotListener { querySnapshot, exception ->
            if (exception != null) {
                Log.e(TAG, "Error getting orders for client $clientId", exception)
                return@addSnapshotListener
            }

            val orders = querySnapshot?.documents?.mapNotNull { document ->
                try {
                    val orderId = document.id
                    val billboardId = document.getDocumentReference("billboardID")!!.id
                    val occupied = document.get("occupied") as? HashMap<String, Boolean> ?: emptyMap()
                    val orderDate = document.getTimestamp("orderDate")?.toDate() ?: Date()
                    val status = document.getString("status") ?: ""
                    val totalCost = document.getDouble("totalCost") ?: 0.0

                    Order(orderId, billboardId, clientId, occupied, orderDate, status, totalCost)
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing order document", e)
                    null
                }
            } ?: emptyList()

            ordersLiveData.postValue(orders)
        }

        return ordersLiveData
    }


    fun updateOrderStatus(orderId: String, status: String) {
        val ordersCollection = firestore.collection("orders")
        val orderDocument = ordersCollection.document(orderId)

        orderDocument.update("status", status)
            .addOnSuccessListener {
                Log.d(TAG, "Order $orderId status updated to $status")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error updating order $orderId status to $status", e)
            }
    }

    fun createOrder(clientId: String, billboardId: String, price: Double, selected: MutableMap<String, Boolean>, orderId: String){
        val orderMap = hashMapOf(
            "clientID" to db.document("clients/$clientId"),
            "billboardID" to db.document("billboards/$billboardId"),
            "totalCost" to price,
            "orderDate" to FieldValue.serverTimestamp(),
            "status" to "Require payment",
            "occupied" to selected
        )

        db.collection("orders")
            .document(orderId) // set the document name to orderId
            .set(orderMap) // use set() instead of add()
            .addOnSuccessListener {
                Log.d(TAG, "Order added with ID: $orderId")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding order", e)
            }
    }

    fun appendSelectedToNotAvailable(billboardId: String, selected: MutableMap<String, Boolean>) {
        val billboardRef = db.collection("billboards").document(billboardId)
        billboardRef.get().addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                val notAvailableMap = document.get("NotAvailable") as? MutableMap<String, Boolean>
                    ?: mutableMapOf()
                notAvailableMap.putAll(selected)
                billboardRef.update("NotAvailable", notAvailableMap)
                    .addOnSuccessListener {
                        Log.d(
                            TAG,
                            "Selected appended to NotAvailable for billboardId: $billboardId"
                        )
                    }
                    .addOnFailureListener { e ->
                        Log.w(
                            TAG,
                            "Error appending selected to NotAvailable for billboardId: $billboardId",
                            e
                        )
                    }
            } else {
                Log.d(TAG, "No such document")
            }
        }.addOnFailureListener { e ->
            Log.w(TAG, "Error getting document", e)
        }
    }

    fun getPrice(billboardId: String): LiveData<Double> {
        val db = FirebaseFirestore.getInstance()
        val billboardsRef = db.collection("billboards")
        val price = MutableLiveData<Double>()

        billboardsRef.document(billboardId).get().addOnSuccessListener { billboardSnapshot ->
            price.value = billboardSnapshot.getDouble("price")
        }

        return price
    }

    fun getOrderById(orderId: String): LiveData<Order> {
        val liveData = MutableLiveData<Order>()
        val currentUser = FirebaseAuth.getInstance().currentUser
        val clientId = currentUser?.uid ?: ""
        firestore.collection("orders").document(orderId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val billboardId = document.getDocumentReference("billboardID")!!.id
                    val occupied =
                        document.get("occupied") as? HashMap<String, Boolean> ?: emptyMap()
                    val orderDate = document.getTimestamp("orderDate")?.toDate() ?: Date()
                    val status = document.getString("status") ?: ""
                    val totalCost = document.getDouble("totalCost") ?: 0.0
                    val order = Order(
                        orderId,
                        billboardId,
                        clientId,
                        occupied,
                        orderDate,
                        status,
                        totalCost
                    )
                    liveData.postValue(order)
                } else {
                    Log.d(TAG, "No order found with ID: $orderId")
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error getting order with ID: $orderId", exception)
            }

        return liveData
    }

    fun getBillboardAvailability(billboardId: String): LiveData<HashMap<String, Boolean>> {
        val db = FirebaseFirestore.getInstance()
        val billboardsRef = db.collection("billboards")
        val availability = MutableLiveData<HashMap<String, Boolean>>()

        billboardsRef.document(billboardId).get().addOnSuccessListener { billboardSnapshot ->
            val billboardData = billboardSnapshot.data ?: return@addOnSuccessListener
            availability.value = ((billboardData["NotAvailable"] as? HashMap<String, Boolean>
                ?: emptyMap()) as HashMap<String, Boolean>?)
        }

        return availability
    }

    fun addRequest(
        clientId: String,
        orderId: String,
        payoutDifference: Double,
        selected: MutableMap<String, Boolean>,
        type: String
    ) {
        val clientRef = FirebaseFirestore.getInstance().collection("clients").document(clientId)
        clientRef.get()
            .addOnSuccessListener { clientSnapshot ->
                val clientName = clientSnapshot.getString("Name")
                val responsableManagerID = clientSnapshot.getString("AttachedManager")
                val request = Request(
                    requestID = "",
                    responsableManagerID = responsableManagerID!!,
                    clientName = clientName!!,
                    clientID = clientRef,
                    orderID = FirebaseFirestore.getInstance().document("orders/$orderId"),
                    date = Timestamp.now(),
                    payoutDifference = payoutDifference,
                    requestedChanges = selected,
                    status = "Pending",
                    type = type
                )

                // Add the request to the Firestore requests collection
                FirebaseFirestore.getInstance().collection("requests")
                    .add(request)
                    .addOnSuccessListener { documentReference ->
                        Log.d(TAG, "Request added with ID: ${documentReference.id}")
                        // Set the requestID field with the ID of the created document
                        val requestId = documentReference.id
                        documentReference.update("requestID", requestId)
                            .addOnSuccessListener {
                                Log.d(TAG, "Request ID added to document")
                            }
                            .addOnFailureListener { e ->
                                Log.w(TAG, "Error updating request document with ID", e)
                            }
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error adding request", e)
                    }
            }
    }
        fun getRequestsByManagerId(managerId: String): LiveData<List<Request>> {
            val requests = MutableLiveData<List<Request>>()
            val requestsList = mutableListOf<Request>()

            FirebaseFirestore.getInstance().collection("requests")
                .whereEqualTo("responsableManagerID", managerId)
                .whereEqualTo("status", "Pending")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val clientID = document.getDocumentReference("clientID")!!
                        val orderID = document.getDocumentReference("orderID")!!
                        val date = document.getTimestamp("date")!!
                        val requestedChanges = document.data["requestedChanges"] as Map<String, Any>
                        val request = Request(
                            requestID = document.id,
                            clientName = document.getString("clientName")!!,
                            clientID = clientID,
                            orderID = orderID,
                            date = date,
                            payoutDifference = document.getDouble("payoutDifference") ?: 0.0,
                            requestedChanges = requestedChanges,
                            status = document.getString("status") ?: "",
                            type = document.getString("type") ?: "",
                            responsableManagerID = document.getString("responsableManagerID") ?: ""
                        )
                        requestsList.add(request)
                    }
                    requests.value = requestsList
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting requests: ", exception)
                }

            return requests
        }
    fun getRequestById(requestId: String): LiveData<Request> {
        val request = MutableLiveData<Request>()

        firestore.collection("requests").document(requestId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val clientID = document.getDocumentReference("clientID")!!
                    val orderID = document.getDocumentReference("orderID")!!
                    val requestedChanges = document.get("requestedChanges") as MutableMap<String, Boolean>
                    val date = document.getTimestamp("date")!!
                    val payoutDifference = document.getDouble("payoutDifference")!!
                    val type = document.getString("type")!!
                    val status = document.getString("status")!!
                    val clientName = document.getString("clientName")
                    val responsableManagerID = document.getString("responsableManagerID")
                            val requestValue = Request(
                                requestID = document.id,
                                responsableManagerID = responsableManagerID!!,
                                clientName = clientName!!,
                                clientID = clientID,
                                orderID = orderID,
                                date = date,
                                payoutDifference = payoutDifference,
                                requestedChanges = requestedChanges,
                                status = status,
                                type = type
                            )
                            request.value = requestValue

                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error getting request document", e)
            }

        return request
    }
    fun getOrderDetails(orderId: String): LiveData<OrderDetails> {
        val orderDetailsLiveData = MutableLiveData<OrderDetails>()
        firestore.collection("orders").document(orderId).get()
            .addOnSuccessListener { document ->
                val price = document.getDouble("totalCost") ?: 0.0
                val status = document.getString("status") ?: ""
                val billboardRef = document.getDocumentReference("billboardID")
                val occupied = document.get("occupied") as? Map<String, Boolean> ?: emptyMap()
                val orderDate = document.getTimestamp("orderDate")?.toDate() ?: Date()
                billboardRef?.get()?.addOnSuccessListener { billboardDocument ->
                    val billboardId = billboardDocument.getString("id") ?: ""
                    val billboardLocation = billboardDocument.getString("location") ?: ""
                    val orderDetails = OrderDetails(billboardId, price, billboardLocation, occupied, status, orderDate)
                    orderDetailsLiveData.value = orderDetails
                }
            }
        return orderDetailsLiveData
    }
    fun getDescription(billboardId: String): LiveData<Description> {
        val descriptionLiveData = MutableLiveData<Description>()

        // Get the reference to the Firestore collection
        val collectionRef = FirebaseFirestore.getInstance().collection("billboards")

        // Query the collection to get the document with the given billboardId
        collectionRef.whereEqualTo("id", billboardId).get()
            .addOnSuccessListener { documents ->
                // If a document is found, create a Description object and set its properties
                if (documents.size() > 0) {
                    val document = documents.documents[0]
                    val description = Description(
                        document.getString("id") ?: "",
                        document.getDouble("price") ?: 0.0,
                        document.getString("location") ?: "",
                        document.getString("size") ?: "",
                        document.getString("surface") ?: "",
                        document.getString("type") ?: ""
                    )
                    descriptionLiveData.postValue(description)
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting document: ", exception)
            }

        return descriptionLiveData
    }
    fun updateRequestStatus(requestId: String) {
        val requestRef = firestore.collection("requests").document(requestId)

        requestRef.update("status", "Accepted")
            .addOnSuccessListener {
                Log.d(TAG, "Request status updated to Accepted")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error updating request status", e)
            }
    }
    fun updateOccupiedMap(orderId: String, requestedChanges: MutableMap<String, Boolean>) {
        val orderRef = firestore.collection("orders").document(orderId)
        orderRef.update("occupied", requestedChanges)
            .addOnSuccessListener {
                Log.d(TAG, "Occupied map updated for order $orderId")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error updating occupied map for order $orderId", e)
            }
    }

    fun signOut() {
        auth.signOut()
    }
    fun getUserName(currentUserUid: String): LiveData<String> {
        val result = MutableLiveData<String>()
        val userDocRef = firestore.collection("clients").document(currentUserUid)

        userDocRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val userName = documentSnapshot.getString("Name")
                result.value = userName!!
            } else {
                Log.d(TAG, "No such document")
            }
        }.addOnFailureListener { exception ->
            Log.d(TAG, "getUserName failed with exception:", exception)
        }

        return result
    }
    fun getManagerName(currentManagerUid: String): LiveData<String> {
        val res = MutableLiveData<String>()
        val managerDocRef = firestore.collection("managers").document(currentManagerUid)

        managerDocRef.get().addOnSuccessListener {  documentSnapshot ->
            if (documentSnapshot.exists()) {
                val managerName = documentSnapshot.getString("Name")
                res.value = managerName!!
            } else {
                Log.d(TAG, "No such document")
            }
        }.addOnFailureListener { exception ->
            Log.d(TAG, "getManagerName failed with exception:", exception)
        }
        return res
    }
    fun addNewClient(email: String, password: String, managerUid: String, name: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                // Return the user UID when the account is created successfully
                val newClientUid = authResult.user!!.uid
                // Save the new user UID to an authorization collection
                val authorizationCollectionRef = db.collection("authorization")
                val authorizationDocumentRef = authorizationCollectionRef.document(newClientUid)
                val authorizationData = mapOf(
                    "status" to "client"
                )
                authorizationDocumentRef.set(authorizationData)
                    .addOnSuccessListener {
                        // Create a new document in the clients collection with the required fields
                        val clientsCollectionRef = db.collection("clients")
                        val newClientDocumentRef = clientsCollectionRef.document(newClientUid)
                        val newClientData = mapOf(
                            "AttachedManager" to managerUid,
                            "Email" to email,
                            "Name" to name,
                            "UID" to newClientUid
                        )
                        newClientDocumentRef.set(newClientData)
                            .addOnSuccessListener {
                                Log.d(TAG, "New client added successfully")
                            }
                            .addOnFailureListener { exception ->
                                Log.e(TAG, "Failed to add new client", exception)
                            }
                    }
                    .addOnFailureListener { exception ->
                        Log.e(TAG, "Failed to add authorization status", exception)
                    }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Failed to create user account", exception)
            }
    }
    fun createPayment(orderId: String, amount: Double) {
        val paymentMap = hashMapOf(
            "amount" to amount,
            "orderID" to db.document("orders/$orderId"),
            "paymentDate" to FieldValue.serverTimestamp(),
            "paymentID" to "",
            "paymentMethod" to "Card"
        )

        db.collection("payments")
            .add(paymentMap)
            .addOnSuccessListener { documentReference ->
                val paymentId = documentReference.id
                db.collection("payments")
                    .document(paymentId)
                    .update("paymentID", paymentId)
                Log.d(TAG, "Payment added with ID: $paymentId")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding payment", e)
            }
    }
    fun getTotalSales(uid: String): LiveData<Double> {
        val totalSalesLiveData = MutableLiveData<Double>()
        // Get clients assigned to current user from managers collection
        val clientsAssignedRef = db.collection("managers").document(uid)
        clientsAssignedRef.get().addOnSuccessListener { managerDocument ->
            if (managerDocument != null && managerDocument.exists()) {
                val clientsAssigned = managerDocument.get("ClientsAssigned") as ArrayList<String>

                // Query payments collection to get total sales for clients assigned to current user
                var totalSales = 0.0
                db.collection("payments")
                    .get()
                    .addOnSuccessListener { paymentsQuerySnapshot ->
                        for (paymentDocument in paymentsQuerySnapshot.documents) {
                            val orderRef = paymentDocument.get("orderID") as DocumentReference
                            orderRef.get().addOnSuccessListener { orderDoc ->
                                val clientIdRef = orderDoc.get("clientID") as DocumentReference
                                if (clientsAssigned.contains(clientIdRef.id)) {
                                    val amount = paymentDocument.getDouble("amount")
                                    if (amount != null) {
                                        totalSales += amount
                                    }
                                }
                                totalSalesLiveData.value = totalSales
                            }
                        }
                    }

            } else {
                Log.d(TAG, "Manager document does not exist")
                totalSalesLiveData.value = 0.0
            }
        }.addOnFailureListener { e ->
            Log.w(TAG, "Error getting clients assigned to manager", e)
            totalSalesLiveData.value = 0.0
        }

        return totalSalesLiveData
    }
}