package com.example.project_mobil

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import java.io.IOException

class RegisterActivity : AppCompatActivity() {
    var username: TextInputLayout? = null
    var password: TextInputLayout? = null
    var repassword: TextInputLayout? = null
    var email: TextInputLayout? = null
    var regButton: Button? = null
    var reButton: Button? = null
    private lateinit var context: Context
    var url = "http://10.0.2.2:3000/authentication/register"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        context = this@RegisterActivity
        init()
        regButton!!.setOnClickListener{
            val usernameText = username!!.editText!!.text.toString()
            val passwordText = password!!.editText!!.text.toString()
            val repasswordText = repassword!!.editText!!.text.toString()
            val emailText = email!!.editText!!.text.toString()
            val user = Users(usernameText,passwordText,emailText)
            val jsonConverter = Gson()
            val task = RequestTask(url, "POST", jsonConverter.toJson(user), context)
            task.execute()
        }
        reButton!!.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    fun init() {
        username = findViewById(R.id.username)
        password = findViewById(R.id.password)
        repassword = findViewById(R.id.repassword)
        email = findViewById(R.id.email)
        regButton = findViewById(R.id.regButton)
        reButton = findViewById(R.id.re)
    }

    class RequestTask : AsyncTask<Void?, Void?, Response?> {
        var requestUrl: String
        var requestType: String
        lateinit var requestParams: String
        private lateinit var context: Context

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
                Toast.makeText(context,"Sikertelen regisztráció ${response.content}",Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(context,"Sikeres regisztráció",Toast.LENGTH_LONG).show()
                val intent = Intent(context, LoginActivity::class.java)
                context.startActivity(intent)
            }
            when (requestType) {
                "GET" -> {}
                "POST" -> {}
                "PUT" -> {}
                "DELETE" -> {}
            }
        }
    }
}