package com.example.instagramclone.Activity

import android.app.Activity
import android.content.Intent

import android.net.Uri
import android.os.Bundle

import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_edit_profile.*

class EditProfileActivity : AppCompatActivity() {
    final val TAG:String = "LogInfo"
    private  var isPictureadd:Boolean=false

    private  var imageUri:Uri?=null
    private var storage: StorageReference= FirebaseStorage.getInstance().getReference("avatar")
    private var auth: FirebaseAuth = Firebase.auth
    private var linkUrl=""
    var user = auth.currentUser
    val fileRef = storage.child(user!!.uid)
    private var database =  FirebaseDatabase.getInstance().reference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        // ПОЛУЧАЕМ ДАННЫЕ ПОЛЬЗОВАТЕЛЯ
        database.child("users").child(user!!.uid).addListenerForSingleValueEvent(object :
            ValueEventListener {
            // !! – "это не null"
            // addListenerForSingleValueEvent() Чтобы прочитать данные по пути,прослушать изменения и добавить ValueEventListener  в DatabaseReference
            // onDataChange() для чтения статического снимка содержимого по заданному пути
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(UserModel::class.java) // Снимок сконвертится по конструктору нашего класса
                nameOfUserEditProfileId.setText(user!!.getName(), TextView.BufferType.EDITABLE)
                nameAndSurnameEditProfileId.setText(
                    user.getNameAndSurname(),
                    TextView.BufferType.EDITABLE
                )
                phoneNumberEditText.setText(user.getPhoneNumber(), TextView.BufferType.EDITABLE)
                mailEditText.setText(user.getMail(), TextView.BufferType.EDITABLE)

                // Условия загрузки аватарки
                if(user.getUrl().equals("default")) {
                    Picasso.get()
                        .load(R.drawable.avatar)
                        .into(avaOfEditProfile)
                } else {

                    Picasso.get()
                        .load(user.getUrl())
                        .into(avaOfEditProfile)
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        // Обрабатываем нажатия
        checkId.setOnClickListener {
            updateProfile()
            val intent = startActivity(Intent(this, MainActivity::class.java))
            overridePendingTransition(R.anim.zoom_in, R.anim.static_anim)
            finish()

        }
        changeAvatarId.setOnClickListener { addPhoto() }

        closeId.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.zoom_in, R.anim.static_anim)
        }
    }



    // МЕТОД РАБОТУЮЩИЙ С ФОТО
    private fun addPhoto() {
        CropImage.activity().setAspectRatio(1,1).start(this@EditProfileActivity)

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Если проверка по коду проходит,то мы получаем путь к картинке
        if (resultCode == Activity.RESULT_OK && requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && data!=null ){
            val result = CropImage.getActivityResult(data)
            imageUri = result.uri
            UploadImageOnFireBase()
        }
    }
    // МЕТОД ЗАГРУЖАЕТ КАРТИНКУ В FIREBASE
     fun UploadImageOnFireBase(){
        fileRef.putFile(imageUri!!).addOnCompleteListener{
            if (it.isSuccessful){
                fileRef.downloadUrl.addOnCompleteListener {
                    if (it.isSuccessful){
                        linkUrl = it.result.toString()
                        database.child("users").child(user!!.uid).child("url").setValue(linkUrl)
                        Picasso.get()
                            .load(linkUrl)
                            .into(avaOfEditProfile)
                    }
                }
            }
        }
    }

    // МЕТОД СОХРАНЯЮЩИЙ ДАННЫЕ ОКНА РЕДАКТИРОВАНИЯ
   private fun updateProfile() {
             auth = Firebase.auth
             val user = auth.currentUser
             var inputMail:String = mailEditText.text.toString()
             var inputNameOfUser:String = nameOfUserEditProfileId.text.toString()
             var inputNameAndSurname:String = nameAndSurnameEditProfileId.text.toString()
             var inputPhoneNumber: String = phoneNumberEditText.text.toString()

             fun validateNameOfUser ():Boolean {
                 if (inputNameOfUser.isEmpty()){
                     nameOfUserEditProfileId.setError("Поле должно быть заполнено")
                     return false
                 } else {
                     return true
                 }
             }
             fun validateNameAndSurname ():Boolean {
                 if (inputNameAndSurname.isEmpty()){
                     nameAndSurnameEditProfileId.setError("Поле должно быть заполнено")
                     return false
                 } else {
                     return true
                 }
             }
             fun validateMail ():Boolean {
                 if (inputMail.isEmpty()){
                     mailEditText.setError("Поле должно быть заполнено")
                     return false
                 } else {
                     auth.currentUser!!.updateEmail(inputMail).addOnCompleteListener { task -> if (task.isSuccessful) {
                         // email update completed
                     }else{
                         Toast.makeText(
                             baseContext, "Authentication failed.",
                             Toast.LENGTH_SHORT
                         ).show()
                     } }
                     return true
                 }
             }
             fun validatePhoneNumber ():Boolean {
                 if (inputPhoneNumber.isEmpty()){
                     phoneNumberEditText.setError("Поле должно быть заполнено")
                     return false
                 } else {
                     return true
                 }
             }
            if(validateMail()&&validateNameAndSurname()&&validateNameOfUser()&&validatePhoneNumber()){
                database.child("users").child(user!!.uid).child("name").setValue(inputNameOfUser)
                database.child("users").child(user.uid).child("nameAndSurname").setValue(
                    inputNameAndSurname
                )
                database.child("users").child(user.uid).child("mail").setValue(inputMail)
                database.child("users").child(user.uid).child("phoneNumber").setValue(
                    inputPhoneNumber
                )
            }


    }


}