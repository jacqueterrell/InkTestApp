package com.example.inktestapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "notes_db")
data class NotesEntity (
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var title: String = "",
    var message: String = "",
    var fileName: String= ""
)