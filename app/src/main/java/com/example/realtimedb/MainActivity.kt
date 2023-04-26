package com.example.realtimedb

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

    private lateinit var recordsRecyclerView: RecyclerView
    private lateinit var titleEditText: EditText
    private lateinit var authorEditText: EditText
    private lateinit var addRecordButton: Button
    private lateinit var logOutButton: Button
    private lateinit var recordsList: MutableList<Book>
    private lateinit var recordsAdapter: RecordsAdapter
    private lateinit var database: DatabaseReference
    private var spinnerFilter: String = "All"
    val spinnerSettings = findViewById<Spinner>(R.id.spinner_settings)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        spinnerAdapter()

        recordsRecyclerView = findViewById(R.id.records_recycler_view)
        titleEditText = findViewById(R.id.title_edit_text)
        authorEditText = findViewById(R.id.author_edit_text)
        addRecordButton = findViewById(R.id.add_record_button)
        logOutButton = findViewById(R.id.logout_button)

        recordsList = mutableListOf()

        recordsAdapter = RecordsAdapter(recordsList)
        recordsRecyclerView.adapter = recordsAdapter
        recordsRecyclerView.layoutManager = LinearLayoutManager(this)

        database = FirebaseDatabase.getInstance().getReference("books")

        // Set up a listener to read from the database and populate the recordsList
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                recordsList.clear()
                for (bookSnapshot in snapshot.children) {
                    val book = bookSnapshot.getValue(Book::class.java)
                    book?.let {
                        recordsList.add(it)
                    }
                }
                recordsAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("MainActivity", "Error reading from database", error.toException())
                Toast.makeText(this@MainActivity, "Error reading from database", Toast.LENGTH_SHORT).show()
            }
        }
        database.addValueEventListener(listener)

        recordsAdapter.setOnReadSwitchClickListener { book, isChecked ->
            book.title?.let {
                database.child(it).child("read").setValue(isChecked)
                    .addOnSuccessListener {
                        book.read = isChecked
                        Log.d("MainActivity", "Book read status updated to $isChecked in database")
                    }
                    .addOnFailureListener {
                        Log.e("MainActivity", "Failed to update book read status in database", it)
                    }
            }
        }


        addRecordButton.setOnClickListener {
            addRecord()
        }

        val auth = FirebaseAuth.getInstance()
        logOutButton.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun spinnerAdapter() {
        val spinner = findViewById<Spinner>(R.id.spinner_settings)
        val options = arrayOf("All", "Read", "Unread")
        spinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, options)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                spinnerFilter = options[position]
                updateList()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun updateList() {
        val selectedItem = spinnerSettings.selectedItem.toString()
        recordsList.clear()

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                recordsList.clear()
                for (ds in dataSnapshot.children) {
                    val book = ds.getValue(Book::class.java)

                    // filter based on the selected option in the spinner
                    if (selectedItem == "All" ||
                        (selectedItem == "Read" && book?.read == true) ||
                        (selectedItem == "Unread" && book?.read == false)
                    ) {
                        book?.let {
                            recordsList.add(it)
                        }
                    }
                }
                recordsAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("MainActivity", "Failed to read value.", error.toException())
            }
        })
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

        val book = Book(title, author)

        database.child(title).setValue(book).addOnCompleteListener {
            Toast.makeText(this, "Book added successfully", Toast.LENGTH_SHORT).show()
            Log.d("MainActivity", "Book added successfully to database")
        }
        // Add the new book to the list and notify the adapter that the data has changed
        recordsList.add(book)
        recordsAdapter.notifyDataSetChanged()

        titleEditText.text.clear()
        authorEditText.text.clear()
    }
}