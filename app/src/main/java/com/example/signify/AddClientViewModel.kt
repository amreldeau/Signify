import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.signify.repository.FirestoreRepository
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AddClientViewModel : ViewModel() {

    private val repository = FirestoreRepository()

    fun addNewClient(email: String, password: String, managerUid: String, name: String) {
        repository.addNewClient(email, password, managerUid, name)
    }
}
