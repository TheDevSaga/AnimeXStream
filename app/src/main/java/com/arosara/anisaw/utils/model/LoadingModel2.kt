package com.arosara.anisaw.utils.model

import com.arosara.anisaw.utils.CommonViewModel2

data class LoadingModel2(
    val loading: CommonViewModel2.Loading,
    val isListEmpty: Boolean,
    val errorCode: Int,
    val errorMsg: Int
)