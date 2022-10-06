package com.example.logingoogleaccount

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.example.logingoogleaccount.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var googleSignInCliente: GoogleSignInClient
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        val view = binding.root

        setContentView(view)

        // Initialize Firebase Auth
        auth = Firebase.auth

        //criar um objeto
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                //IdToken buscar no Google Cloud Console
            .requestIdToken("1065645026212-43ubk9hq6gcfv88go6rl6grdab3b9rdr.apps.googleusercontent.com")
            .requestEmail()
            .build()

        googleSignInCliente = GoogleSignIn.getClient(this, googleSignInOptions)


        binding.buttonEntrar.setOnClickListener {

            if (binding.editTextUsuario.text.isNullOrEmpty()) {
                Toast.makeText(
                    baseContext, "Por favor, coloque o usuário.",
                    Toast.LENGTH_SHORT
                ).show()


            } else if (binding.editTextSenha.text.isNullOrEmpty()) {
                Toast.makeText(
                    baseContext, "Por favor, coloque a senha.",
                    Toast.LENGTH_SHORT
                ).show()

            } else {
                loginUsuarioESenha(
                    binding.editTextUsuario.text.toString(),
                    binding.editTextSenha.text.toString())

            }

        }

        binding.buttonGoogle.setOnClickListener {

        //Chamar um método
            signIn()
        }
    }

    private fun signIn(){
        val intent = googleSignInCliente.signInIntent
        //Método para fazer uma chamada, esperando um resultado
        abreActivity.launch(intent)
    }

    var abreActivity = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()){
        //contrair
        result: ActivityResult ->

        if (result.resultCode == RESULT_OK){
            //Efetuar a tentativa de login
            val intent = result.data
            val task = GoogleSignIn.getSignedInAccountFromIntent(intent)
            try {
                val conta = task.getResult(ApiException::class.java)
                //chamar o método
                loginComGoogle(conta.idToken!!)
            }catch (exception: ApiException){

            }

        }
    }

    private fun loginComGoogle(token: String){
        //Criar uma Credencial
        val credencial = GoogleAuthProvider.getCredential(token, null)
        auth.signInWithCredential(credencial).addOnCompleteListener(this){
            task:Task<AuthResult> ->
            //Fazer uma verificação
            if(task.isSuccessful){
                Toast.makeText(
                    baseContext, "Autentificação Efetuada com o Google",
                    Toast.LENGTH_SHORT
                ).show()
                abrePrincipal()

            }else{
                Toast.makeText(
                    baseContext, "Erro de Autentificação com o Google",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun loginUsuarioESenha(usuario: String, senha: String) {
        auth.signInWithEmailAndPassword(
            usuario,
            senha
        )
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(
                        baseContext, "Autentificação Efetuada.",
                        Toast.LENGTH_SHORT
                    ).show()
                    abrePrincipal()
                    //updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(
                        baseContext, "Erro de Autentificação.",
                        Toast.LENGTH_SHORT
                    ).show()
                    //updateUI(null)
                }
            }
    }

    fun abrePrincipal() {

        //verificar se os campos estão vazio
        binding.editTextUsuario.text.clear()
        binding.editTextSenha.text.clear()

        //chamar a activity principal
        val intent = Intent(
            this,
            PrincipalActivity::class.java
        )

        startActivity(intent)

        finish()

    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser

        if (currentUser != null) {
            if (currentUser.email?.isNotEmpty() == true){
                Toast.makeText(
                    baseContext, "Usuário " + currentUser.email + "logado",
                    Toast.LENGTH_SHORT
                ).show()
                abrePrincipal()
            }
            //updateUI(currentUser)
        }
    }
}