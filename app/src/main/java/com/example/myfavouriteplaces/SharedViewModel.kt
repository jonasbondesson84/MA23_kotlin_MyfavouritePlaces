package com.example.myfavouriteplaces

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng

class SharedViewModel : ViewModel() {
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





    fun setTitle(title: String) {
        _title.value = title
    }

    fun setDescription(text: String) {
        _description.value = text
    }

    fun setStars(numberOfStars: Float) {
        _stars.value = numberOfStars
    }
    fun  setReview(review: String) {
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