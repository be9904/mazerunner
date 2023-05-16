package edu.skku.map.pa2

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class MazeDataAdapter(val context: Context, val mazeDataList: ArrayList<MazeData>):BaseAdapter() {
    override fun getCount(): Int {
        return mazeDataList.count()
    }

    override fun getItem(position: Int): Any {
        return mazeDataList[position]
    }

    override fun getItemId(p0: Int): Long {
        return 0
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
            Toast.makeText(
                context,
                "${textViewMazeName.text}",
                Toast.LENGTH_SHORT
            ).show()
        }

        return view
    }

}