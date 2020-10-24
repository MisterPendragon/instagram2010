package com.example.instagramclone.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramclone.Adapters.CommentAdapter
import com.example.instagramclone.Models.Comment
import com.example.instagramclone.Models.UserModel
import com.example.instagramclone.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_comments.*
import kotlinx.android.synthetic.main.activity_guest_page.*

class CommentsActivity : AppCompatActivity() {
    private var postId = ""
    private var publisherId = ""
    private var fireBaseCurrentUser = FirebaseAuth.getInstance().currentUser
    private var commentAdapter: CommentAdapter? = null
    private var commentList:MutableList<Comment>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)
        val intent = intent
        postId = intent.getStringExtra("postId").toString()
        publisherId = intent.getStringExtra("publisherId").toString()

        // ПОЛУЧАЕМ ИНФУ О ПОЛЬЗОВАТЕЛЕ
        userInfo()
        // ЗАПОЛНЯЕМ COMMENT LIST ДАННЫМИ,ПОЛУЧИВ ИХ С БД
        readComments()
        // ОБРАБАТЫВАЕМ НАЖАТИЕ И ОТПРАВЛЯЕМ КОММЕНТАРИЙ В БД
        publishTextViewOfComment.setOnClickListener(View.OnClickListener { activateButton() })



        // RECYCLER VIEW
        var recyclerView: RecyclerView?=null
        recyclerView = findViewById(R.id.recyclerViewCommentId)
        val linerLayoutManager = LinearLayoutManager(this)
        linerLayoutManager.reverseLayout = true
        recyclerView.layoutManager = linerLayoutManager
        commentList = ArrayList()
        commentAdapter = CommentAdapter(this,commentList)
        recyclerView.adapter = commentAdapter

    }




    // ПРОЧЕЕ

    private fun activateButton() {
        if (editTextOfComment!!.text.toString() != "") {
            publishTextViewOfComment.isClickable = true
            addComment()
        }
    }

    private fun addComment() {
        val commentsRef = FirebaseDatabase.getInstance().reference.child("comments").child(postId)
        val commentsMap = HashMap<String,Any>()
        commentsMap["comment"] = editTextOfComment!!.text.toString()
        commentsMap["publisher"] = fireBaseCurrentUser!!.uid
        commentsRef.push().setValue(commentsMap)
        editTextOfComment.text.clear()
    }

    private fun userInfo() {
        val userRef = FirebaseDatabase.getInstance().reference.child("users").child(fireBaseCurrentUser!!.uid)
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val user = snapshot.getValue<UserModel>(UserModel::class.java)
                    Picasso.get()
                        .load(user!!.getUrl())
                        .into(profileAvaOfComment)

                } }
            override fun onCancelled(error: DatabaseError)
            {}

        })
    }

    private fun readComments() {
        val commentRef = FirebaseDatabase.getInstance().reference
            .child("comments").child(postId)

        commentRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(pO: DataSnapshot) {
                if (pO.exists()){
                    commentList!!.clear()
                    for (snapshot in pO.children){
                        val comment = snapshot.getValue(Comment::class.java)
                        commentList!!.add(comment!!)
                    }
                    commentAdapter!!.notifyDataSetChanged()
                }
            }
            override fun onCancelled(error: DatabaseError)
            {}
        }) }



}