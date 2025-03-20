package com.ravi.skinhealthyai.ui.scan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ravi.skinhealthyai.data.model.History
import com.ravi.skinhealthyai.data.repository.HistoryRepository
import kotlinx.coroutines.launch

class ResultScanViewModel(private val historyRepository: HistoryRepository) : ViewModel() {
    fun insertData(history: History) {
        viewModelScope.launch {
            historyRepository.insertHistory(history)
        }
    }
}