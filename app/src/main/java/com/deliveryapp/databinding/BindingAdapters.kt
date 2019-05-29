package com.deliveryapp.databinding

import android.databinding.BindingAdapter
import android.databinding.BindingConversion
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.deliveryapp.domain.entity.Delivery
import com.deliveryapp.utils.Constants
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.squareup.picasso.Picasso


@BindingConversion
fun setVisibility(state: Boolean): Int {
    return if (state) View.VISIBLE else View.GONE
}

@BindingAdapter("imageUrl")
fun loadImage(imageView: ImageView, url: String?) {
    if (!url.isNullOrEmpty()) {
        Picasso.get().load(url).into(imageView)
    }
}

@BindingAdapter("initMap")
fun addMarkerToMap(mapView: MapView, delivery: Delivery?) {
    if (delivery != null) {
        mapView.onCreate(Bundle())
        mapView.getMapAsync { googleMap ->
            MapsInitializer.initialize(mapView.context)
            val latLng = LatLng(delivery.location.lat, delivery.location.lng)
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, Constants.DEFAULT_MAP_ZOOM))
            googleMap.addMarker(MarkerOptions().position(latLng).title(delivery.location.address))
            mapView.onResume()
        }
    }
}
