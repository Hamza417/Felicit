package app.simple.felicit.ui.sub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import app.simple.felicit.R
import app.simple.felicit.glide.modules.AudioCoverPagers.loadArtistImageForScreen

class SubArtist : Fragment() {
    fun newInstance(name: String, path: String): SubArtist {
        val args = Bundle()
        args.putString("artist_name", name)
        args.putString("file_path", path)
        val fragment = SubArtist()
        fragment.arguments = args
        return fragment
    }

    private lateinit var artist: ImageView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.sub_music_frag_artists, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        artist = view.findViewById(R.id.sub_music_artist_art)

        artist.loadArtistImageForScreen(arguments?.getString("artist_name")!!)
    }
}