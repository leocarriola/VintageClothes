import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import pm.login.Editar_Perfil
import pm.login.MainActivity
import pm.login.R
import java.util.Locale

class UserFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Infla o layout para este fragmento
        val view = inflater.inflate(R.layout.fragment_perfil, container, false)

        // Configura o botão de logout
        val logoutButton = view.findViewById<Button>(R.id.buttonLogout)
        logoutButton.setOnClickListener {
            performLogout()
        }

        // Configura o botão de editar
        val buttonEditProfileButton = view.findViewById<Button>(R.id.buttonEditProfile)
        buttonEditProfileButton.setOnClickListener {
            val intent = Intent(requireContext(), Editar_Perfil::class.java)
            startActivity(intent)
        }

        // Recupera as informações do usuário a partir das SharedPreferences
        val sharedPref = requireActivity().getSharedPreferences("pmLogin", Context.MODE_PRIVATE)
        val userName = sharedPref.getString("nome", "User não encontrado") ?: "Usuário não encontrado"
        val telefone = sharedPref.getInt("telefone", 123)
        val morada = sharedPref.getString("morada", "Morada não encontrada") ?: "Morada não encontrada"
        val email = sharedPref.getString("email", "Email não encontrado") ?: "Email não encontrado"

        // Atualiza os TextViews com as informações
        val nameTextView = view.findViewById<TextView>(R.id.textNome)
        nameTextView.text = userName

        val telTextView = view.findViewById<TextView>(R.id.textTelefone)
        telTextView.text = "${getString(R.string.text_telefone)}: $telefone"

        val emailTextView = view.findViewById<TextView>(R.id.textEmail)
        emailTextView.text = "Email: $email"

        val moradaTextView = view.findViewById<TextView>(R.id.textMorada)
        moradaTextView.text = "${getString(R.string.text_morada)}: $morada"


        // Botão para alternar o idioma
        val languageButton = view.findViewById<Button>(R.id.buttonChangeLanguage)
        languageButton.setOnClickListener {
            // Trocar para inglês (en) ou português (pt)
            val currentLang = Locale.getDefault().language
            if (currentLang == "pt") {
                setLanguage("en") // Troca para Inglês
            } else {
                setLanguage("pt") // Troca para Português
            }
        }

        return view
    }

    private fun performLogout() {
        // Limpa os dados do usuário armazenados em SharedPreferences
        val sharedPref = requireActivity().getSharedPreferences("pmLogin", Context.MODE_PRIVATE)
        sharedPref.edit().apply {
            putBoolean("login", false)
            remove("nome")
            remove("id_user")
            remove("telefone")
            remove("email")
            remove("morada")
            apply()
        }

        // Redireciona para a tela de login (MainActivity)
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    private fun setLanguage(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)

        // Salvar a preferência de idioma para persistência
        saveLanguagePreference(languageCode)

        // Atualizar a interface sem redirecionar para outra tela
        requireActivity().recreate()  // Recria a atividade para aplicar o novo idioma
    }

    private fun saveLanguagePreference(languageCode: String) {
        val sharedPref = requireActivity().getSharedPreferences("pmLogin", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("language", languageCode)
            apply()
        }
    }

    private fun loadLanguagePreference() {
        val sharedPref = requireActivity().getSharedPreferences("pmLogin", Context.MODE_PRIVATE)
        val languageCode = sharedPref.getString("language", "pt") // Default é pt
        setLanguage(languageCode ?: "pt")
    }
}
