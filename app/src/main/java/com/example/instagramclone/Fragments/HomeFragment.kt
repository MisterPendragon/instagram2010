package com.example.instagramclone.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramclone.Adapters.PostAdapter
import com.example.instagramclone.Models.Post
import com.example.instagramclone.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeFragment : Fragment() {
    private var postAdapter:PostAdapter? = null
    private var postList:MutableList<Post>? = null
    private var followingList:MutableList<String> = mutableListOf()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // 1.Находим наш ресайклВью
        var recyclerView:RecyclerView?=null
        recyclerView = view.findViewById(R.id.recyclerViewHomeId)
        // 2. Указываем вид LayoutManager
        val linerLayoutManager = LinearLayoutManager(context)
        // 3. reverseLayout Если установлено значение true, первый элемент размещается в конце  интерфейса, второй элемент размещается перед ним
        linerLayoutManager.reverseLayout = true
        // 4.stackFromEnd Заполняет все снизу
        linerLayoutManager.stackFromEnd = true
        recyclerView.layoutManager = linerLayoutManager

        postList = ArrayList()
        postAdapter = context?.let { PostAdapter(it,postList as ArrayList<Post>) }
        recyclerView.adapter = postAdapter
        checkFollowings()
        return view
    }

    private fun checkFollowings() {
        val followingRef= FirebaseDatabase.getInstance().reference
                .child("follow")
                .child(FirebaseAuth.getInstance().currentUser!!.uid).child("following")

            followingRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(pO: DataSnapshot) {
                    if (pO.exists()){
                        followingList.clear()
                        for (snapshot in pO.children){
                            snapshot.key?.let { followingList.add(it)  }
                        }
                        takePosts()
                    }
                }
                override fun onCancelled(error: DatabaseError)
                {}
    })
}

    // Метод добавляющий посты в список который увидят подписчики
    private fun takePosts() {
        val postRef= FirebaseDatabase.getInstance().reference
            .child("posts")
            postRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(pO: DataSnapshot) {
                   postList?.clear()
                    for (snapshot in pO.children) {
                       val post = snapshot.getValue(Post::class.java)
                        for (idVariable in followingList){
                            if (post!!.getPublisher() == idVariable){
                                postList!!.add(post)
                            }
                        }
                    }
                    
                    postAdapter!!.notifyDataSetChanged()
                }
                override fun onCancelled(error: DatabaseError)
                {}
            })

    }
}