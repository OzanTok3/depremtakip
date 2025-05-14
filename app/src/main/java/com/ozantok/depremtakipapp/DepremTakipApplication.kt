package com.ozantok.depremtakipapp


import android.app.Application
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class DepremTakipApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // AdMob'u başlat
        MobileAds.initialize(this) { initializationStatus ->
            // Başlatma tamamlandığında yapılacak işlemler
        }
    }
}