
package com.dm2.orden_del_auror

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat

// Actividad que muestra la pantalla de presentación de la aplicación
// antes de acceder a la pantalla principal.
class SplashActivity : AppCompatActivity() {

    // Handler asociado al hilo principal de la aplicación.
    // Permite ejecutar acciones después de un tiempo determinado.
    private val handler = Handler(Looper.getMainLooper())

    // Tiempo que permanece visible la pantalla de presentación: 2500 milisegundos.
    private val tiempoCarga = 2500L

    // Acción que abre la actividad principal cuando termina el tiempo de carga.
    private val abrirPrincipal = Runnable {
        // Comprueba que la actividad siga activa antes de abrir la pantalla principal.
        if (!isFinishing && !isDestroyed) {

            // Crea un Intent para iniciar MainActivity.
            startActivity(Intent(this, MainActivity::class.java))

            // Finaliza la pantalla de presentación para que no quede en la pila.
            finish()
        }
    }

    // Método que se ejecuta al crear la actividad.
    override fun onCreate(savedInstanceState: Bundle?) {

        // Inicializa la pantalla de carga proporcionada por el sistema Android.
        installSplashScreen()

        super.onCreate(savedInstanceState)

        // Configura el color de las barras de estado y navegación en negro.
        window.statusBarColor = Color.BLACK
        window.navigationBarColor = Color.BLACK

        // Configura la apariencia de las barras del sistema.
        // Se desactivan los iconos oscuros para mantenerlos claros sobre el fondo negro.
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = false
        }

        // Oculta la barra de acción de la aplicación.
        supportActionBar?.hide()

        // Carga el diseño XML de la pantalla de presentación.
        setContentView(R.layout.activity_splash)

        // Inicia las animaciones del logotipo y de los destellos decorativos.
        iniciarAnimaciones()

        // Programa la apertura de la pantalla principal después del tiempo establecido.
        handler.postDelayed(abrirPrincipal, tiempoCarga)
    }

    // Inicia las animaciones de los elementos visuales de la pantalla de presentación.
    private fun iniciarAnimaciones() {

        // Recupera la referencia al logotipo de la pantalla.
        val logo = findViewById<ImageView>(R.id.ivLogoSplash)

        // Establece la transparencia inicial del logotipo a 0, haciéndolo invisible.
        logo.alpha = 0f

        // Establece el tamaño inicial del logotipo al 85 % de su tamaño original.
        logo.scaleX = 0.85f
        logo.scaleY = 0.85f

        // Configura la animación de aparición del logotipo.
        logo.animate()
            // Hace que el logotipo pase de transparente a completamente visible.
            .alpha(1f)
            // Aumenta progresivamente su tamaño horizontal hasta el tamaño original.
            .scaleX(1f)
            // Aumenta progresivamente su tamaño vertical hasta el tamaño original.
            .scaleY(1f)
            // Establece la duración de la animación en 1200 milisegundos.
            .setDuration(1200)
            // Aplica una interpolación que suaviza el inicio y el final de la animación.
            .setInterpolator(AccelerateDecelerateInterpolator())
            // Inicia la animación.
            .start()

        // Inicia la animación del primer destello sin retraso.
        animarDestello(R.id.destello1, 0)

        // Inicia la animación del segundo destello con 300 milisegundos de retraso.
        animarDestello(R.id.destello2, 300)

        // Inicia la animación del tercer destello con 600 milisegundos de retraso.
        animarDestello(R.id.destello3, 600)

        // Inicia la animación del cuarto destello con 900 milisegundos de retraso.
        animarDestello(R.id.destello4, 900)
    }

    // Configura y ejecuta la animación de un destello decorativo.
    // Recibe el identificador del elemento y el retraso antes de comenzar su animación.
    private fun animarDestello(id: Int, retraso: Long) {

        // Recupera el TextView que representa el destello.
        val destello = findViewById<TextView>(id)

        // Establece la opacidad inicial del destello al 20 %.
        destello.alpha = 0.2f

        // Define los valores de escala horizontal que tendrá el destello durante la animación.
        // Primero se reduce, después aumenta y finalmente vuelve a reducirse.
        val escalaX = PropertyValuesHolder.ofFloat(
            View.SCALE_X, 0.7f, 1.3f, 0.7f
        )

        // Define los valores de escala vertical siguiendo el mismo comportamiento.
        val escalaY = PropertyValuesHolder.ofFloat(
            View.SCALE_Y, 0.7f, 1.3f, 0.7f
        )

        // Define los valores de opacidad del destello.
        // Pasa de una transparencia del 20 % a una visibilidad completa
        // y vuelve a una transparencia del 20 %.
        val opacidad = PropertyValuesHolder.ofFloat(
            View.ALPHA, 0.2f, 1f, 0.2f
        )

        // Crea un ObjectAnimator que modifica simultáneamente
        // la escala horizontal, la escala vertical y la opacidad.
        ObjectAnimator.ofPropertyValuesHolder(
            destello,
            escalaX,
            escalaY,
            opacidad
        ).apply {

            // Establece la duración de cada ciclo de animación en 1800 milisegundos.
            duration = 1800

            // Aplica el retraso recibido como parámetro antes de comenzar.
            startDelay = retraso

            // Configura la animación para repetirse indefinidamente.
            repeatCount = ObjectAnimator.INFINITE

            // Suaviza el inicio y el final de cada ciclo de animación.
            interpolator = AccelerateDecelerateInterpolator()

            // Inicia la animación del destello.
            start()
        }
    }

    // Método que se ejecuta cuando se destruye la actividad.
    override fun onDestroy() {

        // Elimina las acciones pendientes del Handler para evitar
        // que se intente abrir MainActivity después de destruir esta actividad.
        handler.removeCallbacks(abrirPrincipal)

        super.onDestroy()
    }
}
