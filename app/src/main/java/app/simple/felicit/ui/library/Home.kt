package app.simple.felicit.ui.library

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import androidx.viewpager2.widget.ViewPager2
import app.simple.felicit.R
import app.simple.felicit.adapters.home.HomePagerAdapter
import app.simple.felicit.adapters.home.LibraryIcons
import app.simple.felicit.adapters.home.RandomPicksAdapter
import app.simple.felicit.database.SongDatabase
import app.simple.felicit.decoration.itemdecorator.HorizontalMarginItemDecoration
import app.simple.felicit.interfaces.fragments.FragmentNavigator
import app.simple.felicit.interfaces.adapters.Library
import app.simple.felicit.medialoader.mediamodels.AudioContent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Home : Fragment(), Library {

    fun newInstance(): Home {
        val args = Bundle()
        val fragment = Home()
        fragment.arguments = args
        return fragment
    }

    private lateinit var songs: ArrayList<AudioContent>
    private lateinit var artists: ArrayList<AudioContent>

    private lateinit var randomPicksRecyclerView: RecyclerView
    private lateinit var homePager: ViewPager2
    private lateinit var libraryIcons: RecyclerView
    private lateinit var randomPicksAdapter: RandomPicksAdapter
    private lateinit var homePagerAdapter: HomePagerAdapter
    private var fragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.music_frag_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        randomPicksAdapter = RandomPicksAdapter()
        randomPicksRecyclerView = view.findViewById(R.id.random_picks_recycler_view)
        randomPicksRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        randomPicksRecyclerView.setHasFixedSize(true)
        randomPicksRecyclerView.addItemDecoration(HorizontalMarginItemDecoration(resources.getDimensionPixelOffset(R.dimen.horizontal_list_item_margin)))
        //randomPicksRecyclerView.setupHorizontalEdgeEffectFactory<RandomPicksAdapter.Holder>()
        randomPicksRecyclerView.adapter = randomPicksAdapter
        randomPicksRecyclerView.scheduleLayoutAnimation()

        homePagerAdapter = HomePagerAdapter()
        homePager = view.findViewById(R.id.home_pager)
        homePager.adapter = homePagerAdapter

        libraryIcons = view.findViewById(R.id.library_icons_recycler_view)
        libraryIcons.setHasFixedSize(true)
        libraryIcons.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        libraryIcons.addItemDecoration(HorizontalMarginItemDecoration(resources.getDimensionPixelOffset(R.dimen.horizontal_list_item_margin)))
        //libraryIcons.setupHorizontalEdgeEffectFactory<LibraryIcons.Holder>()
        libraryIcons.adapter = LibraryIcons(this as Library)

        try {
            initRandomPicks(requireContext())
        } catch (e: java.lang.IllegalStateException) {
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        initRandomPicks(context)
    }

    private fun initRandomPicks(context: Context) {
        try {
            CoroutineScope(Dispatchers.Default).launch {

                val db = Room.databaseBuilder(context, SongDatabase::class.java, "all_songs.db").build()
                val freshList = db.songDao()?.getSongLinearList()

                songs = freshList!! as ArrayList<AudioContent>
                artists = db.songDao()?.getArtistList() as ArrayList<AudioContent>

                val database = Room.databaseBuilder(context, SongDatabase::class.java, "random_songs.db").build()

                randomPicksAdapter.songs = db.songDao()?.getSongRandomList()!!
                homePagerAdapter.songs = db.songDao()?.getSongRandomList()!!

                db.close()
                database.close()

                withContext(Dispatchers.Main) {
                    randomPicksAdapter.notifyDataSetChanged()
                    homePagerAdapter.notifyDataSetChanged()
                }
            }
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }

    override fun onLibraryIconClicked(id: Int) {
        when (id) {
            0 -> {
                (requireActivity() as FragmentNavigator).navigateTo(id, songs)
            }
            1 -> {
                (requireActivity() as FragmentNavigator).navigateTo(id, artists)
            }
            2 -> {
            }
            3 -> {
            }
        }
    }
}