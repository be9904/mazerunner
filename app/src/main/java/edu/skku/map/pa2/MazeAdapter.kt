package edu.skku.map.pa2

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout

class MazeAdapter(
    val context: Context,
    val size: Int,
    val cellSize: Int,
    val cells: List<MazeCell>
): BaseAdapter() {
    override fun getCount(): Int {
        return size * size
    }

    override fun getItem(p0: Int): Any {
        return cells[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(position: Int, p1: View?, p2: ViewGroup?): View {
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.maze_cell, null)

        val cell = view.findViewById<ConstraintLayout>(R.id.cell)
        val imageViewInner = view.findViewById<ImageView>(R.id.imageViewInner)
        val imageViewIcon = view.findViewById<ImageView>(R.id.imageViewIcon)

        // set cell size
        val cellParams = ConstraintLayout.LayoutParams(cellSize, cellSize)
        cell.layoutParams = cellParams

        // set margins
        var innerParams = imageViewInner.layoutParams as ViewGroup.MarginLayoutParams
        val px = 3 * context.resources.displayMetrics.density
        val margin = if(px > px.toInt()) (px + 1).toInt() else px.toInt()
        val borders = cells[position].getBorders()
        innerParams.setMargins(
            borders[1] * margin,
            borders[0] * margin,
            borders[3] * margin,
            borders[2] * margin
        )
        imageViewInner.layoutParams = innerParams

        // set image
        if(cells[position].imageId != -1){
            imageViewIcon.setImageResource(cells[position].imageId)
            imageViewIcon.rotation = cells[position].imageRotation
        }

        return view
    }

    fun setImage(imageId: Int){
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.maze_cell, null)

        val imageViewIcon = view.findViewById<ImageView>(R.id.imageViewIcon)

        imageViewIcon.setImageResource(imageId)
    }
}