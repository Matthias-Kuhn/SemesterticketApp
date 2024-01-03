package de.makuhn.semesterticket

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object BitmapStorageHelper {

     fun saveBitmapToInternalStorage(context: Context, filename: String, bitmap: Bitmap) {
        try {
            context.openFileOutput(filename, AppCompatActivity.MODE_PRIVATE).use { stream ->
                if(!bitmap.compress(Bitmap.CompressFormat.JPEG, 75, stream)){
                    throw IOException("Unable to save bitmap")
                }
            }
        } catch (e:Exception){
            e.printStackTrace()
        }
    }

    suspend fun loadFilesFromInternalStorage(context: Context): List<Bitmap> {
        return withContext(Dispatchers.IO) {
            val files = context.filesDir.listFiles()
            files?.filter { file -> file.canRead() && file.isFile && file.name.endsWith(".jpg") }?.map {
                val imageBytes = it.readBytes()
                BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            } ?: listOf()
        }
    }

    fun deleteFileFromInternalStorage(context: Context, filename: String): Boolean {
        return try {
            context.deleteFile(filename)
        } catch(e: IOException) {
            e.printStackTrace()
            false
        }
    }
}