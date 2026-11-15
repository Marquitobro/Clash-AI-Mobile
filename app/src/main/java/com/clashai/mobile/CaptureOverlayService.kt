package com.clashai.mobile

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.IBinder
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Button
import android.widget.Spinner
import android.widget.ArrayAdapter
import android.widget.Toast
import java.util.Locale

class CaptureOverlayService : Service() {
    private var projection: MediaProjection? = null
    private var virtualDisplay: VirtualDisplay? = null
    private var imageReader: ImageReader? = null
    private var overlay: LinearLayout? = null
    private var windowManager: WindowManager? = null
    private var overlayLabel: TextView? = null
    private var capturedFrames = 0L
    private var enemyElixir = 5f
    private lateinit var elixirLabel: TextView
    private val cardCosts = linkedMapOf(
        "Esqueletos" to 1, "Espíritu de hielo" to 1, "Descarga" to 2, "Tronco" to 2,
        "Murciélagos" to 2, "Duendes" to 2, "Caballero" to 3, "Arqueras" to 3,
        "Minero" to 3, "Pandilla de duendes" to 3, "Montapuercos" to 4,
        "Mosquetera" to 4, "Valquiria" to 4, "Mini P.E.K.K.A." to 4,
        "Bola de fuego" to 4, "Veneno" to 4, "Gigante" to 5, "Mago" to 5,
        "Bruja" to 5, "Globo bombástico" to 5, "Cohete" to 6, "P.E.K.K.A." to 7,
        "Sabueso de lava" to 7, "Gólem" to 8
    )
    private var lastStatusUpdate = 0L
    private val mainHandler = Handler(Looper.getMainLooper())
    private val projectionCallback = object : MediaProjection.Callback() {
        override fun onStop() {
            stopSelf()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        val notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this, CHANNEL_ID)
                .setContentTitle("Clash AI activo")
                .setContentText("Captura de pantalla autorizada")
                .setSmallIcon(android.R.drawable.ic_menu_view)
                .build()
        } else {
            @Suppress("DEPRECATION")
            Notification.Builder(this)
                .setContentTitle("Clash AI activo")
                .setContentText("Captura de pantalla autorizada")
                .setSmallIcon(android.R.drawable.ic_menu_view)
                .build()
        }
        startForeground(NOTIFICATION_ID, notification)

        val resultCode = intent?.getIntExtra(EXTRA_RESULT_CODE, 0) ?: 0
        val data = if (Build.VERSION.SDK_INT >= 33) {
            intent?.getParcelableExtra(EXTRA_DATA, Intent::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent?.getParcelableExtra(EXTRA_DATA)
        }
        if (data == null) {
            stopSelf()
            return START_NOT_STICKY
        }

        enemyElixir = getSharedPreferences("clash_ai", MODE_PRIVATE).getFloat("enemy_elixir_float", 5f).coerceIn(0f, 10f)\n        val manager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        projection = manager.getMediaProjection(resultCode, data)
        projection?.registerCallback(projectionCallback, mainHandler)
        startCapture()
        showOverlay()
        return START_NOT_STICKY
    }

    private fun startCapture() {
        val metrics = DisplayMetrics()
        @Suppress("DEPRECATION")
        (getSystemService(WINDOW_SERVICE) as WindowManager).defaultDisplay.getRealMetrics(metrics)
        val width = (metrics.widthPixels / 2).coerceAtLeast(1)
        val height = (metrics.heightPixels / 2).coerceAtLeast(1)
        val density = metrics.densityDpi
        imageReader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 2)
        virtualDisplay = projection?.createVirtualDisplay(
            "ClashAI-ScreenCapture",
            width,
            height,
            density,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            imageReader?.surface,
            null,
            null
        )
        imageReader?.setOnImageAvailableListener({ reader ->
            val image = reader.acquireLatestImage() ?: return@setOnImageAvailableListener
            try {
                capturedFrames += 1
                val now = System.currentTimeMillis()
                if (now - lastStatusUpdate >= 1000L) {
                    lastStatusUpdate = now
                    val frameWidth = image.width
                    val frameHeight = image.height
                    overlayLabel?.text = "CLASH AI  •  CAPTURA ACTIVA\\n" +
                        "Señal de pantalla: OK\\n" +
                        "Resolución: ${frameWidth}×${frameHeight}\\n" +
                        "Fotogramas recibidos: ${capturedFrames}\\n" +
                        "Reconocimiento de cartas: pendiente"
                }
            } finally {
                image.close()
            }
        }, mainHandler)
    }

    private fun showOverlay() {
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        val prefs = getSharedPreferences("clash_ai", MODE_PRIVATE)
        val title = TextView(this).apply {
            text = "CLASH AI  •  ASISTENTE"
            textSize = 12f
            setTextColor(0xFFFFFFFF.toInt())
            setPadding(10, 8, 10, 4)
        }
        elixirLabel = TextView(this).apply {
            textSize = 17f
            setTextColor(0xFF66E5FF.toInt())
            setPadding(10, 4, 10, 8)
        }
        updateElixirLabel()
        val spinner = Spinner(this).apply {
            adapter = ArrayAdapter(this@CaptureOverlayService, android.R.layout.simple_spinner_dropdown_item, cardCosts.keys.toList())
        }
        val register = Button(this).apply {
            text = "REGISTRAR CARTA RIVAL"
            isAllCaps = false
            setOnClickListener {
                val card = spinner.selectedItem?.toString() ?: return@setOnClickListener
                val cost = cardCosts[card] ?: return@setOnClickListener
                enemyElixir = (enemyElixir - cost).coerceAtLeast(0f)
                prefs.edit().putFloat("enemy_elixir_float", enemyElixir).apply()
                val cycle = prefs.getString("enemy_cycle", "").orEmpty().split("|||").filter { it.isNotBlank() }.toMutableList()
                cycle.add(0, card)
                while (cycle.size > 8) cycle.removeAt(cycle.lastIndex)
                prefs.edit().putString("enemy_cycle", cycle.joinToString("|||")).apply()
                sendBroadcast(Intent(ACTION_ENEMY_UPDATED).setPackage(packageName))
                updateElixirLabel()
                Toast.makeText(this@CaptureOverlayService, "$card: -$cost de elixir", Toast.LENGTH_SHORT).show()
            }
        }
        val close = Button(this).apply {
            text = "CERRAR ANALIZADOR"
            isAllCaps = false
            setOnClickListener { stopSelf() }
        }
        overlay = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(8, 6, 8, 6)
            setBackgroundColor(0xEE142238.toInt())
            addView(title)
            addView(elixirLabel)
            addView(spinner)
            addView(register)
            addView(close)
        }
        val type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            type,
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.END
            x = 16
            y = 120
        }
        windowManager?.addView(overlay, params)
    }

    private fun updateElixirLabel() {
        if (::elixirLabel.isInitialized) {
            elixirLabel.text = "ELIXIR RIVAL: " + String.format(Locale.getDefault(), "%.1f", enemyElixir) + " / 10"
        }
    }

