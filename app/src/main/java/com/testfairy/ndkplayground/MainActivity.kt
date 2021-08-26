package com.testfairy.ndkplayground

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Process
import android.system.Os
import android.widget.Button
import ru.arvrlab.ndkcrashhandler.SignalHandler

import ru.ivanarh.jndcrash.NDCrash;
import ru.ivanarh.jndcrash.NDCrashError;
import ru.ivanarh.jndcrash.NDCrashUnwinder;

class MainActivity : Activity() {
    init {
        System.loadLibrary("ndk-crash-handler")
    }

    private val signalHandler = SignalHandler()

    // Used to load the 'native-lib' library on application startup.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startService()
        setContentView(R.layout.activity_main)

        // Example of a call to a native method
        val crashButton = findViewById<Button>(R.id.crash_button)
        crashButton.setOnClickListener {
            signalHandler.crashAndGetExceptionMessage(IllegalAccessException())
        }

        val reportPath = cacheDir.absolutePath + "/crash.txt"; // Example.
        val error = NDCrash.initializeInProcess(reportPath, NDCrashUnwinder.libunwind);
        if (error == NDCrashError.ok) {
            // Initialization is successful.
        } else {
            // Initialization failed, check error value.
        }
    }

    override fun onResume() {
        super.onResume()
        signalHandler.initSignalHandler()
    }

    override fun onPause() {
        super.onPause()
        signalHandler.deinitSignalHandler()
    }

    private fun startService() {
        val intent = Intent(this, CrashLogService::class.java)
        intent.putExtra(EXTRA_ACTIVITY_PPID, Os.getppid())
        intent.putExtra(EXTRA_ACTIVITY_PID, Process.myPid())
        stopService(intent)
        startService(intent)
    }
}