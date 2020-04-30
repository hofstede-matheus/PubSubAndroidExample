package com.hofstedematheus.pubsubandroidexample.data.pubsub

import android.util.Log
import com.google.api.core.ApiFuture
import com.google.api.core.ApiFutureCallback
import com.google.api.core.ApiFutures
import com.google.api.gax.core.FixedCredentialsProvider
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.pubsub.v1.AckReplyConsumer
import com.google.cloud.pubsub.v1.MessageReceiver
import com.google.cloud.pubsub.v1.Publisher
import com.google.cloud.pubsub.v1.Subscriber
import com.google.common.util.concurrent.MoreExecutors
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.protobuf.ByteString
import com.google.pubsub.v1.ProjectSubscriptionName
import com.google.pubsub.v1.ProjectTopicName
import com.google.pubsub.v1.PubsubMessage
import com.hofstedematheus.pubsubandroidexample.data.model.Message
import com.hofstedematheus.pubsubandroidexample.data.repository.MessageRepository
import com.hofstedematheus.pubsubandroidexample.presentation.main.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.InputStream
import java.util.*
import java.util.concurrent.TimeUnit


class GoogleCloudPubSubDataSource(val jsonCredentials: InputStream?, val projectName: String, val topic: String) : MessageRepository, PubSub,
    MessageReceiver {
    val pubSubMessages = ArrayList<Message>()
    var credentials  = GoogleCredentials.fromStream(jsonCredentials)
    private var subscriber: Subscriber? = null
    lateinit var viewModel: MainViewModel
    var gson = Gson()


    override fun subscribe(subscription: String) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val subscriptionName = ProjectSubscriptionName.of(projectName, subscription)

                subscriber = Subscriber.newBuilder(subscriptionName, this@GoogleCloudPubSubDataSource)
                    .setCredentialsProvider { credentials }
                    .build()
                subscriber?.startAsync()?.awaitRunning()
                subscriber?.awaitTerminated()
            } catch (t: Throwable) {
                Log.i("PUBSUB", t.message ?: "Something went wrong")
            }
        }

    }

    override fun publish(topicName: String, message: String) {
        var publisher: Publisher? = null

        try {
            val projectTopicName = ProjectTopicName.of(projectName, topicName);
            publisher = Publisher.newBuilder(projectTopicName).setCredentialsProvider(
                FixedCredentialsProvider.create(credentials)).build()
            val data = ByteString.copyFromUtf8(message)
            val pubsubMessage = PubsubMessage.newBuilder().setData(data).build()
            val messageIdFuture: ApiFuture<String> = publisher.publish(pubsubMessage)

            ApiFutures.addCallback(
                messageIdFuture,
                object : ApiFutureCallback<String> {
                    override fun onSuccess(messageId: String) {
                        println("published with message id: $messageId")
                    }

                    override fun onFailure(t: Throwable) {
                        println("failed to publish: $t")
                    }
                },
                MoreExecutors.directExecutor()
            )
        } finally {
            if (publisher != null) {
                publisher.shutdown();
                publisher.awaitTermination(1, TimeUnit.MINUTES);
            }
        }
    }

    override fun getMessages(): List<Message> {
        return pubSubMessages
    }

    override fun sendMessage(message: Message) {
        publish(topic, gson.toJson(message))

    }

    override fun receiveMessage(message: PubsubMessage, consumer: AckReplyConsumer) {
        Log.i("PUBSUB", message.data.toStringUtf8())
        consumer.ack()
        pubSubMessages.add(gson.fromJson(message.data.toStringUtf8(), Message::class.java))
        viewModel.updateCodeArrayList(pubSubMessages)

    }
}