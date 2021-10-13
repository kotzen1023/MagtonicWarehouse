package com.magtonic.magtonicwarehouse.ui.homegrid

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeGridViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is HomeGrid Fragment"
    }
    val text: LiveData<String> = _text
}