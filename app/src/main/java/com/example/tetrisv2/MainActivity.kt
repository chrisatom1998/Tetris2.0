package com.example.tetrisv2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button

class MainActivity : AppCompatActivity() {
    private lateinit var gameView: GameView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        gameView = findViewById(R.id.gameView)

        findViewById<Button>(R.id.btnLeft).setOnClickListener { gameView.moveLeft() }
        findViewById<Button>(R.id.btnRight).setOnClickListener { gameView.moveRight() }
        findViewById<Button>(R.id.btnRotate).setOnClickListener { gameView.rotate() }
        findViewById<Button>(R.id.btnDrop).setOnClickListener { gameView.drop() }
    }
}
