package pm.login

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ProdutoHomeAdapter(
    private val produtos: List<Produto>,
    private val onItemClick: (Produto) -> Unit // Callback para tratar cliques
) : RecyclerView.Adapter<ProdutoHomeAdapter.ProdutoViewHolder>() {

    // URL base para as imagens
    private val baseUrlImagem = "https://esan-tesp-ds-paw.web.ua.pt/tesp-ds-g31/uploads/imagem/"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProdutoViewHolder {
        // Infla o layout do item para o RecyclerView
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_produto_home, parent, false)
        return ProdutoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProdutoViewHolder, position: Int) {
        val produto = produtos[position]

        // Verifica o comprimento do nome do produto e aplica o truncamento se necessário
        val nomeProduto = if (produto.produtoNome.length > 6) {
            produto.produtoNome.substring(0, 15) + "..." // Trunca para 6 caracteres e adiciona "..."
        } else {
            produto.produtoNome // Deixa o nome original se tiver 6 ou menos caracteres
        }

        // Define o nome do produto truncado ou original
        holder.produtoNome.text = nomeProduto

        // Define o preço do produto
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

    // ViewHolder que mantém as referências aos itens do layout
    class ProdutoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val produtoNome: TextView = itemView.findViewById(R.id.productName)
        val produtoPreco: TextView = itemView.findViewById(R.id.productPrice)
        val produtoImagem: ImageView = itemView.findViewById(R.id.imageView)
    }
}
