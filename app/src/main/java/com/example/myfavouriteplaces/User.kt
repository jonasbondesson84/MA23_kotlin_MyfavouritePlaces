package com.example.myfavouriteplaces

import com.google.firebase.firestore.DocumentId

class User(@DocumentId var documentId : String? = null,
           var userID: String? = null,
           var name: String? = null,
           var location: String? = null,
           var userImage: String? = null,

           ) {
}