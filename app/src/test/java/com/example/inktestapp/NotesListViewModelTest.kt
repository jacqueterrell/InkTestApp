package com.example.inktestapp

import com.example.inktestapp.data.AppDataBase
import com.example.inktestapp.data.NotesDao
import com.example.inktestapp.data.NotesEntity
import com.example.inktestapp.userInterface.notesList.NotesListViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.spyk
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class NotesListViewModelTest {

    private lateinit var spyViewModel: NotesListViewModel
    private val mockDatabase: AppDataBase = mockk()
    private val mockDao: NotesDao = mockk()


    @Before
    fun setUp() {
        spyViewModel = spyk(NotesListViewModel())
        every { mockDatabase.notesDao() } returns mockDao
    }

    @Test
    fun updateNotesAfterIndexChange() {
        runBlocking {
            val list = emptyList<NotesEntity>()
            coEvery { mockDao.insertAll(list) } just runs
            spyViewModel.updateNotesAfterIndexChange(mockDatabase, list)
            coVerify { mockDao.insertAll(list) }
        }
    }

    @Test
    fun `getNotesFlow emits expected notes`() = runTest {
        val expectedNotes = mutableListOf(NotesEntity())
        val mockedFlow = flowOf(expectedNotes)
        coEvery { mockDao.getNotesAsFlow() } returns mockedFlow
        val stateFlow = spyViewModel.getNotesFlow(mockDatabase, this, SharingStarted.Eagerly)
        val result = mutableListOf<List<NotesEntity>>()
        launch {
            stateFlow.take(1).collect {
                result.add(it)
                Assert.assertEquals(expectedNotes, it)

            }
        }
    }
}