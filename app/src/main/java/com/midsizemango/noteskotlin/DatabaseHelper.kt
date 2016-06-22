package com.midsizemango.noteskotlin

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.util.*

/**
 * Created by Prasad on 21/06/16.
 */

class DatabaseHelper(private val context: Context) : SQLiteOpenHelper(context, DatabaseHelper.DATABASE_NAME, null, DatabaseHelper.DATABASE_VERSION) {
    var database: SQLiteDatabase? = null

    override fun onCreate(db: SQLiteDatabase) {
        try{
            db.execSQL("CREATE TABLE notes (id INTEGER PRIMARY KEY AUTOINCREMENT,title TEXT,content TEXT,updatedat TEXT)")
        }catch(e: SQLiteException){
            e.printStackTrace()
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Log.d("onUpgrade","onUpgrade")
    }

    fun createNote(note: Note): Long{
        val db: SQLiteDatabase = this.getWritableDatabase();
        val values: ContentValues = ContentValues()
        values.put("title", note.getTitle());
        values.put("content", note.getContent());
        values.put("updatedat", note.getUpdatedAt());
        return db.insert("notes", null, values);
    }

        fun getAllNotes(): MutableList<Note> {
            val result = ArrayList<Note>()
            val db = this.readableDatabase
            val selectQuery = "SELECT * FROM notes"
            Log.e("Select", selectQuery)
            val cursor = db.rawQuery(selectQuery, null)
            try {
                if (cursor!!.moveToFirst()) {
                    do {
                        val note: Note = Note()
                        note.id = cursor.getLong(cursor.getColumnIndex("id"))
                        note.title = cursor.getString(cursor.getColumnIndex("title"))
                        note.content = cursor.getString(cursor.getColumnIndex("content"))
                        note.updatedAt = cursor.getString(cursor.getColumnIndex("updatedat"))
                        result.add(note)
                    } while (cursor.moveToNext())
                }
            } finally {
                cursor?.close()
            }
            return result
        }

    fun updateNote(note: Note) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put("title", note.title)
        values.put("content", note.content)
        values.put("updatedat", note.updatedAt)
        db.update("notes", values,"id = ?", arrayOf(note.id.toString()))
    }

    fun deleteNote(note: Note) {
        val db = this.writableDatabase
        db.delete("notes", "id = ?", arrayOf(note.id.toString()))
    }

    @Synchronized override fun close() {
        if (database != null)
            database!!.close()
        super.close()
    }

    companion object {
        private val DATABASE_VERSION = 1
        val DATABASE_NAME = "NotesDB"
    }
}
