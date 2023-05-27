package com.arosara.anisaw

import android.app.Application
import androidx.viewbinding.BuildConfig
import dagger.hilt.android.HiltAndroidApp
import com.arosara.anisaw.utils.preference.PreferenceHelper
import com.arosara.anisaw.utils.realm.InitalizeRealm
import com.arosara.anisaw.utils.rertofit.RetrofitHelper
import timber.log.Timber


@HiltAndroidApp
class AnimeXStream : Application() {

    override fun onCreate() {
        super.onCreate()
        InitalizeRealm.initializeRealm(this)
        PreferenceHelper(context = this)
        RetrofitHelper(PreferenceHelper.sharedPreference.getBaseUrl())
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

    }

}