package edu.skku.map.pa2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.GridView
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import java.io.IOException

class MazeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maze)

        val mazeName = intent.getStringExtra(MazeSelectionActivity.EXT_NAME)
        val mazeSize = intent.getIntExtra(MazeSelectionActivity.EXT_SIZE, -1)

        // get maze info
        val client = OkHttpClient()
        val url = "http://swui.skku.edu:1399/maze/map?name=$mazeName"

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
                    // get response as string and deserialize
                    val mazeInfoJson = response.body!!.string()
                    val mazeData = Gson().fromJson(mazeInfoJson, MazeInfo::class.java)

                    // parse string
                    val tokens = mazeData.maze.split("[\\n\\s]+".toRegex())
                    val cellsRaw = tokens.filter{ it.isNotEmpty() }
                    val cells = cellsRaw.map { it.toInt() }

                    // create maze cells
                    setupMaze(cells)
                }
            }
        })
    }

    fun setupMaze(cells: List<Int>){
        val gridView = findViewById<GridView>(R.id.gridView)
        val cellInfo = cells.subList(1, cells.size)

        val cellDensity: Float?
        val cellSize: Int?
        val gridSize: Int?

        // calculate grid and cell dimensions
        if(350 % cells[0] == 0){
            cellDensity = (350 / cells[0]) * applicationContext.resources.displayMetrics.density
            cellSize = if(cellDensity > cellDensity.toInt()) cellDensity.toInt() + 1 else cellDensity.toInt()
            gridSize = cellSize * cells[0]
        }
        else{
            gridSize = (350 * applicationContext.resources.displayMetrics.density).toInt()
            cellDensity = (350f / cells[0]) * applicationContext.resources.displayMetrics.density
            cellSize = cellDensity.toInt()
        }

        CoroutineScope(Dispatchers.Main).launch {
            // set grid size
            var gridViewParams = gridView.layoutParams as ViewGroup.LayoutParams
            gridViewParams.width = gridSize
            gridViewParams.height = gridSize
            gridView.layoutParams = gridViewParams

            // set grid column number
            gridView.numColumns = cells[0]

            // set grid adapter
            gridView.adapter = MazeAdapter(applicationContext, cells[0], cellSize, cellInfo)
        }
    }
}