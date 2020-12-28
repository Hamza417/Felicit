package app.simple.felicit.util

import android.content.Context
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager

object IntentHelper {
    fun sendLocalBroadcastIntent(intentAction: String, context: Context) {
        Intent().also { intent ->
            intent.action = intentAction
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
        }
    }
}