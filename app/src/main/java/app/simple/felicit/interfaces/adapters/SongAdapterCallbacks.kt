package app.simple.felicit.interfaces.adapters

import app.simple.felicit.medialoader.mediaHolders.AudioContent
import org.jetbrains.annotations.NotNull

interface SongAdapterCallbacks {
    fun onSongClicked(@NotNull songs: MutableList<AudioContent>, @NotNull position: Int, id: Int?)
}