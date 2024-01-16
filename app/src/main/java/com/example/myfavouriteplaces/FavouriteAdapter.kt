package com.example.myfavouriteplaces

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.card.MaterialCardView

class FavouritesAdapter(context: Context, private val listOfFavourites: MutableList<Place>) :
    RecyclerView.Adapter<FavouritesAdapter.ViewHolder>() {

    private val layoutInflater = LayoutInflater.from(context)

    var onCardClick: ((Place, MaterialCardView) -> Unit)? = null

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvTitle: TextView = itemView.findViewById(R.id.tvFavouriteTitle)
        var tvDesc: TextView = itemView.findViewById(R.id.tvFavouriteDescription)
        var imFavourite: ImageView = itemView.findViewById(R.id.imFavouriteImage)
        var card: MaterialCardView = itemView.findViewById(R.id.card)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FavouritesAdapter.ViewHolder {

        val itemView = layoutInflater.inflate(R.layout.favourite_item, parent, false)


        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FavouritesAdapter.ViewHolder, position: Int) {
        val place = listOfFavourites[position]
        holder.tvTitle.text = place.title
        holder.tvDesc.text = place.description
        holder.card.transitionName = place.docID
        var requestOptions = RequestOptions()
        requestOptions = requestOptions.transforms(FitCenter(), RoundedCorners(16))
        Glide.with(holder.itemView.context)
            .load(listOfFavourites[position].imageURL)
            .apply(requestOptions)
            .placeholder(R.drawable.baseline_image_not_supported_24)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .into(holder.imFavourite)




        holder.itemView.setOnClickListener {
            val place = listOfFavourites[position]
            onCardClick?.invoke(place, holder.card)
        }
    }

    override fun getItemCount(): Int {
        return listOfFavourites.size
    }
}