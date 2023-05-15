package com.example.project_mobil

import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class MainMenuActivity : AppCompatActivity() {
    var listView: ListView? = null
    private val storyList : MutableList<Stories> = ArrayList()
    var url = "http://10.0.2.2:3000/story/list"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)
        init()

        val task = RequestTask(url,"GET")
        task.execute()

    }
    fun init(){
        listView = findViewById(R.id.listView)
        listView!!.adapter = StoryAdapter()
    }
    private inner class StoryAdapter :
        ArrayAdapter<Stories>(this@MainMenuActivity, R.layout.story_list_items, storyList) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val inflater = layoutInflater
            val view: View = inflater.inflate(R.layout.story_list_items, null, false)
            val actualStory: Stories = storyList!!.get(position)
            val textViewTitle: TextView = view.findViewById(R.id.title)
            val imageViewPicture: ImageView = view.findViewById(R.id.pic)
            val textViewDescription: TextView = view.findViewById(R.id.description)
            val textViewRating: TextView = view.findViewById(R.id.rating)
            textViewTitle.setText(actualStory.title)
            Picasso.get().load(actualStory.picture).into(imageViewPicture)
            textViewDescription.setText(actualStory.description)
            textViewRating.text = (actualStory.rating).toString()
            textViewTitle.setOnClickListener {
                val intent = Intent(this@MainMenuActivity, ReaderActivity::class.java)
                intent.putExtra("storyId", actualStory.id)
                startActivity(intent)

            }
            return view
        }
    }

    private inner class RequestTask : AsyncTask<Void?, Void?, Response?> {
        var requestUrl: String
        var requestType: String
        lateinit var requestParams: String
        // private lateinit var context: Context

        constructor(requestUrl: String, requestType: String, requestParams: String) {
            this.requestUrl = requestUrl
            this.requestType = requestType
            this.requestParams = requestParams
            // this.context = context
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
            Log.d("stories", response!!.content)
            val converter = Gson()
            when (requestType) {
                    "GET" -> {
                        val stories: Array<Stories> = converter.fromJson(response!!.content, Array<Stories>::class.java)
                        storyList.addAll(stories)
                    }
                "POST" -> {}
                "PUT" -> {}
                "DELETE" -> {}
            }
        }
    }
}