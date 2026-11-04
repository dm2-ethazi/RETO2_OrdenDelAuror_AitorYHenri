
package com.dm2.orden_del_auror

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.dm2.orden_del_auror.ui.AesActivity
import com.dm2.orden_del_auror.ui.CesarActivity
import com.dm2.orden_del_auror.ui.SettingsActivity

/**
 * Actividad principal de Orden del Auror.
 *
 * Permite acceder a las herramientas de descifrado AES y César,
 * además del menú de ajustes.
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        configurarBotones()
    }

    /**
     * Configura los botones de acceso a las herramientas.
     */
    private fun configurarBotones() {

        // Acceso al descifrado AES
        findViewById<Button>(R.id.btnAes).setOnClickListener {
            val intent = Intent(this, AesActivity::class.java)
            startActivity(intent)
        }

        // Acceso al descifrado César
        findViewById<Button>(R.id.btnCesar).setOnClickListener {
            val intent = Intent(this, CesarActivity::class.java)
            startActivity(intent)
        }

        // Acceso al menú de ajustes mediante el engranaje
        findViewById<ImageButton>(R.id.btnAjustes).setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }
}
