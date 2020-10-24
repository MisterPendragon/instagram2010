package com.example.instagramclone.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.instagramclone.Activity.EditProfileActivity
import com.example.instagramclone.R
import com.example.instagramclone.Models.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_person.*
import kotlinx.android.synthetic.main.fragment_person.editProfileId

class personFragment : Fragment() {
    private var storage: StorageReference = FirebaseStorage.getInstance().getReference("avatar")
    private var database =  FirebaseDatabase.getInstance().reference
    private var auth: FirebaseAuth = Firebase.auth
    var user = auth.currentUser
    var name:TextView?=null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        return inflater.inflate(R.layout.fragment_person, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ПОЛУЧАЕМ ДАННЫЕ ПОЛЬЗОВАТЕЛЯ
        database.child("users").child(user!!.uid).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(UserModel::class.java) // Снимок сконвертится по конструктору нашего класса
                name = getView()?.findViewById(R.id.theNickNameId)
                name?.setText(user!!.getName(), TextView.BufferType.EDITABLE)
                getFollowers() // Получаем подписчиков
                getFollowings() // Получаем наши подписки
                // Условия загрузки аватарки
                if(user?.getUrl().equals("default")) {
                    Picasso.get()
                        .load(R.drawable.avatar)
                        .into(avaId)
                } else {

                    Picasso.get()
                        .load(user?.getUrl())
                        .into(avaId)
                }
            }
            override fun onCancelled(error: DatabaseError) {}

        })


        // Обработка нажатия
        editProfileId.setOnClickListener {
            activity?.let{
                val intent = Intent (it, EditProfileActivity::class.java)
                it.startActivity(intent)
                it.overridePendingTransition(R.anim.zoom_in, R.anim.static_anim)
            }
        }
    }

    // ПРОЧЕЕ
    private fun getFollowers() {
        val followersRef = FirebaseDatabase.getInstance().reference
            .child("follow")
            .child(user!!.uid).child("followers")

        followersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    numberOfSubscribersId.text = snapshot.childrenCount.toString()
                }
            }

            override fun onCancelled(error: DatabaseError)
            {}

        })

    }
    private fun getFollowings() {
        val followersRef = FirebaseDatabase.getInstance().reference
            .child("follow")
            .child(user!!.uid).child("following")

        followersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // exists() Возвращает true, если снимок содержит ненулевое значение.
                if (snapshot.exists()){
                    numberOfYourSubscriptionsId!!.text = snapshot.childrenCount.toString()
                }

            }
            override fun onCancelled(error: DatabaseError)
            {}

        })

    }

}