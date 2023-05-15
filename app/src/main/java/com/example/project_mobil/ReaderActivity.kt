package com.example.project_mobil

import android.app.Activity
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
import com.google.gson.JsonSyntaxException
import org.w3c.dom.Text
import java.io.IOException

/**
 * Reader activity
 *
 * @constructor Create empty Reader activity
 */
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
        val task2 = RequestTask(url2 + id, "GET")
        task2.execute()

        submitBtn!!.setOnClickListener {
            var rating = ratingBar.rating
            val ratingJson: String = "rating: $jsonConverter.toJson(rating)"
            val task1 = RequestTask(url1, "POST", ratingJson, context)
            task1.execute()
            Toast.makeText(
                context,
                "The rating of " + rating + " has been submitted.",
                Toast.LENGTH_LONG
            ).show()
        }

    }

    /**
     * Init
     *
     */
    fun init() {
        ratingBar = findViewById(R.id.ratingBar)
        title = findViewById(R.id.title)
        storyParts = findViewById(R.id.storyParts)
        submitBtn = findViewById(R.id.submitBtn)
    }

    /**
     * Request task
     *
     * @constructor Create empty Request task
     */
    class RequestTask : AsyncTask<Void?, Void?, Response?> {
        var requestUrl: String
        var requestType: String
        lateinit var requestParams: String
        lateinit private var context: Context
        lateinit var title: TextView
        lateinit var storyParts: TextView

        constructor(
            requestUrl: String,
            requestType: String,
            requestParams: String,
            context: Context
        ) {
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
                "GET" -> {
                    if (response != null && response.responseCode == 201) {
                        val responseBody = response.content
                        if (responseBody != null) {
                            try {
                                val story =
                                    Gson().fromJson(responseBody.toString(), Story::class.java)
                                val storyTitle = story.title
                                val storyPart = story.storyparts

                                // Update the UI with the received title and story parts
                                (context as Activity).runOnUiThread {
                                    // Set the title
                                    title.text = storyTitle

                                    // Set the story parts
                                    val addstoryParts = storyPart.joinToString("\n\n")
                                    storyParts.text = addstoryParts
                                }
                            } catch (e: JsonSyntaxException) {
                                Log.e("RequestTask", "Error parsing JSON: ${e.message}")
                            }
                        } else {
                            Log.e("RequestTask", "Empty response body")
                        }
                    } else {
                        Log.e("RequestTask", "Error response. Code: ${response?.responseCode}")
                    }
                }
                "POST" -> {
                    if (response != null) {
                        if (response.responseCode == 201) {
                            Toast.makeText(
                                context,
                                "Rating submitted successfully.",
                                Toast.LENGTH_LONG
                            ).show()
                            Log.d("RequestTask", "Rating submitted successfully.")
                        } else {
                            Toast.makeText(
                                context,
                                "Failed to submit rating. Error Code: $response.responseCode",
                                Toast.LENGTH_LONG
                            ).show()
                            Log.e(
                                "RequestTask",
                                "Failed to submit rating. Error Code: $response.responseCode"
                            )
                        }
                    } else {
                        Toast.makeText(context, "Failed to submit rating.", Toast.LENGTH_LONG)
                            .show()
                        Log.e("RequestTask", "Failed to submit rating. Response is null.")
                    }
                    val token = "${converter.fromJson(response!!.content, Token::class.java)}"
                    val sharedPreferences =
                        context.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
                    var editor = sharedPreferences!!.edit()
                    editor.putString("token", token)
                    editor.commit()
                }
            }
        }
    }
}
