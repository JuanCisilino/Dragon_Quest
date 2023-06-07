package com.frost.dragonquest.model

data class User(
    var email: String,
    var nombre: String) {

    fun mapToUser(email: String, name: String): User {
        this.email = email
        this.nombre = name
        return this
    }
}
