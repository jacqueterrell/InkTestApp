package com.example.inktestapp.userInterface.editNote

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.inktestapp.data.NotesDbHelper
import com.example.inktestapp.data.NotesEntity
import com.example.inktestapp.databinding.CreateNoteLayoutBinding
import com.example.inktestapp.userInterface.AppApplication
import com.example.inktestapp.userInterface.createNote.AlterNoteBaseActivity
import com.example.inktestapp.userInterface.createNote.AlterNoteViewModel
import com.example.inktestapp.utils.DialogHelper
import com.example.inktestapp.utils.EditorUtils
import com.example.inktestapp.utils.getText
import com.example.inktestapp.utils.getWordCount
import com.myscript.iink.ContentPackage
import com.myscript.iink.ContentPart
import com.myscript.iink.Editor
import com.myscript.iink.EditorError
import com.myscript.iink.IEditorListener
import com.myscript.iink.uireferenceimplementation.EditorBinding
import com.myscript.iink.uireferenceimplementation.EditorData
import com.myscript.iink.uireferenceimplementation.FontUtils
import com.myscript.iink.uireferenceimplementation.InputController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditNoteActivity : AlterNoteBaseActivity() {
    override fun isEditingNote(): Boolean = true

    override fun getTag(): String = EditNoteActivity::class.java.simpleName

    override fun getNoteTitle(): String = "Edit Note"

    override fun getEditingNoteEntity(): NotesEntity? = editingNotesEntity

    private lateinit var editingNotesEntity: NotesEntity


    override fun onCreate(savedInstanceState: Bundle?) {
        editingNotesEntity = intent.getParcelableExtra("note")!!
        super.onCreate(savedInstanceState)
    }

    companion object {
        private val TAG = EditNoteActivity::class.java.simpleName
        fun newIntent(context: Context, notesEntity: NotesEntity): Intent {
            return Intent(context, EditNoteActivity::class.java).apply {
                putExtra("note", notesEntity)
            }
        }
    }
}