package pm.login

import android.os.Bundle
import android.util.Log // Import necessário para usar Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import pm.login.databinding.ActivityBaseBinding
import pm.login.fragments.CarrinhoFragment
import pm.login.fragments.HomeFragment
import pm.login.fragments.LojaFragment

import pm.login.fragments.UserFragment

open class BaseActivity : AppCompatActivity() {



    private val binding by lazy {
        ActivityBaseBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val bottomNav: BottomNavigationView = findViewById(R.id.bottom_navigation)

        // Configuração do BottomNavigationView
        bottomNav.setOnItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.menu_home -> {
                    Log.d("BaseActivity", "Menu Home clicado")
                    loadFragment(HomeFragment())
                    true
                }
                R.id.menu_categorias -> {
                    Log.d("BaseActivity", "Menu Categorias clicado")
                    loadFragment(LojaFragment())
                    true
                }
                R.id.menu_carrinho -> {
                    Log.d("BaseActivity", "Menu Carrinho clicado")
                    loadFragment(CarrinhoFragment())
                    true
                }
                R.id.menu_profile -> {
                    Log.d("BaseActivity", "Menu Profile clicado")
                    loadFragment(UserFragment())
                    true
                }
                else -> {
                    Log.d("BaseActivity", "Menu não reconhecido: ${item.itemId}")
                    false
                }
            }
        }

        // Carregar o fragmento inicial se for o primeiro carregamento
        if (savedInstanceState == null) {
            Log.d("BaseActivity", "Carregando fragmento inicial: Home")
            loadFragment(HomeFragment()) // Adiciona o fragmento inicial
            bottomNav.selectedItemId = R.id.menu_home // Define o item como selecionado
        }
    }

    // Função para carregar fragmentos
    private fun loadFragment(fragment: Fragment) {
        Log.d("BaseActivity", "Carregando fragmento: ${fragment.javaClass.simpleName}")
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.content_frame, fragment)
            .commit()
    }
}
