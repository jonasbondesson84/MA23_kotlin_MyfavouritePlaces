package com.example.myfavouriteplaces

import com.google.android.gms.maps.model.LatLng

object currentUser {
    var documentId : String? = null
    var userID: String? = null
    var name: String? = null
    var location: String? = null
    var userImage: String? = null
    var latLng: LatLng? = null

    var favouritesList = mutableListOf<Place>()

    var sharedFavouritesList = mutableListOf<Place>()


    fun resetUser() {
        documentId = null
        userID = null
        name = null
        location = null
        userImage = null
        favouritesList.clear()
    }

}