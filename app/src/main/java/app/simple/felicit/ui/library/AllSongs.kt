package app.simple.felicit.ui.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import app.simple.felicit.R
import app.simple.felicit.adapters.library.SongsAdapter
import app.simple.felicit.decoration.bouncescroll.RecyclerViewVerticalElasticScroll.setupEdgeEffectFactory
import app.simple.felicit.decoration.fastscroll.fastScroller
import app.simple.felicit.decoration.itemdecorator.VerticalMarginItemDecoration
import app.simple.felicit.decoration.views.CustomRecyclerView
import app.simple.felicit.interfaces.adapters.SongAdapterCallbacks
import app.simple.felicit.interfaces.fragments.SongsFragmentCallbacks
import app.simple.felicit.medialoader.mediaHolders.AudioContent
import kotlinx.coroutines.*

class AllSongs : Fragment(), SongAdapterCallbacks {

    fun newInstance(list: ArrayList<AudioContent>): AllSongs {
        val args = Bundle()
        args.putParcelableArrayList("all_songs", list)
        val fragment = AllSongs()
        fragment.arguments = args
        return fragment
    }

    private lateinit var songsRecyclerView: CustomRecyclerView
    private lateinit var songsAdapter: SongsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.music_frag_songs, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        songsRecyclerView = view.findViewById(R.id.songs_recycler_view)
        songsRecyclerView.setHasFixedSize(true)
        songsRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        songsRecyclerView.addItemDecoration(VerticalMarginItemDecoration(resources.getDimensionPixelOffset(R.dimen.vertical_list_item_margin)))

        songsAdapter = SongsAdapter()
        songsAdapter.songAdapterCallbacks = this@AllSongs
        if(arguments != null) songsAdapter.songs = arguments!!.getParcelableArrayList("all_songs")!!

        songsRecyclerView.adapter = songsAdapter
        songsRecyclerView.scheduleLayoutAnimation()
        fastScroller(songsRecyclerView)

        songsRecyclerView.setupEdgeEffectFactory<SongsAdapter.HeaderHolder, SongsAdapter.ListHolder>()
    }

    override fun onDestroy() {
        super.onDestroy()
        songsRecyclerView.clearAnimation()
    }

    override fun onSongClicked(songs: MutableList<AudioContent>, position: Int, id: Int?) {
        (requireActivity() as SongsFragmentCallbacks).onSongClicked(songs, position, id)
    }
}