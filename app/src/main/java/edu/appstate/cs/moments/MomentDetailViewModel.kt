package edu.appstate.cs.moments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID

class MomentDetailViewModel(momentId: UUID): ViewModel() {
    private val momentsRepository = MomentsRepository.get()
    private val sharingRepository = SharingRepository()

    private val _moment: MutableStateFlow<Moment?> = MutableStateFlow(null)
    val moment: StateFlow<Moment?> = _moment.asStateFlow()

//    private val _imageUrls: MutableStateFlow<List<String>> = MutableStateFlow(emptyList())
//    val imageUrls: StateFlow<List<String>> = _imageUrls.asStateFlow()

    init {
        viewModelScope.launch {
            _moment.value = momentsRepository.getMoment(momentId)
        }
    }

    fun updateMoment(onUpdate: (Moment) -> Moment) {
        _moment.update { oldMoment ->
            oldMoment?.let {
                onUpdate(it)
            }
        }
    }

    fun deleteMoment() {
        moment.value?.let {
            momentsRepository.deleteMoment(it)
        }
        _moment.value = null
    }

    fun shareMomentUsingAPI() {
        moment.value?.let {
            viewModelScope.launch {
                sharingRepository.shareMoment(it)
            }
        }
    }

    fun shareMomentFile(file: File) {
        viewModelScope.launch {
            sharingRepository.shareMomentFile(file)
        }
    }

    override fun onCleared() {
        super.onCleared()
        moment.value?.let { momentsRepository.updateMoment(it) }
    }
}

class MomentDetailViewModelFactory(private val momentId: UUID) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MomentDetailViewModel(momentId) as T
    }
}