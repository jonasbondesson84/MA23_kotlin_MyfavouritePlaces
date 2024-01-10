package com.example.myfavouriteplaces

import com.google.firebase.firestore.DocumentId

data class Place(@DocumentId var docID: String? = null,
                 var title: String? = null,
                 var description: String? = null,
                 var lat: Double? = null,
                 var lng: Double? = null,
                 var category: String? = null,
                 var stars: Float? = null,
                 var review: String? = null,
                 var reviewTitle: String? = null,
                 var public: Boolean? = null,
                 var author: String? = null,
                 var imageURL: String? = null

)