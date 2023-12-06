package com.henrik3.whatsapp

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request.Method.GET
import com.android.volley.Request.Method.POST
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.snackbar.Snackbar
import com.henrik3.whatsapp.databinding.ActivityChatBinding
import org.json.JSONArray
import org.json.JSONObject

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding

    private val messages = mutableListOf<String>()
    private lateinit var adapter: ArrayAdapter<String>

    private val queue by lazy { Volley.newRequestQueue(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = "Conversa com ${store.currentChat}"

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, messages)

        binding.listViewChat.adapter = adapter

        setupListeners()

        fetchMessages()
    }

    private fun fetchMessages() {
        val url = "${config.API}/chat/${store.currentChat}"

        val request = object : JsonObjectRequest(
            GET,
            url,
            null,
            ::onMessagesSuccess,
            ::onMessagesError
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()

                headers["Authorization"] = "Bearer ${store.token}"

                return headers
            }
        }

        queue.add(request)
    }

    private fun onMessagesSuccess(res: JSONObject) {
        messages.clear()

        val msgs = res.getJSONArray("msgs")

        for(i in 0..<msgs.length()){
            val jsonObj = JSONObject(msgs[i].toString())

            val sender = jsonObj.getString("sender")
            val destiny = jsonObj.getString("destiny")
            var content = jsonObj.getString("content")

            if (sender.equals(store.currentChat)) content = "> $content"

            messages.add(content)
        }

        adapter.notifyDataSetChanged()
    }

    private fun onMessagesError(err: VolleyError) {

    }

    private fun setupListeners() {
        binding.buttonSendMessage.setOnClickListener { sendMessage() }
    }

    private fun sendMessage() {
        val content = binding.editTextChat.text.toString()

        if (content.isEmpty()) return

        val json = JSONObject()
        json.put("to", store.currentChat)
        json.put("content", content)

        binding.editTextChat.setText("")

        val url = "${config.API}/chat"

        val request = object : JsonObjectRequest(
            POST,
            url,
            json,
            ::onSendMessageSuccess,
            ::onSendMessageError
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()

                headers["Authorization"] = "Bearer ${store.token}"

                return headers
            }
        }

        queue.add(request)

        binding.editTextChat.setText("")

    }

    private fun onSendMessageError(err: VolleyError) {

    }

    private fun onSendMessageSuccess(res: JSONObject) {
        Snackbar.make(binding.root, res.getString("msg"), Snackbar.LENGTH_LONG).show()

        fetchMessages()
    }
}