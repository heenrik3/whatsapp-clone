package com.henrik3.whatsapp

import io.socket.client.IO

object store {
    var token: String = ""
    lateinit var currentChat: String

    //var socket = IO.socket(config.API)

    fun isCurrentlyLogged(): Boolean = token.isNotEmpty()


    fun login(user: String, pass: String) {

    }

}


object config {
    var API = "http://192.168.0.183:3002"
}