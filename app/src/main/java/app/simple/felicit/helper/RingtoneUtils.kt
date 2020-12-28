package app.simple.felicit.helper

import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi

object RingtoneUtils {
    private const val LOG_TAG = "RingtoneUtils"
    fun setRingtone(context: Context, ringtoneUri: Uri): Boolean {
        Log.v(LOG_TAG, "Setting Ringtone to: $ringtoneUri")
        if (!hasMarshmallow()) {
            Log.v(LOG_TAG, "On a Lollipop or below device, so go ahead and change device ringtone")
            setActualRingtone(context, ringtoneUri)
            return true
        } else if (canEditSystemSettings(context)) {
            Log.v(LOG_TAG, "On a marshmallow or above device but app has the permission to edit system settings")
            setActualRingtone(context, ringtoneUri)
            return true
        } else if (hasMarshmallow() && !canEditSystemSettings(context)) {
            Log.d(LOG_TAG, "On android Marshmallow and above but app does not have permission to" +
                    " edit system settings. Opening the manage write settings activity...")
            startManageWriteSettingsActivity(context)
            Toast.makeText(context, "Please allow app to edit settings so your ringtone can be updated", Toast.LENGTH_LONG).show()
            return false
        }
        return false
    }

    private fun setActualRingtone(context: Context, ringtoneUri: Uri) {
        RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE, ringtoneUri)
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private fun startManageWriteSettingsActivity(context: Context) {
        val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
        // Passing in the app package here allows the settings app to open the exact app
        intent.data = Uri.parse("package:" + context.applicationContext.packageName)
        // Optional. If you pass in a service context without setting this flag, you will get an exception
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    private fun hasMarshmallow(): Boolean {
        // returns true if the device is Android Marshmallow or above, false otherwise
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private fun canEditSystemSettings(context: Context): Boolean {
        // returns true if the app can edit system settings, false otherwise
        return Settings.System.canWrite(context.applicationContext)
    }
}