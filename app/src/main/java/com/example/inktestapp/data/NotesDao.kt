package com.example.inktestapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


@Dao
interface NotesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(notes: List<NotesEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(notesEntity: NotesEntity)

    @Query("SELECT * FROM notes_db")
    suspend fun getNotes(): MutableList<NotesEntity>

    @Query("SELECT * FROM notes_db")
    fun getNotesAsFlow(): Flow<MutableList<NotesEntity>>

    @Query("DELETE FROM notes_db")
    suspend fun deleteAllNotes()

    @Delete
    suspend fun delete(notesEntity: NotesEntity)

    @Update
    suspend fun update(notesEntity: NotesEntity)

}