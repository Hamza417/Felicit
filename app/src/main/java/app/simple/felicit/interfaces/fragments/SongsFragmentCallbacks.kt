package app.simple.felicit.interfaces.fragments

import app.simple.felicit.medialoader.mediaHolders.AudioContent
import org.jetbrains.annotations.NotNull

interface SongsFragmentCallbacks {
    fun onSongClicked(@NotNull songs: MutableList<AudioContent>, @NotNull position: Int, id: Int?)
}