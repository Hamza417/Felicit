package app.simple.felicit.interfaces.fragments

import androidx.annotation.NonNull
import app.simple.felicit.models.AudioContent
import org.jetbrains.annotations.NotNull

interface SongsFragmentCallbacks {
    fun onSongClicked(@NotNull songs: MutableList<AudioContent>, @NotNull position: Int, id: Int?)
    fun onOptionsPressed(@NonNull song: AudioContent)
}