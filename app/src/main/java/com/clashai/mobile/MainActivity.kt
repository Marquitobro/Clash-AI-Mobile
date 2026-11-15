
package com.clashai.mobile

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(28, 32, 28, 32)
            setBackgroundColor(Color.rgb(15, 20, 35))
        }

        fun addText(text: String, size: Float, color: Int) {
            layout.addView(TextView(this).apply {
                this.text = text
                textSize = size
                setTextColor(color)
                gravity = Gravity.CENTER
                setPadding(8, 16, 8, 16)
            })
        }

        addText("CLASH AI", 32f, Color.WHITE)
        addText("MOBILE • COMPETITIVE", 14f, Color.CYAN)
        addText("Asistente inteligente de Clash Royale", 16f, Color.WHITE)

        val status = TextView(this).apply {
            text = "Estado: esperando partida"
            textSize = 16f
            setTextColor(Color.GREEN)
            gravity = Gravity.CENTER
            setPadding(12, 24, 12, 24)
        }
        layout.addView(status)

        val start = Button(this).apply {
            text = "INICIAR ANÁLISIS"
            setOnClickListener {
                status.text = "Estado: módulo de análisis pendiente"
            }
        }
        layout.addView(start)

        addText(
            "Próximamente: reconocimiento de cartas, elixir y ciclo rival.",
            14f,
            Color.LTGRAY
        )

        setContentView(layout)
    }
}
