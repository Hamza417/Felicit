package app.simple.felicit.adapters.home

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.simple.felicit.R
import app.simple.felicit.decoration.customholders.HorizontalListViewHolder
import app.simple.felicit.glide.modules.AudioCoverUtil.loadFromUri
import app.simple.felicit.models.AudioContent

class RandomPicksAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var songs: MutableList<AudioContent> = arrayListOf()
    private lateinit var context: Context
    private var lastPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return Holder(LayoutInflater.from(parent.context).inflate(R.layout.adapter_home_random_picks, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is Holder) {
            holder.albumArt.loadFromUri(holder.itemView.context, Uri.parse(songs[position].artUri))
            holder.title.text = songs[position].title
            holder.artist.text = songs[position].artist

            val animation: Animation = AnimationUtils.loadAnimation(context,
                                                                    if (position > lastPosition)
                                                                        R.anim.list_right_to_left else R.anim.list_left_to_right)
            holder.itemView.startAnimation(animation)
            lastPosition = position
        }
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.itemView.clearAnimation()
    }

    override fun getItemCount(): Int {
        return songs.size
    }

    inner class Holder(itemView: View) : HorizontalListViewHolder(itemView) {
        val albumArt: ImageView = itemView.findViewById(R.id.random_picks_album_art)
        val title: TextView = itemView.findViewById(R.id.random_picks_title)
        val artist: TextView = itemView.findViewById(R.id.random_picks_artists)
    }
}