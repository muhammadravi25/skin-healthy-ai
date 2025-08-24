package com.ravi.skinhealthyai.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

object ImageHelper {
    /**
     * ✅ Copy file dari Uri ke internal storage sementara,
     * compress, hapus file sementara, lalu return path file hasil compress
     */
    fun copyAndCompressImageFromUri(
        context: Context,
        uri: Uri,
        quality: Int = 80
    ): String? {
        val tempFile: File
        return try {
            // 1. Copy ke file sementara
            val tempFileName = "temp_${System.currentTimeMillis()}.jpg"
            tempFile = File(context.cacheDir, tempFileName)

            when (uri.scheme) {
                "content" -> {
                    context.contentResolver.openInputStream(uri)?.use { input ->
                        FileOutputStream(tempFile).use { output ->
                            input.copyTo(output)
                        }
                    }
                }
                "file" -> {
                    val sourceFile = File(uri.path ?: return null)
                    if (sourceFile.exists()) {
                        sourceFile.copyTo(tempFile, overwrite = true)
                    } else {
                        return null
                    }
                }
                else -> return null
            }

            // 2. Decode file sementara ke Bitmap
            val bitmap = BitmapFactory.decodeFile(tempFile.absolutePath)
                ?: return null

            // 3. Simpan bitmap ke internal storage (compress)
            val finalFileName = "${System.currentTimeMillis()}.jpg"
            val finalFile = File(context.filesDir, finalFileName)
            FileOutputStream(finalFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)
            }

            // 4. Hapus file sementara
            tempFile.delete()

            // Return path file hasil compress
            finalFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * ✅ Hapus file gambar
     */
    fun deleteImageFromInternalStorage(path: String): Boolean {
        return try {
            val file = File(path)
            file.exists() && file.delete()
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}