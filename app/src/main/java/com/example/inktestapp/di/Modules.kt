package com.example.inktestapp.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.example.inktestapp.userInterface.createNote.AlterNoteViewModel
import com.example.inktestapp.userInterface.notesList.NotesListViewModel
import com.example.inktestapp.userInterface.settings.SettingsViewModel
import com.example.inktestapp.utils.AppSharedPrefs
import org.koin.dsl.module

object Modules {

    val appModule = module {
        single { provideSharedPreferences(get()) }
        single {
            AppSharedPrefs(get())
        }
    }

    val viewModelModule = module {
        single { NotesListViewModel() }
        single { SettingsViewModel() }
        single { AlterNoteViewModel() }
    }

    private fun provideSharedPreferences(application: Application): SharedPreferences {
        return application.getSharedPreferences(application.packageName, Context.MODE_PRIVATE)
    }
}