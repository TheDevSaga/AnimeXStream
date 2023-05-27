package com.arosara.anisaw.utils.preference

import android.content.Context

class PreferenceHelper(context: Context) {

    init {
        sharedPreference = Preference(context)
    }

    companion object {
        lateinit var sharedPreference: Preference
    }


}