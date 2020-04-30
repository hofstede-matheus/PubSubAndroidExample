package com.hofstedematheus.pubsubandroidexample.data.repository

import com.hofstedematheus.pubsubandroidexample.data.model.Message

interface MessageRepository {
    fun getMessages() : List<Message>
    fun sendMessage(message: Message)
}