package com.example.myfavouriteplaces

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng

class SharedViewModel : ViewModel() {
    private val _docID = MutableLiveData<String?>()
    val docID: LiveData<String?> = _docID
    private val _title = MutableLiveData<String?>()
    val title: MutableLiveData<String?> = _title
    private val _description = MutableLiveData<String?>()
    val description: MutableLiveData<String?> = _description
    private val _category = MutableLiveData<String?>()
    val category: MutableLiveData<String?> = _category
    private val _stars = MutableLiveData<Float?>()
    val stars: MutableLiveData<Float?> = _stars
    private val _review = MutableLiveData<String?>()
    val review: MutableLiveData<String?> = _review
    private val _reviewTitle = MutableLiveData<String?>()
    val reviewTitle: MutableLiveData<String?> = _reviewTitle
    private val _sharePublic = MutableLiveData<Boolean?>()
    val sharePublic: MutableLiveData<Boolean?> = _sharePublic
    private val _lat = MutableLiveData<Double?>()
    val lat: MutableLiveData<Double?> = _lat
    private val _lng = MutableLiveData<Double?>()
    val lng: MutableLiveData<Double?> = _lng
    private val _author = MutableLiveData<String?>()
    val author: MutableLiveData<String?> = _author
    private val _imageURI = MutableLiveData<Uri?>()
    val imageUri: MutableLiveData<Uri?> = _imageURI
    private val _imageURL = MutableLiveData<String?>()
    val imageURL: MutableLiveData<String?> = _imageURL


    fun resetValues() {
        _docID.value = null
        _title.value = null
        _description.value = null
        _category.value = null
        _lat.value = null
        _lng.value = null
        _stars.value = null
        _review.value = null
        _reviewTitle.value = null
        _sharePublic.value = null
        _author.value = null
        _imageURI.value = null
        _imageURL.value = null

    }

    fun resetReview() {
        _stars.value = null
        _reviewTitle.value = null
        _review.value = null
    }

    fun setPlace(place: Place) {
        _docID.value = place.docID
        _title.value = place.title
        _description.value = place.description
        _category.value = place.category
        _lat.value = place.lat
        _lng.value = place.lng
        _stars.value = place.stars
        _review.value = place.review
        _reviewTitle.value = place.reviewTitle
        _sharePublic.value = place.public
        _author.value = place.author
        _imageURL.value = place.imageURL
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
            reviewTitle = reviewTitle.value,
            public = sharePublic.value,
            author = author.value,
            imageURL = imageURL.value
        )
    }

    fun setReviewTitle(reviewTitle: String) {
        _reviewTitle.value = reviewTitle
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