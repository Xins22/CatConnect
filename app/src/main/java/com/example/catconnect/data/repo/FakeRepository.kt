package com.example.catconnect.data.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.catconnect.data.model.Post
import com.example.catconnect.data.model.User

object FakeRepository {
    val currentUser = User("u1","Alif","https://picsum.photos/200?catface","Cat lover")

    private val _posts = MutableLiveData(
        listOf(
            Post("p1","u1","Snowy","Persian",8,"Snowy suka tidur 😺","https://picsum.photos/seed/c1/800/500",12),
            Post("p2","u1","Milo","Siamese",14,"Main laser tiap sore","https://picsum.photos/seed/c2/800/500",4)
        )
    )
    val posts: LiveData<List<Post>> = _posts

    fun addPost(p: Post) { _posts.value = _posts.value.orEmpty() + p }
    fun updatePost(p: Post) { _posts.value = _posts.value.orEmpty().map { if (it.id==p.id) p else it } }
    fun deletePost(id: String) { _posts.value = _posts.value.orEmpty().filterNot { it.id==id } }
    fun getPost(id: String) = _posts.value?.firstOrNull { it.id==id }
}
