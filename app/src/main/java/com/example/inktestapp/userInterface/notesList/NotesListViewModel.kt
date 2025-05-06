package com.example.inktestapp.userInterface.notesList

import androidx.lifecycle.ViewModel
import com.example.inktestapp.data.AppDataBase
import com.example.inktestapp.data.NotesEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class NotesListViewModel: ViewModel() {

    var isUpdatingIndex = false

    suspend fun updateNotesAfterIndexChange(dataBase: AppDataBase, notesList: List<NotesEntity>) {
        dataBase.notesDao().insertAll(notesList)
    }

    fun getNotesFlow(dataBase: AppDataBase, scope: CoroutineScope, sharingStarted: SharingStarted = SharingStarted.WhileSubscribed(5_000)): StateFlow<MutableList<NotesEntity>> {
        return dataBase.notesDao()
            .getNotesAsFlow()
            .stateIn(scope, sharingStarted, mutableListOf())
    }
}