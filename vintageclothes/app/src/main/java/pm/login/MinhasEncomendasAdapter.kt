import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import pm.login.R

data class ProdutoEncomenda(
    val imagem: String,
    val descricao: String,
    val quantidade: Int,
    val preco: Double
)

data class Encomenda(
    val id: String,
    val datavenda: String,
    val total: Double,
    val pagamento: String,
    val estado: String,
    val produtos: List<ProdutoEncomenda>
)

class MinhasEncomendasAdapter(private val encomendas: List<Encomenda>) :
    RecyclerView.Adapter<MinhasEncomendasAdapter.EncomendaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EncomendaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_encomenda, parent, false)
        return EncomendaViewHolder(view)
    }

    override fun onBindViewHolder(holder: EncomendaViewHolder, position: Int) {
        val encomenda = encomendas[position]
        holder.bind(encomenda)
    }

    override fun getItemCount(): Int = encomendas.size

    class EncomendaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewId: TextView = itemView.findViewById(R.id.textViewIdEncomenda)
        private val textViewStatus: TextView = itemView.findViewById(R.id.textViewStatus)
        private val textViewtotal: TextView = itemView.findViewById(R.id.textViewprecototal)
        private val recyclerViewProdutos: RecyclerView = itemView.findViewById(R.id.recyclerViewProdutos)

        fun bind(encomenda: Encomenda) {
            textViewId.text = "ID: ${encomenda.id}"
            textViewStatus.text = "Status: ${encomenda.estado}"
            textViewtotal.text = "Total: ${encomenda.total}€"

            // Configura o RecyclerView para os produtos da encomenda
            val produtoAdapter = ProdutoEncomendaAdapter(encomenda.produtos)
            recyclerViewProdutos.layoutManager = LinearLayoutManager(itemView.context)
            recyclerViewProdutos.adapter = produtoAdapter
        }
    }

    // Adapter para os produtos da encomenda
    class ProdutoEncomendaAdapter(private val produtos: List<ProdutoEncomenda>) :
        RecyclerView.Adapter<ProdutoEncomendaAdapter.ProdutoViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProdutoViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_produto_encomenda, parent, false)
            return ProdutoViewHolder(view)
        }

        override fun onBindViewHolder(holder: ProdutoViewHolder, position: Int) {
            val produto = produtos[position]
            holder.bind(produto)
        }

        override fun getItemCount(): Int = produtos.size

        class ProdutoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val baseUrlImagem = "https://esan-tesp-ds-paw.web.ua.pt/tesp-ds-g31/uploads/imagem/"
            private val imageViewProduto: ImageView = itemView.findViewById(R.id.imageProduto)
            private val textViewDescricao: TextView = itemView.findViewById(R.id.textDescricaoProduto)
            private val textViewQuantidade: TextView = itemView.findViewById(R.id.textQuantidadeProduto)
            private val textViewPreco: TextView = itemView.findViewById(R.id.textPrecoProduto)

            fun bind(produto: ProdutoEncomenda) {
                textViewDescricao.text = produto.descricao
                textViewQuantidade.text = "Quantidade: ${produto.quantidade}"
                textViewPreco.text = "Preço: €${produto.preco}"

                // Carregar imagem usando Glide
                val imageUrl = "${baseUrlImagem}${produto.imagem}"
                Glide.with(itemView.context)
                    .load(imageUrl)
                    .apply(RequestOptions().placeholder(R.drawable.logo2))
                    .into(imageViewProduto)
            }
        }
    }
}
