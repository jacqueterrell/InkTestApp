package com.example.inktestapp.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Parcelize
@Entity(tableName = "notes_db")
data class NotesEntity (
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var title: String = "",
    var message: String = "",
    var fileName: String = "",
    var contextFileName: String  = "",
    var listIndex: Int = 0
) : Parcelable