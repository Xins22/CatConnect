package com.example.catconnect.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.catconnect.R
import com.example.catconnect.data.repo.FakeRepository
import com.example.catconnect.data.session.SessionManager
import com.example.catconnect.databinding.FragmentLoginBinding
import com.google.android.material.snackbar.Snackbar

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var session: SessionManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        session = SessionManager(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // jika sudah login, langsung ke feed
        if (session.isLoggedIn()) {
            findNavController().navigate(R.id.feedFragment)
            return
        }

        binding.btnLogin.setOnClickListener {
            val name = binding.etName.text?.toString()?.trim().orEmpty()
            val email = binding.etEmail.text?.toString()?.trim().orEmpty()

            var ok = true
            binding.tilName.error = if (name.isBlank()) { ok=false; "Nama wajib" } else null
            binding.tilEmail.error = if (!email.contains("@")) { ok=false; "Email tidak valid" } else null
            if (!ok) return@setOnClickListener

            // Simpan session
            session.login(name, email)

            // (opsional) sinkronkan user “aktif” di FakeRepository agar Profile pakai nama kamu
            FakeRepository.apply {
                currentUser.copy(name = name).also {
                    // kalau ingin mengganti secara global:
                    // currentUser = it  <-- ubah currentUser jadi var di FakeRepository jika mau bisa diubah
                }
            }

            Snackbar.make(view, "Login sukses", Snackbar.LENGTH_SHORT).show()
            findNavController().navigate(R.id.feedFragment)
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
