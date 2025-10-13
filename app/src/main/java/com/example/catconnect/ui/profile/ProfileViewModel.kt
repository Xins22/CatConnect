package com.example.catconnect.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.example.catconnect.data.repo.FakeRepository
import com.example.catconnect.data.model.Post

class ProfileViewModel : ViewModel() {
    val user = FakeRepository.currentUser

    // filter LiveData posts -> hanya milik user saat ini
    val myPosts = FakeRepository.posts.map { list: List<Post> ->
        list.filter { post -> post.userId == user.id }
    }
}
