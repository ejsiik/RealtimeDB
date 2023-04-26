package com.example.realtimedb

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {

    private lateinit var recordsRecyclerView: RecyclerView
    private lateinit var titleEditText: EditText
    private lateinit var authorEditText: EditText
    private lateinit var addRecordButton: Button
    private lateinit var recordsList: MutableList<Book>
    private lateinit var recordsAdapter: RecordsAdapter
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        spinnerAdapter()

        recordsRecyclerView = findViewById(R.id.records_recycler_view)
        titleEditText = findViewById(R.id.title_edit_text)
        authorEditText = findViewById(R.id.author_edit_text)
        addRecordButton = findViewById(R.id.add_record_button)

        recordsList = mutableListOf()

        recordsAdapter = RecordsAdapter(recordsList)
        recordsRecyclerView.adapter = recordsAdapter
        recordsRecyclerView.layoutManager = LinearLayoutManager(this)

        database = FirebaseDatabase.getInstance().getReference("books")

        addRecordButton.setOnClickListener {
            addRecord()
        }
    }

    fun spinnerAdapter() {
        val spinner: Spinner = findViewById(R.id.spinner_settings)
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            this,
            R.array.spinner,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
        }
    }

    private fun addRecord() {
        val title = titleEditText.text.toString().trim()
        val author = authorEditText.text.toString().trim()

        if (title.isEmpty()) {
            titleEditText.error = "Title required"
            titleEditText.requestFocus()
            return
        }

        if (author.isEmpty()) {
            authorEditText.error = "Author required"
            authorEditText.requestFocus()
            return
        }

        val book = Book(title, title, author)

        if (title != null) {
            database.child(title).setValue(book).addOnCompleteListener {
                Toast.makeText(this, "Book added successfully", Toast.LENGTH_SHORT).show()
                Log.d("MainActivity", "Book added successfully to database")
            }
        }

        titleEditText.text.clear()
        authorEditText.text.clear()
    }
}