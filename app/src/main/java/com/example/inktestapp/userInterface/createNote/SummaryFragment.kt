package com.example.inktestapp.userInterface.createNote

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.inktestapp.databinding.SummaryLayoutBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class SummaryFragment: Fragment() {

    private lateinit var binding: SummaryLayoutBinding
    private val viewModel: AlterNoteViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = SummaryLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.layoutContainer.setOnClickListener {
            viewModel.hideSummaryScreen.postValue(true)
        }
        viewModel.summaryTextLiveDate.observe(viewLifecycleOwner) {
            binding.tvSummary.text = it
        }
        viewModel.summaryTextList.observe(viewLifecycleOwner) {
            val summaryList = it
            val builder: StringBuilder = StringBuilder()
            for (summary in summaryList) {
                builder.append("\u2022 ").append( "${summary.trim()}\n" )
            }
            binding.tvSummary.text = builder.toString()
        }
    }
}