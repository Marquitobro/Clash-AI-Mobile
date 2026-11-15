package com.clashai.mobile

import android.app.Activity
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.*
import android.text.InputType
import java.util.Locale

class MainActivity : Activity() {\n    companion object { private const val REQUEST_CAPTURE = 901 }
    private val handler = Handler(Looper.getMainLooper())
    private var remaining = 180
    private var running = false
    private var ownElixir = 5
    private var enemyElixir = 5
    private val ownCycle = mutableListOf<String>()
    private val enemyCycle = mutableListOf<String>()
    private lateinit var timerText: TextView
    private lateinit var phaseText: TextView
    private lateinit var ownElixirText: TextView
    private lateinit var enemyElixirText: TextView
    private lateinit var ownCycleText: TextView
    private lateinit var enemyCycleText: TextView
    private lateinit var adviceText: TextView
    private lateinit var startButton: Button
    private var lastTick = 0L

    private val tick = object : Runnable {
        override fun run() {
            if (!running) return
            val now = System.currentTimeMillis()
            if (now - lastTick >= 1000) {
                remaining -= 1
                lastTick = now
                if (remaining < -120) remaining = -120
                refresh()
            }
            handler.postDelayed(this, 200)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = getSharedPreferences("clash_ai", MODE_PRIVATE)
        ownElixir = prefs.getInt("own_elixir", 5).coerceIn(0, 10)
        enemyElixir = prefs.getInt("enemy_elixir", 5).coerceIn(0, 10)
        ownCycle.addAll(prefs.getString("own_cycle", "")!!.split("|||").filter { it.isNotBlank() }.take(8))
        enemyCycle.addAll(prefs.getString("enemy_cycle", "")!!.split("|||").filter { it.isNotBlank() }.take(8))

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.rgb(11, 17, 31))
        }
        val scroll = ScrollView(this)
        val page = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 24, 20, 32)
        }
        scroll.addView(page)
        root.addView(scroll)

        text(page, "CLASH AI", 30f, Color.WHITE, true)
        text(page, "MOBILE  •  ASISTENTE COMPETITIVO", 12f, Color.CYAN, true)
        text(page, "Asistente visual para analizar tus partidas.", 14f, Color.LTGRAY, false)

        section(page, "MODO AUTOMÁTICO")
        button("ACTIVAR VENTANA FLOTANTE", page) { activateCaptureOverlay() }
        text(page, "Android solicitará permiso para mostrar la ventana y capturar la pantalla mientras juegas. Esta versión prepara la captura y la ventana; todavía no reconoce cartas ni calcula elixir automáticamente.", 12f, Color.LTGRAY, false)

        section(page, "TIEMPO DE PARTIDA")
        timerText = text(page, "03:00", 42f, Color.WHITE, true)
        phaseText = text(page, "ELIXIR NORMAL", 14f, Color.CYAN, true)
        val timerButtons = row(page)
        startButton = button("INICIAR", timerButtons) { toggleTimer() }
        button("REINICIAR", timerButtons) { resetTimer() }

        section(page, "SEGUIMIENTO DE ELIXIR")
        ownElixirText = text(page, "TU ELIXIR: " + ownElixir + " / 10", 18f, Color.WHITE, true)
        val ownButtons = row(page)
        button("-1", ownButtons) { ownElixir = (ownElixir - 1).coerceAtLeast(0); refresh() }
        button("+1", ownButtons) { ownElixir = (ownElixir + 1).coerceAtMost(10); refresh() }
        enemyElixirText = text(page, "ELIXIR RIVAL ESTIMADO: " + enemyElixir + " / 10", 18f, Color.WHITE, true)
        val enemyButtons = row(page)
        button("RIVAL -1", enemyButtons) { enemyElixir = (enemyElixir - 1).coerceAtLeast(0); refresh() }
        button("RIVAL +1", enemyButtons) { enemyElixir = (enemyElixir + 1).coerceAtMost(10); refresh() }
        text(page, "Ajusta manualmente el elixir cuando observes un gasto. No se lee automáticamente la pantalla del juego.", 12f, Color.LTGRAY, false)

        section(page, "CICLO DE TUS CARTAS")
        val ownInput = edit(page, "Nombre de carta que acabas de usar")
        button("REGISTRAR CARTA PROPIA", page) {
            addCard(ownCycle, ownInput.text.toString())
            ownInput.setText("")
            refresh()
        }
        ownCycleText = text(page, "Cartas registradas: ninguna", 14f, Color.WHITE, false)

        section(page, "CICLO DEL RIVAL")
        val enemyInput = edit(page, "Carta que acaba de usar el rival")
        button("REGISTRAR CARTA DEL RIVAL", page) {
            addCard(enemyCycle, enemyInput.text.toString())
            enemyInput.setText("")
            refresh()
        }
        enemyCycleText = text(page, "Cartas registradas: ninguna", 14f, Color.WHITE, false)

        section(page, "CONSEJO TÁCTICO")
        adviceText = text(page, "Inicia una partida y registra las cartas para recibir orientación.", 15f, Color.rgb(180, 235, 255), false)

        section(page, "PARTIDA Y DATOS")
        button("NUEVA PARTIDA (BORRAR REGISTROS)", page) {
            resetTimer()
            ownElixir = 5
            enemyElixir = 5
            ownCycle.clear()
            enemyCycle.clear()
            refresh()
        }
        text(page, "Asistente de seguimiento manual. Las estimaciones dependen de los datos registrados; no modifica Clash Royale ni automatiza jugadas.", 12f, Color.GRAY, false)
        setContentView(root)
        refresh()
    }


    private fun activateCaptureOverlay() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            val settingsIntent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + packageName)
            )
            startActivity(settingsIntent)
            Toast.makeText(this, "Autoriza mostrar sobre otras aplicaciones y vuelve a pulsar el botón.", Toast.LENGTH_LONG).show()
            return
        }
        val manager = getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        startActivityForResult(manager.createScreenCaptureIntent(), REQUEST_CAPTURE)
    }

    @Deprecated("Deprecated in Android, retained for broad device compatibility")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CAPTURE && resultCode == RESULT_OK && data != null) {
            val serviceIntent = Intent(this, CaptureOverlayService::class.java).apply {
                putExtra(CaptureOverlayService.EXTRA_RESULT_CODE, resultCode)
                putExtra(CaptureOverlayService.EXTRA_DATA, data)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent)
            } else {
                startService(serviceIntent)
            }
            Toast.makeText(this, "Captura y ventana flotante activadas.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun toggleTimer() {
        running = !running
        if (running) {
            lastTick = System.currentTimeMillis()
            startButton.text = "PAUSAR"
            handler.post(tick)
        } else {
            startButton.text = "CONTINUAR"
            handler.removeCallbacks(tick)
        }
        refresh()
    }

    private fun resetTimer() {
        running = false
        handler.removeCallbacks(tick)
        remaining = 180
        if (::startButton.isInitialized) startButton.text = "INICIAR"
        refresh()
    }

    private fun addCard(cycle: MutableList<String>, raw: String) {
        val name = raw.trim()
        if (name.isEmpty()) {
            Toast.makeText(this, "Escribe el nombre de una carta", Toast.LENGTH_SHORT).show()
            return
        }
        cycle.add(0, name)
        if (cycle.size > 8) cycle.removeAt(8)
    }

    private fun refresh() {
        if (!::timerText.isInitialized) return
        val absTime = kotlin.math.abs(remaining)
        val clock = String.format(Locale.getDefault(), "%02d:%02d", absTime / 60, absTime % 60)
        timerText.text = if (remaining < 0) "+" + clock else clock
        phaseText.text = when {
            remaining > 120 -> "ELIXIR NORMAL"
            remaining > 0 -> "DOBLE ELIXIR"
            remaining > -120 -> "TIEMPO EXTRA"
            else -> "PARTIDA FINALIZADA"
        }
        ownElixirText.text = "TU ELIXIR: " + ownElixir + " / 10"
        enemyElixirText.text = "ELIXIR RIVAL (ESTIMADO): " + enemyElixir + " / 10"
        ownCycleText.text = cycleSummary(ownCycle)
        enemyCycleText.text = cycleSummary(enemyCycle)
        adviceText.text = buildAdvice()
        getSharedPreferences("clash_ai", MODE_PRIVATE).edit()
            .putInt("own_elixir", ownElixir)
            .putInt("enemy_elixir", enemyElixir)
            .putString("own_cycle", ownCycle.joinToString("|||"))
            .putString("enemy_cycle", enemyCycle.joinToString("|||"))
            .apply()
    }

    private fun cycleSummary(cards: List<String>): String {
        if (cards.isEmpty()) return "Aún no hay cartas registradas."
        return "Últimas usadas: " + cards.take(4).joinToString(" • ") +
            "\nRegistro (" + cards.size + "/8): " + cards.joinToString(" → ")
    }

    private fun buildAdvice(): String {
        val timeAdvice = when {
            remaining > 120 -> "Tiempo normal: evita gastar todo tu elixir sin una razón."
            remaining > 0 -> "Doble elixir: construye ataques completos, conservando respuesta defensiva."
            remaining > -120 -> "Tiempo extra: prioriza intercambios eficientes y protege tu torre más débil."
            else -> "Partida terminada. Revisa tus intercambios de elixir y cartas."
        }
        val elixirAdvice = when {
            ownElixir >= 8 && enemyElixir <= 4 -> " Tienes ventaja estimada: considera presionar sin comprometer toda tu defensa."
            ownElixir <= 3 && enemyElixir >= 7 -> " El rival podría presionar: juega defensivamente y recupera elixir."
            else -> " Conserva elixir para responder a la condición de victoria rival."
        }
        val cycleAdvice = if (enemyCycle.size >= 4) {
            " Registra patrones para detectar cuándo su condición de victoria sale de ciclo."
        } else {
            " Registra más cartas rivales para estimar mejor su ciclo."
        }
        return timeAdvice + elixirAdvice + cycleAdvice
    }

    private fun section(parent: LinearLayout, title: String) {
        parent.addView(TextView(this).apply {
            text = title
            textSize = 14f
            setTextColor(Color.CYAN)
            setPadding(4, 24, 4, 8)
        })
        parent.addView(View(this).apply { setBackgroundColor(Color.rgb(40, 57, 82)) }, LinearLayout.LayoutParams(-1, 1))
    }

    private fun text(parent: LinearLayout, value: String, size: Float, color: Int, centered: Boolean): TextView {
        val view = TextView(this).apply {
            text = value
            textSize = size
            setTextColor(color)
            gravity = if (centered) Gravity.CENTER else Gravity.START
            setPadding(4, 9, 4, 9)
        }
        parent.addView(view)
        return view
    }

    private fun row(parent: LinearLayout): LinearLayout {
        val view = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER }
        parent.addView(view)
        return view
    }

    private fun button(label: String, parent: LinearLayout, action: () -> Unit): Button {
        val view = Button(this).apply {
            text = label
            isAllCaps = false
            setOnClickListener { action() }
        }
        parent.addView(view, LinearLayout.LayoutParams(0, -2, 1f).apply { setMargins(4, 6, 4, 6) })
        return view
    }

    private fun edit(parent: LinearLayout, hintText: String): EditText {
        val view = EditText(this).apply {
            hint = hintText
            setSingleLine(true)
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
            setTextColor(Color.WHITE)
            setHintTextColor(Color.GRAY)
        }
        parent.addView(view, LinearLayout.LayoutParams(-1, -2))
        return view
    }

    override fun onDestroy() {
        handler.removeCallbacks(tick)
        super.onDestroy()
    }
}
