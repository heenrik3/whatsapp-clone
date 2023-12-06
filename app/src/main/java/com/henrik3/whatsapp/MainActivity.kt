package com.henrik3.whatsapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.android.volley.Request
import com.android.volley.Request.Method.POST
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.snackbar.Snackbar
import com.henrik3.whatsapp.databinding.ActivityMainBinding
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var isLogin: Boolean = false

    private val pref by lazy { getSharedPreferences("whatsapp", Context.MODE_PRIVATE) }

    private val queue by lazy { Volley.newRequestQueue(this) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupStore()

        //println(store.token)

        //store.socket.connect()

        if (store.isCurrentlyLogged()) {
            val intent = Intent(this, UsersActivity::class.java)
            startActivity(intent)

        } else {
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)

            supportActionBar?.hide()

            setupInfo()
            setupListeners()
        }
    }

    private fun setupStore() {
        store.token = pref.getString("token", "").toString()
    }

    private fun setupListeners() {
        binding.textViewSwitchMode.setOnClickListener {
            isLogin = !isLogin

            setupInfo()
        }

        binding.buttonAction.setOnClickListener { handleButtonAction() }
    }

    private fun setupInfo() {
        if (isLogin) {
            binding.buttonAction.text = "Entrar"
            binding.textViewSwitchMode.text = "Ou cadastre aqui"
        } else {
            binding.buttonAction.text = "Cadastrar"
            binding.textViewSwitchMode.text = "Ou entre aqui"
        }
    }

    private fun handleButtonAction() {

        val user = binding.editTextUser.text.toString()
        val pass = binding.editTextPass.text.toString()

        if (user.isEmpty() or pass.isEmpty()) {
            Snackbar.make(binding.root, "Dados inválidos! Tente novamente.", Snackbar.LENGTH_LONG)
                .show()
            return
        }

        if (isLogin) handleLogin(user, pass)
        else handleSignup(user, pass)
    }

    private fun handleSignup(user: String, pass: String) {
        val json = JSONObject()
        json.put("user", user)
        json.put("password", pass)

        val url = "${config.API}/auth/signup"

        val request = JsonObjectRequest(
            POST,
            url,
            json,
            ::onSignupSuccess,
            ::onSignupError
        )

        queue.add(request)


    }

    private fun onSignupError(err: VolleyError) {
        Log.e("ERROR", err.toString())
        Snackbar.make(binding.root, err.toString(), Snackbar.LENGTH_LONG).show()
    }

    private fun onSignupSuccess(res: JSONObject) {
        val msg = res.getString("msg")

        Snackbar.make(binding.root, msg, Snackbar.LENGTH_LONG).show()
    }


    private fun handleLogin(user: String, pass: String) {
        val json = JSONObject()
        json.put("user", user)
        json.put("password", pass)

        val url = "${config.API}/auth/login"

        val request = JsonObjectRequest(
            POST,
            url,
            json,
            ::onLoginSuccess,
            ::onLoginError
        )

        queue.add(request)
    }

    private fun onLoginError(err: VolleyError) {
        Snackbar.make(binding.root, err.toString(), Snackbar.LENGTH_LONG).show()
    }

    private fun onLoginSuccess(res: JSONObject) {

        try {
            val token = res.getString("token")
            val msg = res.getString("msg")
            println("ON LOGIN SUCESS")

            Snackbar.make(binding.root, msg, Snackbar.LENGTH_LONG).show()

            if (token.isNotEmpty())  {
                storeToken(token)

                val intent = Intent(this, UsersActivity::class.java)
                startActivity(intent)
            }
        } catch (err: Exception) {
            val msg = res.getString("msg")

            Snackbar.make(binding.root, msg, Snackbar.LENGTH_LONG).show()
        }


    }

    private fun storeToken(token: String) {
            store.token = token

        pref.edit {
            putString("token", token)
        }
    }


}