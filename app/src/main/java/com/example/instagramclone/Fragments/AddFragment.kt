package com.example.instagramclone.Fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.instagramclone.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.fragment_add.*

class addFragment : Fragment() {
    private var storagePostPic: StorageReference = FirebaseStorage.getInstance().reference.child("posts")
    private var database =  FirebaseDatabase.getInstance().reference
    private var auth: FirebaseAuth = Firebase.auth
    var user = auth.currentUser
    private  var imageUri: Uri?=null
    private var linkUrlOfPosts=""
    private var description:EditText?=null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // загружаем фотку
        postId.setOnClickListener { addNewPhoto() }
        // загружаем фотку в БД
        checkIdOfAddFragment.setOnClickListener {uploadToFireBase() }
        description = getView()?.findViewById(R.id.textOfDescription)
    }



    private fun addNewPhoto() {
        CropImage.activity().setAspectRatio(1,1).start(requireContext(),this@addFragment)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && data!=null ){
            val result = CropImage.getActivityResult(data)
            imageUri = result.uri
            postId.setImageURI(imageUri)
        } }

    private fun uploadToFireBase(){
    val random = System.currentTimeMillis().toString()
    val fileRef = storagePostPic.child(System.currentTimeMillis().toString())
        fileRef.putFile(imageUri!!).addOnCompleteListener{
            if (it.isSuccessful){
                fileRef.downloadUrl.addOnCompleteListener {
                    if (it.isSuccessful){
                        val ref = FirebaseDatabase.getInstance().reference.child("posts")
                        val key = ref.push().key.toString()
                        linkUrlOfPosts = it.result.toString()

                        database.child("posts").child(key).child("postImage").setValue(linkUrlOfPosts)
                        database.child("posts").child(key).child("postId").setValue(key)
                        database.child("posts").child(key).child("description").setValue(description?.text.toString().toLowerCase())
                        database.child("posts").child(key).child("publisher").setValue(FirebaseAuth.getInstance().currentUser!!.uid)

                    }
                }
            }
        }
            // Переход к другому фрагменту
        findNavController().navigate(R.id.homeFragment,null)
    }
}