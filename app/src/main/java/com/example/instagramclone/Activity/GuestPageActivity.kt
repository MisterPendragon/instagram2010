package com.example.instagramclone.Activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.instagramclone.Models.UserModel
import com.example.instagramclone.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_guest_page.*

class GuestPageActivity : AppCompatActivity() {
    final val TAG:String = "LogInfo"
    private lateinit var profileId:String
    private lateinit var firebaseUser: FirebaseUser


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guest_page)
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        val pref = getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        if (pref != null) {
            this.profileId = pref.getString("profileId","none")!!
        }
        if (profileId != firebaseUser.uid){
            followAndFollowingButtonCheck() // Метод меняет название кнопки
        }
        getFollowers() // Метод получает кол.подписчиков
        getFollowings() // Метод получает кол.подписок
        userInfo() // Метод получает и заполняет инфу о пользователе
        subscribeOfGuestPage.setOnClickListener{subscribeButton()} // Обработка нажатия кнопки "Подписаться"
    }


    // ПРОЧЕЕ

    private fun getFollowers() {
        val followersRef = FirebaseDatabase.getInstance().reference
            .child("follow")
            .child(profileId).child("followers")

        followersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    numberOfSubscribersOfGuestPage.text = snapshot.childrenCount.toString()
                }
            }

            override fun onCancelled(error: DatabaseError)
            {}

        })

    }
    private fun getFollowings() {
        val followersRef = FirebaseDatabase.getInstance().reference
            .child("follow")
            .child(profileId).child("following")

        followersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    numberOfYourSubscriptionsOfGuestPage.text = snapshot.childrenCount.toString()
                }
            }
            override fun onCancelled(error: DatabaseError)
            {}

        })

    }
    private fun userInfo() {
        val userRef = FirebaseDatabase.getInstance().reference.child("users").child(profileId)
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val user = snapshot.getValue<UserModel>(UserModel::class.java)
                         Picasso.get()
                        .load(user!!.getUrl())
                        .into(avaIdOfGuestPage)
                    theNickNameIdOfGuestPage.text = user.getName().toString()
                    nameAndSurnameGuestPage.text = user.getNameAndSurname().toString()
                }
            }
            override fun onCancelled(error: DatabaseError)
            {}

        })
    }

    private fun subscribeButton(){
        if (subscribeOfGuestPage.text.toString() == "Подписаться"){
            firebaseUser.uid.let { it ->
                FirebaseDatabase.getInstance().reference
                    .child("follow")
                    .child(it.toString()).child("following").child(profileId)
                    .setValue(true)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful){
                            // Повтор.код !
                            firebaseUser.uid.let { it ->
                                FirebaseDatabase.getInstance().reference
                                    .child("follow")
                                    .child(profileId).child("followers").child(it.toString())
                                    .setValue(true)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful){

                                        }
                                    }
                            }
                        }
                    }}}
        else {
            firebaseUser.uid.let { it ->
                FirebaseDatabase.getInstance().reference
                    .child("follow")
                    .child(it.toString()).child("following").child(profileId)
                    .removeValue()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful){
                            // Повтор.код !
                            firebaseUser.uid.let { it ->
                                FirebaseDatabase.getInstance().reference
                                    .child("follow")
                                    .child(profileId).child("followers").child(it.toString())
                                    .removeValue()
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful){

                                        }
                                    }
                            }
                        }
                    }}
        }
        followAndFollowingButtonCheck()
}
    private fun followAndFollowingButtonCheck() {
        val followingRef = firebaseUser.uid.let { it ->
            FirebaseDatabase.getInstance().reference
                .child("follow")
                .child(it.toString()).child("following")
        }
        if (followingRef != null){
            followingRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.child(profileId).exists()){
                        subscribeOfGuestPage.text = "Подписки"
                    } else { subscribeOfGuestPage.text = "Подписаться" }
                }

                override fun onCancelled(error: DatabaseError)
                {}

            })
        } }

    override fun onStop() {
        super.onStop()
        val pref = getSharedPreferences("PREFS",Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId",firebaseUser.uid)?.apply()
    }

    override fun onPause() {
        super.onPause()
        val pref = getSharedPreferences("PREFS",Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId",firebaseUser.uid)?.apply()
    }

    override fun onDestroy() {
        super.onDestroy()
        val pref = getSharedPreferences("PREFS",Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId",firebaseUser.uid)?.apply()
    }

}