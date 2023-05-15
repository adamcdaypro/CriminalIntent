package com.example.criminalintent.utility

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts

object IntentResolverUtility {

    fun actionSendEnabled(context: Context): Boolean {
        val intent = Intent(Intent.ACTION_SEND).apply { type = "text/plain" }
        return canResolveIntent(intent, context)
    }

    fun pickContactEnabled(context: Context): Boolean {
        val intent = ActivityResultContracts.PickContact().createIntent(context, null)
        return canResolveIntent(intent, context)
    }

    fun takePictureEnabled(context: Context): Boolean {
        val intent = ActivityResultContracts.TakePicture().createIntent(context, Uri.parse(""))
        return canResolveIntent(intent, context)
    }

    private fun canResolveIntent(intent: Intent, context: Context): Boolean {
        val packageManager = context.packageManager
        val resolvedActivity = packageManager.resolveActivity(
            intent,
            PackageManager.MATCH_DEFAULT_ONLY
        )
        return resolvedActivity != null
    }
}