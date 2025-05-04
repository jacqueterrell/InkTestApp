package com.example.inktestapp.userInterface.notesList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    fun getNotesFlow(dataBase: AppDataBase, scope: CoroutineScope): StateFlow<MutableList<NotesEntity>> {
        return dataBase.notesDao()
            .getNotesAsFlow()
            .stateIn(scope, SharingStarted.WhileSubscribed(5_000), mutableListOf())
    }
}