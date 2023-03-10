package com.example.project_mobil

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    var buttonRegister: Button?=null
    var buttonLogin: Button?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
        buttonRegister!!.setOnClickListener{
            val intent = Intent(this,RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    fun init(){
        buttonRegister = findViewById(R.id.reg)
        buttonLogin = findViewById(R.id.logi)
    }
}