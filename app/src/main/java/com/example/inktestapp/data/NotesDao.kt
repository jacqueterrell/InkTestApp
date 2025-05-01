package com.example.inktestapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update


@Dao
interface NotesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(notesEntity: NotesEntity)

    @Query("SELECT * FROM notes_db")
    suspend fun getNotes(): MutableList<NotesEntity>

    @Delete
    suspend fun delete(notesEntity: NotesEntity)

    @Update
    suspend fun update(notesEntity: NotesEntity)

}