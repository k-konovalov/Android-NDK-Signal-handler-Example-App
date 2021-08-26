package com.testfairy.ndkplayground

import android.app.Application
import android.system.Os
import ru.arvrlab.ndkcrashhandler.SignalHandler

class MyApp: Application() {
    init {
        System.loadLibrary("ndk-crash-handler")
    }

    private val signalHandler = SignalHandler()

    override fun onCreate() {
        super.onCreate()

        signalHandler.createLogFile(Os.getppid(), cacheDir.absolutePath + "/")
    }
}