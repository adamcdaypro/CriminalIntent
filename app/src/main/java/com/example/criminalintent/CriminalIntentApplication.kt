package com.example.criminalintent

import android.app.Application
import com.example.criminalintent.database.CrimeDatabase

class CriminalIntentApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        CrimeDatabase.initialize(this)
    }
}