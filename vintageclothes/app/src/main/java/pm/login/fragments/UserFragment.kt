package pm.login.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import pm.login.MainActivity
import pm.login.R

class UserFragment : Fragment() {

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

        // Atualiza o TextView com o nome do usuário
        val nameTextView = view.findViewById<TextView>(R.id.textNome)
        nameTextView.text = "Nome: $userName"  // Atualiza o texto com o nome do usuário

        val telTextView = view.findViewById<TextView>(R.id.texttelefone)
        telTextView.text = "Telefone: $telefone"  // Atualiza o texto com o telefone

        val moradaTextView = view.findViewById<TextView>(R.id.textMorada)
        moradaTextView.text = "Morada: $morada"  // Atualiza o texto com a morada

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
            remove("morada")
            apply()
        }

        // Redireciona para a tela de login (MainActivity)
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }
}
