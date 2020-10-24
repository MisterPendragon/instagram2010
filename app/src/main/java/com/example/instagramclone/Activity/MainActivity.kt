package com.example.instagramclone.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.instagramclone.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Нашли наше нижнее меню и наш фрагмент с контроллером
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
        val navController = findNavController(R.id.fragment)

        //Метод setupWithNavController вешает листенер на BottomNavigationView
        // и выполняет навигацию при нажатии на его элементы. При этом выполняет setChecked для нажатого элемента.
        bottomNavigationView.setupWithNavController(navController)



    }
}