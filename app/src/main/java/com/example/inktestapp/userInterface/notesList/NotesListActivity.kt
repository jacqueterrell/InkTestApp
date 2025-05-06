package com.example.inktestapp.userInterface.notesList

import android.content.Intent
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.inktestapp.R
import com.example.inktestapp.data.AppDataBase
import com.example.inktestapp.data.NotesDbHelper
import com.example.inktestapp.data.NotesEntity
import com.example.inktestapp.databinding.NoteListLayoutBinding
import com.example.inktestapp.userInterface.createNote.CreateNoteActivity
import com.example.inktestapp.userInterface.editNote.EditNoteActivity
import com.example.inktestapp.userInterface.settings.SettingsActivity
import com.example.inktestapp.utils.DialogHelper
import com.google.android.material.snackbar.Snackbar
import com.myscript.iink.Editor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel

class NotesListActivity: AppCompatActivity() {

    private lateinit var binding: NoteListLayoutBinding
    private var notesAdapter: NotesAdapter? = null
    private val viewModel: NotesListViewModel by viewModel()
    var notesList: MutableList<NotesEntity> = mutableListOf()

    val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
        0,
        ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT,
    ) {
        var oldPosition = -1
        var newPosition = -1

        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
            val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            val swipeFlags = ItemTouchHelper.LEFT // or 0 if you want to disable swipe
            return makeMovementFlags(dragFlags, swipeFlags)
        }

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            val fromPos = viewHolder.adapterPosition
            val toPos = target.adapterPosition
            newPosition = toPos
            oldPosition = fromPos
            val db = AppDataBase.getInstance(this@NotesListActivity)
            val notesEntity = notesList.removeAt(fromPos)
            notesList.add(toPos, notesEntity)
            notesList.forEachIndexed { index, notesEntity ->
                notesEntity.listIndex = index
            }
            // Swap items and notify adapter
            notesAdapter?.notifyItemMoved(fromPos, toPos)
            newPosition = target.adapterPosition
            return false
        }

        override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
            val db = AppDataBase.getInstance(this@NotesListActivity)
            when (actionState) {
                ItemTouchHelper.ACTION_STATE_DRAG -> {
                    viewHolder?.adapterPosition?.let { oldPosition = it }
                }
                ItemTouchHelper.ACTION_STATE_IDLE -> {
                    if (oldPosition != -1 && newPosition != -1 && oldPosition != newPosition) {
                        lifecycleScope.launch {
                            viewModel.isUpdatingIndex = true
                            viewModel.updateNotesAfterIndexChange(db, notesList)
                        }
                        oldPosition = -1
                        newPosition = -1
                    }
                }
            }
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            if (direction == ItemTouchHelper.LEFT) {
                val position = viewHolder.adapterPosition
                val notesEntity = notesList[position]
                val title = "Delete Note"
                val msg = "Are you sure you want to delete this note?"
                DialogHelper.showAlertDialogTwoActions(this@NotesListActivity, title, msg,"Cancel", "Delete", {
                    notesList.removeAt(position)
                    notesAdapter?.refresh(notesList)
                    showUndoDeleteSnackbar(position, notesEntity)
                }, {
                    notesAdapter?.notifyItemChanged(position)
                })
            }
        }

        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {

            val itemView = viewHolder.itemView
            val background = ColorDrawable(Color.RED)
            val icon = ContextCompat.getDrawable(recyclerView.context, R.drawable.ic_delete)
            icon?.setTint(Color.WHITE)
            val scale = 1.5f
            val iconWidth = (icon?.intrinsicWidth?.times(scale))?.toInt() ?: 0
            val iconHeight = (icon?.intrinsicHeight?.times(scale))?.toInt() ?: 0
            val iconMargin = 32
            val iconTop = itemView.top + (itemView.height - iconHeight) / 2
            val iconBottom = iconTop + iconHeight
            val iconRight = itemView.right - 32  // right margin
            val iconLeft = iconRight - iconWidth

            // Swipe to the right
           if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE && dX < 0) {
                background.setBounds(
                    itemView.right + dX.toInt(), itemView.top,
                    itemView.right, itemView.bottom
                )
               icon?.setBounds(iconLeft, iconTop, iconRight, iconBottom)

           } else {
                background.setBounds(0, 0, 0, 0)
                icon?.setBounds(0, 0, 0, 0)
            }

            background.draw(c)
            icon?.draw(c)

            val paint = Paint().apply {
                color = Color.WHITE
                textSize = 40f
                isAntiAlias = true
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textAlign = Paint.Align.RIGHT
            }

            val labelX = iconLeft - 20f  // 20px spacing between icon and text
            val labelY = iconTop + (icon?.intrinsicHeight ?: 0) / 2f + 15f  // vertical center-ish
            c.drawText("Delete", labelX, labelY, paint)

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = NoteListLayoutBinding.inflate(layoutInflater)
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.rvNotes)
        setContentView(binding.root)
        setUpUI()
        setUpClickListeners()
    }

    private fun showUndoDeleteSnackbar(postion: Int, notesEntity: NotesEntity) {
        Snackbar.make(binding.rvNotes, "Note Deleted", Snackbar.LENGTH_LONG)
            .setAction("Undo") {
                notesList.add(postion, notesEntity)
                notesAdapter?.insertDeletedNote(postion, notesEntity)
                binding.rvNotes.scrollToPosition(postion)
            }
            .addCallback(object : Snackbar.Callback() {
                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    if (event != DISMISS_EVENT_ACTION) {
                        lifecycleScope.launch {
                            NotesDbHelper.deleteNote(this@NotesListActivity, notesEntity)
                        }
                    }
                }
            })
            .setAnchorView(binding.fabAnchor)
            .show()
    }

    private fun setUpUI() {
        val db = AppDataBase.getInstance(this)
        val displayMetrics = Resources.getSystem().displayMetrics
        val screenWidthDp = displayMetrics.widthPixels / displayMetrics.density
        val desiredItemWidthDp = 250
        val spanCount = (screenWidthDp / desiredItemWidthDp).toInt().coerceAtLeast(1)

        notesAdapter = NotesAdapter(notesList, { notesEntity ->
            onNoteSelected(notesEntity)
        })
        binding.rvNotes.apply {
            layoutManager = GridLayoutManager(context, spanCount)
            adapter = notesAdapter
        }

        lifecycleScope.launch(Dispatchers.IO) {
            val list = NotesDbHelper.getAllNotes(this@NotesListActivity)
            viewModel.getNotesFlow(db, lifecycleScope).collect { notes ->
                withContext(Dispatchers.Main) {
                    if (viewModel.isUpdatingIndex) {
                        viewModel.isUpdatingIndex = false
                    } else {
                        if (notes.isEmpty()) {
                            binding.rvNotes.visibility = View.INVISIBLE
                            binding.fabAddNote.visibility = View.GONE
                            binding.layoutEmptyNotes.visibility = View.VISIBLE
                        } else {
                            binding.rvNotes.visibility = View.VISIBLE
                            binding.fabAddNote.visibility = View.VISIBLE
                            binding.layoutEmptyNotes.visibility = View.GONE
                        }
                        notesList = notes
                        notesList.sortBy { it.listIndex }
                        notesAdapter?.refresh(notesList)
                    }
                }
            }
        }
    }

    private fun onNoteSelected(notesEntity: NotesEntity) {
        val intent = EditNoteActivity.newIntent(this, notesEntity)
        startActivity(intent)
    }

    private fun setUpClickListeners() {
        binding.btnCreateNote.setOnClickListener {
            val intent = CreateNoteActivity.newIntent(this)
            startActivity(intent)
        }
        binding.fabAddNote.setOnClickListener {
            val intent = CreateNoteActivity.newIntent(this)
            startActivity(intent)
        }
        binding.btnSettings.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }
}