package com.example.myfavouriteplaces

import com.google.android.gms.maps.model.LatLng

object CurrentUser {
    var documentId: String? = null
    var userID: String? = null
    var name: String? = null
    var location: String? = null
    var userImage: String? = null
    var latLng: LatLng? = null

    var favouritesList = mutableListOf<Place>()

    var sharedFavouritesList = mutableListOf<Place>()


    fun resetUser() {
        this.documentId = null
        this.userID = null
        this.name = null
        this.location = null
        this.userImage = null
        this.favouritesList.clear()
    }

    fun updateUser(name: String?, location: String?, userImage: String?) {
        this.name = name
        this.location = location
        this.userImage = userImage

    }

}