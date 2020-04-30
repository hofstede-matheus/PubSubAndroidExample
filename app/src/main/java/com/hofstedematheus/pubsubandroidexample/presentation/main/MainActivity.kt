package com.hofstedematheus.pubsubandroidexample.presentation.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.hofstedematheus.pubsubandroidexample.R
import com.hofstedematheus.pubsubandroidexample.data.model.Message
import com.hofstedematheus.pubsubandroidexample.data.pubsub.GoogleCloudPubSubDataSource
import io.bloco.faker.Faker
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val dataSource = GoogleCloudPubSubDataSource(resources.openRawResource(R.raw.credentials), "wired-record-275613", "topic")
        dataSource.subscribe("subscription")

        val viewModel: MainViewModel = MainViewModel.ViewModelFactory(dataSource)
            .create(MainViewModel::class.java)
        dataSource.viewModel = viewModel


        viewModel.messageLiveData.observe(this, Observer {
            with(recyclerView) {
                layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                setHasFixedSize(true)
                adapter = MessagesAdapter(it, context)
            }
        })

        floatingActionButton.setOnClickListener {
            viewModel.sendMessage(
                Message(
                    Faker().name.firstName(),
                    Faker().lorem.sentence()
                )
            )
        }

    }
}
