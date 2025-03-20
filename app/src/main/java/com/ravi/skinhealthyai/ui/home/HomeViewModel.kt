package com.ravi.skinhealthyai.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ravi.skinhealthyai.data.model.History
import com.ravi.skinhealthyai.data.repository.HistoryRepository
import kotlinx.coroutines.launch

class HomeViewModel(private val historyRepository: HistoryRepository) : ViewModel() {

    private val _lastThreeHistory = MutableLiveData<List<History>>()
    val lastThreeHistory: LiveData<List<History>> get() = _lastThreeHistory

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _isEmpty = MutableLiveData<Boolean>()
    val isEmpty: LiveData<Boolean> get() = _isEmpty

    fun fetchLastThreeHistory() {
        viewModelScope.launch {
            historyRepository.getLastThreeHistory().observeForever { historyList ->
                _isLoading.value = true
                _lastThreeHistory.value = historyList
                _isEmpty.value = historyList.isNullOrEmpty()
                _isLoading.value = false
            }
        }
    }
}