package com.ozantok.depremtakipapp.ui.map


import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory

object BitmapDescriptorFactory {
    const val HUE_RED = BitmapDescriptorFactory.HUE_RED
    const val HUE_ORANGE = BitmapDescriptorFactory.HUE_ORANGE
    const val HUE_GREEN = BitmapDescriptorFactory.HUE_GREEN

    fun defaultMarker(hue: Float): BitmapDescriptor {
        return BitmapDescriptorFactory.defaultMarker(hue)
    }
}