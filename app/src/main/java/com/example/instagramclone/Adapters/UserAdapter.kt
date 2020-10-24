package com.example.instagramclone.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramclone.Activity.GuestPageActivity
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

class UserAdapter(
    private var mContext:Context,
    private var mUser:List<UserModel>,
    private var isFragment:Boolean = false):RecyclerView.Adapter<UserAdapter.ViewHolder>()

{
private var userFB:FirebaseUser? = FirebaseAuth.getInstance().currentUser
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserAdapter.ViewHolder {
val view = LayoutInflater.from(mContext).inflate(R.layout.user_item_layout,parent,false)
        return UserAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserAdapter.ViewHolder, position: Int) {
        val user = mUser[position]
    holder.userNameTextView.text = user.getName()
    holder.userNameAndSurName.text = user.getNameAndSurname()
        Picasso.get()
            .load(user.getUrl())
            .placeholder(R.drawable.avatar)
            .into(holder.userProfileImage)
        checkFollowingStatus(user.getUid(),holder.subscribeButton)

        holder.itemView.setOnClickListener(View.OnClickListener {
            // Создаем ключ-значение
            val pref = mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit()
            pref.putString("profileId",user.getUid()).apply()

            (mContext as FragmentActivity).let {
                val intent = Intent(it, GuestPageActivity::class.java)
                it.startActivity(intent)
                it.overridePendingTransition(R.anim.zoom_in, R.anim.static_anim)}
           // (mContext as FragmentActivity).findNavController().navigate(R.id.guestFragment,null)
                //(mContext as FragmentActivity).supportFragmentManager.beginTransaction().replace(R.id.fragment_container_view_tag,GuestFragment()).commit()
        })

            // Обработка нажатия на кнопку подписаться
        holder.subscribeButton.setOnClickListener {
            if (holder.subscribeButton.text.toString() == "Подписаться"){
                userFB?.uid.let { it ->
                    FirebaseDatabase.getInstance().reference
                        .child("follow")
                        .child(it.toString()).child("following").child(user.getUid())
                        .setValue(true)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful){
                                userFB?.uid.let { it ->
                                    FirebaseDatabase.getInstance().reference
                                        .child("follow")
                                        .child(user.getUid()).child("followers").child(it.toString())
                                        .setValue(true)
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful){

                                            }
                            }

                        }
                }
            }}}
            else {
                userFB?.uid.let { it ->
                    FirebaseDatabase.getInstance().reference
                        .child("follow")
                        .child(it.toString()).child("following").child(user.getUid())
                        .removeValue()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful){
                                // Повтор.код !
                                userFB?.uid.let { it ->
                                    FirebaseDatabase.getInstance().reference
                                        .child("follow")
                                        .child(user.getUid()).child("followers").child(it.toString())
                                        .removeValue()
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful){

                                            }
                                        }
                                }
                            }
                        }}
            }
        }

    }

    private fun checkFollowingStatus(uid: String, subscribeButton: Button) {
        val followingRef =  userFB?.uid.let { it ->
            FirebaseDatabase.getInstance().reference
                .child("follow")
                .child(it.toString()).child("following")
        }
        followingRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.child(uid).exists()){
                    subscribeButton.text = "Подписки"
                }
                else{
                    subscribeButton.text = "Подписаться"
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    override fun getItemCount(): Int {
    return mUser.size
    }

    class ViewHolder(@NonNull itemView:View):RecyclerView.ViewHolder(itemView) {
        var userNameTextView:TextView = itemView.findViewById(R.id.nameOfUserSearch)
        var userNameAndSurName:TextView = itemView.findViewById(R.id.nameAndSurnameSearch)
        var userProfileImage:CircleImageView = itemView.findViewById(R.id.avaFromSearch)
        var subscribeButton:Button = itemView.findViewById(R.id.subscribeButtonOfSearch)
    }

}