import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.myapplication.model.data.Contact
import com.example.myapplication.model.repository.ContactRepository
import com.example.myapplication.view.fragment.Tab1Fragment
import kotlinx.coroutines.launch

class ContactViewModel(application: Application) : AndroidViewModel(application) {

    private val contactRepository = ContactRepository

    // LiveData를 사용하여 UI에 반영될 데이터를 관리
    val favoriteContacts = MutableLiveData<List<Contact>>()
    private var contacts: List<Contact> = mutableListOf()


    init {
        loadFavoriteContacts() // 초기화 시 데이터 로드
    }

    fun loadFavoriteContacts() {
        viewModelScope.launch {
            try {
                // 데이터 로드를 백그라운드에서 실행
                contactRepository.loadAllContacts(getApplication())
                contacts = contactRepository.getFavoriteContacts()
                favoriteContacts.value = contacts // LiveData에 데이터 설정
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
