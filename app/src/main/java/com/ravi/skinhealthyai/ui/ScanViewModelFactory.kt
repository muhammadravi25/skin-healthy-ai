package com.ravi.skinhealthyai.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ravi.skinhealthyai.ui.scan.ScanViewModel

class ScanViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ScanViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ScanViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}