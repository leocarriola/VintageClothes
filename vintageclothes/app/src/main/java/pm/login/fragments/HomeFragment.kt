package pm.login.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import pm.login.Produto
import pm.login.ProdutoHomeAdapter
import pm.login.R

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProdutoHomeAdapter
    private lateinit var requestQueue: RequestQueue

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Configura o RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewProdutos)
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)


        // Atualiza a mensagem de boas-vindas com o nome do user
        val welcomeTextView = view.findViewById<TextView>(R.id.home_text)
        val sharedPref = requireActivity().getSharedPreferences("pmLogin", Context.MODE_PRIVATE)
        val userName = sharedPref.getString("nome", "User não encontrado") ?: "User não encontrado"
        welcomeTextView.text = "${getString(R.string.text_bemvindo)}: $userName"

        // Inicializa a fila de requisições do Volley
        requestQueue = Volley.newRequestQueue(requireContext())

        // Carregar os produtos da API
        carregarProdutos()

        return view
    }

    private fun carregarProdutos() {
        val url = "https://esan-tesp-ds-paw.web.ua.pt/tesp-ds-g31/api/home.php"

        val emptyJson = JSONObject()

        val request = JsonObjectRequest(
            Request.Method.GET, url, emptyJson,
            { response ->
                try {
                    // Log de resposta para verificar o conteúdo recebido
                    Log.d("CarregarProdutos", "Resposta recebida: $response")

                    val produtos = parseProdutos(response)
                    adapter = ProdutoHomeAdapter(
                        produtos,
                        onItemClick = { produto ->
                            Toast.makeText(
                                requireContext(),
                                "Produto clicado: ${produto.produtoNome}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )

                    recyclerView.adapter = adapter
                } catch (e: JSONException) {
                    // Log de erro caso o JSON não seja processado corretamente
                    Log.e("CarregarProdutos", "Erro ao processar o JSON: ${e.message}", e)
                    Toast.makeText(
                        requireContext(),
                        "Erro ao carregar produtos: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            },
            { error ->
                // Log de erro na requisição
                Log.e("CarregarProdutos", "Erro ao carregar produtos: ${error.message}", error)
                Toast.makeText(
                    requireContext(),
                    "Erro ao carregar produtos: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        )

        requestQueue.add(request)
    }

    private fun parseProdutos(response: JSONObject): List<Produto> {
        val produtos = mutableListOf<Produto>()

        // Extraímos o objeto 'data' que contém as listas
        val data = response.optJSONObject("data") ?: return produtos

        // Acessa os arrays 'mais_vendidos' e 'colecao_outono' diretamente
        val maisVendidos = data.optJSONArray("mais_vendidos") ?: JSONArray()
        val colecaoOutono = data.optJSONArray("colecao_outono") ?: JSONArray()

        // Processar o array 'mais_vendidos'
        for (i in 0 until maisVendidos.length()) {
            val produtoJson = maisVendidos.optJSONObject(i)
            produtoJson?.let {
                val id = it.optString("id_produto", null)?.toIntOrNull()
                val nome = it.optString("produto_nome", null)
                val preco = it.optString("preco", "0.00")
                val imagem = it.optString("imagem", "")

                if (id != null && !nome.isNullOrEmpty()) {
                    val produto = Produto(
                        idProduto = id.toString(),
                        produtoNome = nome,
                        preco = preco,
                        imagem = imagem
                    )
                    produtos.add(produto)
                } else {
                    Log.e(
                        "CarregarProdutos",
                        "Erro: Produto com ID ou nome ausente no array 'mais_vendidos'."
                    )
                }
            }
        }

        // Processar o array 'colecao_outono' de forma similar
        for (i in 0 until colecaoOutono.length()) {
            val produtoJson = colecaoOutono.optJSONObject(i)
            produtoJson?.let {
                val id = it.optString("id_produto", null)?.toIntOrNull()
                val nome = it.optString("produto_nome", null)
                val preco = it.optString("preco", "0.00")
                val imagem = it.optString("imagem", "")

                if (id != null && !nome.isNullOrEmpty()) {
                    val produto = Produto(
                        idProduto = id.toString(),
                        produtoNome = nome,
                        preco = preco,
                        imagem = imagem
                    )
                    produtos.add(produto)
                } else {
                    Log.e(
                        "CarregarProdutos",
                        "Erro: Produto com ID ou nome ausente no array 'colecao_outono'."
                    )
                }
            }
        }

        return produtos
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Cancela todas as requisições pendentes quando o fragmento é destruído
        requestQueue.cancelAll { true }
    }
}
