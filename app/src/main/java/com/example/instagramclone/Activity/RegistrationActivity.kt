package com.example.instagramclone.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.instagramclone.R
import com.example.instagramclone.Models.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_registration.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener

class RegistrationActivity : AppCompatActivity(), KeyboardVisibilityEventListener {
    final val TAG:String = "RegistrationActivity"
    private var inputPhoneNumber:EditText?=null
    private var inputNameOfUser:EditText?=null

    private var inputMail:EditText?=null
    private var inputPassword:EditText?=null
    private var inputNameAndSurname:EditText?=null
    private var submitButton:Button?=null
    private var passwordInput:String=""
    private var nameAndSurnameInput:String=""
    lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        inputPhoneNumber = findViewById(R.id.phoneNumberId)
        inputNameOfUser = findViewById(R.id.nameOfUserRegistrationId)
        inputMail = findViewById(R.id.mailOfRegistration)
        inputPassword = findViewById(R.id.passwordOfRegistration)
        inputNameAndSurname = findViewById(R.id.nameAndSurnameId)
        submitButton = findViewById(R.id.buttonOfReg)
        database = Firebase.database.reference
        auth = Firebase.auth
        // Для визуального эффекта клавиатуры
        KeyboardVisibilityEvent.setEventListener(this,this)
    }

    // МЕТОД КНОПКИ ПОДТВЕРЖДЕНИЯ РЕГИСТРИРУЮЩИЙ ПОЛЬЗОВАТЕЛЯ
    fun submitInformation(view: View) {
        if (validateEmail() && validatePassword() && validateNameAndSurname() && validateNameOfUser() && validatePhoneNumber()){
            auth.createUserWithEmailAndPassword(inputMail?.text.toString().trim(),inputPassword?.text.toString().trim())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success")
                        val user = auth.currentUser
                        writeNewUser(
                            user!!.uid,
                            inputNameOfUser?.text.toString().trim(),
                            inputNameAndSurname?.text.toString().trim(),
                            inputPhoneNumber?.text.toString().trim(),
                            inputMail?.text.toString().trim(),
                            user.uid
                        )

                        // Переход в аккаунт после регистрации
                        val intent = startActivity(Intent(this, MainActivity::class.java))
                        finish()
                        overridePendingTransition(R.anim.zoom_in, R.anim.static_anim)

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                    }
                }
        }


    }


    // МЕТОДЫ ПРОВЕРКИ ВВОДА
    fun validatePhoneNumber ():Boolean {
        val phoneNumberInput:String = inputPhoneNumber?.text.toString().trim()
        if (phoneNumberInput.isEmpty()){
            inputPhoneNumber?.setError("Поле должно быть заполнено")
            return false
        } else {
            return true
        }

    }

    fun validateNameOfUser ():Boolean {
        val nameOfUserInput:String = inputNameOfUser?.text.toString().trim()
        if (nameOfUserInput.isEmpty()){
            inputNameOfUser?.setError("Поле должно быть заполнено")
            return false
        } else {
            return true
        }

    }


    fun validateEmail ():Boolean {
        val emailInput:String = inputMail?.text.toString().trim()
        if (emailInput.isEmpty()){
            inputMail?.setError("Введите эл.адрес")
            return false
        } else {
            return true
        }

    }
    fun validatePassword ():Boolean {
        passwordInput = inputPassword?.text.toString().trim()
        if (passwordInput.isEmpty()){
            inputPassword?.setError("Введите пароль")
            return false
        } else if(passwordInput.length <= 6) {
            inputPassword?.setError("Пароль должен быть больше 6")
            return false
        }
        return true
    }

    fun validateNameAndSurname ():Boolean {
        nameAndSurnameInput = inputNameAndSurname?.text.toString().trim()
        if (nameAndSurnameInput.isEmpty()){
            inputNameAndSurname?.setError("Поле должно быть заполнено")
            return false
        } else {
            return true
        }
    }

         // ПРОЧИЕ МЕТОДЫ

    // МЕТОД СОЗДАЮЩИЙ ПОЛЬЗОВАТЕЛЯ
private fun writeNewUser(userId: String, name: String,nameAndSurname: String,phone: String, email: String, uid: String) {
    val newUser = UserModel(name,nameAndSurname,phone,email,uid,"default")
    database.child("users").child(userId).setValue(newUser)
    // setValue() перезаписывает данные в указанном месте
}
    // МЕТОД ПРОКРУЧИВАЮЩИЙ АКТИВИТИ ВНИЗ ПРИ ОТКРЫТОЙ КЛАВИАТУРЕ
    override fun onVisibilityChanged(isOpen: Boolean) {
        if (isOpen){
            scrollId2.scrollTo(0,scrollId2.bottom)
        } else
            scrollId2.scrollTo(0,scrollId2.top)
    }
}