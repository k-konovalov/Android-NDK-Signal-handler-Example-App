package com.testfairy.ndkplayground

import android.app.Service
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.IBinder
import android.os.Process
import android.system.Os
import android.util.Log
import ru.arvrlab.ndkcrashhandler.SignalWatcher

const val EXTRA_ACTIVITY_PID = "EXTRA_ACTIVITY_PID"
const val EXTRA_ACTIVITY_PPID = "EXTRA_ACTIVITY_PPID"
class CrashLogService : Service() {
    init {
        System.loadLibrary("ndk-crash-handler")
    }
    private val signalWatcher = SignalWatcher()
    private val actionAfterSignalError = SignalWatcher.ActionAfterError {
        val activityIntent = Intent(this@CrashLogService, MainActivity::class.java)
        activityIntent.addFlags(FLAG_ACTIVITY_NEW_TASK)
        startActivity(activityIntent)
    }

    override fun onBind(intent: Intent): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        logPids(intent)
        setupAndLaunchSignalWatcher()
        return START_STICKY
    }

    private fun logPids(intent: Intent?) {
        val activityPid = intent?.getIntExtra(EXTRA_ACTIVITY_PID, 0) ?: 0
        val activityPpid = intent?.getIntExtra(EXTRA_ACTIVITY_PPID, 0) ?: 0
        val servicePid = Process.myPid()
        Log.e(
            this.javaClass.simpleName,
            "Service with PID: $servicePid watch over Activity with PID $activityPid in APP with PPID ${Os.getppid()}"
        )
    }

    private fun setupAndLaunchSignalWatcher(){
        signalWatcher.actionAfterError = actionAfterSignalError
        signalWatcher.startCrashHadler()
    }

}