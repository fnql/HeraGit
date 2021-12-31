package com.cookandroid.heragit

import com.google.gson.annotations.SerializedName

data class UserEvent (
    val id : String,
    val type : String,
    val actor: Actor,
    val repo: Repo
    )

data class Repo (
    val id:String,
    val name:String,
    val url:String
)

data class Actor(
    val  id: String,
    val  login: String,
    val  display_login: String,
    val  gravatar_id: String,
    val  url :String,
    val  avatar_url:String

)
