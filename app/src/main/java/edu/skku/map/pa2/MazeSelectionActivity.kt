package edu.skku.map.pa2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import android.widget.TextView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import java.io.IOException

class MazeSelectionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mazeselection)

        val textViewUsername = findViewById<TextView>(R.id.textViewUsername)
        val listView = findViewById<ListView>(R.id.mazeList)
        textViewUsername.text = intent.getStringExtra(SignInActivity.EXT_USERNAME)

        // get maze list from server as json string
        val client = OkHttpClient()
        val url = "http://swui.skku.edu:1399/maps"

        val req = Request.Builder().url(url).build()
        var mazeListJson = ""
        client.newCall(req).enqueue(object: Callback{
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }
            override fun onResponse(call: Call, response: Response) {
                response.use{
                    if(!response.isSuccessful) throw IOException("Unexpected code $response")
                    println("Got Response")
                    mazeListJson = response.body!!.string()

                    // parse json
                    val listType = object : TypeToken<ArrayList<MazeData>>() {}.type
                    val data = Gson().fromJson<ArrayList<MazeData>>(mazeListJson, listType)
                    CoroutineScope(Dispatchers.Main).launch {
                        listView.adapter = MazeDataAdapter(applicationContext, data)
                    }
                }
            }
        })



    }
}