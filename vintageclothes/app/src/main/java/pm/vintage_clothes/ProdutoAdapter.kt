package pm.vintage_clothes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import pm.login.R

// Data class que representa um produto
data class Produto(
    val idProduto: String,
    val produtoNome: String,
    val preco: String,
    val imagem: String?, // URL da imagem do produto
)

class ProdutoAdapter(
    private val produtos: List<Produto>,
    private val onItemClick: (Produto) -> Unit // Callback para tratar cliques
) : RecyclerView.Adapter<ProdutoAdapter.ProdutoViewHolder>() {

    // Defina a URL base para as imagens
    private val baseUrlImagem = "https://esan-tesp-ds-paw.web.ua.pt/tesp-ds-g31/uploads/imagem/"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProdutoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_produto, parent, false)
        return ProdutoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProdutoViewHolder, position: Int) {
        val produto = produtos[position]

        // Defina o nome do produto e preço
        holder.produtoNome.text = produto.produtoNome
        holder.produtoPreco.text = "${produto.preco} €" // Supondo que o preço seja em euros

        // Construir a URL da imagem ou usar uma imagem padrão
        val imagemUrl = produto.imagem?.let { baseUrlImagem + it } ?: R.drawable.vc.toString()

        // Carregar a imagem usando Glide
        Glide.with(holder.itemView.context)
            .load(imagemUrl) // Carregar a URL completa ou a imagem padrão
            .placeholder(R.drawable.logo2) // Imagem de carregamento padrão
            .error(R.drawable.homeicon) // Imagem de erro
            .into(holder.produtoImagem)

        // Configurar clique no item
        holder.itemView.setOnClickListener {
            onItemClick(produto) // Chamar o callback com o produto clicado
        }
    }


    override fun getItemCount(): Int = produtos.size

    // ViewHolder que mantém as referências para os itens do layout
    class ProdutoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val produtoNome: TextView = itemView.findViewById(R.id.nomeProduto)
        val produtoPreco: TextView = itemView.findViewById(R.id.precoProduto)
        val produtoImagem: ImageView = itemView.findViewById(R.id.produtoImage)
    }
}
