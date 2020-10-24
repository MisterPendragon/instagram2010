package com.example.instagramclone.Adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramclone.Models.Comment
import com.example.instagramclone.R
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class CommentAdapter(private val mContext:Context,private val mComment:MutableList<Comment>?) :RecyclerView.Adapter<CommentAdapter.ViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentAdapter.ViewHolder {

    }

    override fun onBindViewHolder(holder: CommentAdapter.ViewHolder, position: Int) {


    override fun getItemCount(): Int {
        return mComment!!.size
    }



    inner class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView){
        var imageProfile: CircleImageView
        var userNameTV: TextView
        var commentTV: TextView
        init {
            imageProfile = itemView.findViewById(R.id.avatarCommentModel)
            userNameTV= itemView.findViewById(R.id.usernameIdComment)
            commentTV = itemView.findViewById(R.id.commentOfUserId) }
    }





}