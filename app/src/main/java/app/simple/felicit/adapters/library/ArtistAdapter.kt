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
import app.simple.felicit.glide.modules.AudioCoverUtil.loadFromUri
import app.simple.felicit.interfaces.sub.ArtistCallbacks
import app.simple.felicit.medialoader.mediamodels.AudioContent

class ArtistAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var artists = arrayListOf<AudioContent>()
    lateinit var artistCallbacks: ArtistCallbacks

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            SongsAdapter.TYPE_ITEM -> {
                ListHolder(LayoutInflater.from(parent.context).inflate(R.layout.adapter_list_artists, parent, false))
            }
            SongsAdapter.TYPE_HEADER -> {
                HeaderHolder(LayoutInflater.from(parent.context).inflate(R.layout.header_artists, parent, false))
            }
            else -> {
                throw RuntimeException("there is no type that matches the type $viewType + make sure your using types correctly")
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ListHolder) {
            holder.name.text = artists[position].artist
            holder.photo.loadFromUri(holder.itemView.context, Uri.parse(artists[position].artUri))
            holder.layout.setOnClickListener {
                artistCallbacks.onArtistClicked(artists[position].artist, artists[position].filePath)
            }
        } else if (holder is HeaderHolder) {
            holder.total.text = "TOTAL ${artists.size}"
        }
    }

    override fun getItemCount(): Int {
        return artists.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            SongsAdapter.TYPE_HEADER
        } else SongsAdapter.TYPE_ITEM
    }

    inner class HeaderHolder(itemView: View) : VerticalListViewHolder(itemView) {
        val total: TextView = itemView.findViewById(R.id.adapter_artists_total_count)
        val menu: ImageButton = itemView.findViewById(R.id.adapter_artists_main_menu)
    }

    inner class ListHolder(itemView: View) : VerticalListViewHolder(itemView) {
        val photo: ImageView = itemView.findViewById(R.id.adapter_artist_art)
        val name: TextView = itemView.findViewById(R.id.adapter_artist_name)
        val layout: LinearLayout = itemView.findViewById(R.id.adapter_artist_layout)
    }
}