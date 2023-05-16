package edu.skku.map.pa2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import okhttp3.*
import java.io.IOException

class MazeSelectionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mazeselection)

        val textViewUsername = findViewById<TextView>(R.id.textViewUsername)
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
                    mazeListJson = response.body!!.string()
//                    CoroutineScope(Dispatchers.Main).launch {
//                        textView3.text = str
//                    }
                }
            }
        })

        // parse json
    }
}