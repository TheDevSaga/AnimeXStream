package com.arosara.anisaw.utils.model

import com.google.gson.annotations.SerializedName

data class M3U8FromAjaxModel(
    @SerializedName("source")
    val sourceMp4: ArrayList<Source>,
    @SerializedName("source_bk")
    val sourceM3U8: ArrayList<Source>
)

data class Source(

    @SerializedName("file")
    val url: String,
    @SerializedName("label")
    var label: String,
    @SerializedName("type")
    val type: String


)