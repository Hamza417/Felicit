package app.simple.felicit.adapters.library

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.simple.felicit.R
import app.simple.felicit.decoration.customholders.VerticalListViewHolder
import app.simple.felicit.decoration.fastscroll.PopupTextProvider
import app.simple.felicit.glide.modules.AudioCoverUtil.loadFromUri
import app.simple.felicit.glide.modules.GlideApp
import app.simple.felicit.interfaces.adapters.SongAdapterCallbacks
import app.simple.felicit.medialoader.mediamodels.AudioContent
import app.simple.felicit.util.NumberHelper.getFormattedTime
import app.simple.felicit.util.UriHelper.getFileExtension

class SongsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(), PopupTextProvider {

    var songs: MutableList<AudioContent> = arrayListOf()
    lateinit var songAdapterCallbacks: SongAdapterCallbacks
    var itemPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_ITEM -> {
                ListHolder(LayoutInflater.from(parent.context).inflate(R.layout.adapter_list_songs, parent, false))
            }
            TYPE_HEADER -> {
                HeaderHolder(LayoutInflater.from(parent.context).inflate(R.layout.header_song, parent, false))
            }
            else -> {
                throw RuntimeException("there is no type that matches the type $viewType + make sure your using types correctly")
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position_: Int) {
        if (holder is ListHolder) {
            val position = position_ - 1

            holder.title.text = songs[position].title
            holder.artist.text = songs[position].artist

            holder.extension.text = StringBuilder()
                .append(getFormattedTime(songs[position].duration))
                .append("  |  ")
                .append(getFileExtension(holder.itemView.context, Uri.parse(songs[position].fileStringUri)))

            holder.image.loadFromUri(holder.itemView.context, Uri.parse(songs[position].artUri))

            itemPosition = position

            holder.layout.setOnClickListener {
                songAdapterCallbacks.onSongClicked(songs, position, null)
            }

            holder.menu.setOnClickListener {
                songAdapterCallbacks.onOptionsPressed(songs[position])
            }
        }

        if (holder is HeaderHolder) {
            holder.total.text = "${songs.size} SONGS"
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            TYPE_HEADER
        } else TYPE_ITEM
    }

    override fun getItemCount(): Int {
        return songs.size + 1
    }

    override fun getPopupText(): String {
        return songs[itemPosition].title.substring(0, 1)
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        GlideApp.get(holder.itemView.context).clearMemory()
    }

    inner class HeaderHolder(itemView: View) : VerticalListViewHolder(itemView) {
        val total: TextView = itemView.findViewById(R.id.adapter_songs_total_count)
        val shuffle: ImageButton = itemView.findViewById(R.id.adapter_songs_shuffle)
        val menu: ImageButton = itemView.findViewById(R.id.adapter_songs_main_menu)
    }

    inner class ListHolder(itemView: View) : VerticalListViewHolder(itemView) {
        val layout: LinearLayout = itemView.findViewById(R.id.song_adapter_layout)
        val title: TextView = itemView.findViewById(R.id.song_adapter_title)
        val artist: TextView = itemView.findViewById(R.id.song_adapter_artist)
        val image: ImageView = itemView.findViewById(R.id.song_adapter_image)
        val extension: TextView = itemView.findViewById(R.id.song_adapter_extension)
        val menu: ImageButton = itemView.findViewById(R.id.song_adapter_menu)
    }

    companion object {
        const val TYPE_HEADER = 0
        const val TYPE_ITEM = 1
    }
}