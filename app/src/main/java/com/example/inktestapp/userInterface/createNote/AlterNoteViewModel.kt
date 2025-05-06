package com.example.inktestapp.userInterface.createNote

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AlterNoteViewModel: ViewModel() {
    val summaryTextList = MutableLiveData<List<String>>()
    val hideSummaryScreen = MutableLiveData<Boolean>()
    val summaryTextLiveDate = MutableLiveData<String>()
    var contentFileName: String = ""
    var text = ""
}