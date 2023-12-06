package com.henrik3.whatsapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import com.android.volley.Request.Method.GET
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.henrik3.whatsapp.databinding.ActivityUsersBinding
import org.json.JSONArray
import org.json.JSONObject

class UsersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUsersBinding

    private var users = mutableListOf<String>()
    private lateinit var adapter: ArrayAdapter<String>

    private val queue by lazy {
        Volley.newRequestQueue(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*
        store.socket.on("users", { data: Array<String>? ->
            for (i in 0 .. data!!.size) {
                val item = data[i]

                users[i] = item
            }

            adapter.notifyDataSetChanged()

        } as ((Array<Any>) -> Unit)?) */



        binding = ActivityUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, users)

        binding.listViewUsers.adapter = adapter

        binding.listViewUsers.setOnItemClickListener { parent, view, position, id ->
            val intent = Intent(this, ChatActivity::class.java)

            store.currentChat = users[position]

            startActivity(intent)
        }

        fetchUsers()
    }

    private fun fetchUsers() {
        val url = "${config.API}/users"

       val request = object : JsonArrayRequest(
           GET,
           url,
           null,
           ::onUsersSuccess,
           ::onUsersError
       ){
           override fun getHeaders(): MutableMap<String, String> {
               val headers = HashMap<String, String>()
               headers["Authorization"] = "Bearer ${store.token}"
               return headers
           }
       }

        queue.add(request)
    }

    private fun onUsersError(err: VolleyError) {

    }

    private fun onUsersSuccess(res: JSONArray) {
        users.clear()

        for (i in 0..<res.length()) {
            val user = res[i].toString()

            users.add(user)
        }

       adapter.notifyDataSetChanged()
    }
}