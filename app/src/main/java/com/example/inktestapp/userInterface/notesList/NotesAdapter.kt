package com.example.inktestapp.userInterface.notesList

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.inktestapp.data.NotesEntity
import com.example.inktestapp.databinding.NotesListRowBinding

class NotesAdapter(
    private val notesList: MutableList<NotesEntity>,
    private val onNoteClicked: (NotesEntity) -> Unit
) : RecyclerView.Adapter<NotesAdapter.NotesViewHolder>() {

    inner class NotesViewHolder(private val binding: NotesListRowBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(notesEntity: NotesEntity) {

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
}