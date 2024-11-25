package edu.appstate.cs.moments

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "MomentsListViewModel"

class MomentsListViewModel() : ViewModel() {
    private val momentsRepository = MomentsRepository.get()
    private val sharingRepository = SharingRepository()

    private val _uiState: MutableStateFlow<MomentsListUiState> = MutableStateFlow(MomentsListUiState())

    val uiState: StateFlow<MomentsListUiState>
        get() = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            momentsRepository.getMoments().collect {
                _uiState.update { oldState ->
                    oldState.copy(
                        moments = it
                    )
                }
            }
        }

        viewModelScope.launch {
            try {
                // TODO: This is a good place to load the moments using the API
            } catch (ex: Exception) {
                Log.e(TAG, "Failed to load moments list from API", ex)
            }
        }
    }

    suspend fun addMoment(moment: Moment) {
        momentsRepository.addMoment(moment)
    }
}

data class MomentsListUiState(
    val moments: List<Moment> = listOf(),
    val sharedMoments: List<Moment> = listOf()
)