package app.simple.felicit.adapters.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import app.simple.felicit.R
import app.simple.felicit.decoration.customholders.HorizontalListViewHolder
import app.simple.felicit.interfaces.adapters.Library

class LibraryIcons(private val library: Library) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return Holder(LayoutInflater.from(parent.context).inflate(R.layout.adapter_library, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is Holder) {
            holder.icon.setImageResource(libraryList[position].icon)
            holder.category.text = libraryList[position].category
            holder.layout.setOnClickListener {
                library.onLibraryIconClicked(position)
            }
        }
    }

    override fun getItemCount(): Int {
        return libraryList.size
    }

    inner class Holder(itemView: View) : HorizontalListViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.adapter_library_icon)
        val category: TextView = itemView.findViewById(R.id.adapter_library_category)
        val layout: LinearLayout = itemView.findViewById(R.id.adapter_library_layout)
    }

    companion object {
        private var libraryList = arrayListOf<LibraryModel>()

        init {
            libraryList.add(LibraryModel(R.drawable.ic_music_note, "Songs"))
            libraryList.add(LibraryModel(R.drawable.ic_person, "Artists"))
            libraryList.add(LibraryModel(R.drawable.ic_album, "Album"))
            libraryList.add(LibraryModel(R.drawable.ic_genre, "Genre"))
            libraryList.add(LibraryModel(R.drawable.ic_folder, "Folder"))
            libraryList.add(LibraryModel(R.drawable.ic_history, "History"))
        }

        private class LibraryModel() {
            var icon: Int = 0
            lateinit var category: String

            constructor(icon: Int, category: String) : this() {
                this.icon = icon
                this.category = category
            }
        }
    }
}