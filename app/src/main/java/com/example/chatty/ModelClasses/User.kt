package com.example.chatty.ModelClasses

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class User(
    val uid: String,
    val username: String,
    val email: String,
    val status: String,
    val image: String,
    val thum_img: String
): Parcelable{
    constructor(): this ("","","","","","")
}