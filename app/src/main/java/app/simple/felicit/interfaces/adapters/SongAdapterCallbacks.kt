package app.simple.felicit.interfaces.adapters

import androidx.annotation.NonNull
import app.simple.felicit.models.AudioContent
import org.jetbrains.annotations.NotNull

interface SongAdapterCallbacks {
    fun onSongClicked(@NotNull songs: MutableList<AudioContent>, @NotNull position: Int, id: Int?)
    fun onOptionsPressed(@NonNull song: AudioContent)
}