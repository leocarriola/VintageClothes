package pm.vintage_clothes.fragments

import MinhasencFragment
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject
import pm.login.R
import pm.vintage_clothes.CartItem
import pm.vintage_clothes.adapters.CartAdapter
import pm.vintage_clothes.utils.CartManager

class CarrinhoFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var cartAdapter: CartAdapter
    private lateinit var cartManager: CartManager
    private lateinit var cartTotalTextView: TextView  // Referência para o TextView do total

    private val baseUrl = "https://esan-tesp-ds-paw.web.ua.pt/tesp-ds-g31/api"  // URL base para a API

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_carrinho, container, false)

        // Inicializar o CartManager e RecyclerView
        cartManager = CartManager(requireContext())
        recyclerView = rootView.findViewById(R.id.cartRecyclerView)

        // Inicializando o TextView do total
        cartTotalTextView = rootView.findViewById(R.id.cartTotal)

        // Configurar o RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)

        val checkoutButton: Button = rootView.findViewById(R.id.checkoutButton)

        checkoutButton.setOnClickListener {
            realizarEncomenda()  // Chama a função de realizar a encomenda
        }

        // Inicializando o adaptador com lista vazia, a função de remoção foi corrigida
        cartAdapter = CartAdapter(listOf()) { cartItem ->
            removeItemFromCart(cartItem)  // Passa a função de remoção
        }
        recyclerView.adapter = cartAdapter

        // Buscar os produtos no carrinho via API
        fetchCartItems()

        return rootView
    }

    private fun realizarEncomenda() {
        // Obter o ID do cliente do SharedPreferences
        val sharedPref = requireContext().getSharedPreferences("pmLogin", Context.MODE_PRIVATE)
        val clientId = sharedPref.getInt("id_user", 0)

        if (clientId == 0) {
            Toast.makeText(requireContext(), "Erro ao obter ID do cliente!", Toast.LENGTH_SHORT).show()
            return
        }

        // Calcular o total do carrinho (já feito na função fetchCartItems, mas reusando a variável total)
        var total = 0.0
        val cartItems = cartAdapter.getCartItems() // Método para pegar os itens do carrinho
        cartItems.forEach { cartItem ->
            total += cartItem.price // Acumula o total
        }

        // Montar a requisição para realizar a encomenda
        val url = "$baseUrl/realizar_encomenda.php" // URL para processar a encomenda

        val requestBody = JSONObject()
        requestBody.put("id_cliente", clientId)
        requestBody.put("total", total)
        requestBody.put("pagamento", "Efetuado")
        requestBody.put("estado", "Em processamento")
        requestBody.put("datavenda", System.currentTimeMillis() / 1000) // Data atual em timestamp

        // Lista de produtos para enviar com a encomenda
        val productsArray = JSONArray()
        cartItems.forEach { cartItem ->
            val productObject = JSONObject()
            productObject.put("id_produto", cartItem.id)
            productObject.put("quantidade", cartItem.quantity)
            productsArray.put(productObject)
        }
        requestBody.put("produtos", productsArray)

        val queue = Volley.newRequestQueue(requireContext())
        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.POST, url, requestBody,
            Response.Listener { response ->
                try {
                    if (response.getBoolean("success")) {
                        Toast.makeText(requireContext(), "Encomenda realizada com sucesso!", Toast.LENGTH_SHORT).show()
                        // Limpar o carrinho após a compra
                        clearcarrinho()

                        // Navegar para MinhasencFragment
                        val fragment = MinhasencFragment()
                        requireActivity().supportFragmentManager.beginTransaction()
                            .replace(R.id.content_frame, fragment) // Substitua 'fragment_container' pelo ID correto do seu container de fragmentos
                            .addToBackStack(null) // Permite voltar para o carrinho, se necessário
                            .commit()
                    } else {
                        Toast.makeText(requireContext(), "Erro ao realizar encomenda.", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Log.e("CarrinhoFragment", "Erro ao processar a resposta: ${e.message}")
                    Toast.makeText(requireContext(), "Erro ao processar dados", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { error ->
                Log.e("CarrinhoFragment", "Erro na requisição: ${error.message}")
                Toast.makeText(requireContext(), "Erro na requisição", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json"
                return headers
            }
        }

        // Envia a requisição
        queue.add(jsonObjectRequest)
    }

    private fun clearcarrinho() {
        // Obter o ID do cliente do SharedPreferences
        val sharedPref = requireContext().getSharedPreferences("pmLogin", Context.MODE_PRIVATE)
        val clientId = sharedPref.getInt("id_user", 0)

        if (clientId == 0) {
            Toast.makeText(requireContext(), "Erro ao obter ID do cliente!", Toast.LENGTH_SHORT).show()
            return
        }

        // Obter os itens do carrinho
        val cartItems = cartAdapter.getCartItems()

        // Montar a requisição para limpar o carrinho
        val url = "$baseUrl/limpar_carrinho.php" // URL para limpar o carrinho

        val requestBody = JSONObject()
        requestBody.put("id_cliente", clientId)

        // Lista de produtos para enviar com a requisição de limpeza do carrinho
        val productsArray = JSONArray()
        cartItems.forEach { cartItem ->
            val productObject = JSONObject()
            productObject.put("id_produto", cartItem.id)
            productsArray.put(productObject)
        }
        requestBody.put("produtos", productsArray)

        // Enviar a requisição para a API
        val queue = Volley.newRequestQueue(requireContext())
        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.POST, url, requestBody,
            Response.Listener { response ->
                try {
                    if (response.getBoolean("success")) {
                        // Carrinho limpo com sucesso
                        Toast.makeText(requireContext(), "Carrinho limpo com sucesso!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Erro ao limpar carrinho.", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Log.e("CarrinhoFragment", "Erro ao processar a resposta: ${e.message}")
                    Toast.makeText(requireContext(), "Erro ao processar dados", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { error ->
                Log.e("CarrinhoFragment", "Erro na requisição: ${error.message}")
                Toast.makeText(requireContext(), "Erro na requisição", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json"
                return headers
            }
        }

        // Envia a requisição
        queue.add(jsonObjectRequest)
    }



    private fun fetchCartItems() {
        val url = "$baseUrl/cart_items.php"  // Substitua com a URL correta para buscar itens no carrinho

        // Obter o ID do cliente do SharedPreferences dentro de um Fragment
        val sharedPref = requireContext().getSharedPreferences("pmLogin", Context.MODE_PRIVATE)
        val clientId = sharedPref.getInt("id_user", 0) // Definido como 0 por padrão

        // Validar o ID do cliente
        if (clientId == 0) { // Verifique se é 0, não -1, já que 0 é o valor padrão
            Toast.makeText(requireContext(), "Erro ao obter ID do cliente!", Toast.LENGTH_SHORT).show()
            return
        }

        val queue = Volley.newRequestQueue(requireContext())

        // Criar o corpo da requisição (JSON)
        val requestBody = JSONObject()
        requestBody.put("id_cliente", clientId)

        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.POST, url, requestBody,
            Response.Listener { response ->
                try {
                    if (response.getBoolean("success")) {
                        // Recebe os itens do carrinho em um array
                        val cartItemsJsonArray: JSONArray = response.getJSONArray("data")
                        val cartItems = mutableListOf<CartItem>()
                        var total = 0.0  // Variável para acumular o total do carrinho

                        for (i in 0 until cartItemsJsonArray.length()) {
                            val item = cartItemsJsonArray.getJSONObject(i)

                            val cartItem = CartItem(
                                item.getInt("id_produto"),    // ID do produto
                                item.getString("nome"),       // Nome do produto
                                item.getDouble("subtotal"),   // Subtotal do item
                                item.getInt("quantidade"),    // Quantidade
                                item.getString("imagem")      // Nome da imagem
                            )
                            cartItems.add(cartItem)

                            // Acumular o subtotal de cada item para o total do carrinho
                            total += cartItem.price
                        }

                        // Atualizar o adaptador com os itens do carrinho
                        cartAdapter = CartAdapter(cartItems) { cartItem ->
                            // Ação de remover item do carrinho
                            removeItemFromCart(cartItem)
                        }
                        recyclerView.adapter = cartAdapter

                        // Atualizar o TextView do total do carrinho
                        updateCartTotal(total)

                    } else {
                        Toast.makeText(requireContext(), "Erro ao carregar carrinho", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Log.e("CarrinhoFragment", "Erro ao processar a resposta: ${e.message}")
                    Toast.makeText(requireContext(), "Erro ao processar dados", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { error ->
                Log.e("CarrinhoFragment", "Erro na requisição: ${error.message}")
                Toast.makeText(requireContext(), "Erro na requisição", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json"
                return headers
            }
        }

        // Adiciona a requisição à fila de requisições
        queue.add(jsonObjectRequest)
    }

    private fun updateCartTotal(total: Double) {
        // Atualizar o TextView com o valor total do carrinho
        cartTotalTextView.text = "Total: € %.2f".format(total)  // Exibe com 2 casas decimais
    }

    private fun removeItemFromCart(cartItem: CartItem) {
        // Função para remover o item do carrinho
        // Aqui você pode implementar a lógica para remover o item do servidor e atualizar o carrinho local
        Toast.makeText(requireContext(), "Item removido: ${cartItem.name}", Toast.LENGTH_SHORT).show()

        // Após remover o item, você pode atualizar a lista
        fetchCartItems()  // Recarregar a lista de itens após remoção
    }
}