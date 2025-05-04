package com.example.inktestapp.di

import com.example.inktestapp.userInterface.createNote.AlterNoteViewModel
import com.example.inktestapp.userInterface.editNote.EditNoteViewModel
import com.example.inktestapp.userInterface.notesList.NotesListViewModel
import com.example.inktestapp.userInterface.settings.SettingsViewModel
import org.koin.dsl.module

object Modules {

    val viewModelModule = module {
        single { NotesListViewModel() }
        single { SettingsViewModel() }
        single { AlterNoteViewModel() }
        single { EditNoteViewModel() }
    }
}