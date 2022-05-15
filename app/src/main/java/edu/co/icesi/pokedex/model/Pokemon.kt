package edu.co.icesi.pokedex.model

import java.io.Serializable

data class Pokemon(
    val uid: String = "",
    val user: String = "",
    val name: String = "",
    val img: String = "",
    val type: String = "",
    val dateCatch: Long = 0,
    val life: String = "",
    val attack: String = "",
    val defense: String = "",
    val speed: String = "",

):Serializable
