package com.example.catconnect.ui.feed

import androidx.lifecycle.ViewModel
import com.example.catconnect.data.repo.FakeRepository

class FeedViewModel : ViewModel() {
    val posts = FakeRepository.posts
}
