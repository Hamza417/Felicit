package app.simple.felicit.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.room.Room
import app.simple.felicit.R
import app.simple.felicit.database.SongDatabase
import app.simple.felicit.medialoader.AudioGet
import app.simple.felicit.medialoader.MediaLoader
import kotlinx.coroutines.*
import java.util.*

class LauncherActivity : AppCompatActivity() {

    private lateinit var loader: ImageView
    private lateinit var icon: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)

        loader = findViewById(R.id.launcher_loader_icon)

        loader.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate_infinte))

        checkRunTimePermission()
    }

    private fun checkRunTimePermission() {
        if (ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ), DEFAULT_PERMISSION_REQUEST_CODE)

        } else {
            updateDatabase()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        var permission = true
        if (requestCode == DEFAULT_PERMISSION_REQUEST_CODE) {
            for (i in grantResults.indices) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    permission = false
                } else {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) &&
                        ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    }
                }
            }

            if (permission) {
                updateDatabase()
            }
        }
    }

    private fun updateDatabase() {
        CoroutineScope(Dispatchers.IO).launch {
            val list = MediaLoader.withAudioContext(applicationContext)?.getAllAudioContent(AudioGet.externalContentUri)
            val db = Room.databaseBuilder(applicationContext, SongDatabase::class.java, "all_songs.db").build()

            if (list != null) {
                db.songDao()?.insertSong(list)
            }

            withContext(Dispatchers.Main) {
                val intent = Intent(this@LauncherActivity, MainActivity::class.java)
                startActivity(intent)
                this@LauncherActivity.finish()
            }
        }
    }

    companion object {
        var DEFAULT_PERMISSION_REQUEST_CODE = 123
    }
}