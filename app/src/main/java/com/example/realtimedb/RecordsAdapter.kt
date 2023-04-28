package com.example.realtimedb

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RecordsAdapter(private val recordsList: MutableList<Book>) :
    RecyclerView.Adapter<RecordsAdapter.ViewHolder>() {

    private var onReadSwitchClickListener: ((book: Book, isChecked: Boolean) -> Unit)? = null
    private lateinit var database: DatabaseReference

    fun setOnReadSwitchClickListener(listener: (book: Book, isChecked: Boolean) -> Unit) {
        onReadSwitchClickListener = listener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        database = FirebaseDatabase.getInstance().getReference("books")
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.record_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val book = recordsList[position]
        holder.titleTextView.text = book.title
        holder.authorTextView.text = book.author
        holder.readSwitch.isChecked = book.read
        holder.readSwitch.setOnClickListener {
            val isChecked = holder.readSwitch.isChecked
            onReadSwitchClickListener?.invoke(book, isChecked)
            database.child(book.title!!).child("read").setValue(isChecked)
        }
    }


    override fun getItemCount(): Int {
        return recordsList.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.title_text_view)
        val authorTextView: TextView = view.findViewById(R.id.author_text_view)
        val readSwitch: Switch = view.findViewById(R.id.read)
    }

    fun updateList(newList: List<Book>) {
        recordsList.clear()
        recordsList.addAll(newList)
        notifyDataSetChanged()
    }

}