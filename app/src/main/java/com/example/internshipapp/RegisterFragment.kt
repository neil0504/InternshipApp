package com.example.internshipapp

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.internshipapp.databinding.FragmentRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

private const val TAG = "RegisterFragment"
class RegisterFragment(var thisContext: Context, val listener: FromRegisterFragment) : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    lateinit var emailEditText: EditText
    lateinit var passwordEditText: EditText
    lateinit var loginText: TextView
    lateinit var registerButton: Button
    lateinit var mauth: FirebaseAuth

    interface FromRegisterFragment{
        fun onLoginClicked()
        fun userRegistered(currentUser: FirebaseUser?)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        emailEditText = binding.loginEmail
        passwordEditText = binding.password
        loginText = binding.loginText
        registerButton = binding.registerButton


        mauth = Firebase.auth

        registerButton.setOnClickListener {
            register()
        }
        loginText.setOnClickListener {
            listener.onLoginClicked()
        }
    }

    private fun register() {
        mauth.createUserWithEmailAndPassword(emailEditText.text.toString(), passwordEditText.text.toString())
            .addOnCompleteListener(thisContext as Activity) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    listener.userRegistered(mauth.currentUser)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(thisContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = mauth.currentUser
        if(currentUser != null){
            googleAccount = null
            listener.userRegistered(mauth.currentUser)
        }
    }
}