package com.example.instagramclone.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramclone.Activity.CommentsActivity
import com.example.instagramclone.Models.Post
import com.example.instagramclone.R
import com.example.instagramclone.Models.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class PostAdapter(private val mContext:Context,private val mPost: List<Post>): RecyclerView.Adapter<PostAdapter.ViewHolder>()
{
    private var fireBaseUser:FirebaseUser? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // В 1-ом параметре указывается идентификатор ресурса разметки, который мы собираемся надуть.
        // В 2-ом параметре указывается корневой компонент, к которому нужно присоединить надутые объекты.
        // В 3-ем параметре (если он используется) указывается, нужно ли присоединять надутые объекты к корневому элементу.
        val view = LayoutInflater.from(mContext).inflate(R.layout.posts_for_recycler_view,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        fireBaseUser = FirebaseAuth.getInstance().currentUser
        val post = mPost[position]
        // ЗАГРУЗКА ФОТО
        Picasso.get()
            .load(post.getPostImage())
            .into(holder.postImage)

        // ПОЯВЛЕНИЕ ОПИСАНИЯ
        if (post.getDescription().equals("")){
            holder.description.visibility = View.GONE
        } else {
            holder.description.visibility = View.GONE
            holder.description.setText(post.getDescription())
        }

        // ИНФОРМАЦИЯ О ТОМ КТО ЗАГРУЗИЛ ПОСТ
        getInfoAboutPublisher(holder.profileImage,holder.userName,holder.publisher,post.getPublisher())

        // ЦВЕТ КНОПКИ LIKE
        isLike(post.getPostId(),holder.likeButton)

        // КОЛИЧЕСТВО ЛАЙКОВ
        numberOfLikes(holder.like,post.getPostId())

        // ОБРАБОТКА НАЖАТИЯ НА КНОПКУ LIKE
        holder.likeButton.setOnClickListener {
            if (holder.likeButton.tag == "Like")
        {
                 FirebaseDatabase.getInstance().reference.child("Likes").child(post.getPostId()).child(fireBaseUser!!.uid).setValue(true)
        } else
            {
                 FirebaseDatabase.getInstance().reference.child("Likes").child(post.getPostId()).child(fireBaseUser!!.uid).removeValue()
            }
        }
        // ОБРАБОТКА НАЖАТИЯ НА COMMENTS 1
        holder.commentButton.setOnClickListener {
            val intentComment = Intent(mContext,CommentsActivity::class.java)
            intentComment.putExtra("postId",post.getPostId())
            intentComment.putExtra("publisherId",post.getPublisher())
            mContext.startActivity(intentComment)
        }
        // УСТАНОВКА КОЛИЧЕСТВА КОММЕНТАРИЕВ
        getTotalComments(holder.comment,post.getPostId())

        // ОБРАБОТКА НАЖАТИЯ НА COMMENTS 2
        holder.comment.setOnClickListener {
            val intentCommentText = Intent(mContext,CommentsActivity::class.java)
            intentCommentText.putExtra("postId",post.getPostId())
            intentCommentText.putExtra("publisherId",post.getPublisher())
            mContext.startActivity(intentCommentText)

        }
    }

    override fun getItemCount(): Int {
        return mPost.size
    }

    inner class ViewHolder(@NonNull itemView:View) : RecyclerView.ViewHolder(itemView){
    var profileImage:CircleImageView
    var postImage:ImageView
    var likeButton:ImageView
    var commentButton:ImageView
    var userName:TextView
    var like:TextView
    var publisher:TextView
    var description:TextView
    var comment:TextView
    // Основной конструктор не может содержать в себе исполняемого кода.Инициализирующий код может быть помещён в init.
    init {
        profileImage = itemView.findViewById(R.id.user_profile_image_search)
        postImage = itemView.findViewById(R.id.post_image_home)
        likeButton = itemView.findViewById(R.id.post_image_like_btn)
        commentButton = itemView.findViewById(R.id.post_image_comment_btn)
        userName = itemView.findViewById(R.id.user_name_search)
        like= itemView.findViewById(R.id.likes)
        publisher = itemView.findViewById(R.id.publisher)
        description = itemView.findViewById(R.id.description)
        comment= itemView.findViewById(R.id.comments)
    }
}

    // ПРОЧЕЕ
    private fun getInfoAboutPublisher(profileImage: CircleImageView, userName: TextView, publisher: TextView, publisherId: String) {
    val usersRef = FirebaseDatabase.getInstance().reference.child("users").child(publisherId)
        usersRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
            if (snapshot.exists()){
            val user = snapshot.getValue<UserModel>(UserModel::class.java)
                Picasso.get()
                    .load(user!!.getUrl())
                    .into(profileImage)
                userName.text = user.getName()
                publisher.text = user.getNameAndSurname()
            } }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun isLike(postId: String, likeButton: ImageView) {
    val fireBaseUser = FirebaseAuth.getInstance().currentUser
        val likeRef =  FirebaseDatabase.getInstance().reference.child("Likes").child(postId)
        likeRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(pO: DataSnapshot) {
                if (pO.child(fireBaseUser!!.uid).exists()){
                likeButton.setImageResource(R.drawable.ic_like_red)
                    likeButton.tag = "Liked"
                } else
                {
                    likeButton.setImageResource(R.drawable.ic_like)
                    likeButton.tag = "Like"
                }
            }
            override fun onCancelled(error: DatabaseError)
            {}
        })
    }

    private fun numberOfLikes(like: TextView, postId: String) {
        val likeRef =  FirebaseDatabase.getInstance().reference.child("Likes").child(postId)

        likeRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(pO: DataSnapshot) {
                if (pO.exists()){
                like.text = pO.childrenCount.toString() + " это нравится"
                }
            }
            override fun onCancelled(error: DatabaseError)
            {}
        })
    }

    private fun getTotalComments(comment: TextView, postId: String) {
        val commentsRef =  FirebaseDatabase.getInstance().reference.child("comments").child(postId)

        commentsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(pO: DataSnapshot) {
                if (pO.exists()){
                    comment.text = "Посмотреть " + pO.childrenCount.toString() + " комментарий(ев)"
                }
            }
            override fun onCancelled(error: DatabaseError)
            {}
        })
    }
}