package com.example.internshipapp

import android.app.Activity
import android.content.Context
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.internshipapp.databinding.FragmentLoginBinding
import com.example.internshipapp.googleAccount

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

private const val TAG = "LoginFragment"
class LoginFragment(var thisContext: Context, val listener: FromLoginFragment) : Fragment() {

    private var _binding: FragmentLoginBinding? = null

    private val binding get() = _binding!!

    lateinit var emailEditText: EditText
    lateinit var passwordEditText: EditText
    lateinit var registerText: TextView
    lateinit var loginButton: Button
    lateinit var mauth: FirebaseAuth

    interface FromLoginFragment{
        fun onregisterClicked()
        fun userSignedIn(currentUser: FirebaseUser?)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        emailEditText = binding.loginEmail
        passwordEditText = binding.password
        registerText = binding.registerText
        loginButton = binding.loginButton

        mauth = Firebase.auth

        loginButton.setOnClickListener {
            signIn()
        }
        registerText.setOnClickListener {
            listener.onregisterClicked()
        }
    }

    private fun signIn() {
        mauth.signInWithEmailAndPassword(emailEditText.text.toString(), passwordEditText.text.toString())
            .addOnCompleteListener(thisContext as Activity) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    listener.userSignedIn(mauth.currentUser)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(thisContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStart() {
        super.onStart()
        val currentUser = mauth.currentUser
        if(currentUser != null){
            googleAccount = null
            listener.userSignedIn(mauth.currentUser)
        }
    }
}