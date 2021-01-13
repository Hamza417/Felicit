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
import app.simple.felicit.decoration.bouncescroll.RecyclerViewVerticalElasticScroll.setupEdgeEffectFactory
import app.simple.felicit.interfaces.sub.ArtistCallbacks
import app.simple.felicit.models.AudioContent
import app.simple.felicit.ui.sub.SubArtist

class Artists : Fragment(), ArtistCallbacks {

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
        artistAdapter.artistCallbacks = this

        val gridLayoutManager = GridLayoutManager(requireContext(), 2)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (position == 0) 2 else 1
            }
        }

        recyclerView = view.findViewById(R.id.songs_recycler_view)
        recyclerView.layoutManager = gridLayoutManager
        //recyclerView.addItemDecoration(SpacingItemDecoration(resources.getDimensionPixelOffset(R.dimen.vertical_list_item_margin), 2))
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = artistAdapter
        recyclerView.scheduleLayoutAnimation()
        recyclerView.setupEdgeEffectFactory<ArtistAdapter.HeaderHolder, ArtistAdapter.ListHolder>()
    }

    override fun onArtistClicked(name: String, path: String) {
        // TODO - test if a bug exist here
        val frag = requireFragmentManager().findFragmentByTag("sub_artists")

        if (frag != null) {
            requireFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.dialog_in, R.anim.dialog_out, R.anim.dialog_in, R.anim.dialog_out)
                .replace(R.id.fragment_navigator, frag, "sub_artists")
                .addToBackStack(tag)
                .commit()
        } else {
            requireFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.dialog_in, R.anim.dialog_out, R.anim.dialog_in, R.anim.dialog_out)
                .replace(R.id.fragment_navigator, SubArtist().newInstance(name, path), "sub_artists")
                .addToBackStack(tag)
                .commit()
        }
    }
}