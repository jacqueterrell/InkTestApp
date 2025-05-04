package com.example.inktestapp.userInterface.notesList

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.inktestapp.data.NotesEntity
import com.example.inktestapp.databinding.NotesListRowBinding
import com.example.inktestapp.utils.EditorUtils

class NotesAdapter(
    val notesList: MutableList<NotesEntity>,
    private val onNoteClicked: (NotesEntity) -> Unit
) : RecyclerView.Adapter<NotesAdapter.NotesViewHolder>() {

    inner class NotesViewHolder(private val binding: NotesListRowBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(notesEntity: NotesEntity) {
            val ctx = binding.root.context
            val bitmap = EditorUtils.retrieveSavedBitMap(ctx, notesEntity.fileName)
            binding.tvTitle.text = notesEntity.title
            Glide.with(ctx)
                .load(bitmap)
                .into(binding.ivImage)

            binding.root.setOnClickListener {
                onNoteClicked.invoke(notesEntity)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = NotesListRowBinding.inflate(inflater, parent, false)
        return NotesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        holder.bind(notesList[position])
    }

    override fun getItemCount(): Int {
        return notesList.size
    }

    fun refresh(notes: MutableList<NotesEntity>) {
        notesList.apply {
            clear()
            addAll(notes)
        }
        notifyDataSetChanged()
    }

    fun insertDeletedNote(position: Int, notesEntity: NotesEntity) {
        notesList.add(position, notesEntity)
        notifyItemInserted(position)
    }
}