package com.example.instagramclone.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.example.instagramclone.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_authentication.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener

class AuthenticationActivity : AppCompatActivity(), KeyboardVisibilityEventListener {
    final val TAG:String = "RegistrationActivity"
    lateinit var auth: FirebaseAuth
    private var inputMail: EditText?=null
    private var inputPassword: EditText?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)
        // Инициализация
        inputMail = findViewById(R.id.mailInput)
        inputPassword = findViewById(R.id.passwordInput)
        auth = Firebase.auth
KeyboardVisibilityEvent.setEventListener(this,this)

    }

    fun goToRegistration(view: View) {
        val intent =startActivity(Intent(this, RegistrationActivity::class.java))
        overridePendingTransition(R.anim.zoom_in, R.anim.static_anim)
    }




    fun LogIn(view: View) {
        val email:String = inputMail?.text.toString().trim()
        val password:String = inputPassword?.text.toString().trim()
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    val intent =startActivity(Intent(this, MainActivity::class.java))
                    overridePendingTransition(R.anim.zoom_in, R.anim.static_anim)
                    finish()
                    //updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    //updateUI(null)
                    // ...
                }

                // ...
            }
    }

    override fun onVisibilityChanged(isKeyBoardOpen: Boolean) {
        if (isKeyBoardOpen){
            scrollId.scrollTo(0,scrollId.bottom)
        } else
            scrollId.scrollTo(0,scrollId.top)
    }
}