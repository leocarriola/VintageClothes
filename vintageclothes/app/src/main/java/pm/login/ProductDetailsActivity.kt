package pm.login

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import org.json.JSONException
import org.json.JSONObject

class ProductDetailsActivity : AppCompatActivity() {
    private var productImage: ImageView? = null
    private var productName: TextView? = null
    private var productDescription: TextView? = null
    private var productPrice: TextView? = null
    private var backArrow: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_details)

        productImage = findViewById(R.id.product_image)
        productName = findViewById(R.id.product_name)
        productDescription = findViewById(R.id.product_description)
        productPrice = findViewById(R.id.product_price)
        backArrow = findViewById(R.id.back_arrow)

        // Configurar a ação de voltar ao clicar na seta
        backArrow?.setOnClickListener {
            finish() // Encerra esta atividade e volta para a anterior
        }

        // Obter o ID do produto do Intent
        val productId: Int = intent.getIntExtra("id_produto", -1)

        // Verificar se o ID é válido
        if (productId != -1) {
            fetchProductDetails(productId)
        } else {
            Toast.makeText(this, "Produto inválido!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    // URL base para imagens
    private val baseUrlImagem = "https://esan-tesp-ds-paw.web.ua.pt/tesp-ds-g31/uploads/imagem/"

    private fun fetchProductDetails(productId: Int) {
        // URL da API
        val url = "https://esan-tesp-ds-paw.web.ua.pt/tesp-ds-g31/api/detalhesprod.php"

        // Criar o corpo da requisição em JSON
        val requestBody = JSONObject()
        try {
            requestBody.put("id", productId)
        } catch (e: JSONException) {
            Log.e("FetchProductDetails", "Erro ao criar JSON do corpo: ${e.message}", e)
            Toast.makeText(this, "Erro ao preparar requisição!", Toast.LENGTH_SHORT).show()
            return
        }

        // Log do corpo da requisição
        Log.d("FetchProductDetails", "Corpo da requisição: $requestBody")

        val queue = Volley.newRequestQueue(this)
        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.POST, url, requestBody,
            { response: JSONObject ->
                try {
                    // Log da resposta completa
                    Log.d("FetchProductDetails", "Resposta da API: $response")

                    if (response.getBoolean("success")) {
                        val data = response.getJSONObject("data")

                        // Log dos dados recebidos
                        Log.d("FetchProductDetails", "Dados do produto: $data")

                        // Atualizar os componentes da UI
                        productName?.text = data.getString("produto_nome")
                        productDescription?.text = data.getString("descricao")
                        productPrice?.text = "€ ${data.getString("preco")}"

                        // Construir a URL da imagem
                        val imageUrl = baseUrlImagem + data.getString("imagem")

                        // Carregar imagem (usando Glide)
                        productImage?.let {
                            Glide.with(this)
                                .load(imageUrl)
                                .placeholder(R.drawable.logo2) // Imagem de placeholder
                                .error(R.drawable.logo2) // Imagem de erro
                                .into(it)
                        }
                    } else {
                        // Log caso o campo 'success' seja false
                        val errorMessage = response.getString("message")
                        Log.e("FetchProductDetails", "Erro da API: $errorMessage")
                        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    // Log detalhado para erros de JSON
                    Log.e("FetchProductDetails", "Erro ao processar JSON: ${e.message}", e)
                    Toast.makeText(this, "Erro ao processar dados!", Toast.LENGTH_SHORT).show()
                }
            },
            { error: VolleyError ->
                // Log do erro na requisição
                Log.e("FetchProductDetails", "Erro na requisição: ${error.message}", error)
                Toast.makeText(this, "Erro ao conectar: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json" // Define o tipo de conteúdo como JSON
                return headers
            }
        }

        // Adicionar a requisição à fila
        queue.add(jsonObjectRequest)
    }
}
