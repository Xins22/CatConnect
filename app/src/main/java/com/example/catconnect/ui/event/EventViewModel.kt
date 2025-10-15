package com.example.catconnect.ui.event

import androidx.lifecycle.ViewModel
import com.example.catconnect.data.repo.FakeRepository

class EventViewModel : ViewModel() {
    val events = FakeRepository.events
}
