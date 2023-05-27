package com.arosara.anisaw.ui.main.animeinfo.di

import dagger.assisted.AssistedFactory
import com.arosara.anisaw.ui.main.animeinfo.AnimeInfoViewModel

@AssistedFactory
interface AnimeInfoFactory {
    fun create(categoryUrl: String): AnimeInfoViewModel
}
