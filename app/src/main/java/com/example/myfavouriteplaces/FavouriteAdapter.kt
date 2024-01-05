package com.example.myfavouriteplaces

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FavouritesAdapter (context: Context, val listOfFavourites: MutableList<Place>): RecyclerView.Adapter<FavouritesAdapter.ViewHolder>() {

    private val layoutInflater = LayoutInflater.from(context)

    var onCardClick: ((Place) -> Unit)? = null
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvTitle: TextView = itemView.findViewById(R.id.tvCardTitle)
        var tvDesc: TextView = itemView.findViewById(R.id.tvCardDesc)
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FavouritesAdapter.ViewHolder {

        val itemView = layoutInflater.inflate(R.layout.favourite_item, parent, false)


        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FavouritesAdapter.ViewHolder, position: Int) {
        holder.tvTitle.text = listOfFavourites[position].title
        holder.tvDesc.text = listOfFavourites[position].description

        holder.itemView.setOnClickListener {
            val place = listOfFavourites[position]
            onCardClick?.invoke(place)
        }
    }

    override fun getItemCount(): Int {
        return listOfFavourites.size
    }
}