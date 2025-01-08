package pm.vintage_clothes.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import pm.vintage_clothes.ProdutoAdapter

class LojaFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProdutoAdapter
    private lateinit var requestQueue: RequestQueue

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_loja, container, false)

        // Configura o RecyclerView para produtos
        recyclerView = view.findViewById(R.id.recyclerViewProdutos)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Inicializa a fila de requisições do Volley
        requestQueue = Volley.newRequestQueue(requireContext())

        // Carregar os produtos
        carregarProdutos()

        return view
    }

    private fun carregarProdutos() {
        val url = "https://esan-tesp-ds-paw.web.ua.pt/tesp-ds-g31/api/loja.php"

        val emptyJson = JSONObject()

        val request = JsonObjectRequest(
            Request.Method.GET, url, emptyJson,
            { response ->
                try {
                    val produtos = parseProdutos(response)
                    adapter = ProdutoAdapter(produtos) { produto ->
                        // Tratamento de clique no produto
                        abrirDetalhesProduto(produto)
                    }
                    recyclerView.adapter = adapter
                } catch (e: JSONException) {
                    Toast.makeText(requireContext(), "Erro ao carregar produtos: ${e.message}", Toast.LENGTH_LONG).show()
                }
            },
            { error ->
                Toast.makeText(requireContext(), "Erro ao carregar produtos: ${error.message}", Toast.LENGTH_LONG).show()
            }
        )

        requestQueue.add(request)
    }

    private fun parseProdutos(response: JSONObject): List<Produto> {
        val listaProdutos = mutableListOf<Produto>()
        val dataArray: JSONArray = response.getJSONArray("data")

        for (i in 0 until dataArray.length()) {
            val produtoObj = dataArray.getJSONObject(i)
            listaProdutos.add(
                Produto(
                    idProduto = produtoObj.getString("id_produto"),
                    produtoNome = produtoObj.getString("produto_nome"),
                    preco = produtoObj.getString("preco"),
                    imagem = produtoObj.optString("imagem", null)
                )
            )
        }
        return listaProdutos
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
