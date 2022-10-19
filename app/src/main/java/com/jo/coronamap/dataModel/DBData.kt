package com.jo.coronamap.dataModel

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class DataList(
    @PrimaryKey
    @SerializedName("data")
    var list:List<Corona>
)
@Entity
data class Corona(
    @PrimaryKey
    var id:String,
    var address:String,
    var centerName:String,
    var centerType:String,
    var facilityName:String,
    var lat:String,
    var lng:String,
    var phoneNumber:String,
    var updatedAt:String,
)

