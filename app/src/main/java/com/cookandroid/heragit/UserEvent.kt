package com.cookandroid.heragit

import com.google.gson.annotations.SerializedName
import java.time.LocalDate
import java.time.LocalDateTime

data class UserEvent (
    val id : String,
    val type : String,
    val actor: Actor,
    val repo: Repo,
    val payload: Payload,
    val public:Boolean,
    val created_at:String
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

data class Payload(
    val push_id:Long,
    val size:Int,
    val distinct_size:Int,
    val ref:String,
    val head:String,
    val before:String,
    val commits:Array<Commits>
)

data class Commits (
    val sha:String,
    val author:Author,
    val message:String,
    val distinct:Boolean,
    val url:String,
)

data class Author (
    val email:String,
    val name: String
)

