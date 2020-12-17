package app.simple.felicit.adapters.library

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.simple.felicit.R
import app.simple.felicit.decoration.customholders.VerticalListViewHolder
import app.simple.felicit.glide.modules.loadFromUri
import app.simple.felicit.medialoader.mediaHolders.AudioContent

class ArtistAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var artists = arrayListOf<AudioContent>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return Holder(LayoutInflater.from(parent.context).inflate(R.layout.adapter_artist_list, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is Holder) {
            holder.name.text = artists[position].artist
            holder.photo.loadFromUri(holder.itemView.context, Uri.parse(artists[position].artUri))
        }
    }

    override fun getItemCount(): Int {
        return artists.size
    }

    inner class Holder(itemView: View) : VerticalListViewHolder(itemView) {
        val photo: ImageView = itemView.findViewById(R.id.adapter_artist_art)
        val name: TextView = itemView.findViewById(R.id.adapter_artist_name)
    }
}