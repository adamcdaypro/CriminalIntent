package com.example.criminalintent.filemanager

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import java.io.File
import java.util.Date

object PhotoFileManager {

    fun createPhotoFile(context: Context): File {
        val photoName = "IMG_${Date()}.JPG"
        return File(context.applicationContext.filesDir, photoName)
    }

    fun createPhotoUri(photoFile: File, context: Context): Uri {
        return FileProvider.getUriForFile(
            context,
            "com.example.criminalintent.fileProvider",
            photoFile
        )
    }

    fun deletePhoto(photoFileName: String, context: Context) {
        Log.d("TEST", File(context.applicationContext.filesDir, photoFileName).name)
        File(context.applicationContext.filesDir, photoFileName).delete()
    }

}