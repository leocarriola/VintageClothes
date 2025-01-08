package pm.vintage_clothes.utils

import android.content.Context
import android.content.SharedPreferences
import pm.vintage_clothes.CartItem

class CartManager(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("cart", Context.MODE_PRIVATE)

    // Salva os itens no carrinho
    fun addItemToCart(cartItem: CartItem) {
        val cartItems = getCartItems().toMutableList()
        val existingItemIndex = cartItems.indexOfFirst { it.id == cartItem.id }

        if (existingItemIndex != -1) {
            // Atualiza a quantidade se o produto já estiver no carrinho
            val existingItem = cartItems[existingItemIndex]
            cartItems[existingItemIndex] = existingItem.copy(quantity = existingItem.quantity + cartItem.quantity)
        } else {
            // Adiciona um novo item ao carrinho
            cartItems.add(cartItem)
        }

        saveCartItems(cartItems)
    }

    // Recupera os itens do carrinho
    fun getCartItems(): List<CartItem> {
        val cartItemsJson = sharedPreferences.getString("cart_items", null) ?: return emptyList()
        return cartItemsJson.split(";").mapNotNull {
            val parts = it.split(":")
            if (parts.size == 4) {
                try {
                    CartItem(
                        id = parts[0].toInt(),
                        name = parts[1],
                        price = parts[2].toDouble(),
                        quantity = parts[3].toInt(),
                        imageUrl = ""
                    )
                } catch (e: Exception) {
                    null
                }
            } else {
                null
            }
        }
    }

    // Limpa o carrinho
    fun clearCart() {
        sharedPreferences.edit().remove("cart_items").apply()
    }

    // Salva os itens no SharedPreferences
    private fun saveCartItems(cartItems: List<CartItem>) {
        val cartItemsString = cartItems.joinToString(";") {
            "${it.id}:${it.name}:${it.price}:${it.quantity}"
        }
        sharedPreferences.edit().putString("cart_items", cartItemsString).apply()
    }
}
