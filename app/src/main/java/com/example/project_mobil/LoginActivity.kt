package com.example.project_mobil

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import java.io.IOException

class LoginActivity : AppCompatActivity() {
    var email: EditText? = null
    var password: EditText? = null
    var logButton: Button? = null
    private lateinit var context: Context
    var menuButton: Button? = null
    var url = "http://10.0.2.2:3000/authentication/log-in"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this@LoginActivity
        setContentView(R.layout.activity_login)
        init()
        logButton!!.setOnClickListener{
            val emailText = email!!.text.toString()
            val passwordText = password!!.text.toString()
            val user = Login(emailText,passwordText)
            val jsonConverter = Gson()
            val task = RequestTask(url,"POST",jsonConverter.toJson(user), context)
            task.execute()
            val intent = Intent(this, MainMenuActivity::class.java)
            startActivity(intent)
        }
        menuButton!!.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }


    }


    fun init() {
        email = findViewById(R.id.et_email)
        password = findViewById(R.id.et_password)
        logButton = findViewById(R.id.btn_login)
        menuButton = findViewById(R.id.returnButton)
    }

    class RequestTask : AsyncTask<Void?, Void?, Response?> {
        var requestUrl: String
        var requestType: String
        lateinit var requestParams: String
        lateinit private var context: Context

        constructor(requestUrl: String, requestType: String, requestParams: String, context: Context) {
            this.requestUrl = requestUrl
            this.requestType = requestType
            this.requestParams = requestParams
            this.context = context
        }

        constructor(requestUrl: String, requestType: String) {
            this.requestUrl = requestUrl
            this.requestType = requestType
        }

        override fun doInBackground(vararg p0: Void?): Response? {
            var response: Response? = null
            try {
                when (requestType) {
                    "GET" -> response = RequestHandler.get(requestUrl)
                    "POST" -> response = RequestHandler.post(requestUrl, requestParams)
                    "PUT" -> response = RequestHandler.put(requestUrl, requestParams)
                    "DELETE" -> response = RequestHandler.delete("$requestUrl/$requestParams")
                }
            } catch (e: IOException) {
            }
            return response
        }

        override fun onPostExecute(response: Response?) {
            super.onPostExecute(response)
            val converter = Gson()
            if (response!!.responseCode >= 400) {
                Log.d("onPostExecuteError:", response.content)
                Toast.makeText(context,"Sikertelen belépés ${response.content}",Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(context,"Sikeres belépés${converter.fromJson(response.content,Token::class.java)}",Toast.LENGTH_LONG).show()
            }
            when (requestType) {
                "GET" -> {}
                "POST" -> {
                    val toast = Toast.makeText(context,"${converter.fromJson(response.content,Token::class.java)}",Toast.LENGTH_LONG)
                    toast.show()
                    val token = "${converter.fromJson(response.content,Token::class.java)}"
                    val sharedPreferences = context.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
                    var editor = sharedPreferences!!.edit()
                    editor.putString("token",token)
                    editor.commit()
                }
                "PUT" -> {}
                "DELETE" -> {}
            }
        }
    }
}