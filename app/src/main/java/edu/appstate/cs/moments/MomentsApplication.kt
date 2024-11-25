package edu.appstate.cs.moments

import android.app.Application

class MomentsApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        MomentsRepository.initialize(this)
    }
}