package net.xblacky.animexstream.utils.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class VidCdnModel(
    @PrimaryKey
    var vidCdnId: String = "",
    var ajaxParams: String = "",
) : RealmObject()