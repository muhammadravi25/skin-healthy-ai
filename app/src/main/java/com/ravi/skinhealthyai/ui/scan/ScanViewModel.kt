package com.ravi.skinhealthyai.ui.scan

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ravi.skinhealthyai.data.di.Event
import com.ravi.skinhealthyai.utils.ImageClassifierHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.tensorflow.lite.task.vision.classifier.Classifications

class ScanViewModel(application: Application) : AndroidViewModel(application) {

    private val _classificationResult = MutableLiveData<Event<List<Classifications>>>()
    val classificationResult: LiveData<Event<List<Classifications>>> get() = _classificationResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val imageClassifierHelper = ImageClassifierHelper(application.applicationContext)

    fun classifyImage(imageUri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.postValue(true)
            val result = imageClassifierHelper.classifyStaticImage(imageUri)
            result?.let {
                _classificationResult.postValue(Event(it))
            }
            _isLoading.postValue(false)
        }
    }

    override fun onCleared() {
        super.onCleared()
        imageClassifierHelper.closeClassifier()
    }
}
