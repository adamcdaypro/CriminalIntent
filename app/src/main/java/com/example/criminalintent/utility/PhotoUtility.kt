package com.example.criminalintent.utility

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.util.Log
import androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_180
import androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_270
import androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_90

private const val NINETY_DEGREES = 90f
private const val ONE_HUNDRED_EIGHTY_DEGREES = 180f
private const val TWO_HUNDRED_SEVENTY_DEGREES = 270f

object PhotoUtility {

    fun getScaledBitmap(path: String, destinationWidth: Int, destinationHeight: Int): Bitmap? {
        val bitmap = BitmapFactory.decodeFile(path) ?: return null
        val rotatedBitmap = rotateBitmap(path, bitmap)
        return Bitmap.createScaledBitmap(rotatedBitmap, destinationWidth, destinationHeight, false)
    }

    private fun rotateBitmap(path: String, bitmap: Bitmap): Bitmap {
        val exif = ExifInterface(path)
        val orientation: Int = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1)
        Log.d("EXIF", "Exif: $orientation")
        val matrix = Matrix()
        if (orientation == ORIENTATION_ROTATE_90) {
            matrix.postRotate(NINETY_DEGREES)
        } else if (orientation == ORIENTATION_ROTATE_180) {
            matrix.postRotate(ONE_HUNDRED_EIGHTY_DEGREES)
        } else if (orientation == ORIENTATION_ROTATE_270) {
            matrix.postRotate(TWO_HUNDRED_SEVENTY_DEGREES)
        }
        val rotatedBitmap = Bitmap.createBitmap(
            bitmap,
            0,
            0,
            bitmap.width,
            bitmap.height,
            matrix,
            true
        )

        bitmap.recycle()
        return rotatedBitmap

    }

}