package com.poolaim.overlay

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {
    private lateinit var status: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(28, 36, 28, 28)
        }
        val title = TextView(this).apply {
            text = "PoolAim"
            textSize = 28f
            gravity = Gravity.CENTER
        }
        val description = TextView(this).apply {
            text = "Protótipo de assistente flutuante para 8 Ball Pool.\n\nEsta versão testa apenas a sobreposição. Ainda não reconhece bolas nem calcula trajetórias automaticamente."
            textSize = 16f
            setPadding(0, 24, 0, 24)
        }
        status = TextView(this).apply {
            textSize = 14f
            setPadding(0, 8, 0, 18)
        }
        val permissionButton = Button(this).apply {
            text = "Permitir sobreposição"
            setOnClickListener {
                if (!Settings.canDrawOverlays(this@MainActivity)) {
                    startActivity(Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:$packageName")
                    ))
                } else updateStatus()
            }
        }
        val startButton = Button(this).apply {
            text = "Iniciar janela flutuante"
            setOnClickListener {
                if (Settings.canDrawOverlays(this@MainActivity)) {
                    startService(Intent(this@MainActivity, OverlayService::class.java))
                    status.text = "Janela flutuante iniciada."
                } else status.text = "Conceda primeiro a permissão de sobreposição."
            }
        }
        val stopButton = Button(this).apply {
            text = "Fechar janela flutuante"
            setOnClickListener {
                stopService(Intent(this@MainActivity, OverlayService::class.java))
                status.text = "Janela flutuante fechada."
            }
        }
        fun add(view: android.view.View) {
            root.addView(view, LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ))
        }
        add(title)
        add(description)
        add(status)
        add(permissionButton)
        add(startButton)
        add(stopButton)
        setContentView(root)
        updateStatus()
    }

    override fun onResume() {
        super.onResume()
        if (::status.isInitialized) updateStatus()
    }

    private fun updateStatus() {
        status.text = if (Settings.canDrawOverlays(this))
            "Permissão de sobreposição: concedida."
        else "Permissão de sobreposição: não concedida."
    }
}
