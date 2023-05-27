package com.arosara.anisaw.utils.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.arosara.anisaw.utils.constants.C
import com.arosara.anisaw.utils.preference.Preference
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModules {

    annotation class RequestHeader

    @Provides
    @RequestHeader
    @Singleton
    fun provideNetworkHeader(preference: Preference): Map<String, String> {
        return mapOf(
            "referer" to preference.getReferrer(),
            "origin" to preference.getOrigin(),
            "user-agent" to C.USER_AGENT
        )
    }

    annotation class Referer

    @Provides
    fun provideReferer(preference: Preference): String {
        return preference.getReferrer()
    }
}