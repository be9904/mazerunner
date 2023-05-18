package edu.skku.map.pa2

data class MazeCell(
    val cellId: Int,
    val cellSize: Int,
    val borderInfo: Int,
    var imageId: Int,
    var imageRotation: Float
){
    fun getBorders() : ArrayList<Int> {
        var cellBorders = borderInfo
        var margins = ArrayList<Int>(0)
        if(cellBorders >= 8){
            cellBorders -= 8
            margins.add(1)
        } else margins.add(0)
        if(cellBorders >= 4){
            cellBorders -= 4
            margins.add(1)
        } else margins.add(0)
        if(cellBorders >= 2){
            cellBorders -= 2
            margins.add(1)
        } else margins.add(0)
        if(cellBorders >= 1){
            cellBorders -= 1
            margins.add(1)
        } else margins.add(0)

        return margins
    }
}