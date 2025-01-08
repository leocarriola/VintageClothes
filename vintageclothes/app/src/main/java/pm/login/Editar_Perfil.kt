package pm.login

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import pm.login.databinding.ActivityEditarPerfilBinding

class Editar_Perfil : AppCompatActivity() {

    private lateinit var binding: ActivityEditarPerfilBinding
    private lateinit var requestQueue: RequestQueue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa o binding
        binding = ActivityEditarPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializa o Volley RequestQueue
        requestQueue = Volley.newRequestQueue(this)

        // Recupera as informações do usuário a partir das SharedPreferences
        val sharedPref = getSharedPreferences("pmLogin", Context.MODE_PRIVATE)
        val userName = sharedPref.getString("nome", "")
        val idUser = sharedPref.getInt("id_user", -1)
        val telefone = sharedPref.getInt("telefone", 0)
        val email = sharedPref.getString("email", "")
        val morada = sharedPref.getString("morada", "")

        // Preenche os campos com os dados existentes
        binding.editTextName.setText(userName)
        binding.editTextPhone.setText(if (telefone != 0) telefone.toString() else "")
        binding.editTextEmail.setText(email)
        binding.editTextAddress.setText(morada)

        // Configura o botão de salvar
        binding.buttonSave.setOnClickListener {

            val updatedName = binding.editTextName.text.toString()
            val updatedPhone = binding.editTextPhone.text.toString().toIntOrNull()
            val updatedEmail = binding.editTextEmail.text.toString()
            val updatedAddress = binding.editTextAddress.text.toString()

            if (updatedName.isBlank() || updatedPhone == null || updatedEmail.isBlank() || updatedAddress.isBlank()) {
                Toast.makeText(this, "Todos os campos devem ser preenchidos!", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            // Envia os dados para a API
            updateUserDataOnAPI(
                idUser,
                updatedName,
                updatedPhone,
                updatedEmail,
                updatedAddress,
                sharedPref
            )
        }
    }

    private fun updateUserDataOnAPI(
        idUser: Int, // Adicione idUser como parâmetro
        name: String,
        phone: Int,
        email: String,
        address: String,
        sharedPref: SharedPreferences
    ) {
        val url = "https://esan-tesp-ds-paw.web.ua.pt/tesp-ds-g31/api/alterardados.php"

        // Cria o JSON com os dados
        val json = JSONObject().apply {
            put("id", idUser) // Use o idUser passado como parâmetro
            put("nome", name)
            put("telefone", phone)
            put("email", email)
            put("morada", address)
        }

        // Cria a requisição POST com Volley
        val request = JsonObjectRequest(
            Request.Method.POST,
            url,
            json,
            Response.Listener { response ->
                try {
                    // Processa a resposta da API
                    if (response.getBoolean("success")) {
                        // Atualiza os dados no SharedPreferences
                        val sharedPreferences = getSharedPreferences("pmLogin", MODE_PRIVATE)
                        sharedPreferences.edit().apply {
                            // Atualiza os dados na chave pmlogin
                            putString("nome", name)
                            putInt("telefone", phone)
                            putString("email", email)
                            putString("morada", address)
                            apply()
                        }

                        Toast.makeText(this, "Dados atualizados com sucesso!", Toast.LENGTH_SHORT)
                            .show()
                        finish() // Fecha a Activity (ou outra ação que você queira)
                    } else {
                        val message = response.optString("message", "Erro desconhecido")
                        Toast.makeText(this, "Erro ao atualizar: $message", Toast.LENGTH_SHORT)
                            .show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Erro ao processar resposta da API", Toast.LENGTH_SHORT)
                        .show()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Erro ao conectar à API: ${error.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        )

        // Adiciona a requisição à fila
        requestQueue.add(request)
    }
}


