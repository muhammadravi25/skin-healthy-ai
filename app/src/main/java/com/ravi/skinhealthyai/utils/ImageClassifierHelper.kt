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
    private val context: Context,
    private val modelName: String = "model_with_metadata.tflite"
) {
    private var imageClassifier: ImageClassifier? = null

    init {
        setupImageClassifier()
    }

    private fun setupImageClassifier() {
        imageClassifier = try {
            ImageClassifier.createFromFile(context, modelName)
        } catch (e: IOException) {
            null
        }
    }

    fun classifyStaticImage(imageUri: Uri): List<Classifications>? {
        val bitmap = getBitmapFromUri(imageUri) ?: return null

        return try {
            imageClassifier?.classify(TensorImage.fromBitmap(bitmap))
        } catch (e: Exception) {
            null
        }
    }

    fun closeClassifier() {
        imageClassifier?.close()
        imageClassifier = null
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
            null
        }
    }
}

