package com.example.camera_app

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SessionDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, "sessions.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE sessions (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "sessionId TEXT," +
                    "name TEXT," +
                    "age INTEGER)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS sessions")
        onCreate(db)
    }

    fun insertSession(sessionId: String, name: String, age: Int): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("sessionId", sessionId)
            put("name", name)
            put("age", age)
        }
        val result = db.insert("sessions", null, values)
        return result != -1L
    }


    data class Session(val id: Int, val sessionId: String, val name: String, val age: Int)

    fun getAllSessions(): List<Session> {
        val list = mutableListOf<Session>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM sessions", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val sessionId = cursor.getString(cursor.getColumnIndexOrThrow("sessionId"))
                val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                val age = cursor.getInt(cursor.getColumnIndexOrThrow("age"))
                list.add(Session(id, sessionId, name, age))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }



}
