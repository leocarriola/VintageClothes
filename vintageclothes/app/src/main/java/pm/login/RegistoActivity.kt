
package pm.login
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import pm.login.databinding.ActivityRegistoBinding

class RegistoActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityRegistoBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Configurando o botão de registro
        binding.buttonRegistar.setOnClickListener {
            doRegister()
        }
    }

    fun doRegister() {
        // Pegando os dados inseridos pelo usuário
        val nome = binding.editTextNome.text.toString().trim()
        val telefone = binding.editTextTelefone.text.toString().trim()
        val email = binding.editTextEmail.text.toString().trim()
        val morada = binding.editTextMorada.text.toString().trim()
        val password = binding.editTextPassword.text.toString().trim()
        val confirmPassword = binding.editTextConfirmPassword.text.toString().trim()

        // Verifica se todos os campos estão preenchidos
        if (nome.isEmpty() || telefone.isEmpty() || email.isEmpty() || morada.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show()
            return
        }

        // Verifica se as senhas coincidem
        if (password != confirmPassword) {
            Toast.makeText(this, "As senhas não coincidem", Toast.LENGTH_SHORT).show()
            return
        }

        // Instancia a fila de requisições
        val queue = Volley.newRequestQueue(this)
        val url = "https://esan-tesp-ds-paw.web.ua.pt/tesp-ds-g31/api/registo.php"

        // JSON com os dados de registro
        val jsonParams = JSONObject().apply {
            put("nome", nome)
            put("telefone", telefone)
            put("email", email)
            put("morada", morada)
            put("password", password)
        }

        Log.d("JSONRequest", "Enviando JSON: $jsonParams")

        // Criar a requisição para o servidor
        val jsonRequest = JsonObjectRequest(
            Request.Method.POST, url, jsonParams,
            { response ->
                try {
                    Log.d("JSONResponse", "Resposta JSON: $response")

                    // Verifica se o JSON contém o status
                    if (response.has("status")) {
                        val status = response.getString("status")
                        val msg = response.optString("message", "Sem mensagem")

                        if (status == "OK") {
                            Toast.makeText(this, "Registro realizado com sucesso!", Toast.LENGTH_SHORT).show()
                            finish()  // Finaliza a activity de registro e volta para a tela anterior
                        } else {
                            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Log.e("JSONResponse", "Resposta JSON não contém 'status'")
                        Toast.makeText(this, "Erro: Status ausente na resposta", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                    Log.e("JSONResponse", "Erro ao analisar resposta JSON", e)
                }
            },
            { error ->
                Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show()
                Log.e("JSONResponse", "Erro na requisição JSON", error)
            }
        )

        // Adiciona a requisição na fila
        queue.add(jsonRequest)
    }
}
