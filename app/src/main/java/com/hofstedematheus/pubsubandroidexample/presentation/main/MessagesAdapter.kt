package com.hofstedematheus.pubsubandroidexample.presentation.main

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hofstedematheus.pubsubandroidexample.R
import com.hofstedematheus.pubsubandroidexample.data.model.Message
import kotlinx.android.synthetic.main.rv_item.view.*

class MessagesAdapter (private val messages: ArrayList<Message>,
                       private val context: Context?
) : RecyclerView.Adapter<MessagesAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(messages[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.rv_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindView(message: Message) {
            val senderName = itemView.senderName
            val text = itemView.text
            senderName.text = message.senderName
            text.text = message.text
        }

    }


}