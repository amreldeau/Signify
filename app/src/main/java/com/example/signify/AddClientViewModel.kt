import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AddClientViewModel : ViewModel() {

    private val auth = Firebase.auth
    private val db = Firebase.firestore

    private val _message = MutableLiveData<String>()
    val message: LiveData<String>
        get() = _message

    fun addNewClient(email: String, password: String, managerUid: String) {
        val completionSource = TaskCompletionSource<Void>()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                // Return the user UID when the account is created successfully
                val newClientUid = authResult.user!!.uid
                // Save the new user UID to an authorization collection
                val authorizationCollectionRef = db.collection("authorization")
                val authorizationDocumentRef = authorizationCollectionRef.document(newClientUid)
                val authorizationData = mapOf(
                    "authorizedBy" to managerUid,
                    "status" to "client"
                )
                authorizationDocumentRef.set(authorizationData)
                    .addOnSuccessListener {
                        // Both tasks have completed successfully, so complete the
                        // `completionSource` task to trigger the success listener.
                        completionSource.setResult(null)
                    }
                    .addOnFailureListener { exception ->
                        // Handle any errors that occur during data creation
                        completionSource.setException(exception)
                    }
                // Add the new user UID as a field to the `client_to_manager` collection
                val clientToManagerCollectionRef = db.collection("client_to_manager")
                val managerDocumentRef = clientToManagerCollectionRef.document(managerUid)
                managerDocumentRef.update("clients_id", FieldValue.arrayUnion(newClientUid))
                    .addOnFailureListener { exception ->
                        // Handle any errors that occur during data creation
                        completionSource.setException(exception)
                    }
            }
            .addOnFailureListener { exception ->
                // Handle any errors that occur during account creation
                completionSource.setException(exception)
            }

        // Wait for the completion source task to complete before returning
        completionSource.task.addOnSuccessListener {
            // Handle the success case
            _message.value = "success"
        }.addOnFailureListener { exception ->
            // Handle any errors that occur during user creation or data creation
            _message.value = "error"
        }
    }

}
