package com.example.instagramclone.Models


class Post{
    private var description: String = ""
    private var postId: String = ""
    private var postImage: String = ""
    private var publisher: String = ""

    constructor()

    constructor(description: String, postId: String, postImage: String, publisher: String) {
        this.description = description
        this.postId = postId
        this.postImage = postImage
        this.publisher = publisher
    }
    fun getDescription(): String{
        return description
    }
    fun getPostId(): String{
        return postId
    }
    fun getPostImage(): String{
        return postImage
    }
    fun getPublisher(): String{
        return publisher
    }
    fun setDescription(description:String){
        this.description = description
    }
    fun setPostId(postId: String){
        this.postId = postId
    }
    fun setPostImage(postImage: String){
        this.postImage = postImage
    }
    fun setPublisher(publisher: String){
        this.publisher = publisher
    }

}