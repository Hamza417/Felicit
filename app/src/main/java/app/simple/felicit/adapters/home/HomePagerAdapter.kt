package app.simple.felicit.adapters.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.simple.felicit.R
import app.simple.felicit.decoration.customholders.HorizontalListViewHolder
import app.simple.felicit.glide.modules.AudioCoverPagers.loadCoverForPagerFromFile
import app.simple.felicit.models.AudioContent

class HomePagerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var songs: MutableList<AudioContent> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return Holder(LayoutInflater.from(parent.context).inflate(R.layout.adapter_home_pager, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is Holder) {
            holder.albumArt.loadCoverForPagerFromFile(songs[position].filePath)
            holder.title.text = songs[position].title
            holder.artist.text = songs[position].artist
        }
    }

    override fun getItemCount(): Int {
        return songs.size
    }

    inner class Holder(itemView: View) : HorizontalListViewHolder(itemView) {
        val albumArt: ImageView = itemView.findViewById(R.id.adapter_home_pager_album_art)
        val title: TextView = itemView.findViewById(R.id.adapter_home_pager_title)
        val artist: TextView = itemView.findViewById(R.id.adapter_home_pager_artist)

        init {
            title.isSelected = true
            artist.isSelected = true
        }
    }
}