package com.example.camera_app

import com.example.camera_app.ViewImagesActivity

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class ViewSessionsActivity : AppCompatActivity() {

    lateinit var dbHelper: SessionDatabaseHelper
    lateinit var textView: TextView
    lateinit var sessionsContainer: LinearLayout
    lateinit var searchBar: EditText
    var allSessions: List<SessionDatabaseHelper.Session> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Root Layout
        val rootLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(70, 75, 70, 60)
        }

        // Search Bar
        searchBar = EditText(this).apply {
            hint = "Search by Name or SessionID"
            textSize = 20f
        }

        // TextView for session data
        textView = TextView(this).apply {
            textSize = 16f
            setTextColor(resources.getColor(android.R.color.black, theme)) // black text
        }

        // ScrollView so content is scrollable
        sessionsContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }
        val scrollView = ScrollView(this)
        scrollView.addView(sessionsContainer)

        rootLayout.addView(searchBar)
        rootLayout.addView(scrollView)

        setContentView(rootLayout)

        dbHelper = SessionDatabaseHelper(this)
        allSessions = dbHelper.getAllSessions()

        // Show all data initially
        displaySessions(allSessions)

        // Add search functionality
        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().lowercase()
                val filtered = allSessions.filter {
                    it.name.lowercase().contains(query) || it.sessionId.lowercase().contains(query)
                }
                displaySessions(filtered)
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    /*private fun displaySessions(sessions: List<SessionDatabaseHelper.Session>) {
        if (sessions.isNotEmpty()) {
            val builder = StringBuilder()
            builder.append("\n" +
                    "\n" +
                    "\n")
            for (session in sessions) {
                builder.append("        ID: ${session.id}\n")
                builder.append("        SessionID: ${session.sessionId}\n")
                builder.append("        Name: ${session.name},  ")
                builder.append("        Age: ${session.age}\n        ---------------------------------------------------------------\n\n")
            }
            textView.text = builder.toString()
        } else {
            textView.text = "No session data found!"
        }
    }*/



    /*
    private fun displaySessions(sessions: List<SessionDatabaseHelper.Session>) {
        sessionsContainer.removeAllViews() // clear previous

        if (sessions.isNotEmpty()) {
            for (session in sessions) {
                val sessionView = TextView(this).apply {
                    text = "ID: ${session.id}\n" +
                            "SessionID: ${session.sessionId}\n" +
                            "Name: ${session.name}, Age: ${session.age}\n" +
                            "--------------------------------------"
                    textSize = 16f
                    setTextColor(resources.getColor(android.R.color.black, theme))
                    setPadding(20, 20, 20, 20)

                    // make SessionID clickable
                    setOnClickListener {
                        // Navigate to images activity
                        val intent = Intent(this@ViewSessionsActivity, ViewImagesActivity::class.java)
                        intent.putExtra("SESSION_ID", session.sessionId)
                        startActivity(intent)
                    }
                }
                sessionsContainer.addView(sessionView)
            }
        } else {
            val emptyView = TextView(this).apply {
                text = "No session data found!"
                textSize = 16f
            }
            sessionsContainer.addView(emptyView)
        }
    }*/



    private fun displaySessions(sessions: List<SessionDatabaseHelper.Session>) {
        sessionsContainer.removeAllViews()

        if (sessions.isNotEmpty()) {
            for (session in sessions) {
                val sessionView = TextView(this).apply {
                    text = "ID: ${session.id}\n" +
                            "SessionID: ${session.sessionId}\n" +
                            "Name: ${session.name}, Age: ${session.age}\n" +
                            "--------------------------------------"
                    textSize = 16f
                    setTextColor(resources.getColor(android.R.color.black, theme))
                    setPadding(40, 30, 20, 20)

                    setOnClickListener {
                        val folder = File(getExternalMediaDirs()[0], "Sessions/${session.sessionId}")
                        val firstImage = folder.listFiles()?.firstOrNull()

                        Log.d("DEBUG", "Clicked session: ${session.sessionId}, folder=$folder")
                        Log.d("DEBUG", "First image found: ${firstImage?.absolutePath}")

                        if (firstImage != null) {
                            val intent = Intent(this@ViewSessionsActivity, ViewImagesActivity::class.java)
                            intent.putExtra("IMAGE_PATH", firstImage.absolutePath)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this@ViewSessionsActivity, "No images found in ${session.sessionId}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                sessionsContainer.addView(sessionView)
            }
        } else {
            val emptyView = TextView(this).apply {
                text = "No session data found!"
                textSize = 16f
            }
            sessionsContainer.addView(emptyView)
        }
    }
}




















/*
package com.example.camera_app

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ViewSessionsActivity : AppCompatActivity() {

    lateinit var dbHelper: SessionDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val textView = TextView(this)
        textView.textSize = 16f
        setContentView(textView)

        dbHelper = SessionDatabaseHelper(this)

        val data = dbHelper.getAllSessions()
        if (data.isNotEmpty()) {
            val builder = StringBuilder()
            builder.append("\n\n\n\n" +
                    "\n" +
                    "\n")
            for (session in data) {
                builder.append("        ID: ${session.id}\n")
                builder.append("        SessionID: ${session.sessionId}\n")
                builder.append("        Name: ${session.name}"+",  ")
                builder.append("        Age: ${session.age}\n        -------------------------\n\n")
                //builder.append("-------------------------\n\n\n")
            }
            textView.text = builder.toString()
        } else {
            textView.text = "No session data found!"
        }
    }
}
*/