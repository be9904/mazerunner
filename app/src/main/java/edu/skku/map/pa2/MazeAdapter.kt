package edu.skku.map.pa2

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout.LayoutParams
import androidx.constraintlayout.widget.ConstraintLayout

class MazeAdapter(val context: Context, val size: Int, val cellSize: Int, val cells: List<Int>): BaseAdapter() {
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
        val imageViewIcon = view.findViewById<ImageView>(R.id.imageViewIcon)
        val imageViewInner = view.findViewById<ImageView>(R.id.imageViewInner)

        // set cell size
        val cellParams = ConstraintLayout.LayoutParams(cellSize, cellSize)
        cell.layoutParams = cellParams

        // set margins
        var innerParams = imageViewInner.layoutParams as ViewGroup.MarginLayoutParams
        val margins = setBorders(cells[position])
        innerParams.setMargins(
            margins[1],
            margins[0],
            margins[3],
            margins[2]
        )
        imageViewInner.layoutParams = innerParams

        return view
    }

    fun setBorders(cellInfo: Int) : List<Int> {
        var cellBorders = cellInfo
        var margins = MutableList(4){0}
        val px = 3 * context.resources.displayMetrics.density
        val margin = if(px > px.toInt()) (px + 1).toInt() else px.toInt()
        if(cellBorders >= 8){
            cellBorders -= 8
            margins[0] = margin
        }
        if(cellBorders >= 4){
            cellBorders -= 4
            margins[1] = margin
        }
        if(cellBorders >= 2){
            cellBorders -= 2
            margins[2] = margin
        }
        if(cellBorders >= 1){
            cellBorders -= 1
            margins[3] = margin
        }

        return margins.toList()
    }
}