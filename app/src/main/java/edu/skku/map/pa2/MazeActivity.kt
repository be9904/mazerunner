package edu.skku.map.pa2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class MazeActivity : AppCompatActivity() {
    var turns = 0
    var hintUsed = false
    var userPos = 0
    var hintPos = -1
    var isFinished = false

    var mazeCells = ArrayList<MazeCell>(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maze)

        val mazeName = intent.getStringExtra(MazeSelectionActivity.EXT_NAME)
        val mazeSize = intent.getIntExtra(MazeSelectionActivity.EXT_SIZE, -1)

        // get ui elements
        val turnText = findViewById<TextView>(R.id.textViewTurns)
        val hintBtn = findViewById<Button>(R.id.buttonHint)
        val gridView = findViewById<GridView>(R.id.gridView)
        val leftBtn = findViewById<Button>(R.id.buttonLeft)
        val rightBtn = findViewById<Button>(R.id.buttonRight)
        val upBtn = findViewById<Button>(R.id.buttonUp)
        val downBtn = findViewById<Button>(R.id.buttonDown)

        // setup ui element default values
        turnText.text = "Turn : $turns"

        CoroutineScope(Dispatchers.IO).launch {
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

                        CoroutineScope(Dispatchers.Default).launch {
                            // parse string
                            val tokens = mazeData.maze.split("[\\n\\s]+".toRegex())
                            val cellsRaw = tokens.filter{ it.isNotEmpty() }
                            val cells = cellsRaw.map { it.toInt() }

                            // create maze cells
                            val adapter = setupMaze(cells)

                            // setup ui elements
                            hintBtn.setOnClickListener{
                                if(!hintUsed){
                                    showHint(cells[0])
                                    adapter.notifyDataSetChanged()
                                    hintUsed = true
                                }
                                else{
                                    Toast.makeText(
                                        applicationContext,
                                        "Hint already used",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                            rightBtn.setOnClickListener{
                                if(userPos % cells[0] != cells[0] - 1) {
                                    if(mazeCells[userPos].getBorders()[3] != 1){
                                        updateUserPos(userPos, userPos + 1, 90f)
                                        turnText.text = "Turn : $turns"
                                    }
                                }
                                adapter.notifyDataSetChanged()
                            }
                            leftBtn.setOnClickListener{
                                if(userPos % cells[0] != 0){
                                    if(mazeCells[userPos].getBorders()[1] != 1){
                                        updateUserPos(userPos, userPos - 1, 270f)
                                        turnText.text = "Turn : $turns"
                                    }
                                }
                                adapter.notifyDataSetChanged()
                            }
                            upBtn.setOnClickListener{
                                if(userPos - cells[0] >= 0){
                                    if(mazeCells[userPos].getBorders()[0] != 1){
                                        updateUserPos(userPos, userPos - cells[0], 0f)
                                        turnText.text = "Turn : $turns"
                                    }
                                }
                                adapter.notifyDataSetChanged()
                            }
                            downBtn.setOnClickListener{
                                if(userPos + cells[0] <= cells[0] * cells[0] - 1){
                                    if(mazeCells[userPos].getBorders()[2] != 1){
                                        updateUserPos(userPos, userPos + cells[0], 180f)
                                        turnText.text = "Turn : $turns"
                                    }
                                }
                                adapter.notifyDataSetChanged()
                            }
                        }
                    }
                }
            })
        }
    }

    fun setupMaze(cells: List<Int>) : MazeAdapter{
        mazeCells.clear()

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

        for((index, cell) in cellInfo.withIndex()) {
            mazeCells.add(
                MazeCell(
                    index,
                    cellSize,
                    cell,
                    if(index == 0) R.drawable.user
                    else if(index == cellInfo.size - 1) R.drawable.goal
                    else -1,
                    0f
                )
            )
        }
        val adapter = MazeAdapter(
            applicationContext,
            cells[0],
            cellSize,
            mazeCells
        )

        CoroutineScope(Dispatchers.Main).launch {
            // set grid size
            var gridViewParams = gridView.layoutParams as ViewGroup.LayoutParams
            gridViewParams.width = gridSize
            gridViewParams.height = gridSize
            gridView.layoutParams = gridViewParams

            // set grid column number
            gridView.numColumns = cells[0]

            // set grid adapter
            gridView.adapter = adapter
        }

        return adapter
    }

    fun updateUserPos(before: Int, after: Int, rotation: Float){
        mazeCells[before].imageId = -1
        userPos = after
        mazeCells[after].imageId = R.drawable.user
        mazeCells[after].imageRotation = rotation
        if(!isFinished) {
            turns++
        }
        if(userPos == mazeCells.count() - 1 && !isFinished){
            Toast.makeText(
                applicationContext,
                "Finish!",
                Toast.LENGTH_SHORT
            ).show()
            isFinished = true
        }
    }

    fun showHint(size: Int){
        val idx = findPath(size)
        if(mazeCells[findPath(size)].imageId != R.drawable.user &&
            idx != size * size - 1)
            mazeCells[findPath(size)].imageId = R.drawable.hint
    }

    fun findPath(size: Int): Int {
        val curPos = Pair(userPos / size, userPos % size)
        val visited = Array(size) { IntArray(size) }
        val queue: Queue<Pair<Int, Int>> = LinkedList()
        val move = arrayOf(intArrayOf(-1, 0), intArrayOf(0, -1), intArrayOf(1, 0), intArrayOf(0, 1))

        queue.offer(Pair(curPos.first, curPos.second))
        visited[curPos.first][curPos.second] = 1

        while (queue.isNotEmpty()) {
            val (y, x) = queue.poll()!!
            var it = 0
            for ((dy, dx) in move) {
                val nx = x + dx
                val ny = y + dy
                if (ny in 0 until size && nx in 0 until size &&
                    mazeCells[y * size + x].getBorders()[it] != 1 &&
                    visited[ny][nx] == 0)
                {
                    visited[ny][nx] = visited[y][x] + 1
                    queue.offer(Pair(ny, nx))
                    if(ny == size-1 && nx == size-1){
                        queue.clear()
                        break
                    }
                }
                it++
            }
        }

        var count = visited[size - 1][size - 1]
        var pos = Pair(size - 1, size - 1)
        val debug_visited = Array(size) { IntArray(size) }
        while (count > 2){
            var it = 0
            for ((dy, dx) in move){
                val px = pos.first + dx
                val py = pos.second + dy
                if(px in 0 until size && py in 0 until size &&
                    visited[py][px] == count - 1 &&
                    mazeCells[pos.second * size + pos.first].getBorders()[it] != 1)
                {
                    debug_visited[py][px] = 1
                    pos = Pair(px, py)
                    count--
                    break
                }
                it++
            }
        }

        return size * pos.second + pos.first
    }
}