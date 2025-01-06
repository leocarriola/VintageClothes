package pm.login.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import pm.login.MainActivity
import pm.login.R
import java.util.Locale

class UserFragment() : Fragment(), Parcelable {

    constructor(parcel: Parcel) : this() {
    }

    @SuppressLint("MissingInflatedId")
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

        // Recupera as informações do usuário a partir das SharedPreferences
        val sharedPref = requireActivity().getSharedPreferences("pmLogin", Context.MODE_PRIVATE)
        val userName = sharedPref.getString("nome", "Usuário não encontrado") ?: "Usuário não encontrado"
        val telefone = sharedPref.getInt("telefone", 123)
        val morada = sharedPref.getString("morada", "Morada não encontrada") ?: "Morada não encontrada"
        val email = sharedPref.getString("email", "Email não encontrado") ?: "Email não encontrado"

        // Atualiza o TextView com o nome do usuário
        val nameTextView = view.findViewById<TextView>(R.id.textNome)
        nameTextView.text = "$userName"

        val telTextView = view.findViewById<TextView>(R.id.textTelefone)
        telTextView.text = "Telefone: $telefone" // Atualiza o texto com o telefone

        val emailTextView = view.findViewById<TextView>(R.id.textEmail)
        emailTextView.text = "Email: $email "

        val moradaTextView = view.findViewById<TextView>(R.id.textMorada)
        moradaTextView.text = "Morada: $morada"  // Atualiza o texto com a morada

        // Botão para alterar os dados do usuário
        val editProfileButton = view.findViewById<Button>(R.id.buttonEditProfile)
        editProfileButton.setOnClickListener {
            // Substituir o fragmento atual pelo EditProfileFragment
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, UserFragment()) // Certifique-se de que R.id.fragment_container seja o ID correto do contêiner do fragmento
            transaction.addToBackStack(null) // Adiciona a transação à pilha de retorno
            transaction.commit()
        }

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
        requireContext().createConfigurationContext(config)

        // Salvar a preferência do idioma para persistência
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

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserFragment> {
        override fun createFromParcel(parcel: Parcel): UserFragment {
            return UserFragment(parcel)
        }

        override fun newArray(size: Int): Array<UserFragment?> {
            return arrayOfNulls(size)
        }
    }
}
