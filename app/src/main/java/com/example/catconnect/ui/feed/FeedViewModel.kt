package com.example.catconnect.ui.feed

import androidx.lifecycle.ViewModel
import com.example.catconnect.data.repo.FakeRepository
import androidx.lifecycle.*
import com.example.catconnect.data.model.Post


class FeedViewModel : ViewModel() {
    val posts: LiveData<List<Post>> = FakeRepository.posts

    private val _query = MutableLiveData("")
    val query: LiveData<String> = _query
    fun setQuery(q: String) { _query.value = q }

    val filteredPosts: LiveData<List<Post>> = MediatorLiveData<List<Post>>().apply {
        fun recompute() {
            val q = _query.value.orEmpty().trim().lowercase()
            val src = posts.value.orEmpty()
            value = if (q.isBlank()) src else src.filter { p ->
                p.title.lowercase().contains(q) ||
                        p.breed.lowercase().contains(q) ||
                        p.caption.lowercase().contains(q)
            }
        }
        addSource(posts) { recompute() }
        addSource(_query) { recompute() }
    }
}
