package com.example.myfavouriteplaces

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng

class SharedViewModel : ViewModel() {
    private val _docID = MutableLiveData<String>()
    val docID: LiveData<String> = _docID
    private val _title = MutableLiveData<String>()
    val title: LiveData<String> = _title
    private val _description = MutableLiveData<String>()
    val description: LiveData<String> = _description
    private val _category = MutableLiveData<String>()
    val category: LiveData<String> = _category
    private val _stars = MutableLiveData<Float>()
    val stars: LiveData<Float> = _stars
    private val _review = MutableLiveData<String>()
    val review: LiveData<String> = _review
    private val _sharePublic = MutableLiveData<Boolean>()
    val sharePublic: LiveData<Boolean> = _sharePublic
    private val _lat = MutableLiveData<Double>()
    val lat: LiveData<Double> = _lat
    private val _lng = MutableLiveData<Double>()
    val lng: LiveData<Double> = _lng
    private val _author = MutableLiveData<String>()
    val author: LiveData<String> = _author
    private val _imageURI = MutableLiveData<Uri>()
    val imageUri: LiveData<Uri> = _imageURI
    private val _imageURL = MutableLiveData<String>()
    val imageURL: LiveData<String> = _imageURL


    fun setPlace(place: Place) {
        val docID = place.docID
        docID.let { _docID.value = it }
        val title = place.title
        title.let { _title.value = it }
        val desc = place.description
        desc.let { _description.value = it }
        val category = place.category
        category.let { _category.value = it }
        val lat = place.lat
        lat.let { _lat.value = it }
        val lng = place.lng
        lng.let { _lng.value }
        val stars = place.stars
        stars.let { _stars.value = it }
        val review = place.review
        review.let { _review.value = it }
        val sharePublic = place.public
        sharePublic.let { _sharePublic.value = it }
        val author = place.author
        author.let { _author.value = it }
        val imageURL = place.imageURL
        imageURL.let { _imageURL.value = it }

    }

    fun getPlace(): Place {

        return Place(
            docID = docID.value,
            title = title.value,
            description = description.value,
            lat = lat.value,
            lng = lng.value,
            category = category.value,
            stars = stars.value,
            review = review.value,
            public = sharePublic.value,
            author = author.value,
            imageURL = imageURL.value
        )
    }

    fun setImageURL(url: String) {
        _imageURL.value = url
    }

    fun setImageUri(uri: Uri) {
        _imageURI.value = uri
    }

    fun setTitle(title: String) {
        _title.value = title
    }

    fun setDescription(text: String) {
        _description.value = text
    }

    fun setStars(numberOfStars: Float) {
        _stars.value = numberOfStars
    }

    fun setReview(review: String) {
        _review.value = review
    }

    fun setSharePublic(shared: Boolean) {
        _sharePublic.value = shared
    }

    fun setLocation(location: LatLng) {
        _lat.value = location.latitude
        _lng.value = location.longitude
    }

    fun setCategory(category: String) {
        _category.value = category
    }


}