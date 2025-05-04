package com.example.inktestapp.data

import android.content.Context
import kotlinx.coroutines.flow.Flow

object NotesDbHelper {

    suspend fun addNote(context: Context, notesEntity: NotesEntity) {
        val db = AppDataBase.getInstance(context.applicationContext)
        return db.notesDao().add(notesEntity)
    }

    suspend fun insertAllNotes(context: Context, notes: List<NotesEntity>) {
        val db = AppDataBase.getInstance(context.applicationContext)
        return db.notesDao().insertAll(notes)
    }

    suspend fun getNote(context: Context, noteId: Long): NotesEntity? {
        val db = AppDataBase.getInstance(context.applicationContext)
        return db.notesDao().getNotes().firstOrNull { it.id == noteId }
    }

    suspend fun getAllNotes(context: Context): MutableList<NotesEntity> {
        val db = AppDataBase.getInstance(context.applicationContext)
        return db.notesDao().getNotes()
    }

    suspend fun deleteNote(context: Context, notesEntity: NotesEntity) {
        val db = AppDataBase.getInstance(context.applicationContext)
        return db.notesDao().delete(notesEntity)
    }

    suspend fun deleteAllNotes(context: Context) {
        val db = AppDataBase.getInstance(context.applicationContext)
        return db.notesDao().deleteAllNotes()
    }

    suspend fun updateNote(context: Context, notesEntity: NotesEntity) {
        val db = AppDataBase.getInstance(context.applicationContext)
        return db.notesDao().update(notesEntity)
    }
}