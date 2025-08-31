package com.example.camera_app

import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.File


/*
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class ViewImagesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sessionId = intent.getStringExtra("SESSION_ID")

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }
        val scroll = ScrollView(this)
        scroll.addView(root)
        setContentView(scroll)

        if (sessionId != null) {
            // path: /storage/emulated/0/Android/data/<package>/files/Pictures/SessionID/
            val folder = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), sessionId)
            if (folder.exists()) {
                val images = folder.listFiles()
                images?.forEach { file ->
                    val imageView = ImageView(this)

                    val uri = Uri.fromFile(file)
                    imageView.setImageURI(uri)

                    root.addView(imageView)
                }
            }
        }
    }
}*/



class ViewImagesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 20, 20, 20)
        }
        setContentView(root)

        val imagePath = intent.getStringExtra("IMAGE_PATH")
        val debugText = TextView(this)

        if (imagePath != null) {
            val file = File(imagePath)
            debugText.text = "Received path: $imagePath"

            if (file.exists()) {
                val imageView = ImageView(this).apply {
                    setImageURI(Uri.fromFile(file))
                    adjustViewBounds = true
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                }
                root.addView(imageView)
            } else {
                debugText.append("\nFile does not exist")
            }
        } else {
            debugText.text = "No IMAGE_PATH received!"
        }

        root.addView(debugText)
    }
}
