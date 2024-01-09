package com.example.myfavouriteplaces

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

class FavouritesAdapter (context: Context, val listOfFavourites: MutableList<Place>): RecyclerView.Adapter<FavouritesAdapter.ViewHolder>() {

    private val layoutInflater = LayoutInflater.from(context)

    var onCardClick: ((Place) -> Unit)? = null
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvTitle: TextView = itemView.findViewById(R.id.tvCardTitle)
        var tvDesc: TextView = itemView.findViewById(R.id.tvCardDesc)
        var imFavourite: ImageView = itemView.findViewById(R.id.imCardImage)
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
        var requestOptions = RequestOptions()
        requestOptions = requestOptions.transforms(FitCenter(), RoundedCorners(16))
        Glide.with(holder.itemView.context)
            .load(listOfFavourites[position].imageURL)
            .apply(requestOptions)
            .placeholder(R.drawable.baseline_image_not_supported_24)
            .skipMemoryCache(true)//for caching the image url in case phone is offline
            .into(holder.imFavourite)




        holder.itemView.setOnClickListener {
            val place = listOfFavourites[position]
            onCardClick?.invoke(place)
        }
    }

    override fun getItemCount(): Int {
        return listOfFavourites.size
    }
}