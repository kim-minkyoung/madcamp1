import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.myapplication.model.data.Contact
import com.example.myapplication.model.repository.ContactRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ContactViewModel(application: Application) : AndroidViewModel(application) {

    private val contactRepository = ContactRepository
    val allContacts = MutableLiveData<List<Contact>>()

    init {
        // 최초 데이터 로드
        loadAllContacts()
    }

    fun loadAllContacts() {
        viewModelScope.launch {
            if (contactRepository.contacts.isEmpty()) {
                // 최초 한 번만 데이터 로드
                contactRepository.loadAllContacts(getApplication())
            }
            allContacts.value = contactRepository.getFavoriteContacts()
        }
    }
}
