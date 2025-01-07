package pm.login

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import pm.login.databinding.ActivityEditarPerfilBinding

class Editar_Perfil : AppCompatActivity() {

    private lateinit var binding: ActivityEditarPerfilBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inicializa o binding
        binding = ActivityEditarPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}