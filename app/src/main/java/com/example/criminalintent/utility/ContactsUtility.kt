package com.example.criminalintent.utility

import android.content.Context
import android.net.Uri
import android.provider.ContactsContract

object ContactsUtility {

    fun getSuspectFromContacts(uri: Uri?, context: Context): String {
        if (uri == null) return ""

        val queryFields = arrayOf(ContactsContract.Contacts.DISPLAY_NAME)
        val queryCursor = context.contentResolver.query(
            uri,
            queryFields,
            null,
            null,
            null
        )
        val suspect = queryCursor?.use { cursor ->
            if (cursor.moveToFirst()) cursor.getString(0) else ""
        }
        return suspect ?: ""
    }
}