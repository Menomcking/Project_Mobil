package com.example.project_mobil

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import com.google.gson.Gson
import org.w3c.dom.Text
import java.io.IOException

class ReaderActivity : AppCompatActivity() {
    private lateinit var context: Context
    var url1 = "http://10.0.2.2:3000/ratings/create"
    var url2 = "http://10.0.2.2:3000/story/"
    lateinit var ratingBar: RatingBar
    var title: TextView? = null
    var storyParts: TextView? = null
    var submitBtn: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reader)
        context = this@ReaderActivity
        init()
        var intent: Intent = getIntent()
        var id = intent.getIntExtra("storyId", 0)
        val jsonConverter = Gson()
        val task2 = RequestTask(url2+id,"GET")
        task2.execute()
        submitBtn!!.setOnClickListener{
            val rating = ratingBar.rating.toString()
            val ratingJson = jsonConverter.toJson(rating)
            val task1 = RequestTask(url1,"POST", ratingJson, context)
            task1.execute()
            Toast.makeText(context,"The rating of "+rating+" has been submitted.", Toast.LENGTH_LONG).show()
        }

    }
    fun init() {
        ratingBar = findViewById(R.id.ratingBar)
        title = findViewById(R.id.title)
        storyParts = findViewById(R.id.storyParts)
        submitBtn = findViewById(R.id.submitBtn)
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
                Log.e("RequestTask", "Error executing request: ${e.message}")
            }
            return response
        }

        override fun onPostExecute(response: Response?) {
            super.onPostExecute(response)
            val converter = Gson()
            when (requestType) {
                "GET" -> {}
                "POST" -> {
                    if (response != null) {
                        if (response.responseCode == 201) {
                            Toast.makeText(context, "Rating submitted successfully.", Toast.LENGTH_LONG).show()
                            Log.d("RequestTask", "Rating submitted successfully.")
                        } else {
                            Toast.makeText(context, "Failed to submit rating. Error Code: $response.responseCode", Toast.LENGTH_LONG).show()
                            Log.e("RequestTask", "Failed to submit rating. Error Code: $response.responseCode")
                        }
                    } else {
                        Toast.makeText(context, "Failed to submit rating.", Toast.LENGTH_LONG).show()
                        Log.e("RequestTask", "Failed to submit rating. Response is null.")
                    }
                }
                "PUT" -> {}
                "DELETE" -> {}
            }
        }
    }
}
