package com.example.inktestapp.data

import android.content.Context

object NotesDbHelper {

    suspend fun addNote(context: Context, notesEntity: NotesEntity) {
        val db = AppDataBase.getInstance(context.applicationContext)
        return db.notesDao().add(notesEntity)
    }

    suspend fun getNote(context: Context, noteId: Long): NotesEntity? {
        val db = AppDataBase.getInstance(context.applicationContext)
        return db.notesDao().getNotes().firstOrNull { it.id == noteId }
    }

    suspend fun deleteNote(context: Context, notesEntity: NotesEntity) {
        val db = AppDataBase.getInstance(context.applicationContext)
        return db.notesDao().delete(notesEntity)
    }

    suspend fun updateNote(context: Context, notesEntity: NotesEntity) {
        val db = AppDataBase.getInstance(context.applicationContext)
        return db.notesDao().update(notesEntity)
    }
}