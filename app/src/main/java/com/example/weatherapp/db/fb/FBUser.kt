package com.example.weatherapp.db.fb

import com.example.weatherapp.model.City
import com.example.weatherapp.model.User
import com.google.android.gms.maps.model.LatLng

class FBUser {
    var name : String? = null
    var email: String? = null
    fun toUser(): User {
        return User(name = name!!, email = email!!)
    }
}
fun User.toFBUser() : FBUser {
    val fbUser = FBUser()
    fbUser.name = this.name
    fbUser.email = this.email
    return fbUser
}