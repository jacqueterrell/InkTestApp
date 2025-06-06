package com.example.inktestapp.userInterface.createNote

import android.annotation.SuppressLint
import android.app.Application
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.example.inktestapp.R
import com.example.inktestapp.data.NotesDbHelper
import com.example.inktestapp.data.NotesEntity
import com.example.inktestapp.databinding.CreateNoteLayoutBinding
import com.example.inktestapp.userInterface.AppApplication
import com.example.inktestapp.utils.AppSharedPrefs
import com.example.inktestapp.utils.DialogHelper
import com.example.inktestapp.utils.EditorUtils
import com.example.inktestapp.utils.fadeIntoVisibility
import com.example.inktestapp.utils.fadeOutToInvisible
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
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.java.KoinJavaComponent
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.nio.charset.StandardCharsets

abstract class AlterNoteBaseActivity : AppCompatActivity() {

    abstract fun isEditingNote(): Boolean
    abstract fun getTag(): String
    abstract fun getNoteTitle(): String
    abstract fun getEditingNoteEntity(): NotesEntity?

    private lateinit var binding: CreateNoteLayoutBinding
    private var sharedPrefs: AppSharedPrefs = KoinJavaComponent.get(AppSharedPrefs::class.java)

    private val viewModel: AlterNoteViewModel by viewModel()
    private var editor: Editor? = null
    private var editorData: EditorData? = null
    private var contentPart: ContentPart? = null
    private var contentPackage: ContentPackage? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CreateNoteLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        binding.editorView.post {
            setUpUI()
            setOnClickListeners()
            val renderer = binding.editorView.renderer
            renderer?.let {
                renderer.setViewOffset(0f, 0f)
                binding.editorView.getRenderer()?.setViewScale(1f)
                binding.editorView.setVisibility(View.VISIBLE)
                editor!!.part = contentPart
            }
        }
    }

    private fun provideEditorTheme(application: Application): String {
        application.resources.openRawResource(R.raw.theme).use { input ->
            ByteArrayOutputStream().use { output ->
                input.copyTo(output)
                return output.toString(StandardCharsets.UTF_8.name())
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setUpUI() {
        binding.tvToolbarTitle.text = getNoteTitle()
        val engine = AppApplication.getEngine()

        // configure recognition
        val conf = engine.configuration
        val confDir = "zip://$packageCodePath!/assets/conf"
        conf.setStringArray("configuration-manager.search-path", arrayOf(confDir))
        val tempDir = filesDir.path + File.separator + "tmp"
        conf.setString("content-package.temp-folder", tempDir)


        // load fonts
        val assetManager = applicationContext.assets
        val typefaceMap = FontUtils.loadFontsFromAssets(assetManager)
        val editorBinding = EditorBinding(engine, typefaceMap)

        editorData = editorBinding.openEditor(binding.editorView)
        editor = editorData?.editor
        binding.editorView.editor = editor

        setCachedUIValues()

        binding.editorView.setOnHoverListener { view, motionEvent ->
            if (motionEvent.getToolType(0) == MotionEvent.TOOL_TYPE_STYLUS) {
                val usingEraser = binding.switchErasePen.isChecked
                when (motionEvent.action) {
                    MotionEvent.ACTION_HOVER_MOVE -> binding.cursorOverlay.updateCursor(
                        motionEvent.x,
                        motionEvent.y,
                        usingEraser
                    )

                    MotionEvent.ACTION_HOVER_EXIT -> binding.cursorOverlay.hideCursor()
                }
            } else if (motionEvent.getToolType(0) == MotionEvent.TOOL_TYPE_ERASER) {
                when (motionEvent.action) {
                    MotionEvent.ACTION_HOVER_MOVE -> binding.cursorOverlay.updateCursor(
                        motionEvent.x,
                        motionEvent.y,
                        true
                    )

                    MotionEvent.ACTION_HOVER_EXIT -> binding.cursorOverlay.hideCursor()
                }
            }
            false
        }

        editor?.addListener(object : IEditorListener {
            override fun partChanging(p0: Editor, p1: ContentPart?, p2: ContentPart?) {
            }

            override fun partChanged(p0: Editor) {
                if (binding.layoutOptions.isVisible) {
                    binding.layoutOptions.fadeOutToInvisible()
                }
                invalidateIconButtons()
            }

            override fun contentChanged(p0: Editor, p1: Array<out String>) {
                if (binding.layoutOptions.isVisible) {
                    binding.layoutOptions.fadeOutToInvisible()
                }
                invalidateIconButtons()
            }

            override fun onError(p0: Editor, p1: String, p2: EditorError, p3: String) {
            }

            override fun selectionChanged(p0: Editor) {
            }

            override fun activeBlockChanged(p0: Editor, p1: String) {
            }

        })

        setInputMode()

        val ts = System.currentTimeMillis()

        val contentFileName = if (isEditingNote()) {
            getEditingNoteEntity()!!.contextFileName
        } else {
            "file_$ts"
        }
        viewModel.contentFileName = contentFileName
        val file = File(filesDir, contentFileName)
        try {
            contentPackage = if (isEditingNote()) {
                engine.openPackage(file)
            } else {
                engine.createPackage(file)
            }
            // Choose type of content (possible values are: "Text Document", "Text", "Diagram", "Math", "Drawing" and "Raw Content")
            if (isEditingNote()) {
                contentPart = contentPackage!!.getPart(0)
                editor?.part = contentPart
            } else {
                contentPart = contentPackage!!.createPart("Text Document")
            }
        } catch (e: IOException) {
            Log.e(
                getTag(),
                "Failed to open package \"$contentFileName\"", e
            )
        } catch (e: IllegalArgumentException) {
            Log.e(
                getTag(),
                "Failed to open package \"$contentFileName\"", e
            )
        }
        viewModel.hideSummaryScreen.observe(this) {
            binding.layoutSummaryContainer.fadeOutToInvisible()
        }
        binding.switchSmoothPen.isChecked = sharedPrefs.hasEnabledSmoothPen
    }

    private fun setCachedUIValues() {
        if (isEditingNote()) {
            EditorUtils.setExistingCachedNoteUI(editor, getEditingNoteEntity()!!, binding)
        } else {
            EditorUtils.setNewlyCreatedNoteUI(editor,sharedPrefs, binding)
        }
    }

    private fun setOnClickListeners() {
        binding.btnUndo.setOnClickListener { editor?.undo() }
        binding.btnRedo.setOnClickListener { editor?.redo() }
        binding.btnClear.setOnClickListener { editor?.clear() }
        binding.btnMore.setOnClickListener {
            if (binding.layoutOptions.isVisible) {
                binding.layoutOptions.fadeOutToInvisible()
            } else {
                binding.layoutOptions.fadeIntoVisibility()
            }
        }
        binding.btnSave.setOnClickListener {
            binding.layoutOptions.fadeOutToInvisible()
            val title = if (isEditingNote()) {
                getEditingNoteEntity()!!.title
            } else {
                ""
            }
            DialogHelper.showSaveNoteDialog(title, this, { noteTitle ->
                lifecycleScope.launch(Dispatchers.IO) {

                    if (isEditingNote()) {
                        getEditingNoteEntity()?.apply {
                            this.title = noteTitle
                            this.message = viewModel.text
                        }

                        val bitmap = EditorUtils.getBitmapFromEditorView(binding.editorView)
                        EditorUtils.saveBitMap(
                            this@AlterNoteBaseActivity,
                            bitmap,
                            getEditingNoteEntity()!!.fileName
                        )
                        NotesDbHelper.updateNote(
                            this@AlterNoteBaseActivity,
                            getEditingNoteEntity()!!
                        )

                        contentPackage?.save()
                    } else {
                        val currentList = NotesDbHelper.getAllNotes(this@AlterNoteBaseActivity)
                        val newIndex = currentList.size + 1
                        val ts = System.currentTimeMillis()

                        val fileName = "${noteTitle}_$ts"
                        val notesEntity = NotesEntity(
                            title = noteTitle,
                            message = viewModel.text,
                            fileName = fileName,
                            contextFileName = viewModel.contentFileName,
                            listIndex = newIndex,
                            isDarkTheme = sharedPrefs.hasEnabledDarkTheme,
                            scalingValue = sharedPrefs.penSensitivityValue
                        )
                        val bitmap = EditorUtils.getBitmapFromEditorView(binding.editorView)
                        EditorUtils.saveBitMap(this@AlterNoteBaseActivity, bitmap, fileName)
                        NotesDbHelper.addNote(this@AlterNoteBaseActivity, notesEntity)
                    }

                    contentPackage?.save()

                    withContext(Dispatchers.Main) {
                        onBackPressed()
                    }
                    //  Log.i(getTag(), "bitmap saved to $fileName")
                }
            })
        }
        binding.btnShowSummary.setOnClickListener {
            binding.layoutOptions.fadeOutToInvisible()
            val summary = EditorUtils.extractKeySentences(viewModel.text.replace("\n", " "))
            viewModel.summaryTextList.postValue(summary)
            binding.layoutSummaryContainer.fadeIntoVisibility()
            Log.i(getTag(), "Summary")
        }
        binding.btnSearch.setOnClickListener {
            binding.layoutOptions.fadeOutToInvisible()
        }
        binding.switchErasePen.setOnCheckedChangeListener { compoundButton, isChecked ->
            if (isChecked) {
                setInputMode(InputController.INPUT_MODE_ERASER)
            } else {
                setInputMode(InputController.INPUT_MODE_AUTO)
            }
        }
    }

    private fun setInputMode(inputMode: Int = InputController.INPUT_MODE_AUTO) {
        editorData?.inputController?.inputMode = inputMode
    }

    override fun onDestroy() {
        val editor = editorData!!.editor
        if (editor != null) {
            editor.renderer.close()
            editor.close()
        }
        binding.editorView.setOnTouchListener(null)
        binding.editorView.setEditor(null)

        if (contentPart != null) {
            contentPart!!.close()
            contentPart = null
        }
        if (contentPackage != null) {
            contentPackage!!.close()
            contentPackage = null
        }


        super.onDestroy()

    }

    private fun invalidateIconButtons() {
        val editor = editorData!!.editor ?: return
        val canUndo = editor.canUndo()
        val canRedo = editor.canRedo()
        runOnUiThread {
            binding.btnUndo.isEnabled = canUndo
            binding.btnRedo.isEnabled = canRedo
            binding.btnClear.isEnabled = contentPart != null
            val wordCount = editor.getWordCount()
            val text = editor.getText().replace("\n", " ")
            binding.tvNotesToText.text = text

            viewModel.text = text
            if (wordCount > 0) {
                binding.btnMore.visibility = View.VISIBLE
            } else {
                binding.btnMore.visibility = View.INVISIBLE
            }
            Log.i(getTag(), "word count = $wordCount")
            Log.i(getTag(), "word text = $text")
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (binding.layoutSummaryContainer.isVisible) {
                binding.layoutSummaryContainer.fadeOutToInvisible()
                return false
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}