package edu.skku.map.pa2

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView

class MazeDataAdapter(val context: Context, val mazeDataList: ArrayList<MazeName>):BaseAdapter() {
    val mazeIntent = Intent(context, MazeActivity::class.java)

    override fun getCount(): Int {
        return mazeDataList.count()
    }

    override fun getItem(position: Int): Any {
        return mazeDataList[position]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(position: Int, p1: View?, p2: ViewGroup?): View {
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.maze_entry, null)

        val textViewMazeName = view.findViewById<TextView>(R.id.textViewMazeName)
        val textViewMazeSize = view.findViewById<TextView>(R.id.textViewMazeSize)
        val btn = view.findViewById<Button>(R.id.buttonStart)

        textViewMazeName.text = mazeDataList[position].name
        textViewMazeSize.text = mazeDataList[position].size.toString()

        btn.setOnClickListener{
            MazeSelectionActivity.startMazeActivity(context, mazeDataList[position])
        }

        return view
    }
}