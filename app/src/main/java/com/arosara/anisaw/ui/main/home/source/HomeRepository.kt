package com.arosara.anisaw.ui.main.home.source

import kotlinx.coroutines.flow.Flow
import com.arosara.anisaw.utils.Result
import com.arosara.anisaw.utils.model.AnimeMetaModel

interface HomeRepository {
    suspend fun fetchHomeData(page: Int, type: Int): Flow<Result<ArrayList<AnimeMetaModel>>>
    suspend fun removeOldData()
}