
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import pm.login.databinding.FragmentMinhasencBinding

class MinhasencFragment : Fragment() {

    private var _binding: FragmentMinhasencBinding? = null
    private val binding get() = _binding!!

    // URL base da API
    private val url = "https://esan-tesp-ds-paw.web.ua.pt/tesp-ds-g31/api/getenc.php"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMinhasencBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Pega o id_user do SharedPreferences
        val sharedPref = requireContext().getSharedPreferences("pmLogin", Context.MODE_PRIVATE)
        val clientId = sharedPref.getInt("id_user", 0)

        // Chama a função para obter as encomendas
        getMinhasEncomendas(clientId)
    }

    private fun getMinhasEncomendas(idUser: Int) {
        val queue = Volley.newRequestQueue(requireContext())

        val jsonParams = JSONObject()
        jsonParams.put("id_user", idUser)

        Log.d("MinhasEncomendas", "Enviando requisição com o seguinte JSON: $jsonParams")

        val jsonObjectRequest = object : JsonObjectRequest(
            Method.POST, url, jsonParams,
            Response.Listener { response ->
                try {
                    Log.d("MinhasEncomendas", "Resposta da API: $response")

                    val encomendasArray = response.getJSONArray("encomendas")
                    Log.d("MinhasEncomendas", "Número de encomendas recebidas: ${encomendasArray.length()}")

                    val encomendasList = mutableListOf<Encomenda>()

                    for (i in 0 until encomendasArray.length()) {
                        val encomendaObject = encomendasArray.getJSONObject(i)
                        val id = encomendaObject.getString("id_encomenda")
                        val datavenda = encomendaObject.getString("datavenda")
                        val total = encomendaObject.getDouble("total")
                        val pagamento = encomendaObject.getString("pagamento")
                        val estado = encomendaObject.getString("estado")
                        Log.d(
                            "MinhasEncomendas",
                            "Processando encomenda ID: $id, Data: $datavenda, Total: $total, Pagamento: $pagamento, Estado: $estado"
                        )

                        val produtosArray = encomendaObject.getJSONArray("produtos")
                        Log.d("MinhasEncomendas", "Número de produtos na encomenda $id: ${produtosArray.length()}")

                        val produtosList = mutableListOf<ProdutoEncomenda>()

                        for (j in 0 until produtosArray.length()) {
                            val produtoObject = produtosArray.getJSONObject(j)
                            val imagem = produtoObject.getString("imagem")
                            val descricao = produtoObject.getString("nome") // Ajuste para o nome correto do campo
                            val quantidade = produtoObject.getInt("quantidade")
                            val preco = produtoObject.getDouble("preco")

                            Log.d(
                                "MinhasEncomendas",
                                "Produto na encomenda $id -> Imagem: $imagem, Descrição: $descricao, Quantidade: $quantidade, Preço: $preco"
                            )

                            produtosList.add(ProdutoEncomenda(imagem, descricao, quantidade, preco))
                        }

                        encomendasList.add(
                            Encomenda(
                                id = id,
                                datavenda = datavenda,
                                total = total,
                                pagamento = pagamento,
                                estado = estado,
                                produtos = produtosList
                            )
                        )
                    }

                    // Atualiza o RecyclerView
                    val adapter = MinhasEncomendasAdapter(encomendasList)
                    binding.recyclerViewEncomendas.layoutManager = LinearLayoutManager(requireContext())
                    binding.recyclerViewEncomendas.adapter = adapter

                } catch (e: Exception) {
                    Log.e("MinhasEncomendas", "Erro ao processar os dados da resposta", e)
                }
            },
            Response.ErrorListener { error ->
                Log.e("MinhasEncomendas", "Erro na requisição: ${error.message}")
                Log.e("MinhasEncomendas", "Detalhes do erro: ${String(error.networkResponse?.data ?: byteArrayOf())}")
            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json"
                return headers
            }
        }

        queue.add(jsonObjectRequest)
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
