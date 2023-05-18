package edu.skku.map.pa2

import android.content.Context
import android.content.Intent
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
    companion object{
        fun startMazeActivity(context: Context, maze: MazeName){
            val mazeIntent = Intent(context, MazeActivity::class.java)
            mazeIntent.apply {
                putExtra(EXT_NAME, maze.name)
                putExtra(EXT_SIZE, maze.size)
            }
            mazeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            context.startActivity(mazeIntent)
        }
        const val EXT_NAME = "ext_name"
        const val EXT_SIZE = "ext_size"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mazeselection)

        // get xml elements
        val textViewUsername = findViewById<TextView>(R.id.textViewUsername)
        val listView = findViewById<ListView>(R.id.mazeList)
        textViewUsername.text = intent.getStringExtra(SignInActivity.EXT_USERNAME)

        // get maze list from server as json string
        val client = OkHttpClient()
        val url = "http://swui.skku.edu:1399/maps"

        val req = Request.Builder()
            .url(url)
            .addHeader("Connection", "close")
            .build()
        client.newCall(req).enqueue(object: Callback{
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use{
                    if(!response.isSuccessful) throw IOException("Unexpected code $response")
                    // get response as string
                    val mazeListJson = response.body!!.string()

                    // deserialize json
                    val listType = object : TypeToken<ArrayList<MazeName>>() {}.type
                    val data = Gson().fromJson<ArrayList<MazeName>>(mazeListJson, listType)
                    CoroutineScope(Dispatchers.Main).launch {
                        listView.adapter = MazeDataAdapter(applicationContext, data)
                    }
                }
            }
        })
    }
}