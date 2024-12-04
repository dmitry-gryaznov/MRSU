package com.example.mrsu.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.mrsu.R
import com.example.mrsu.databinding.FragmentHomeBinding
import com.example.mrsu.objects.RequestObj.getUser
import com.example.mrsu.objects.RequestObj.getUserInfoRequest
import com.example.mrsu.activities.AuthActivity
import com.example.mrsu.dataclasses.User

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val context = requireContext()
        val user = getUser(context)

        // Загрузка информации о пользователе
        getUserInfoRequest(
            context = context,
            onSuccess = {
                val user = getUser(context)
                updateUserInfo(user)
            },
            onFailure = {
            }
        )
        Glide.with(context).load(user?.photo?.urlSmall).into(binding.userPhoto)
        binding.userId.text = "ID: ${user?.id.toString().take(8)}"
        binding.userName.text = "ФИО: ${user?.userName}"
        binding.userBirthDate.text = "Дата рождения: ${user?.birthDate.toString().take(10)}"

        // Логика для кнопки выхода
        binding.logoutButton.setOnClickListener {
            logout(context)
            val intent = Intent(context, AuthActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }
    }

    private fun logout(context: Context) {
        val sharedPref = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            remove("access_token")
            remove("refresh_token")
            remove("token_expires_at")
            remove("user")
            apply()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateUserInfo(user: User?) {
        binding.userId.text = "ID: ${user?.id.toString().take(8)}"
        binding.userName.text = "ФИО пользователя: ${user?.userName}"
        binding.userBirthDate.text = "Дата рождения: ${user?.birthDate.toString().take(10)}"
    }

}
