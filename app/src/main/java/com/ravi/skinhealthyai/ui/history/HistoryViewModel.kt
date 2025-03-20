package com.ravi.skinhealthyai.ui.history

import androidx.lifecycle.*
import androidx.paging.PagedList
import com.ravi.skinhealthyai.data.model.History
import com.ravi.skinhealthyai.data.repository.HistoryRepository
import kotlinx.coroutines.launch

class HistoryViewModel(private val historyRepository: HistoryRepository) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _isEmpty = MutableLiveData<Boolean>()
    val isEmpty: LiveData<Boolean> get() = _isEmpty

    private val _historyData = MediatorLiveData<PagedList<History>>()
    val historyData: LiveData<PagedList<History>> get() = _historyData

    private val _selectedHistory = MutableLiveData<History?>()
    val selectedHistory: LiveData<History?> get() = _selectedHistory

    fun getAllHistory(start: Long? = null, end: Long? =  null) {
        _isLoading.value = true
        _isEmpty.value = false

        val source = historyRepository.getAllHistory(start, end)

        _historyData.addSource(source) { data ->
            _historyData.value = data
            _isLoading.value = false
            _isEmpty.value = data.isNullOrEmpty()
            _historyData.removeSource(source)
        }
    }

    fun getHistoryById(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            val data = historyRepository.getHistoryById(id)
            _selectedHistory.value = data
            _isEmpty.value = data == null
            _isLoading.value = false
        }
    }

}
