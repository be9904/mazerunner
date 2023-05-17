package edu.skku.map.pa2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

class MazeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maze)

        val mazeName = intent.getStringExtra(MazeSelectionActivity.EXT_NAME)

        Toast.makeText(
            applicationContext,
            mazeName,
            Toast.LENGTH_SHORT
        ).show()
    }
}