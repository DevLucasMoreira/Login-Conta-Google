package com.example.logingoogleaccount

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.logingoogleaccount.databinding.ActivityPrincipalBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class PrincipalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPrincipalBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPrincipalBinding.inflate(layoutInflater)

        val view = binding.root

        setContentView(view)

        auth = Firebase.auth

        binding.buttonSair.setOnClickListener{
            auth.signOut()

            val intent = Intent(this,
                MainActivity::class.java)

            startActivity(intent)

            finish()

        }
    }
}