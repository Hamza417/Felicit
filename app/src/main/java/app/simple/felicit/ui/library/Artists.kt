package app.simple.felicit.ui.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.simple.felicit.R
import app.simple.felicit.adapters.library.ArtistAdapter
import app.simple.felicit.medialoader.mediaHolders.AudioContent

class Artists : Fragment() {

    fun newInstance(list: ArrayList<AudioContent>): Artists {
        val args = Bundle()
        args.putParcelableArrayList("all_artists", list)
        val fragment = Artists()
        fragment.arguments = args
        return fragment
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var artistAdapter: ArtistAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.music_frag_songs, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        artistAdapter = ArtistAdapter()
        artistAdapter.artists = arguments?.getParcelableArrayList("all_artists")!!

        recyclerView = view.findViewById(R.id.songs_recycler_view)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = artistAdapter
        recyclerView.scheduleLayoutAnimation()
    }
}