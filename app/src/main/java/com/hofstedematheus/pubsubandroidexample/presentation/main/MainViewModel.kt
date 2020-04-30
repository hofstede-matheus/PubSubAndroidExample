package com.hofstedematheus.pubsubandroidexample.presentation.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hofstedematheus.pubsubandroidexample.data.model.Message
import com.hofstedematheus.pubsubandroidexample.data.repository.MessageRepository
import io.bloco.faker.Faker

class MainViewModel(private val dataSource: MessageRepository) : ViewModel() {
    val messageLiveData: MutableLiveData<ArrayList<Message>> = MutableLiveData()

    fun sendMessage(message: Message) {
        dataSource.sendMessage(message)
    }

    fun updateCodeArrayList(list: ArrayList<Message>) {
        messageLiveData.postValue(list)
    }

    class ViewModelFactory(private val dataSource: MessageRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                return MainViewModel(dataSource) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}