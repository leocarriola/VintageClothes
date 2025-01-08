package pm.login.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import pm.login.CartItem
import pm.login.R

class CartAdapter(
    private val cartItems: List<CartItem>, // Lista de itens do carrinho
    private val onRemoveClick: (CartItem) -> Unit // Callback para remover itens
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    private val baseUrlImagem = "https://esan-tesp-ds-paw.web.ua.pt/tesp-ds-g31/uploads/imagem/" // URL base para a imagem

    // ViewHolder que contém a referência aos elementos do layout
    class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemImage: ImageView = view.findViewById(R.id.item_image)  // Adiciona a referência da imagem
        val itemName: TextView = view.findViewById(R.id.item_name)
        val itemPrice: TextView = view.findViewById(R.id.item_price)
        val itemQuantity: TextView = view.findViewById(R.id.item_quantity)
        val removeButton: ImageView = view.findViewById(R.id.remove_button)
    }

    // Função que retorna a lista de itens do carrinho
    fun getCartItems(): List<CartItem> {
        return cartItems
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cart_item_layout, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartItem = cartItems[position]

        // Preencher os dados no ViewHolder
        holder.itemName.text = cartItem.name
        holder.itemPrice.text = "€ ${cartItem.price}"
        holder.itemQuantity.text = "Qtd: ${cartItem.quantity}"

        // Carregar a imagem do produto usando Glide
        val imageUrl = "$baseUrlImagem${cartItem.imageUrl}" // Concatenar a URL base com o nome da imagem do produto
        Glide.with(holder.itemView.context)
            .load(imageUrl)  // Carregar a imagem a partir da URL
            .placeholder(R.drawable.logo2) // Imagem de placeholder enquanto carrega
            .error(R.drawable.logo2) // Imagem de erro caso falhe o carregamento
            .into(holder.itemImage) // Atribuir à ImageView

        // Configurar ação do botão de remoção
        holder.removeButton.setOnClickListener {
            onRemoveClick(cartItem)
        }
    }

    override fun getItemCount(): Int {
        return cartItems.size
    }
}
