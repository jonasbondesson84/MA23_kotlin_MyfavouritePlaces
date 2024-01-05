package com.example.myfavouriteplaces

import com.google.firebase.firestore.DocumentId

data class Place(@DocumentId var docID: String? = null,
                 val title: String? = null,
                 var description: String? = null,
                 val lat: Double? = null,
                 val lng: Double? = null,
                 val category: String? = null,
                 val stars: Float? = null,
                 val review: String? = null,
                 val public: Boolean? = null,
                 val author: String? = null,
)