package edu.skku.map.pa2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class SignInActivity : AppCompatActivity() {
    companion object{
        const val EXT_USERNAME = "ext_username"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        val btn = findViewById<Button>(R.id.button)
        val editText = findViewById<EditText>(R.id.editText)

        val client = OkHttpClient()
        val host = "http://swui.skku.edu:1399"
        // val host = "http://121.169.12.99:10099"
        val path = "/users"

        btn.setOnClickListener{
            // send username to maze selection activity
            val secondIntent = Intent(this, MazeSelectionActivity::class.java).apply{
                putExtra(EXT_USERNAME, editText.text.toString()) // save string for now
            }

            val json = Gson().toJson(PostUsername(editText.text.toString()))
            val mediaType = "application/json; charset=utf-8".toMediaType()

            val req = Request.Builder().url(host + path)
                .post(json.toString().toRequestBody(mediaType))
                .build()

            client.newCall(req).enqueue(object: Callback{
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use{
                        if(!response.isSuccessful) throw IOException("Unexpected code $response")
                        val str = response.body!!.string()
                        val data = Gson().fromJson(str, PostUsernameResponse::class.java)

                        if(data.success) // move to map selection
                        {
                            // start maze selection activity
                            startActivity(secondIntent)
                        }
                        else // wrong username
                        {
                            CoroutineScope(Dispatchers.Main).launch {
                                Toast.makeText(
                                    applicationContext,
                                    "Wrong User Name",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            })
        }
    }
}