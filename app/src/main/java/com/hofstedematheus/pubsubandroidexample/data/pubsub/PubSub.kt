package com.hofstedematheus.pubsubandroidexample.data.pubsub

import com.google.auth.oauth2.GoogleCredentials

interface PubSub {
    fun subscribe(subscription: String)
    fun publish(topicName: String, message: String)
}