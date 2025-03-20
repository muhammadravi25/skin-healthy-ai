package com.ravi.skinhealthyai.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.vision.classifier.Classifications
import org.tensorflow.lite.task.vision.classifier.ImageClassifier
import java.io.IOException

class ImageClassifierHelper(
    val modelName: String = "skin_diseases_model.tflite",
    val context: Context,
    val classifierListener: ClassifierListener?
) {

    private var imageClassifier: ImageClassifier? = null

    init {
        setupImageClassifier()
    }

    private fun setupImageClassifier() {
        try {
            imageClassifier = ImageClassifier.createFromFile(context, modelName)
        } catch (e: IOException) {
            classifierListener?.onError("Failed to create ImageClassifier: ${e.message}")
        }
    }

    fun classifyStaticImage(imageUri: Uri) {
        val bitmap = getBitmapFromUri(imageUri)

        bitmap?.let { bitmap ->
            try {
                // Memproses gambar menggunakan ImageClassifier
                val startTime = System.currentTimeMillis()
                val classifications = imageClassifier?.classify(TensorImage.fromBitmap(bitmap))

                // Memeriksa hasil klasifikasi
                if (classifications != null && classifications.isNotEmpty()) {
                    classifierListener?.onResults(classifications)
                } else {
                    classifierListener?.onError("No results found.")
                }
            } catch (e: Exception) {
                classifierListener?.onError("Error during classification: ${e.message}")
            }
        } ?: classifierListener?.onError("Error loading bitmap from Uri.")
    }


    @Suppress("DEPRECATION")
    private fun getBitmapFromUri(uri: Uri): Bitmap? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(context.contentResolver, uri)
                ImageDecoder.decodeBitmap(source)
            } else {
                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            }?.copy(Bitmap.Config.ARGB_8888, true)
        } catch (e: IOException) {
            classifierListener?.onError("Error loading bitmap from Uri: ${e.message}")
            null
        }
    }

    interface ClassifierListener {
        fun onError(error: String)
        fun onResults(
            results: List<Classifications>?
        )
    }
}