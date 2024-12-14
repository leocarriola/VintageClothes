package pm.login

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

data class Produto(
    val idProduto: String,
    val produtoNome: String,
    val preco: String,
    val imagem: String?
)

class ProdutoAdapter(private val produtos: List<Produto>) :
    RecyclerView.Adapter<ProdutoAdapter.ProdutoViewHolder>() {

    // Defina a URL base para as imagens
    private val baseUrlImagem = "https://esan-tesp-ds-paw.web.ua.pt/tesp-ds-g31/uploads/imagem/"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProdutoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_produto, parent, false)
        return ProdutoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProdutoViewHolder, position: Int) {
        val produto = produtos[position]

        // Defina o nome do produto e preço
        holder.produtoNome.text = produto.produtoNome
        holder.produtoPreco.text = "${produto.preco} €"

        // Se a imagem não for nula, use a URL completa
        val imagemUrl = produto.imagem?.let { baseUrlImagem + it } ?: R.drawable.vc.toString()

        // Carregar a imagem com Glide
        Glide.with(holder.itemView.context)
            .load(imagemUrl) // Carregar a URL completa ou a imagem padrão
            .into(holder.produtoImagem)
    }

    override fun getItemCount(): Int = produtos.size

    class ProdutoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val produtoNome: TextView = itemView.findViewById(R.id.produtoNome)
        val produtoPreco: TextView = itemView.findViewById(R.id.produtoPreco)
        val produtoImagem: ImageView = itemView.findViewById(R.id.produtoImagem)
    }
}
