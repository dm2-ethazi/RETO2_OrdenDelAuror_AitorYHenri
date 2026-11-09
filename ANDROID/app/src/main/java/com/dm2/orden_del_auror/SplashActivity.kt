
package com.dm2.orden_del_auror

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class SplashActivity : AppCompatActivity() {

    private val handler = Handler(Looper.getMainLooper())
    private val tiempoCarga = 2500L

    private val abrirPrincipal = Runnable {
        if (!isFinishing && !isDestroyed) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        // Inicializar la pantalla de carga del sistema
        installSplashScreen()

        super.onCreate(savedInstanceState)

        window.statusBarColor = android.graphics.Color.BLACK
        window.navigationBarColor = android.graphics.Color.BLACK

        supportActionBar?.hide()

        setContentView(R.layout.activity_splash)

        iniciarAnimaciones()

        handler.postDelayed(abrirPrincipal, tiempoCarga)
    }

    private fun iniciarAnimaciones() {

        val logo = findViewById<ImageView>(R.id.ivLogoSplash)

        logo.alpha = 0f
        logo.scaleX = 0.85f
        logo.scaleY = 0.85f

        logo.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(1200)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .start()

        animarDestello(R.id.destello1, 0)
        animarDestello(R.id.destello2, 300)
        animarDestello(R.id.destello3, 600)
        animarDestello(R.id.destello4, 900)
    }

    private fun animarDestello(id: Int, retraso: Long) {

        val destello = findViewById<TextView>(id)

        destello.alpha = 0.2f

        val escalaX = PropertyValuesHolder.ofFloat(
            View.SCALE_X, 0.7f, 1.3f, 0.7f
        )

        val escalaY = PropertyValuesHolder.ofFloat(
            View.SCALE_Y, 0.7f, 1.3f, 0.7f
        )

        val opacidad = PropertyValuesHolder.ofFloat(
            View.ALPHA, 0.2f, 1f, 0.2f
        )

        ObjectAnimator.ofPropertyValuesHolder(
            destello,
            escalaX,
            escalaY,
            opacidad
        ).apply {
            duration = 1800
            startDelay = retraso
            repeatCount = ObjectAnimator.INFINITE
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }
    }

    override fun onDestroy() {
        handler.removeCallbacks(abrirPrincipal)
        super.onDestroy()
    }
}
