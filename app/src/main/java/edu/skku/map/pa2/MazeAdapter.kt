package edu.skku.map.pa2

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout.LayoutParams
import androidx.constraintlayout.widget.ConstraintLayout

class MazeAdapter(val context: Context, val size: Int, val cells: List<Int>): BaseAdapter() {
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

        val cellSize = 350 / size
        val density = context.resources.displayMetrics.density
        val cellSizePx = (cellSize * density).toInt()

        val cell = view.findViewById<ConstraintLayout>(R.id.cell)
        val imageViewIcon = view.findViewById<ImageView>(R.id.imageViewIcon)
        val imageViewInner = view.findViewById<ImageView>(R.id.imageViewInner)

        // set cell size
        val cellParams = ConstraintLayout.LayoutParams(cellSizePx, cellSizePx)
        cell.layoutParams = cellParams

        // set margins
        var innerParams = imageViewInner.layoutParams as ViewGroup.MarginLayoutParams
        val margins = setBorders(cells[position])
        println("Margins: " + margins.toString())
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
        if(cellBorders >= 8){
            cellBorders -= 8
            margins[0] = (3 * context.resources.displayMetrics.density).toInt()
        }
        if(cellBorders >= 4){
            cellBorders -= 4
            margins[1] = (3 * context.resources.displayMetrics.density).toInt()
        }
        if(cellBorders >= 2){
            cellBorders -= 2
            margins[2] = (3 * context.resources.displayMetrics.density).toInt()
        }
        if(cellBorders >= 1){
            cellBorders -= 1
            margins[3] = (3 * context.resources.displayMetrics.density).toInt()
        }
        println("margin: " + (3 * context.resources.displayMetrics.density).toInt())

        return margins.toList()
    }
}