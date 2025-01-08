package pm.vintage_clothes.fragments

import android.content.Context
import android.content.Intent
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
import pm.login.R
import pm.vintage_clothes.ProductDetailsActivity
import pm.vintage_clothes.Produto
import pm.vintage_clothes.ProdutoHomeAdapter

class HomeFragment : Fragment() {

    private lateinit var recyclerViewMaisVendidos: RecyclerView
    private lateinit var recyclerViewColecaoOutono: RecyclerView
    private lateinit var adapterMaisVendidos: ProdutoHomeAdapter
    private lateinit var adapterColecaoOutono: ProdutoHomeAdapter
    private lateinit var requestQueue: RequestQueue

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Configura os RecyclerViews
        recyclerViewMaisVendidos = view.findViewById(R.id.recyclerViewMaisVendidos)
        recyclerViewMaisVendidos.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        recyclerViewColecaoOutono = view.findViewById(R.id.recyclerViewColecaoOutono)
        recyclerViewColecaoOutono.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

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

                    // Para "Mais Vendidos", você pega os 6 primeiros produtos, sem remover duplicatas
                    val maisVendidos = produtos.take(6) // Pegando os 6 produtos conforme a resposta
                    Log.d("CarregarProdutos", "Mais Vendidos: ${maisVendidos.size} produtos")

                    // Para "Coleção Outono", filtra os produtos corretamente
                    val colecaoOutono = produtos.filter { it.produtoNome.contains("Sweatshirt", true) || it.produtoNome.contains("Casaco", true) }

                    // Sem aplicar distinctBy se não houver duplicatas
                    val colecaoOutonoSemDuplicatas = colecaoOutono.take(6) // Pegando 6 produtos sem duplicatas

                    Log.d("CarregarProdutos", "Coleção Outono: ${colecaoOutonoSemDuplicatas.size} produtos")
                    Log.d("CarregarProdutos", "Produtos da Coleção Outono (sem duplicatas): $colecaoOutonoSemDuplicatas")

                    // Adicionando os adaptadores nos RecyclerViews
                    if (maisVendidos.isNotEmpty()) {
                        adapterMaisVendidos = ProdutoHomeAdapter(
                            maisVendidos,
                            onItemClick = { produto ->
                                // Chama a função para abrir os detalhes do produto
                                abrirDetalhesProduto(produto)
                            }
                        )
                        recyclerViewMaisVendidos.adapter = adapterMaisVendidos
                    } else {
                        Log.d("CarregarProdutos", "Nenhum produto em 'Mais Vendidos'")
                    }

                    if (colecaoOutonoSemDuplicatas.isNotEmpty()) {
                        adapterColecaoOutono = ProdutoHomeAdapter(
                            colecaoOutonoSemDuplicatas,
                            onItemClick = { produto ->
                                // Chama a função para abrir os detalhes do produto
                                abrirDetalhesProduto(produto)
                            }
                        )
                        recyclerViewColecaoOutono.adapter = adapterColecaoOutono
                    } else {
                        Log.d("CarregarProdutos", "Nenhum produto em 'Coleção Outono'")
                    }

                } catch (e: JSONException) {
                    Log.e("CarregarProdutos", "Erro ao processar o JSON: ${e.message}", e)
                    Toast.makeText(
                        requireContext(),
                        "Erro ao carregar produtos: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            },
            { error ->
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
        val data = response.optJSONObject("data") ?: return produtos
        val maisVendidos = data.optJSONArray("mais_vendidos") ?: JSONArray()
        val colecaoOutono = data.optJSONArray("colecao_outono") ?: JSONArray()

        // Adicionando produtos ao array
        produtos.addAll(parseCategoria(maisVendidos))
        produtos.addAll(parseCategoria(colecaoOutono))

        return produtos
    }

    private fun parseCategoria(array: JSONArray): List<Produto> {
        val produtos = mutableListOf<Produto>()
        Log.d("CarregarProdutos", "Parsing categoria com ${array.length()} itens.") // Log para verificar o tamanho do array
        for (i in 0 until array.length()) {
            val produtoJson = array.optJSONObject(i)
            produtoJson?.let {
                val id = it.optString("id_produto", null)?.toIntOrNull()
                val nome = it.optString("produto_nome", null)
                val preco = it.optString("preco", "0.00")
                val imagem = it.optString("imagem", "")

                Log.d("CarregarProdutos", "Produto processado: $nome") // Log para verificar cada produto processado

                if (id != null && !nome.isNullOrEmpty()) {
                    val produto = Produto(
                        idProduto = id.toString(),
                        produtoNome = nome,
                        preco = preco,
                        imagem = imagem
                    )
                    produtos.add(produto)
                }
            }
        }
        Log.d("CarregarProdutos", "Número de produtos processados: ${produtos.size}") // Log para contar os produtos processados
        return produtos
    }

    private fun abrirDetalhesProduto(produto: Produto) {
        // Log do ID do produto que está sendo passado
        Log.d("AbrirDetalhesProduto", "ID do produto: ${produto.idProduto}")

        // Navegar para a tela de detalhes do produto
        val intent = Intent(requireContext(), ProductDetailsActivity::class.java)
        intent.putExtra("id_produto", produto.idProduto.toInt())
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Cancela todas as requisições pendentes quando o fragmento é destruído
        requestQueue.cancelAll { true }
    }
}
