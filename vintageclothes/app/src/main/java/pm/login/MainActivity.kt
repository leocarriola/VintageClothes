package pm.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import pm.login.databinding.ActivityMainBinding
import pm.login.fragments.HomeFragment


class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Verificar se o usuário está logado ao iniciar o aplicativo
        val sharedPref = getSharedPreferences("pmLogin", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPref.getBoolean("login", false)

        if (isLoggedIn) {
            val idUser = sharedPref.getInt("id_user", -1)
            val nome = sharedPref.getString("nome", "")

            if (idUser != -1 && !nome.isNullOrEmpty()) {
                // O user está logado, redireciona para o HomeFragment
                navigateToFragment(HomeFragment().apply {
                    arguments = Bundle().apply {
                        putInt("id_user", idUser)
                        putString("nome", nome)
                    }
                })
            }
        }

        // Configurando o botão de login
        binding.button.setOnClickListener {
            doLogin(it)
        }

        // Configurando o botão de registro

        binding.buttonRegister?.setOnClickListener {
            // Redireciona para a RegistoActivity
            val intent = Intent(this, RegistoActivity::class.java)
            startActivity(intent)
        }
    }

    fun doLogin(view: View) {
        val email = binding.editTextEmail.text.toString()
        val password = binding.editTextPassword.text.toString().trim()

        // Verifica se os campos estão vazios
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show()
            return
        }

        // Instancia a fila de requisições
        val queue = Volley.newRequestQueue(this)
        val url = "https://esan-tesp-ds-paw.web.ua.pt/tesp-ds-g31/api/modelo.php"

        val jsonParams = JSONObject().apply {
            put("email", email)
            put("password", password)
        }

        Log.d("JSONRequest", "Enviando JSON: $jsonParams")

        val jsonRequest = JsonObjectRequest(
            Request.Method.POST, url, jsonParams,
            { response ->
                try {
                    Log.d("JSONResponse", "Resposta JSON: $response")

                    if (response.has("status")) {
                        val status = response.getString("status")
                        val msg = response.optString("message", "Sem mensagem")
                        if (status == "OK") {
                            val nome = response.getString("nome")
                            val idUser = response.getInt("id_user")
                            val telefone = response.getInt("telefone")
                            val morada = response.getString("morada")

                            val sharedPref = getSharedPreferences("pmLogin", MODE_PRIVATE)
                            sharedPref.edit().apply {
                                putBoolean("login", true)
                                putString("nome", nome)
                                putInt("id_user", idUser)
                                putInt("telefone", telefone)
                                putString("morada", morada)

                                apply()
                            }

                            val intent = Intent(this,BaseActivity::class.java).apply{
                                putExtra("id_user",idUser)
                                putExtra("nome",nome)
                            }
                            startActivity(intent)
                            finish()

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

        queue.add(jsonRequest)
    }

    private fun navigateToFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commitAllowingStateLoss()
    }
}

