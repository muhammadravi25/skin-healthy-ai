package com.ravi.skinhealthyai.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ravi.skinhealthyai.data.di.Injection
import com.ravi.skinhealthyai.data.repository.HistoryRepository
import com.ravi.skinhealthyai.ui.history.HistoryViewModel
import com.ravi.skinhealthyai.ui.home.HomeViewModel
import com.ravi.skinhealthyai.ui.scan.ResultScanViewModel

class ViewModelFactory(private val historyRepository: HistoryRepository): ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HistoryViewModel::class.java) -> {
                HistoryViewModel(historyRepository) as T
            }
            modelClass.isAssignableFrom(ResultScanViewModel::class.java) -> {
                ResultScanViewModel(historyRepository) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(historyRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null
        @JvmStatic
        fun getInstance(context: Context): ViewModelFactory {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ViewModelFactory(
                    Injection.provideHistoryRepository(context)
                ).also { INSTANCE = it }
            }
        }
    }
}