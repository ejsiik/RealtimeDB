package com.example.realtimedb

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecordsAdapter(private val recordsList: MutableList<Book>) :
    RecyclerView.Adapter<RecordsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.record_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val book = recordsList[position]
        holder.titleTextView.text = book.title
        holder.authorTextView.text = book.author
    }

    override fun getItemCount(): Int {
        return recordsList.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.title_text_view)
        val authorTextView: TextView = view.findViewById(R.id.author_text_view)
    }
}