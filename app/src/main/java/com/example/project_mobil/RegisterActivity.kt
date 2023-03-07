package com.example.project_mobil

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
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
    var url = "http://10.0.2.2:3000/register"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        init()
        regButton!!.setOnClickListener{
            val usernameText = username!!.editText!!.text.toString()
            val passwordText = password!!.editText!!.text.toString()
            val repasswordText = repassword!!.editText!!.text.toString()
            val emailText = email!!.editText!!.text.toString()
            val user = Users(usernameText,passwordText,emailText)
            val jsonConverter = Gson()
            val task = RequestTask(url, "POST", jsonConverter.toJson(user))
            task.execute()
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

        constructor(requestUrl: String, requestType: String, requestParams: String) {
            this.requestUrl = requestUrl
            this.requestType = requestType
            this.requestParams = requestParams
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
            } else {

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